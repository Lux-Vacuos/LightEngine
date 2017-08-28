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

import net.luxvacuos.lightengine.client.rendering.api.nanovg.NanoWindow;

public class ComponentWindow extends NanoWindow {

	protected RootComponent rootComponent;

	public ComponentWindow(int x, int y, int w, int h, String title) {
		super(x, y, w, h, title);
		if (super.compositor)
			rootComponent = new RootComponent(0, 0, w, h);
		else
			rootComponent = new RootComponent(x, y, w, h);
	}

	@Override
	public void initApp() {
		rootComponent.init(super.window);
	}

	@Override
	public void renderApp() {
		if (super.compositor)
			rootComponent.render(fx, super.window.getHeight() - fy, w, h);
		else
			rootComponent.render();
	}

	@Override
	public void updateApp(float delta) {
		rootComponent.update(delta);
	}

	@Override
	public void alwaysUpdateApp(float delta) {
		rootComponent.alwaysUpdate(delta, x, y, w, h);
	}

	@Override
	public void disposeApp() {
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
