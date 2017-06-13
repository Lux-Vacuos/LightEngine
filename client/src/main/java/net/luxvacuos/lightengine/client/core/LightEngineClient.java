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

package net.luxvacuos.lightengine.client.core;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.client.bootstrap.Bootstrap;
import net.luxvacuos.lightengine.client.core.states.CrashState;
import net.luxvacuos.lightengine.client.core.states.StateNames;
import net.luxvacuos.lightengine.client.core.subsystems.ClientCoreSubsystem;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.core.subsystems.SoundSubsystem;
import net.luxvacuos.lightengine.client.input.Mouse;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.Timers;
import net.luxvacuos.lightengine.client.rendering.api.opengl.GPUProfiler;
import net.luxvacuos.lightengine.client.rendering.api.opengl.GPUTaskProfile;
import net.luxvacuos.lightengine.universal.core.AbstractEngine;
import net.luxvacuos.lightengine.universal.core.EngineType;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class LightEngineClient extends AbstractEngine {

	public LightEngineClient() {
		GLFW.glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
		super.engineType = EngineType.CLIENT;
		init();
	}

	/**
	 * Init function
	 */
	@Override
	public void init() {
		Logger.init();
		Logger.log("Starting Client");

		super.addSubsystem(new ClientCoreSubsystem());
		super.addSubsystem(new GraphicalSubsystem());
		if (!ClientVariables.WSL)
			super.addSubsystem(new SoundSubsystem());

		super.initSubsystems();

		Logger.log("Light Engine Client Version: " + ClientVariables.version);
		Logger.log("Running on: " + Bootstrap.getPlatform());
		Logger.log("LWJGL Version: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/lwjgl")));
		Logger.log("GLFW Version: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/glfw")));
		Logger.log("OpenGL Version: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/opengl")));
		Logger.log("GLSL Version: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/glsl")));
		Logger.log("Assimp: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/assimp")));
		Logger.log("Vendor: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/vendor")));
		Logger.log("Renderer: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/renderer")));

		StateMachine.setCurrentState(StateNames.SPLASH_SCREEN);
		try {
			StateMachine.run();
			update();
			dispose();
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			handleError(t);
		}
	}

	/**
	 * Main Loop
	 */
	@Override
	public void update() {
		float delta = 0;
		float accumulator = 0f;
		float interval = 1f / (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/ups"));
		float alpha = 0;
		int fps = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/fps"));
		Window window = GraphicalSubsystem.getMainWindow();
		while (StateMachine.isRunning() && !(window.isCloseRequested())) {
			// Timers.startCPUTimer();
			TaskManager.update();
			if (window.getTimeCount() > 1f) {
				CoreSubsystem.ups = CoreSubsystem.upsCount;
				CoreSubsystem.upsCount = 0;
				window.setTimeCount(window.getTimeCount() - 1);
			}
			delta = window.getDelta();
			accumulator += delta;
			while (accumulator >= interval) {
				super.updateSubsystems(interval);
				StateMachine.update(this, interval);
				CoreSubsystem.upsCount++;
				accumulator -= interval;
			}
			alpha = accumulator / interval;
			// Timers.stopCPUTimer();
			// Timers.startGPUTimer();
			GPUProfiler.startFrame();
			GPUProfiler.start("Render");
			StateMachine.render(this, alpha);
			GPUProfiler.end();
			// Timers.stopGPUTimer();
			// Timers.update();
			window.updateDisplay(fps);
			GPUProfiler.endFrame();
		}
	}

	/**
	 * Handle all errors
	 * 
	 * @param e
	 *            Throwable
	 */
	@Override
	public void handleError(Throwable e) {
		CrashState.t = e;
		StateMachine.registerState(new CrashState());
		if (!StateMachine.isRunning())
			StateMachine.run();
		StateMachine.setCurrentState(StateNames.CRASH);
		Mouse.setGrabbed(false);
		update();
		dispose();
		GLFW.glfwTerminate();
	}

	@Override
	public void dispose() {
		super.dispose();
		StateMachine.dispose();
	}

}
