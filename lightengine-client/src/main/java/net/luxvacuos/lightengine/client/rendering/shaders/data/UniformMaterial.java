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

package net.luxvacuos.lightengine.client.rendering.shaders.data;

import net.luxvacuos.lightengine.client.rendering.GL;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Material;

public class UniformMaterial extends UniformArray {

	private Material currentValue;
	private boolean used = false;

	public UniformMaterial(String matName) {
		super(matName + ".diffuse", matName + ".emissive", matName + ".roughness", matName + ".metallic",
				matName + ".diffuseTex", matName + ".normalTex", matName + ".roughnessTex", matName + ".metallicTex");
	}

	public void loadMaterial(Material value) {
		if (!used || !currentValue.equals(value)) {
			GL.glUniform4f(super.getLocation()[0], value.getDiffuse().x(), value.getDiffuse().y(),
					value.getDiffuse().z(), value.getDiffuse().w());
			GL.glUniform4f(super.getLocation()[1], value.getEmissive().x(), value.getEmissive().y(),
					value.getEmissive().z(), value.getEmissive().w());
			GL.glUniform1f(super.getLocation()[2], value.getRoughness());
			GL.glUniform1f(super.getLocation()[3], value.getMetallic());
			GL.glUniform1i(super.getLocation()[4], 0);
			GL.glUniform1i(super.getLocation()[5], 1);
			GL.glUniform1i(super.getLocation()[6], 2);
			GL.glUniform1i(super.getLocation()[7], 3);
			used = true;
			currentValue = value;
		}
	}

}
