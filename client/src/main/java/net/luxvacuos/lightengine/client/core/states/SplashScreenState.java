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

package net.luxvacuos.lightengine.client.core.states;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.opengl.Renderer;
import net.luxvacuos.lightengine.client.ui.Alignment;
import net.luxvacuos.lightengine.client.ui.Image;
import net.luxvacuos.lightengine.client.ui.RootComponentWindow;
import net.luxvacuos.lightengine.client.ui.Spinner;
import net.luxvacuos.lightengine.client.ui.windows.Shell;
import net.luxvacuos.lightengine.universal.core.AbstractEngine;
import net.luxvacuos.lightengine.universal.core.GlobalVariables;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.util.registry.Key;

/**
 * Splash screen State, show only in the load.
 * 
 * @author Guerra24 <pablo230699@hotmail.com>
 *
 */
public class SplashScreenState extends AbstractState {

	private RootComponentWindow component;

	public SplashScreenState() {
		super(StateNames.SPLASH_SCREEN);
	}

	@Override
	public void init() {
		component = new RootComponentWindow(0, (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")), "splash");
		component.toggleTitleBar();
		component.setDecorations(false);
		component.setBackgroundColor(1, 1, 1, 1);
		component.setBlurBehind(false);
		component.setAsBackground(true);
		Image lv = new Image(0, 0, 512, 512,
				GraphicalSubsystem.getMainWindow().getResourceLoader().loadNVGTexture("LuxVacuos-Logo"));
		lv.setAlignment(Alignment.CENTER);
		lv.setWindowAlignment(Alignment.CENTER);

		Spinner spinner = new Spinner(0, -220, 20);
		spinner.setWindowAlignment(Alignment.CENTER);

		component.addComponent(lv);
		component.addComponent(spinner);

		GraphicalSubsystem.getWindowManager().addWindow(component);
	}

	@Override
	public void end() {
		super.end();
		component.closeWindow();
		Shell shell = new Shell(0, 30, (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")), 30);
		GraphicalSubsystem.getWindowManager().addWindow(shell);
		GraphicalSubsystem.getWindowManager().setShell(shell);
	}

	@Override
	public void render(AbstractEngine lightengine, float alpha) {
		Window window = GraphicalSubsystem.getMainWindow();
		Renderer.clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		Renderer.clearColors(1, 1, 1, 1);
		window.beingNVGFrame();
		GraphicalSubsystem.getWindowManager().render();
		window.endNVGFrame();
	}

	@Override
	public void update(AbstractEngine lightengine, float delta) {
		GraphicalSubsystem.getWindowManager().update(delta);
		if (TaskManager.isEmpty())
			StateMachine.setCurrentState(GlobalVariables.MAIN_STATE);
	}

}
