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

package net.luxvacuos.lightengine.client.core.states;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.rendering.nanovg.WindowMessage;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Surface;
import net.luxvacuos.lightengine.client.ui.OnAction;
import net.luxvacuos.lightengine.client.ui.v2.surfaces.SplashSurface;
import net.luxvacuos.lightengine.client.ui.windows.LoadWindow;
import net.luxvacuos.lightengine.client.ui.windows.Shell;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.states.StateNames;
import net.luxvacuos.lightengine.universal.util.registry.KeyCache;

/**
 * Splash screen State, show only in the load.
 * 
 * @author Guerra24 <pablo230699@hotmail.com>
 *
 */
public class SplashScreenState extends AbstractState {

	private LoadWindow window;
	private boolean ready, showSpinner;
	private float timer;

	public SplashScreenState() {
		super(StateNames.SPLASH_SCREEN);
	}

	@Override
	public void start() {
		window = new LoadWindow();
		GraphicalSubsystem.getWindowManager().addWindow(window);

		GraphicalSubsystem.getSurfaceManager().setRootSurface(new SplashSurface());
		super.start();
	}

	@Override
	public void end() {
		super.end();
		window.closeWindow();
		Shell shell = new Shell(0, 0, (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/width")),
				30);
		GraphicalSubsystem.getWindowManager().addWindow(shell);
		GraphicalSubsystem.getWindowManager().setShell(shell);
		shell.toggleShell();

	}

	@Override
	public void update(float delta) {
		if (ready)
			return;
		timer += delta;
		if (timer > 4) {
			if (StateMachine.hasState(StateNames.MAIN)) {
				if (TaskManager.tm.isEmpty()) {
					ready = true;
					window.notifyWindow(WindowMessage.WM_FADE_OUT,
							(OnAction) () -> StateMachine.setCurrentState(StateNames.MAIN));
				} else {
					if (!showSpinner) {
						window.addSpinner();
						showSpinner = true;
					}
				}
			} else {
				if (!window.onLoadFailed()) {
					ready = true;
					window.notifyWindow(WindowMessage.WM_FADE_OUT,
							(OnAction) () -> TaskManager.tm.addTaskBackgroundThread(() -> StateMachine.stop()));
				}
			}
		}
	}

}
