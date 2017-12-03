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

package net.luxvacuos.lightengine.tools.ui;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.LANG;
import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.rendering.nanovg.WindowMessage;
import net.luxvacuos.lightengine.client.ui.Alignment;
import net.luxvacuos.lightengine.client.ui.Button;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;
import net.luxvacuos.lightengine.client.ui.ModalWindow;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class MainWindow extends ComponentWindow {

	public MainWindow(int x, int y, int w, int h) {
		super(x, y, w, h, LANG.getRegistryItem("lightengine.mainwindow.name"));
	}

	@Override
	public void initApp() {
		super.setBackgroundColor(0.4f, 0.4f, 0.4f, 1f);

		Button entityEditorButton = new Button(0, 0, 200, 40,
				LANG.getRegistryItem("lightengine.mainwindow.btnentityeditor"));
		Button optionsButton = new Button(0, -50, 200, 40,
				LANG.getRegistryItem("lightengine.mainwindow.btnoptions"));
		Button exitButton = new Button(0, 0, 200, 40, LANG.getRegistryItem("lightengine.mainwindow.btnexit"));

		entityEditorButton.setAlignment(Alignment.RIGHT_BOTTOM);
		entityEditorButton.setWindowAlignment(Alignment.LEFT_TOP);
		optionsButton.setAlignment(Alignment.RIGHT_BOTTOM);
		optionsButton.setWindowAlignment(Alignment.LEFT_TOP);
		exitButton.setAlignment(Alignment.RIGHT_TOP);
		exitButton.setWindowAlignment(Alignment.LEFT_BOTTOM);

		entityEditorButton.setOnButtonPress(() -> {
			super.toggleMinimize();
			int ww = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width"));
			int wh = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height"));
			int x = ww / 2 - 512;
			int y = wh / 2 - 300;
			GraphicalSubsystem.getWindowManager().addWindow(new EntityEditorWindow(x, wh - y, 1024, 600));
		});

		optionsButton.setOnButtonPress(() -> {
			GraphicalSubsystem.getWindowManager().addWindow(new OptionsWindow());
		});

		exitButton.setOnButtonPress(() -> {
			super.closeWindow();
		});

		super.addComponent(entityEditorButton);
		super.addComponent(optionsButton);
		super.addComponent(exitButton);

		super.setWindowClose(WindowClose.DO_NOTHING);
		super.initApp();
	}

	@Override
	public void processWindowMessage(int message, Object param) {
		switch (message) {
		case WindowMessage.WM_CLOSE:
			WindowClose wc = (WindowClose) param;
			switch (wc) {
			case DISPOSE:
				break;
			case DO_NOTHING:
				ModalWindow window = new ModalWindow(340, 200, "", "Exit Dev Tools");
				GraphicalSubsystem.getWindowManager().addWindow(window);
				TaskManager.addTask(() -> {
					window.setOnAccept(() -> {
						new Thread(() -> {
							while (GraphicalSubsystem.getWindowManager().getTotalWindows() > 0)
								try {
									Thread.sleep(400);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							TaskManager.addTask(() -> StateMachine.stop());
						}).start();
						super.setWindowClose(WindowClose.DISPOSE);
						GraphicalSubsystem.getWindowManager().closeAllWindows();
					});
				});
				break;
			}
			break;
		}
		super.processWindowMessage(message, param);
	}

}
