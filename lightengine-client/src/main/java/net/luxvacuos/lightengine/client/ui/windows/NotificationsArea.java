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

package net.luxvacuos.lightengine.client.ui.windows;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_TOP;

import org.lwjgl.glfw.GLFW;

import com.badlogic.gdx.math.Interpolation;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.rendering.nanovg.WindowMessage;
import net.luxvacuos.lightengine.client.ui.Alignment;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;
import net.luxvacuos.lightengine.client.ui.Container;
import net.luxvacuos.lightengine.client.ui.Direction;
import net.luxvacuos.lightengine.client.ui.FlowLayout;
import net.luxvacuos.lightengine.client.ui.Notification;
import net.luxvacuos.lightengine.client.ui.Text;
import net.luxvacuos.lightengine.client.ui.TextArea;
import net.luxvacuos.lightengine.universal.util.registry.KeyCache;

public class NotificationsArea extends ComponentWindow {

	public NotificationsArea() {
		super((int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/width")) - 200,
				(int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/height")), 200,
				(int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/height")) - (int) REGISTRY
						.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/shellHeight")),
				"");
	}

	@Override
	public void initApp() {
		super.setDecorations(false);
		super.setLayout(new FlowLayout(Direction.DOWN, 5, 0));
		super.setAsBackground(true);
		super.setBlurBehind(false);
		super.setHidden(true);
		super.initApp();
	}

	private void remove(Container cont) {
		super.removeComponent(cont);
	}

	private void addNotification(Notification not) {
		Container notification = new Container(0, 0, 0, 150);
		notification.setResizeH(true);
		notification.setWindowAlignment(Alignment.LEFT_TOP);
		notification.setAlignment(Alignment.RIGHT_BOTTOM);

		super.addComponent(notification);

		GraphicalSubsystem.getWindowManager()
				.addWindow(new ComponentWindow((int) x + 300, (int) (y + super.getFinalH()), 300, 150, "") {

					private float time;
					private boolean fadeIn = true;
					private boolean fadeOut;
					private float timer;

					@Override
					public void initApp() {
						super.setDecorations(false);
						super.setBackgroundColor("#1F1F1F78");
						super.setAlwaysOnTop(true);
						Text title = new Text(not.getTitle(), 5, 0);
						title.setAlign(NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
						title.setFontSize(32);
						title.setWindowAlignment(Alignment.LEFT_TOP);
						super.addComponent(title);
						TextArea message = new TextArea(not.getMessage(), 5, -30, 300);
						message.setAlign(NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
						message.setWindowAlignment(Alignment.LEFT_TOP);
						message.setFontSize(22);
						super.addComponent(message);
						super.initApp();
					}

					@Override
					public void disposeApp() {
						remove(notification);
						super.disposeApp();
					}

					@Override
					public void updateApp(float delta) {
						if (super.insideWindow() && window.getMouseHandler().isButtonPressed(0)) {
							window.getMouseHandler().ignoreKeyUntilRelease(0);
							time = 6;
							fadeIn = false;
							timer = timer * -1 + 1;
						}
						super.updateApp(delta);
					}

					@Override
					public void alwaysUpdateApp(float delta) {
						if (fadeIn) {
							timer += delta;
							int width = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/width"));
							int xt = width - 300;
							x = (int) Interpolation.exp5Out.apply(width, xt, timer);
							updateRenderSize();
							if (x <= xt) {
								x = xt;
								updateRenderSize();
								fadeIn = false;
								timer = 0;
							}
						}
						if (fadeOut) {
							timer += delta;
							int width = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/width"));
							int xt = width - 300;
							x = (int) Interpolation.exp5In.apply(xt, width, timer);
							updateRenderSize();
							if (x >= width) {
								fadeOut = false;
								super.closeWindow();
							}
						}
						time += delta;
						if (time > 5)
							fadeOut = true;
						super.alwaysUpdateApp(delta);
					}

					@Override
					public void processWindowMessage(int message, Object param) {
						switch (message) {
						case WindowMessage.WM_RESIZE:
							x = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/width")) - 295;
							break;
						}
						super.processWindowMessage(message, param);
					}
				});
	}

	@Override
	public void alwaysUpdateApp(float delta) {
		KeyboardHandler kb = window.getKeyboardHandler();
		if (kb.isKeyPressed(GLFW.GLFW_KEY_N)) {
			kb.ignoreKeyUntilRelease(GLFW.GLFW_KEY_N);
		}
		super.alwaysUpdateApp(delta);
	}

	@Override
	public void processWindowMessage(int message, Object param) {
		switch (message) {
		case WindowMessage.WM_SHELL_NOTIFICATION_ADD:
			addNotification((Notification) param);
			break;
		case WindowMessage.WM_RESIZE:
			x = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/width")) - 200;
			y = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/height"));
			h = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/height")) - (int) REGISTRY
					.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/shellHeight"));
			w = 200;
			break;
		}
		super.processWindowMessage(message, param);
	}

}
