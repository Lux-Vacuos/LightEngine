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
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;

import net.luxvacuos.igl.vector.Matrix4d;
import net.luxvacuos.igl.vector.Vector3d;
import net.luxvacuos.igl.vector.Vector3f;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Light;

public class UniformLight extends UniformArray {

	private static DoubleBuffer matrixBuffer = BufferUtils.createDoubleBuffer(16);

	public UniformLight(String name) {
		super(name + ".position", name + ".color", name + ".direction", name + ".radius", name + ".inRadius",
				name + ".type", name + ".shadowEnabled", name + ".shadowMap", name + ".shadowViewMatrix",
				name + ".shadowProjectionMatrix");

	}

	public void loadLight(Light light, int offset, int number) {
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
		glUniform1i(super.getLocation()[6], light.isShadow() ? 1 : 0);
		if (light.isShadow()) {
			glUniform1i(super.getLocation()[7], offset + number);
			loadMatrix(light.getCamera().getViewMatrix(), super.getLocation()[8]);
			loadMatrix(light.getCamera().getProjectionMatrix(), super.getLocation()[9]);
		}
	}

	public void loadMatrix(Matrix4d matrix, int loc) {
		matrixBuffer.clear();
		matrix.store(matrixBuffer);
		matrixBuffer.flip();

		double[] dm = new double[16];
		matrixBuffer.get(dm);
		float[] fm = new float[16];
		fm[0] = (float) dm[0];
		fm[1] = (float) dm[1];
		fm[2] = (float) dm[2];
		fm[3] = (float) dm[3];
		fm[4] = (float) dm[4];
		fm[5] = (float) dm[5];
		fm[6] = (float) dm[6];
		fm[7] = (float) dm[7];
		fm[8] = (float) dm[8];
		fm[9] = (float) dm[9];
		fm[10] = (float) dm[10];
		fm[11] = (float) dm[11];
		fm[12] = (float) dm[12];
		fm[13] = (float) dm[13];
		fm[14] = (float) dm[14];
		fm[15] = (float) dm[15];
		glUniformMatrix4fv(loc, false, fm);
	}

}
