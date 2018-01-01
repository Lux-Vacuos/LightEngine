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

package net.luxvacuos.lightengine.client.resources;

import org.joml.Matrix4f;
import org.joml.Rayf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class CastRay {
	private Rayf ray;

	public CastRay(Matrix4f proj, Matrix4f view, Vector2f mouse, int width, int height) {
		Vector3f v = new Vector3f();
		v.x = (((2.0f * mouse.x) / width) - 1) / proj.m00();
		v.y = -(((2.0f * mouse.y) / height) - 1) / proj.m11();
		v.z = 1.0f;

		Matrix4f invertView = view.invert(new Matrix4f());

		Vector3f rayDirection = new Vector3f();
		rayDirection.x = v.x * invertView.m00() + v.y * invertView.m10() + v.z * invertView.m20();
		rayDirection.y = v.x * invertView.m01() + v.y * invertView.m11() + v.z * invertView.m21();
		rayDirection.z = v.x * invertView.m02() + v.y * invertView.m12() + v.z * invertView.m22();
		Vector3f rayOrigin = new Vector3f(invertView.m30(), invertView.m31(), invertView.m32());
		ray = new Rayf(rayOrigin, new Vector3f(-rayDirection.x, -rayDirection.y, -rayDirection.z));
	}

	public void update(Matrix4f proj, Matrix4f view, Vector2f mouse, int width, int height) {
		Vector3f v = new Vector3f();
		v.x = (((2.0f * mouse.x) / width) - 1) / proj.m00();
		v.y = -(((2.0f * mouse.y) / height) - 1) / proj.m11();
		v.z = 1.0f;

		Matrix4f invertView = view.invert(new Matrix4f());

		Vector3f rayDirection = new Vector3f();
		rayDirection.x = v.x * invertView.m00() + v.y * invertView.m10() + v.z * invertView.m20();
		rayDirection.y = v.x * invertView.m01() + v.y * invertView.m11() + v.z * invertView.m21();
		rayDirection.z = v.x * invertView.m02() + v.y * invertView.m12() + v.z * invertView.m22();
		Vector3f rayOrigin = new Vector3f(invertView.m30(), invertView.m31(), invertView.m32());
		ray = new Rayf(rayOrigin, new Vector3f(-rayDirection.x, -rayDirection.y, -rayDirection.z));
	}

	public Rayf getRay() {
		return ray;
	}

}
