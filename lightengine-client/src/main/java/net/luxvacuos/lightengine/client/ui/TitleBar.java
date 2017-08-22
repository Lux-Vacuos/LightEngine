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

package net.luxvacuos.lightengine.client.ui;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import java.util.ArrayList;
import java.util.List;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.Event;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.IWindow;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class TitleBar implements ITitleBar {

	private boolean enabled = true, dragging, pressed, context;
	private Event drag;
	private IWindow window;
	private RootComponent left, right, center;
	private float time;
	private boolean count;

	public TitleBar(IWindow window) {
		this.window = window;
		left = new RootComponent(this.window.getX(), this.window.getY(), this.window.getWidth(),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight")));
		right = new RootComponent(this.window.getX(), this.window.getY(), this.window.getWidth(),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight")));
		center = new RootComponent(this.window.getX(), this.window.getY(), this.window.getWidth(),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight")));
	}

	public void init(Window window) {
		left.init(window);
		right.init(window);
		center.init(window);
	}

	@Override
	public void render() {
		if (enabled) {
			left.render();
			right.render();
			center.render();
		}
	}

	@Override
	public void update(float delta, Window window) {
		if (enabled) {
			MouseHandler mh = window.getMouseHandler();
			if ((mh.isButtonPressed(0) && canDrag(this.window, mh)) || dragging) {
				dragging = mh.isButtonPressed(0);
				drag.event(window);
			}
			if ((mh.isButtonPressed(1) && canDrag(this.window, mh)) && !context) {

				ContextMenu context = new ContextMenu(180, 75);
				List<Component> buttons = new ArrayList<>();
				ContextMenuButton btnRes = new ContextMenuButton(0, 0, 180, 25, "Restore");
				ContextMenuButton btnMax = new ContextMenuButton(0, 0, 180, 25, "Maximize");
				ContextMenuButton btnMin = new ContextMenuButton(0, 0, 180, 25, "Minimize");
				ContextMenuButton btnClo = new ContextMenuButton(0, 0, 180, 25, "Close");

				btnRes.setOnButtonPress(() -> {
					this.window.toggleMaximize();
					context.closeWindow();
				});
				btnMax.setOnButtonPress(() -> {
					this.window.toggleMaximize();
					context.closeWindow();
				});
				btnMin.setOnButtonPress(() -> {
					this.window.toggleMinimize();
					context.closeWindow();
				});
				btnClo.setOnButtonPress(() -> {
					this.window.closeWindow();
					context.closeWindow();
				});

				btnRes.setWindowAlignment(Alignment.LEFT_TOP);
				btnRes.setAlignment(Alignment.RIGHT_BOTTOM);
				btnMin.setWindowAlignment(Alignment.LEFT_TOP);
				btnMin.setAlignment(Alignment.RIGHT_BOTTOM);
				btnMax.setWindowAlignment(Alignment.LEFT_TOP);
				btnMax.setAlignment(Alignment.RIGHT_BOTTOM);
				btnClo.setWindowAlignment(Alignment.LEFT_TOP);
				btnClo.setAlignment(Alignment.RIGHT_BOTTOM);
				if (this.window.isMaximized())
					buttons.add(btnRes);
				else
					buttons.add(btnMax);
				buttons.add(btnMin);
				buttons.add(btnClo);
				context.setButtons(buttons);
				GraphicalSubsystem.getWindowManager().addWindow(context);
			}
			context = mh.isButtonPressed(1);
			if (mh.isButtonPressed(0) && canDrag(this.window, mh) || pressed) {
				if (!pressed) {
					count = true;
					if (time != 0) {
						this.window.toggleMaximize();
						time = 0;
						count = false;
					}
				}
				pressed = mh.isButtonPressed(0);
			}
			if (mh.isButtonPressed(0) && canDrag(this.window, mh)) {
				if (this.window.isMaximized()) {
					if (mh.getDX() < -2 || mh.getDX() > 2 || mh.getDY() < -2 || mh.getDY() > 2) {
						this.window.toggleMaximize();
					}
				}
			}
			if (count) {
				time += 1 * delta;
				if (time > 0.5f) {
					count = false;
					time = 0;
				}
			}
			if (!dragging) {
				left.update(delta);
				right.update(delta);
				center.update(delta);
			}
		}
	}

	@Override
	public void alwaysUpdate(float delta, Window window) {
		if (enabled) {
			int titleBarHeight = (int) REGISTRY
					.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"));
			left.alwaysUpdate(delta, this.window.getX(), this.window.getY() + titleBarHeight, this.window.getWidth(),
					titleBarHeight);
			right.alwaysUpdate(delta, this.window.getX(), this.window.getY() + titleBarHeight, this.window.getWidth(),
					titleBarHeight);
			center.alwaysUpdate(delta, this.window.getX(), this.window.getY() + titleBarHeight, this.window.getWidth(),
					titleBarHeight);
		}
	}

	@Override
	public void setOnDrag(Event event) {
		this.drag = event;
	}

	@Override
	public RootComponent getLeft() {
		return left;
	}

	@Override
	public RootComponent getRight() {
		return right;
	}

	@Override
	public RootComponent getCenter() {
		return center;
	}

	private boolean canDrag(IWindow iWindow, MouseHandler mh) {
		return mh.getX() > iWindow.getX() + left.getFinalW() && mh.getY() < iWindow.getY()
				+ (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"))
				&& mh.getX() < iWindow.getX() + iWindow.getWidth() + right.getFinalW() && mh.getY() > iWindow.getY();
	}

	@Override
	public void dispose() {
		left.dispose();
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean isDragging() {
		return dragging;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
