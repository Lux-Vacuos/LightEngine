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

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ui.Alignment;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;
import net.luxvacuos.lightengine.client.ui.Image;
import net.luxvacuos.lightengine.client.ui.windows.Shell;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.states.StateNames;
import net.luxvacuos.lightengine.universal.util.registry.Key;

/**
 * Splash screen State, show only in the load.
 * 
 * @author Guerra24 <pablo230699@hotmail.com>
 *
 */
public class SplashScreenState extends AbstractState {

	private ComponentWindow component;

	public SplashScreenState() {
		super(StateNames.SPLASH_SCREEN);
	}

	@Override
	public void init() {
		component = new ComponentWindow(0, (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")),
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

		component.addComponent(lv);

		GraphicalSubsystem.getWindowManager().addWindow(component);
	}

	@Override
	public void end() {
		super.end();
		component.closeWindow();
		Shell shell = new Shell(0, 0, (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")), 30);
		GraphicalSubsystem.getWindowManager().addWindow(shell);
		GraphicalSubsystem.getWindowManager().setShell(shell);
		shell.toggleShell();
	}

	public void render(float alpha) {
	}

	@Override
	public void update(float delta) {
		if (TaskManager.isEmpty()) {
			try {
				StateMachine.setCurrentState(StateNames.MAIN);
			} catch (NullPointerException e) {
				StateMachine.registerState(new LoadState());
				StateMachine.setCurrentState(StateNames.LOAD);
			}
		}
	}

}
