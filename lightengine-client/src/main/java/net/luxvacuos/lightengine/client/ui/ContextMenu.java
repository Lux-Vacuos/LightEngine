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

import java.util.List;

import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;

public class ContextMenu extends ComponentWindow {

	private List<Component> buttons;

	public ContextMenu(float w, float h) {
		super(0, 0, w, h, "Panel");
	}

	@Override
	public void initApp(Window window) {
		super.x = window.getMouseHandler().getX() - 5;
		super.y = window.getMouseHandler().getY() + 5;
		super.toggleTitleBar();
		super.setDecorations(false);
		super.setBackgroundColor(0f, 0f, 0f, 0f);
		super.setLayout(new FlowLayout(Direction.DOWN, 0, 0));
		super.addAllComponents(buttons);
		super.initApp(window);
	}

	@Override
	public void alwaysUpdateApp(float delta, Window window) {
		if ((window.getMouseHandler().isButtonPressed(0) || window.getMouseHandler().isButtonPressed(1)) && !insideWindow())
			super.closeWindow();
		super.alwaysUpdateApp(delta, window);
	}
	
	@Override
	public void disposeApp(Window window) {
		super.disposeApp(window);
		buttons.clear();
	}
	
	public void setButtons(List<Component> buttons) {
		this.buttons = buttons;
	}

}
