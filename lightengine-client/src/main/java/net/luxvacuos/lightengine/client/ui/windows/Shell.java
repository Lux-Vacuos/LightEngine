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

package net.luxvacuos.lightengine.client.ui.windows;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.rendering.nanovg.IShell;
import net.luxvacuos.lightengine.client.rendering.nanovg.IWindow;
import net.luxvacuos.lightengine.client.rendering.nanovg.WindowMessage;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;
import net.luxvacuos.lightengine.client.ui.Container;
import net.luxvacuos.lightengine.client.ui.Direction;
import net.luxvacuos.lightengine.client.ui.FlowLayout;
import net.luxvacuos.lightengine.client.ui.WindowButton;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem;
import net.luxvacuos.lightengine.universal.util.registry.KeyCache;

public class Shell extends ComponentWindow implements IShell {

	private Map<Integer, WindowButton> buttons;
	private Container apps;
	private boolean enabled = true;
	private boolean fadeIn, fadeOut;
	private IWindow notificationsWindow;

	public Shell(int x, int y, int w, int h) {
		super(x, y, w, h, "Shell");
		CoreSubsystem.REGISTRY.register(KeyCache.getKey("/Light Engine/Settings/WindowManager/shellHeight"), y);
	}

	@Override
	public void initApp() {
		buttons = new HashMap<>();
		super.setDecorations(false);
		super.setBackgroundColor("#1F1F1F78");
		super.setLayout(new FlowLayout(Direction.RIGHT, 0, 0));
		super.setAsBackground(true);
		Container left = new Container(0, 0, 82, h);
		WindowButton btn = new WindowButton(0, 0, 80, h, "Start");
		// btn.setColor("#00000000");
		// btn.setHighlightColor("#FFFFFF64");
		// btn.setTextColor("#FFFFFFFF");
		btn.setOnButtonPress(() -> {
		});
		left.addComponent(btn);
		// super.addComponent(left);
		apps = new Container(0, 0, super.w - 100, h);
		apps.setLayout(new FlowLayout(Direction.RIGHT, 0, 0));
		super.addComponent(apps);
		super.initApp();
		notificationsWindow = new NotificationsArea();
		GraphicalSubsystem.getWindowManager().addWindow(notificationsWindow);
	}

	@Override
	public void alwaysUpdateApp(float delta) {
		if (fadeIn) {
			y += 100f * delta;
			if (y >= h) {
				y = h;
				fadeIn = false;
				CoreSubsystem.REGISTRY.register(KeyCache.getKey("/Light Engine/Settings/WindowManager/shellHeight"), y);
			}
		}
		if (fadeOut) {
			y -= 100f * delta;
			if (y <= 0) {
				super.setHidden(!enabled);
				fadeOut = false;
				y = 0;
				CoreSubsystem.REGISTRY.register(KeyCache.getKey("/Light Engine/Settings/WindowManager/shellHeight"), 0);
			}
		}
		KeyboardHandler kb = window.getKeyboardHandler();
		if (kb.isShiftPressed() && kb.isKeyPressed(GLFW.GLFW_KEY_F10)) {
			kb.ignoreKeyUntilRelease(GLFW.GLFW_KEY_F10);
			GraphicalSubsystem.getWindowManager().addWindow(new Console(100, 600, 600, 400));
		}
		super.alwaysUpdateApp(delta);
	}

	@Override
	public void disposeApp() {
		super.disposeApp();
		buttons.clear();
		CoreSubsystem.REGISTRY.register(KeyCache.getKey("/Light Engine/Settings/WindowManager/shellHeight"), 0);
	}

	@Override
	public void processWindowMessage(int message, Object param) {
		switch (message) {
		case WindowMessage.WM_SHELL_WINDOW_CLOSED:
			IWindow window = (IWindow) param;
			if (!(window.hasDecorations() && !window.isHidden()) || !buttons.containsKey(window.hashCode()))
				return;
			apps.removeComponent(buttons.get(window.hashCode()));
			buttons.remove(window.hashCode());
			break;
		case WindowMessage.WM_SHELL_WINDOW_CREATED:
			window = (IWindow) param;
			if (!(window.hasDecorations() && !window.isHidden()))
				return;
			WindowButton btn = new WindowButton(0, 0, 100, h, window.getTitle());
			btn.setOnButtonPress(() -> {
				if (!GraphicalSubsystem.getWindowManager().isOnTop(window) && !window.isMinimized()) {
					GraphicalSubsystem.getWindowManager().bringToFront(window);
					return;
				}
				if (window.isMinimized())
					GraphicalSubsystem.getWindowManager().bringToFront(window);
				window.toggleMinimize();
			});
			apps.addComponent(btn);
			buttons.put(window.hashCode(), btn);
			break;
		case WindowMessage.WM_SHELL_WINDOW_FOCUS:
			window = (IWindow) param;
			if (!(window.hasDecorations() && !window.isHidden()) || !buttons.containsKey(window.hashCode()))
				return;
			for (WindowButton btn1 : buttons.values()) {
				btn1.setActive(false);
			}
			buttons.get(window.hashCode()).setActive(true);
			break;
		case WindowMessage.WM_RESIZE:
			w = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/width"));
			break;
		}
		super.processWindowMessage(message, param);
	}

	@Override
	public void toggleShell() {
		enabled = !enabled;
		if (enabled) {
			TaskManager.addTask(() -> GraphicalSubsystem.getWindowManager().bringToFront(this));
			fadeIn = true;
			fadeOut = false;
			super.setHidden(!enabled);
		} else {
			fadeIn = false;
			fadeOut = true;
		}
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public IWindow getNotificationsWindow() {
		return notificationsWindow;
	}

}
