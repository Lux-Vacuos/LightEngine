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

package net.luxvacuos.lightengine.universal.util;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import net.luxvacuos.igl.vector.Quaternion;

public final class VectoVec {

	private VectoVec() {
	}

	public static Vector3f toVec3(net.luxvacuos.igl.vector.Vector3f vec) {
		return new Vector3f(vec.getX(), vec.getY(), vec.getZ());
	}

	public static Vector3f toVec3(net.luxvacuos.igl.vector.Vector3d vec) {
		return new Vector3f((float) vec.getX(), (float) vec.getY(), (float) vec.getZ());
	}

	public static net.luxvacuos.igl.vector.Vector3d toVec3(Vector3f vec) {
		return new net.luxvacuos.igl.vector.Vector3d(vec.x, vec.y, vec.z);
	}

	public static Quat4f toQuat4(Quaternion q) {
		return new Quat4f(q.x, q.y, q.z, q.w);
	}

}
