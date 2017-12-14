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

package net.luxvacuos.lightengine.client.ecs.entities;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import net.luxvacuos.lightengine.client.resources.CastRay;
import net.luxvacuos.lightengine.client.util.Maths;

public class SunCamera extends CameraEntity {

	private Vector2f center;

	private Matrix4f[] projectionArray;

	public SunCamera(Matrix4f[] projectionArray) {
		super("sun");
		this.projectionArray = projectionArray;
		center = new Vector2f(1024, 1024);
		castRay = new CastRay(this.getProjectionMatrix(), Maths.createViewMatrix(this), center, 2048, 2048);
		setProjectionMatrix(projectionArray[0]);
		setViewMatrix(Maths.createViewMatrix(this));
	}

	public void updateShadowRay(boolean inverted) {
		setViewMatrix(Maths.createViewMatrix(this));
		if (inverted)
			castRay.update(this.getProjectionMatrix(),
					Maths.createViewMatrixPos(position,
							Maths.createViewMatrixRot(rotation.x() + 180, rotation.y(), rotation.z(), null)),
					center, 2048, 2048);
		else
			castRay.update(this.getProjectionMatrix(), getViewMatrix(), center, 2048, 2048);
	}

	public void switchProjectionMatrix(int id) {
		setProjectionMatrix(this.projectionArray[id]);
	}
	
	public void setProjectionArray(Matrix4f[] projectionArray) {
		this.projectionArray = projectionArray;
	}

	public Matrix4f[] getProjectionArray() {
		return projectionArray;
	}

	public CastRay getDRay() {
		return castRay;
	}

}
