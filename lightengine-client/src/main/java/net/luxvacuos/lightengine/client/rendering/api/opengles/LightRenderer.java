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

package net.luxvacuos.lightengine.client.rendering.api.opengles;

import java.util.ArrayList;
import java.util.List;

import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.Light;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public class LightRenderer implements IDisposable {

	private List<Light> lights;

	public LightRenderer() {
		lights = new ArrayList<>();
	}

	public void addLight(Light light) {
		light.init();
		lights.add(light);
	}

	public void addAllLights(List<Light> lights) {
		TaskManager.addTask(() -> {
			for (Light light : lights)
				light.init();
			this.lights.addAll(lights);
		});
	}

	public void removeLight(Light light) {
		lights.remove(light);
		if (light.isShadow())
			TaskManager.addTask(() -> light.getShadowMap().dispose());
	}

	public void removeAllLights(List<Light> lights) {
		this.lights.removeAll(lights);
		for (Light light : lights)
			if (light.isShadow())
				TaskManager.addTask(() -> light.getShadowMap().dispose());
	}

	public void update(float delta) {
		for (Light light : lights) {
			light.update(delta);
		}
	}

	@Override
	public void dispose() {
		for (Light light : lights) {
			if (light.isShadow())
				light.getShadowMap().dispose();
		}
		lights.clear();
	}

	List<Light> getLights() {
		return lights;
	}

}
