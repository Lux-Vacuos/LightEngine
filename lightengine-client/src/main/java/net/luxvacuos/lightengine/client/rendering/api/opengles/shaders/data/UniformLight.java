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

package net.luxvacuos.lightengine.client.rendering.api.opengles.shaders.data;

import static org.lwjgl.opengles.GLES20.glUniform1f;
import static org.lwjgl.opengles.GLES20.glUniform1i;
import static org.lwjgl.opengles.GLES20.glUniform3f;
import static org.lwjgl.opengles.GLES20.glUniformMatrix4fv;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.Light;

public class UniformLight extends UniformArray {

	public UniformLight(String name) {
		super(name + ".position", name + ".color", name + ".direction", name + ".radius", name + ".inRadius",
				name + ".type", name + ".shadowEnabled", name + ".shadowMap", name + ".shadowViewMatrix",
				name + ".shadowProjectionMatrix");

	}

	public void loadLight(Light light, int offset, int number) {
		Vector3f pos = light.getPosition();
		Vector3f color = light.getColor();
		glUniform3f(super.getLocation()[0], (float) pos.x(), (float) pos.y(), (float) pos.z());
		glUniform3f(super.getLocation()[1], color.x(), color.y(), color.z());
		if (light.getType() == 1) {
			Vector3f dir = light.getDirection();
			glUniform3f(super.getLocation()[2], (float) dir.x(), (float) dir.y(), (float) dir.z());
			glUniform1f(super.getLocation()[3], (float) Math.cos(Math.toRadians(light.getRadius())));
			glUniform1f(super.getLocation()[4], (float) Math.cos(Math.toRadians(light.getInRadius())));
		}
		glUniform1i(super.getLocation()[5], light.getType());
		glUniform1i(super.getLocation()[6], light.isShadow() ? 1 : 0);
		if (light.isShadow()) {
			glUniform1i(super.getLocation()[7], offset + number);
			loadMatrix(light.getCamera().getViewMatrix(), super.getLocation()[8]);
			loadMatrix(light.getCamera().getProjectionMatrix(), super.getLocation()[9]);
		}
	}

	public void loadMatrix(Matrix4f matrix, int loc) {
		float[] fm = new float[16];
		matrix.get(fm);
		glUniformMatrix4fv(loc, false, fm);
	}

}
