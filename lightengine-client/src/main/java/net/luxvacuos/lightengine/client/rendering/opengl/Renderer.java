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

package net.luxvacuos.lightengine.client.rendering.opengl;

import org.joml.Matrix4f;

public class Renderer {

	public static Matrix4f createProjectionMatrix(int width, int height, float fov, float nearPlane, float farPlane) {
		return createProjectionMatrix(new Matrix4f(), width, height, fov, nearPlane, farPlane, false);
	}

	public static Matrix4f createProjectionMatrix(int width, int height, float fov, float nearPlane, float farPlane,
			boolean zZeroToOne) {
		return createProjectionMatrix(new Matrix4f(), width, height, fov, nearPlane, farPlane, zZeroToOne);
	}

	public static Matrix4f createProjectionMatrix(Matrix4f proj, int width, int height, float fov, float nearPlane,
			float farPlane, boolean zZeroToOne) {
		if (zZeroToOne && farPlane > 0 && Float.isInfinite(farPlane)) {
			float y_scale = (float) (1f / Math.tan(Math.toRadians(fov / 2f)));
			float x_scale = y_scale / ((float) width / (float) height);
			proj.identity();
			proj.m00(x_scale);
			proj.m11(y_scale);
			proj.m22(0);
			proj.m23(-1);
			proj.m32(nearPlane);
			proj.m33(0);
		} else {
			proj.setPerspective((float) Math.toRadians(fov), (float) width / (float) height, nearPlane, farPlane,
					zZeroToOne);
		}
		return proj;
	}

}
