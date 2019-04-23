/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2019 Lux Vacuos
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

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;

public class DynamicObject {

	private btRigidBody body;

	public DynamicObject(btCollisionShape collisionShape, Matrix4 transform, float mass) {
		boolean isDynamic = (mass != 0f);
		Vector3 localInertia = new Vector3(0, 0, 0);
		if (isDynamic)
			collisionShape.calculateLocalInertia(mass, localInertia);
		btDefaultMotionState myMotionState = new btDefaultMotionState(transform);
		btRigidBodyConstructionInfo rbInfo = new btRigidBodyConstructionInfo(mass, myMotionState, collisionShape,
				localInertia);
		body = new btRigidBody(rbInfo);
	}

	public btRigidBody getBody() {
		return body;
	}

}
