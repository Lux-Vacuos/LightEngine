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

package net.luxvacuos.lightengine.demo;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import net.luxvacuos.lightengine.client.bootstrap.Bootstrap;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.WM;
import net.luxvacuos.lightengine.client.ui.windows.BackgroundWindow;
import net.luxvacuos.lightengine.demo.ui.MainWindow;
import net.luxvacuos.lightengine.universal.core.GlobalVariables;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class MainMenuState extends AbstractState {

	private BackgroundWindow background;

	public MainMenuState() {
		super("_main");
	}

	@Override
	public void init() {
		super.init();
		TaskManager.addTask(() -> StateMachine.registerState(new MainState()));
		//TaskManager.addTask(() -> StateMachine.registerState(new GameState()));
	}

	@Override
	public void start() {
		if (background == null)
			background = new BackgroundWindow(0,
					(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")),
					(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")),
					(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")));
		if (!GraphicalSubsystem.getWindowManager().existWindow(background))
			GraphicalSubsystem.getWindowManager().addWindow(0, background);
		int ww = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width"));
		int wh = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height"));
		int x = ww / 2 - 512;
		int y = wh / 2 - 300;
		GraphicalSubsystem.getWindowManager().addWindow(new MainWindow(x, wh - y, 1024, 600));
		//GraphicalSubsystem.getWindowManager().addWindow(new Profiler());
		super.start();
	}

	@Override
	public void render(float delta) {
	}

	@Override
	public void update(float delta) {
	}

	public static void main(String[] args) {
		GlobalVariables.PROJECT = "LightEngineDemo";
		TaskManager.addTask(() -> StateMachine.registerState(new MainMenuState()));
		new Bootstrap(args);
	}

}