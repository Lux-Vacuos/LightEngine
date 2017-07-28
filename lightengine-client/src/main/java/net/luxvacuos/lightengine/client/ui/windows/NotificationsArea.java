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

package net.luxvacuos.lightengine.client.ui.windows;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.nanovg.NanoVG.*;

import org.lwjgl.glfw.GLFW;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.ui.Alignment;
import net.luxvacuos.lightengine.client.ui.Box;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;
import net.luxvacuos.lightengine.client.ui.Container;
import net.luxvacuos.lightengine.client.ui.Direction;
import net.luxvacuos.lightengine.client.ui.FlowLayout;
import net.luxvacuos.lightengine.client.ui.Text;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class NotificationsArea extends ComponentWindow {

	public NotificationsArea() {
		super((int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")) - 200,
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")), 200,
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height"))
						- (float) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/shellHeight")),
				"");
	}

	@Override
	public void initApp(Window window) {
		super.setDecorations(false);
		super.setBackgroundColor("#00000000");
		super.setLayout(new FlowLayout(Direction.DOWN, 5, 0));
		super.setAsBackground(true);
		super.setBlurBehind(false);

		super.initApp(window);
	}

	private void remove(Container cont) {
		super.removeComponent(cont);
	}

	public void addNotification() {
		Container notification = new Container(0, 0, 0, 150);
		notification.setResizeH(true);
		notification.setWindowAlignment(Alignment.LEFT_TOP);
		notification.setAlignment(Alignment.RIGHT_BOTTOM);

		super.addComponent(notification);

		GraphicalSubsystem.getWindowManager()
				.addWindow(new ComponentWindow(x + 300, y + super.getFinalH(), 300, 150, "") {

					private float time;
					private boolean fadeIn = true;
					private boolean fadeOut;

					@Override
					public void initApp(Window window) {
						super.setDecorations(false);
						super.setBackgroundColor("#1F1F1F78");
						super.setLayout(new FlowLayout(Direction.DOWN, 5, 0));
						super.setAlwaysOnTop(true);
						Text title = new Text("Notification Test", 5, 0);
						title.setAlign(NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
						title.setFontSize(32);
						title.setWindowAlignment(Alignment.LEFT_TOP);
						super.addComponent(title);
						super.initApp(window);
					}

					@Override
					public void disposeApp(Window window) {
						remove(notification);
						super.disposeApp(window);
					}

					@Override
					public void alwaysUpdateApp(float delta, Window window) {
						if (fadeIn) {
							x -= 500 * delta;
							float xt = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")) - 295;
							if (x <= xt)
								fadeIn = false;
						}
						if (fadeOut) {
							x += 500 * delta;
							float xt = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")) + 5;
							if (x >= xt)
								super.closeWindow();
						}
						time += delta;
						if (time > 5)
							fadeOut = true;
						super.alwaysUpdateApp(delta, window);
					}
				});
	}

	@Override
	public void alwaysUpdateApp(float delta, Window window) {
		KeyboardHandler kb = window.getKeyboardHandler();
		if (kb.isKeyPressed(GLFW.GLFW_KEY_N)) {
			addNotification();
			kb.ignoreKeyUntilRelease(GLFW.GLFW_KEY_N);
		}
		super.alwaysUpdateApp(delta, window);
	}

	@Override
	public void onMainResize() {
		x = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")) - 200;
		y = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height"));
		h = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height"))
				- (float) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/shellHeight"));
		w = 200;
	}

}
