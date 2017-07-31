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
import net.luxvacuos.lightengine.client.rendering.api.nanovg.NanoWindow;

public class ComponentWindow extends NanoWindow {

	protected RootComponent rootComponent;

	public ComponentWindow(float x, float y, float w, float h, String title) {
		super(x, y, w, h, title);
		rootComponent = new RootComponent(x, y, w, h);
	}

	@Override
	public void initApp(Window window) {
	}

	@Override
	public void renderApp(Window window) {
		rootComponent.render(window);
	}

	@Override
	public void updateApp(float delta, Window window) {
		rootComponent.update(delta, window);
	}

	@Override
	public void alwaysUpdateApp(float delta, Window window) {
		rootComponent.alwaysUpdate(delta, window, x, y, w, h);
	}

	@Override
	public void disposeApp(Window window) {
		rootComponent.dispose();
	}

	public void addComponent(Component component) {
		rootComponent.addComponent(component);
	}

	public void addAllComponents(List<Component> components) {
		rootComponent.addAllComponents(components);
	}

	public void setLayout(ILayout layout) {
		rootComponent.setLayout(layout);
	}

	public void removeComponent(Component component) {
		rootComponent.removeComponent(component);
	}

	public float getFinalW() {
		return rootComponent.getFinalW();
	}

	public float getFinalH() {
		return rootComponent.getFinalH();
	}

}