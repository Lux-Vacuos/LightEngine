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

import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.opengl.Renderer;
import net.luxvacuos.lightengine.client.ui.Image;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;

public class GameWindow extends ComponentWindow {

	public GameWindow(float x, float y, float w, float h) {
		super(x, y, w, h, "Light Engine");
	}

	@Override
	public void initApp(Window window) {
		super.setBackgroundColor(0.0f, 0.0f, 0.0f, 0.5f);
		super.setResizable(false);
		super.setDecorations(false);
		super.setAsBackground(true);
		super.setBlurBehind(false);
		super.toggleTitleBar();

		Image game = new Image(0, 0, w, h, Renderer.getResultTexture(), false);

		super.addComponent(game);

		super.initApp(window);
	}

}
