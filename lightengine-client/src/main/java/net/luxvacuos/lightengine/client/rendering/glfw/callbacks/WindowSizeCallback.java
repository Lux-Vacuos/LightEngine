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

package net.luxvacuos.lightengine.client.rendering.glfw.callbacks;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFWWindowSizeCallback;

public class WindowSizeCallback extends GLFWWindowSizeCallback {

	private List<IWindowSizeCallback> callbacks = new ArrayList<>();

	@Override
	public void invoke(long window, int width, int height) {
		for (IWindowSizeCallback callback : callbacks)
			callback.windowSize(window, width, height);
	}

	public void addCallback(IWindowSizeCallback callback) {
		callbacks.add(callback);
	}

	public void removeCallback(IWindowSizeCallback callback) {
		callbacks.remove(callback);
	}

}
