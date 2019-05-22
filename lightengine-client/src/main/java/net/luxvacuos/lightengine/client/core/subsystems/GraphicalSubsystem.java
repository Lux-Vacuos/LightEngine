/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2019 Lux Vacuos
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
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_RENDERER;
import static org.lwjgl.opengl.GL11C.GL_VENDOR;
import static org.lwjgl.opengl.GL11C.GL_VERSION;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL11C.glGetString;
import static org.lwjgl.opengl.GL20C.GL_SHADING_LANGUAGE_VERSION;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.client.core.ClientTaskManager;
import net.luxvacuos.lightengine.client.core.exception.GLFWException;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.rendering.IRenderer;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.glfw.WindowHandle;
import net.luxvacuos.lightengine.client.rendering.glfw.WindowManager;
import net.luxvacuos.lightengine.client.rendering.nanovg.IWindowManager;
import net.luxvacuos.lightengine.client.rendering.nanovg.NanoWindowManager;
import net.luxvacuos.lightengine.client.rendering.nanovg.Timers;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.NanoTheme;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.ThemeManager;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.SurfaceManager;
import net.luxvacuos.lightengine.client.rendering.opengl.GLRenderer;
import net.luxvacuos.lightengine.client.rendering.opengl.GLResourcesManagerBackend;
import net.luxvacuos.lightengine.client.rendering.opengl.RenderingSettings;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.CachedAssets;
import net.luxvacuos.lightengine.client.resources.DefaultData;
import net.luxvacuos.lightengine.client.resources.ResourcesManager;
import net.luxvacuos.lightengine.client.resources.config.GraphicalSubConfig;
import net.luxvacuos.lightengine.client.ui.Font;
import net.luxvacuos.lightengine.universal.core.GlobalVariables;
import net.luxvacuos.lightengine.universal.core.Task;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.subsystems.EventSubsystem;
import net.luxvacuos.lightengine.universal.core.subsystems.Subsystem;
import net.luxvacuos.lightengine.universal.loader.EngineData;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class GraphicalSubsystem extends Subsystem<GraphicalSubConfig> {

	private static IWindowManager windowManager;
	private static Window window;
	private static WindowHandle handle;
	private static long renderThreadID;
	private static IRenderer renderer;

	private static RenderingSettings renderingSettings;
	private static File renderingSettingsFile;

	private static SurfaceManager surfaceManager;

	private static Font robotoRegular, robotoBold, poppinsRegular, poppinsLight, poppinsMedium, poppinsBold,
			poppinsSemiBold, entypo;

	public GraphicalSubsystem() {
		super(GraphicalSubConfig.class, "engine/config/graphicalSub.json");
	}

	@Override
	public void init(EngineData ed) {
		super.init(ed);
		REGISTRY.register(new Key("/Light Engine/Display/width"), 1280);
		REGISTRY.register(new Key("/Light Engine/Display/height"), 720);
		renderingSettingsFile = new File(ed.userDir + "/config/rendering.json");

		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
		if (!glfwInit())
			throw new GLFWException("Unable to initialize GLFW");

		handle = WindowManager.generateHandle((int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")), ed.project);
		handle.isVisible(false).setIcon(config.getIcons()).setCursor(config.getCursor())
				.useDebugContext(GlobalVariables.debug);

		window = WindowManager.generateWindow(handle);

		((ClientTaskManager) TaskManager.tm).switchToSharedContext();

		loadSettings();
		EventSubsystem.addEvent("lightengine.renderer.savesettings", () -> saveSettings());
	}

	@Override
	public void initRender() {
		((ClientTaskManager) TaskManager.tm).setRenderThread(Thread.currentThread());

		WindowManager.createWindow(handle, window, renderingSettings.vsyncEnabled);
		TaskManager.tm.submitRenderBackgroundThread(new Task<Void>() {
			@Override
			protected Void call() {
				REGISTRY.register(new Key("/Light Engine/System/lwjgl"), Version.getVersion());
				REGISTRY.register(new Key("/Light Engine/System/glfw"), GLFW.glfwGetVersionString());
				REGISTRY.register(new Key("/Light Engine/System/assimp"),
						aiGetVersionMajor() + "." + aiGetVersionMinor() + "." + aiGetVersionRevision());
				REGISTRY.register(new Key("/Light Engine/System/vk"), "Not Available");

				REGISTRY.register(new Key("/Light Engine/System/opengl"), glGetString(GL_VERSION));
				REGISTRY.register(new Key("/Light Engine/System/glsl"), glGetString(GL_SHADING_LANGUAGE_VERSION));
				REGISTRY.register(new Key("/Light Engine/System/vendor"), glGetString(GL_VENDOR));
				REGISTRY.register(new Key("/Light Engine/System/renderer"), glGetString(GL_RENDERER));
				return null;
			}
		}).get();

		renderer = new GLRenderer(renderingSettings);

		ResourcesManager.setBackend(new GLResourcesManagerBackend(window));
		ResourcesManager.processShaderIncludes("ENGINE_ISL_common");
		ResourcesManager.processShaderIncludes("ENGINE_ISL_lighting");
		ResourcesManager.processShaderIncludes("ENGINE_ISL_materials");
		ResourcesManager.processShaderIncludes("ENGINE_ISL_global");

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

		surfaceManager = new SurfaceManager(window);

		window.getSizeCallback().addCallback((windowID, width, height) -> {
			REGISTRY.register(new Key("/Light Engine/Display/width"), width);
			REGISTRY.register(new Key("/Light Engine/Display/height"), height);
			TaskManager.tm.addTaskRenderThread(() -> window.resetViewport());
		});
		window.getCloseCallback().addCallback((windowID) -> {
			StateMachine.stop();
		});
	}

	@Override
	public void run() {
		TaskManager.tm.addTaskMainThread(() -> window.setVisible(true));
		DefaultData.init();
	}

	@Override
	public void update(float delta) {
		KeyboardHandler kh = window.getKeyboardHandler();
		if (kh.isKeyPressed(GLFW.GLFW_KEY_F11)) {
			kh.ignoreKeyUntilRelease(GLFW.GLFW_KEY_F11);
			if (window.isFullscreen())
				window.exitFullScreen();
			else
				window.enterFullScreen();
		}
		WindowManager.update();
		if (!window.isIconified()) {
			if (window.wasResized()) {
				TaskManager.tm.addTaskRenderThread(() -> {
					renderer.resize(window.getWidth(), window.getHeight());
					//windowManager.resize(window.getWidth(), window.getHeight());
					EventSubsystem.triggerEvent("lightengine.renderer.resize");
				});
			}
			//windowManager.update(delta);
			surfaceManager.update(delta);
		}
	}

	@Override
	public void render(float delta) {
		if (!window.isIconified()) {
			glClearColor(0, 0, 0, 1);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			//windowManager.render(delta);
			surfaceManager.render(delta);
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
		surfaceManager.dispose();
		window.dispose();
	}

	@Override
	public void dispose() {
		saveSettings();
		WindowManager.closeAllDisplays();
		GLFW.glfwTerminate();
	}

	private void loadSettings() {
		renderingSettings = new RenderingSettings();
		if (!renderingSettingsFile.exists())
			return;
		try (Reader reader = new FileReader(renderingSettingsFile);) {
			renderingSettings = new Gson().fromJson(reader, RenderingSettings.class);
		} catch (Exception e) {
		}
	}

	private void saveSettings() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setPrettyPrinting();
		try (Writer writer = new FileWriter(renderingSettingsFile)) {
			gsonBuilder.create().toJson(renderingSettings, writer);
		} catch (Exception e) {
		}
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

	public static RenderingSettings getRenderingSettings() {
		return renderingSettings;
	}

	public static long getRenderThreadID() {
		return renderThreadID;
	}

	public static void setRenderThreadID(long renderThreadID) {
		GraphicalSubsystem.renderThreadID = renderThreadID;
	}

	public static SurfaceManager getSurfaceManager() {
		return surfaceManager;
	}

}
