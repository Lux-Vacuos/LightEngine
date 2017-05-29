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

import net.luxvacuos.igl.vector.Matrix4d;
import net.luxvacuos.igl.vector.Vector3d;
import net.luxvacuos.lightengine.client.ecs.ClientComponents;
import net.luxvacuos.lightengine.client.rendering.api.opengl.Renderer;
import net.luxvacuos.lightengine.client.util.Maths;

public class SpotlightCamera extends CameraEntity {
	
	private Vector3d direction = new Vector3d();

	public SpotlightCamera(float radius, int width, int height) {
		super("spotlight");
		setProjectionMatrix(Renderer.createProjectionMatrix(width, height, radius, 0.1f, 100f));
		setViewMatrix(Maths.createViewMatrix(this));
	}

	@Override
	public void update(float delta) {
		ClientComponents.VIEW_MATRIX.get(this).setViewMatrix(Maths.createViewMatrix(this));
		Matrix4d proj = getProjectionMatrix();
		Vector3d v = new Vector3d();
		v.x = (((2.0f * 8) / 16) - 1) / proj.m00;
		v.y = -(((2.0f * 8) / 16) - 1) / proj.m11;
		v.z = 1.0f;
		Matrix4d invertView = Matrix4d.invert(getViewMatrix(), null);
		direction.x = v.x * invertView.m00 + v.y * invertView.m10 + v.z * invertView.m20;
		direction.y = v.x * invertView.m01 + v.y * invertView.m11 + v.z * invertView.m21;
		direction.z = v.x * invertView.m02 + v.y * invertView.m12 + v.z * invertView.m22;
	}
	
	public Vector3d getDirection() {
		return direction;
	}

}
