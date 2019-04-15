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

package net.luxvacuos.lightengine.client.rendering.opengl;

import java.util.ArrayList;
import java.util.List;

import net.luxvacuos.lightengine.client.rendering.opengl.v2.lights.Light;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

/**
 * <b>INTERNAL USE ONLY</b>
 */
public class LightRenderer implements IDisposable {

	private List<Light> lights;

	public LightRenderer() {
		lights = new ArrayList<>();
	}

	/**
	 * <b>INTERNAL USE ONLY</b>
	 */
	public void addLight(Light light) {
		lights.add(light);
	}

	/**
	 * <b>INTERNAL USE ONLY</b>
	 */
	public void addAllLights(List<Light> lights) {
		this.lights.addAll(lights);
	}

	/**
	 * INTERNAL USE ONLY
	 */
	public void removeLight(Light light) {
		lights.remove(light);
	}

	/**
	 * <b>INTERNAL USE ONLY</b>
	 */
	public void removeAllLights(List<Light> lights) {
		this.lights.removeAll(lights);
	}

	/**
	 * <b>INTERNAL USE ONLY</b>
	 */
	@Override
	public void dispose() {
		lights.clear();
	}

	List<Light> getLights() {
		return lights;
	}

}
