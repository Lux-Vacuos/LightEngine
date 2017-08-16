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

package net.luxvacuos.lightengine.client.core.subsystems;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.assimp.Assimp.aiGetVersionMajor;
import static org.lwjgl.assimp.Assimp.aiGetVersionMinor;
import static org.lwjgl.assimp.Assimp.aiGetVersionRevision;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.core.states.SplashScreenState;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Icon;
import net.luxvacuos.lightengine.client.rendering.api.glfw.PixelBufferHandle;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.glfw.WindowHandle;
import net.luxvacuos.lightengine.client.rendering.api.glfw.WindowManager;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.IWindowManager;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.NanoWindowManager;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.Timers;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.themes.ITheme;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.themes.NanoTheme;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.themes.Theme;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.themes.ThemeManager;
import net.luxvacuos.lightengine.client.rendering.api.opengl.ParticleDomain;
import net.luxvacuos.lightengine.client.rendering.api.opengl.Renderer;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.CachedAssets;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.DefaultData;
import net.luxvacuos.lightengine.client.rendering.api.opengl.shaders.ShaderIncludes;
import net.luxvacuos.lightengine.client.resources.ResourceLoader;
import net.luxvacuos.lightengine.client.ui.Font;
import net.luxvacuos.lightengine.universal.core.GlobalVariables;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.subsystems.ISubsystem;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class GraphicalSubsystem implements ISubsystem {

	private static IWindowManager windowManager;
	private static ThemeManager themeManager;
	private static Window window;

	private Font robotoRegular, robotoBold, poppinsRegular, poppinsLight, poppinsMedium, poppinsBold, poppinsSemiBold,
			entypo;

	@Override
	public void init() {
		REGISTRY.register(new Key("/Light Engine/Display/width"), ClientVariables.WIDTH);
		REGISTRY.register(new Key("/Light Engine/Display/height"), ClientVariables.HEIGHT);

		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		Icon[] icons = new Icon[] { new Icon("icon32"), new Icon("icon64") };
		WindowHandle handle = WindowManager.generateHandle(
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")), GlobalVariables.PROJECT);
		handle.isVisible(false).setIcon(icons).setCursor("arrow").useDebugContext(GlobalVariables.debug);
		PixelBufferHandle pb = new PixelBufferHandle();
		pb.setSrgbCapable(1);
		handle.setPixelBuffer(pb);
		long gameWindowID = WindowManager.createWindow(handle,
				(boolean) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Graphics/vsync")));
		window = WindowManager.getWindow(gameWindowID);
		themeManager = new ThemeManager();
		themeManager.addTheme(new NanoTheme());
		ITheme theme = themeManager
				.getTheme((String) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/theme")));
		if (theme == null)
			theme = themeManager.getTheme("Nano");
		Theme.setTheme(theme);

		setWindowManager(new NanoWindowManager(window));

		Timers.initDebugDisplay();
		ResourceLoader loader = window.getResourceLoader();
		robotoRegular = loader.loadNVGFont("Roboto-Regular", "Roboto-Regular");
		robotoBold = loader.loadNVGFont("Roboto-Bold", "Roboto-Bold");
		poppinsRegular = loader.loadNVGFont("Poppins-Regular", "Poppins-Regular");
		poppinsLight = loader.loadNVGFont("Poppins-Light", "Poppins-Light");
		poppinsMedium = loader.loadNVGFont("Poppins-Medium", "Poppins-Medium");
		poppinsBold = loader.loadNVGFont("Poppins-Bold", "Poppins-Bold");
		poppinsSemiBold = loader.loadNVGFont("Poppins-SemiBold", "Poppins-SemiBold");
		entypo = loader.loadNVGFont("Entypo", "Entypo", 40);
		TaskManager.addTask(() -> ShaderIncludes.processIncludeFile("common.isl"));
		TaskManager.addTask(() -> ShaderIncludes.processIncludeFile("lighting.isl"));
		TaskManager.addTask(() -> ShaderIncludes.processIncludeFile("materials.isl"));
		TaskManager.addTask(() -> DefaultData.init(loader));
		TaskManager.addTask(() -> ParticleDomain.init());
		TaskManager.addTask(() -> Renderer.init(window));
		StateMachine.registerState(new SplashScreenState());
		REGISTRY.register(new Key("/Light Engine/System/lwjgl"), Version.getVersion());
		REGISTRY.register(new Key("/Light Engine/System/glfw"), GLFW.glfwGetVersionString());
		REGISTRY.register(new Key("/Light Engine/System/opengl"), glGetString(GL_VERSION));
		REGISTRY.register(new Key("/Light Engine/System/glsl"), glGetString(GL_SHADING_LANGUAGE_VERSION));
		REGISTRY.register(new Key("/Light Engine/System/vendor"), glGetString(GL_VENDOR));
		REGISTRY.register(new Key("/Light Engine/System/renderer"), glGetString(GL_RENDERER));
		REGISTRY.register(new Key("/Light Engine/System/assimp"),
				aiGetVersionMajor() + "." + aiGetVersionMinor() + "." + aiGetVersionRevision());
		REGISTRY.register(new Key("/Light Engine/System/vk"), "Not Available");
		window.setVisible(true);
		for (int x = 0; x < 16; x++) {
			window.updateDisplay((int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/fps")));
			WindowManager.update();
		}
	}

	@Override
	public void restart() {
	}

	@Override
	public void update(float delta) {
		GraphicalSubsystem.getWindowManager().update(delta);
	}

	@Override
	public void render(float delta) {
		if (window.wasResized()) {
			REGISTRY.register(new Key("/Light Engine/Display/width"), window.getWidth());
			REGISTRY.register(new Key("/Light Engine/Display/height"), window.getHeight());
			Renderer.reloadDeferred();
			windowManager.reloadCompositor();
		}
		CachedAssets.update(delta);
		WindowManager.update();
		Renderer.clearBuffer(GL_COLOR_BUFFER_BIT);
		Renderer.clearColors(0, 0, 0, 1);
		GraphicalSubsystem.getWindowManager().render();
	}

	@Override
	public void dispose() {
		robotoRegular.dispose();
		robotoBold.dispose();
		poppinsRegular.dispose();
		poppinsLight.dispose();
		poppinsMedium.dispose();
		poppinsBold.dispose();
		poppinsSemiBold.dispose();
		entypo.dispose();
		DefaultData.dispose();
		Renderer.cleanUp();
		CachedAssets.dispose();
		windowManager.dispose();
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

}
