/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2017 Lux Vacuos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.luxvacuos.lightengine.client.rendering.glfw;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.glGetIntegerv;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.NVXGPUMemoryInfo;
import org.lwjgl.opengl.WGLAMDGPUAssociation;
import org.lwjgl.system.MemoryStack;

import com.badlogic.gdx.utils.Array;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.core.exception.DecodeTextureException;
import net.luxvacuos.lightengine.client.core.exception.GLFWException;
import net.luxvacuos.lightengine.client.resources.ResourceLoader;

public final class WindowManager {

	private static Array<Window> windows = new Array<>();

	private WindowManager() {
	}

	public static WindowHandle generateHandle(int width, int height, String title) {
		return new WindowHandle(width, height, title);
	}

	public static long createWindow(WindowHandle handle, boolean vsync) {
		Logger.log("Creating new Window '" + handle.title + "'");
		if (glfwVulkanSupported()) {
			// TODO: Implement Vulkan
		}

		long windowID = GLFW.glfwCreateWindow(handle.width, handle.height, (handle.title == null ? "" : handle.title),
				NULL, NULL);
		if (windowID == NULL)
			throw new GLFWException("Failed to create GLFW Window '" + handle.title + "'");

		Window window = new Window(windowID, handle.width, handle.height);

		GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		GLFW.glfwSetWindowPos(windowID, (vidmode.width() - window.width) / 2, (vidmode.height() - window.height) / 2);
		GLFW.glfwMakeContextCurrent(windowID);
		GLFW.glfwSwapInterval(vsync ? 1 : 0);
		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.callocInt(1);
			IntBuffer h = stack.callocInt(1);
			IntBuffer comp = stack.callocInt(1);

			if (handle.cursor != null) {

				ByteBuffer imageBuffer;
				try {
					imageBuffer = ResourceLoader.ioResourceToByteBuffer("assets/cursors/" + handle.cursor + ".png",
							1 * 1024);
				} catch (IOException e) {
					throw new GLFWException(e);
				}

				if (!stbi_info_from_memory(imageBuffer, w, h, comp))
					throw new DecodeTextureException("Failed to read image information: " + stbi_failure_reason());

				ByteBuffer image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
				if (image == null)
					throw new DecodeTextureException("Failed to load image: " + stbi_failure_reason());

				GLFWImage img = GLFWImage.malloc().set(w.get(0), h.get(0), image);
				GLFW.glfwSetCursor(windowID, GLFW.glfwCreateCursor(img, 0, 0));

				stbi_image_free(image);
			}

			if (handle.icons.size != 0) {
				GLFWImage.Buffer iconsbuff = GLFWImage.malloc(handle.icons.size);
				int i = 0;
				for (Icon icon : handle.icons) {
					ByteBuffer imageBuffer;
					try {
						imageBuffer = ResourceLoader.ioResourceToByteBuffer("assets/icons/" + icon.path + ".png",
								16 * 1024);
					} catch (IOException e) {
						throw new GLFWException(e);
					}

					if (!stbi_info_from_memory(imageBuffer, w, h, comp))
						throw new DecodeTextureException("Failed to read image information: " + stbi_failure_reason());

					icon.image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
					if (icon.image == null)
						throw new DecodeTextureException("Failed to load image: " + stbi_failure_reason());

					icon.image.flip();
					iconsbuff.position(i).width(w.get(0)).height(h.get(0)).pixels(icon.image);
					i++;
				}
				iconsbuff.position(0);
				GLFW.glfwSetWindowIcon(windowID, iconsbuff);
				iconsbuff.free();
				for (Icon icon : handle.icons) {
					stbi_image_free(icon.image);
				}

			}
		}

		boolean forwardCompat = GLFW.glfwGetWindowAttrib(windowID, GLFW.GLFW_OPENGL_FORWARD_COMPAT) == GLFW.GLFW_TRUE;
		window.capabilities = GL.createCapabilities(forwardCompat);

		int nvgFlags = NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_STENCIL_STROKES;
		if (ClientVariables.debug)
			nvgFlags = (nvgFlags | NanoVGGL3.NVG_DEBUG);
		window.nvgID = NanoVGGL3.nvgCreate(nvgFlags);

		if (window.nvgID == NULL)
			throw new GLFWException("Fail to create NanoVG context for Window '" + handle.title + "'");

		window.lastLoopTime = getTime();

		int[] h = new int[1];
		int[] w = new int[1];

		GLFW.glfwGetFramebufferSize(windowID, w, h);
		window.framebufferHeight = h[0];
		window.framebufferWidth = w[0];
		GLFW.glfwGetWindowSize(windowID, w, h);
		window.height = h[0];
		window.width = w[0];
		window.pixelRatio = (float) window.framebufferWidth / (float) window.width;
		window.resetViewport();

		window.created = true;

		windows.add(window);

		return windowID;
	}

	public static Window getWindow(long windowID) {
		for (Window window : windows) {
			if (window.windowID == windowID) {
				int index = windows.indexOf(window, true);
				if (index != 0)
					windows.swap(0, index); // Swap the window to the front of
											// the array to speed up future
											// recurring searches
				if (GLFW.glfwGetCurrentContext() != windowID)
					GLFW.glfwMakeContextCurrent(windowID);
				return window;
			}

		}

		return null;
	}

	public static void closeAllDisplays() {
		for (Window window : windows) {
			if (!window.created)
				continue;
			window.dispose();
			window.closeDisplay();
		}
	}

	public static void update() {
		for (Window window : windows) {
			window.dirty = false;
			window.resized = false;
			window.getMouseHandler().update();
		}
		glfwPollEvents();
	}

	public static double getTime() {
		return GLFW.glfwGetTime();
	}

	public static long getNanoTime() {
		return (long) (getTime() * (1000L * 1000L * 1000L));
	}

	private static int[] maxVram = new int[1];
	private static int[] usedVram = new int[1];
	private static boolean nvidia = false;
	private static boolean amd = false;
	private static boolean detected = false;

	public static int getUsedVRAM() {
		if (!detected)
			detectGraphicsCard();

		if (nvidia)
			glGetIntegerv(NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_CURRENT_AVAILABLE_VIDMEM_NVX, usedVram);
		return maxVram[0] - usedVram[0];
	}

	public static boolean isNvidia() {
		if (!detected)
			detectGraphicsCard();
		return nvidia;
	}

	public static boolean isAmd() {
		if (!detected)
			detectGraphicsCard();
		return amd;
	}

	private static void detectGraphicsCard() {
		if (glGetString(GL_VENDOR).contains("NVIDIA")) {
			nvidia = true;
			glGetIntegerv(NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_DEDICATED_VIDMEM_NVX, maxVram);
			Logger.log("Max VRam: " + maxVram[0] + "KB");
		} else if (glGetString(GL_VENDOR).contains("AMD")) {
			amd = true;
			glGetIntegerv(WGLAMDGPUAssociation.WGL_GPU_RAM_AMD, maxVram);
			Logger.log("Max VRam: " + maxVram[0] + "MB");
		}

		detected = true;
	}

}
