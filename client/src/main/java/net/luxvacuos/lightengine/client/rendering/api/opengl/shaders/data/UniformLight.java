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

package net.luxvacuos.lightengine.client.rendering.api.opengl.shaders.data;

import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3f;

import net.luxvacuos.igl.vector.Vector3d;
import net.luxvacuos.igl.vector.Vector3f;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Light;

public class UniformLight extends UniformArray {

	public UniformLight(String name) {
		super(name + ".position", name + ".color", name + ".direction", name + ".radius", name + ".inRadius",
				name + ".type");
	}

	public void loadLight(Light light) {
		Vector3d pos = light.getPosition();
		Vector3f color = light.getColor();
		Vector3d dir = light.getDirection();
		glUniform3f(super.getLocation()[0], (float) pos.getX(), (float) pos.getY(), (float) pos.getZ());
		glUniform3f(super.getLocation()[1], color.getX(), color.getY(), color.getZ());
		if (light.getType() == 1) {
			glUniform3f(super.getLocation()[2], (float) dir.getX(), (float) dir.getY(), (float) dir.getZ());
			glUniform1f(super.getLocation()[3], (float) Math.cos(Math.toRadians(light.getRadius())));
			glUniform1f(super.getLocation()[4], (float) Math.cos(Math.toRadians(light.getInRadius())));
		}
		glUniform1i(super.getLocation()[5], light.getType());
	}
}
