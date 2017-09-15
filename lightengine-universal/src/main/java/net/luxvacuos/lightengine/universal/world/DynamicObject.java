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

package net.luxvacuos.lightengine.universal.world;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class DynamicObject {

	private RigidBody body;

	public DynamicObject(CollisionShape collisionShape, Transform transform, float mass) {
		boolean isDynamic = (mass != 0f);
		Vector3f localInertia = new Vector3f(0, 0, 0);
		if (isDynamic)
			collisionShape.calculateLocalInertia(mass, localInertia);
		DefaultMotionState myMotionState = new DefaultMotionState(transform);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, collisionShape,
				localInertia);
		body = new RigidBody(rbInfo);
	}

	public RigidBody getBody() {
		return body;
	}

}
