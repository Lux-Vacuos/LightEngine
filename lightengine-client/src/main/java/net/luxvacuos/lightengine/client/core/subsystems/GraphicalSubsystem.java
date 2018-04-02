/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2018 Lux Vacuos
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

package net.luxvacuos.lightengine.client.core.subsystems;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.assimp.Assimp.aiGetVersionMajor;
import static org.lwjgl.assimp.Assimp.aiGetVersionMinor;
import static org.lwjgl.assimp.Assimp.aiGetVersionRevision;
import static org.lwjgl.glfw.GLFW.glfwInit;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengles.GLES20;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.client.core.ClientTaskManager;
import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.core.states.SplashScreenState;
import net.luxvacuos.lightengine.client.rendering.GL;
import net.luxvacuos.lightengine.client.rendering.IRenderer;
import net.luxvacuos.lightengine.client.rendering.glfw.Icon;
import net.luxvacuos.lightengine.client.rendering.glfw.PixelBufferHandle;
import net.luxvacuos.lightengine.client.rendering.glfw.RenderingAPI;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.glfw.WindowHandle;
import net.luxvacuos.lightengine.client.rendering.glfw.WindowManager;
import net.luxvacuos.lightengine.client.rendering.nanovg.IWindowManager;
import net.luxvacuos.lightengine.client.rendering.nanovg.NVGFramebuffers;
import net.luxvacuos.lightengine.client.rendering.nanovg.NanoWindowManager;
import net.luxvacuos.lightengine.client.rendering.nanovg.Timers;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.NanoTheme;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.ThemeManager;
import net.luxvacuos.lightengine.client.rendering.opengl.GLRenderer;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.CachedAssets;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.DefaultData;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.ShaderIncludes;
import net.luxvacuos.lightengine.client.rendering.opengles.GLESRenderer;
import net.luxvacuos.lightengine.client.ui.Font;
import net.luxvacuos.lightengine.universal.core.GlobalVariables;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.subsystems.EventSubsystem;
import net.luxvacuos.lightengine.universal.core.subsystems.UniversalSubsystem;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class GraphicalSubsystem extends UniversalSubsystem {

	private static IWindowManager windowManager;
	private static Window window;
	private static WindowHandle handle;
	private static long renderThreadID;
	private static IRenderer renderer;
	private static RenderingAPI api;

	private static Font robotoRegular, robotoBold, poppinsRegular, poppinsLight, poppinsMedium, poppinsBold,
			poppinsSemiBold, entypo;

	private static boolean resized;

	@Override
	public void init() {
		REGISTRY.register(new Key("/Light Engine/Display/width"), ClientVariables.WIDTH);
		REGISTRY.register(new Key("/Light Engine/Display/height"), ClientVariables.HEIGHT);
		if (ClientVariables.GLES)
			api = RenderingAPI.GLES;
		else
			api = RenderingAPI.GL;

		GLFW.glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		var icons = new Icon[] { new Icon("icon32"), new Icon("icon64") };
		handle = WindowManager.generateHandle((int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")), GlobalVariables.PROJECT);
		handle.isVisible(false).setIcon(icons).setCursor("arrow").useDebugContext(GlobalVariables.debug);
		var pb = new PixelBufferHandle();
		pb.setSrgbCapable(1);
		handle.setPixelBuffer(pb);
		window = WindowManager.generateWindow(handle);
	}

	@Override
	public void initRender() {
		WindowManager.createWindow(handle, window,
				(boolean) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Graphics/vsync")));
		NVGFramebuffers.init(api);
		GL.init(api);
		((ClientTaskManager) TaskManager.tm).switchToSharedContext();

		REGISTRY.register(new Key("/Light Engine/System/lwjgl"), Version.getVersion());
		REGISTRY.register(new Key("/Light Engine/System/glfw"), GLFW.glfwGetVersionString());
		REGISTRY.register(new Key("/Light Engine/System/assimp"),
				aiGetVersionMajor() + "." + aiGetVersionMinor() + "." + aiGetVersionRevision());
		REGISTRY.register(new Key("/Light Engine/System/vk"), "Not Available");

		switch (api) {
		case GL:
			REGISTRY.register(new Key("/Light Engine/System/opengl"), GL11.glGetString(GL11.GL_VERSION));
			REGISTRY.register(new Key("/Light Engine/System/glsl"), GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
			REGISTRY.register(new Key("/Light Engine/System/vendor"), GL11.glGetString(GL11.GL_VENDOR));
			REGISTRY.register(new Key("/Light Engine/System/renderer"), GL11.glGetString(GL11.GL_RENDERER));
			TaskManager.tm.addTaskRenderThread(() -> renderer = new GLRenderer());
			break;
		case GLES:
			REGISTRY.register(new Key("/Light Engine/System/opengl"), GLES20.glGetString(GLES20.GL_VERSION));
			REGISTRY.register(new Key("/Light Engine/System/glsl"),
					GLES20.glGetString(GLES20.GL_SHADING_LANGUAGE_VERSION));
			REGISTRY.register(new Key("/Light Engine/System/vendor"), GLES20.glGetString(GLES20.GL_VENDOR));
			REGISTRY.register(new Key("/Light Engine/System/renderer"), GLES20.glGetString(GLES20.GL_RENDERER));
			TaskManager.tm.addTaskRenderThread(() -> renderer = new GLESRenderer());
			break;
		default:
			break;
		}
		TaskManager.tm.addTaskBackgroundThread(() -> ShaderIncludes.processIncludeFile("common.isl"));
		TaskManager.tm.addTaskBackgroundThread(() -> ShaderIncludes.processIncludeFile("lighting.isl"));
		TaskManager.tm.addTaskBackgroundThread(() -> ShaderIncludes.processIncludeFile("materials.isl"));
		TaskManager.tm.addTaskBackgroundThread(() -> ShaderIncludes.processIncludeFile("global.isl"));
		TaskManager.tm.addTaskMainThread(() -> DefaultData.init());

		ThemeManager.addTheme(new NanoTheme());
		ThemeManager.setTheme((String) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/theme")));

		setWindowManager(new NanoWindowManager(window));

		Timers.initDebugDisplay();
		var loader = window.getResourceLoader();
		robotoRegular = loader.loadNVGFont("Roboto-Regular", "Roboto-Regular");
		robotoBold = loader.loadNVGFont("Roboto-Bold", "Roboto-Bold");
		poppinsRegular = loader.loadNVGFont("Poppins-Regular", "Poppins-Regular");
		poppinsLight = loader.loadNVGFont("Poppins-Light", "Poppins-Light");
		poppinsMedium = loader.loadNVGFont("Poppins-Medium", "Poppins-Medium");
		poppinsBold = loader.loadNVGFont("Poppins-Bold", "Poppins-Bold");
		poppinsSemiBold = loader.loadNVGFont("Poppins-SemiBold", "Poppins-SemiBold");
		entypo = loader.loadNVGFont("Entypo", "Entypo", 40);

		StateMachine.registerState(new SplashScreenState());
		TaskManager.tm.addTaskMainThread(() -> window.setVisible(true));
	}

	@Override
	public void update(float delta) {
		WindowManager.update();
		if (!window.isIconified()) {
			if (window.wasResized()) {
				REGISTRY.register(new Key("/Light Engine/Display/width"), window.getWidth());
				REGISTRY.register(new Key("/Light Engine/Display/height"), window.getHeight());
				EventSubsystem.triggerEvent("lightengine.renderer.resize");
				resized = true;
			}
			windowManager.update(delta);
		}
	}

	@Override
	public void render(float delta) {
		if (!window.isIconified()) {
			if (resized) {
				resized = false;
				window.resetViewport();
				renderer.resize(window.getWidth(), window.getHeight());
				windowManager.resize(window.getWidth(), window.getHeight());
			}
			GL.glClearColor(0, 0, 0, 1);
			GL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			windowManager.render(delta);
		}
		CachedAssets.update(delta);
	}

	@Override
	public void disposeRender() {
		robotoRegular.dispose();
		robotoBold.dispose();
		poppinsRegular.dispose();
		poppinsLight.dispose();
		poppinsMedium.dispose();
		poppinsBold.dispose();
		poppinsSemiBold.dispose();
		entypo.dispose();
		DefaultData.dispose();
		renderer.dispose();
		CachedAssets.dispose();
		((ClientTaskManager) TaskManager.tm).stopRenderBackgroundThread();
		windowManager.dispose();
		window.dispose();
	}

	@Override
	public void dispose() {
		WindowManager.closeAllDisplays();
		GLFW.glfwTerminate();
	}

	public static void setWindowManager(IWindowManager iwm) {
		if (windowManager != null)
			windowManager.dispose();
		windowManager = iwm;
		Logger.log("Window Manager: " + iwm.getClass().getSimpleName());
	}

	public static IWindowManager getWindowManager() {
		return windowManager;
	}

	public static Window getMainWindow() {
		return window;
	}

	public static IRenderer getRenderer() {
		return renderer;
	}

	public static RenderingAPI getAPI() {
		return api;
	}

	public static long getRenderThreadID() {
		return renderThreadID;
	}

	public static void setRenderThreadID(long renderThreadID) {
		GraphicalSubsystem.renderThreadID = renderThreadID;
	}

}
