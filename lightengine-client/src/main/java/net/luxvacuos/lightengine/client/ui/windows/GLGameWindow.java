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

import net.luxvacuos.lightengine.client.rendering.nanovg.NanoWindow;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme;

public class GLGameWindow extends NanoWindow {

	private int img;

	public GLGameWindow() {
		super("GLGameWindow");
	}

	@Override
	public void initApp() {
		super.setAsBackground(true);
		super.setBlurBehind(false);
	}

	@Override
	public void renderApp() {
		Theme.renderImage(window.getNVGID(), x, window.getHeight() - y, w, h, img, 1);
	}

	@Override
	public void updateApp(float delta) {
	}

	@Override
	public void alwaysUpdateApp(float delta) {
	}

	@Override
	public void disposeApp() {
	}

	public void setImageID(int img) {
		this.img = img;
	}

}
