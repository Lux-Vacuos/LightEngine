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

package net.luxvacuos.lightengine.client.ecs.entities;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.util.Maths;

public class CubeMapCamera extends CameraEntity {

	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000f;
	private static final float FOV = 90;
	private static final float ASPECT_RATIO = 1f;

	public CubeMapCamera(Vector3f position) {
		super("cubeCam");
		this.localPosition = position;
		createProjectionMatrix();
	}

	public void switchToFace(int faceIndex) {
		switch (faceIndex) {
		case 0:
			localRotation.x = 0;
			localRotation.y = -90;
			break;
		case 1:
			localRotation.x = 0;
			localRotation.y = 90;
			break;
		case 2:
			localRotation.x = 90;
			localRotation.y = 180;
			break;
		case 3:
			localRotation.x = -90;
			localRotation.y = 180;
			break;
		case 4:
			localRotation.x = 0;
			localRotation.y = 180;
			break;
		case 5:
			localRotation.x = 0;
			localRotation.y = 0;
			break;
		}
		localRotation.z = 180;
		updateViewMatrix();
	}
	
	@Override
	public Vector3f getRotation() {
		return localRotation;
	}

	private void createProjectionMatrix() {
		float y_scale = (float) (1f / Math.tan(Math.toRadians(FOV / 2f)));
		float x_scale = y_scale / ASPECT_RATIO;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.identity();
		projectionMatrix.m00(x_scale);
		projectionMatrix.m11(y_scale);
		projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		projectionMatrix.m23(-1);
		projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
		projectionMatrix.m33(0);
		setProjectionMatrix(projectionMatrix);
	}

	private void updateViewMatrix() {
		setViewMatrix(Maths.createViewMatrix(this));
	}

}
