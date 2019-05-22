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

package net.luxvacuos.lightengine.universal.ecs.components;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags;
import com.badlogic.gdx.physics.bullet.collision.btConvexShape;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;
import com.hackhalo2.nbt.exceptions.NBTException;
import com.hackhalo2.nbt.tags.TagCompound;

public class Player implements LEComponent {
	
	public btKinematicCharacterController character;
	public btPairCachingGhostObject ghostObject;
	
	public float characterScale = 1f;
	
	public Player(btTransform initialTransform) {
		ghostObject = new btPairCachingGhostObject();
		ghostObject.setWorldTransform(new Matrix4().trn(initialTransform.getOrigin()));
		float characterHeight = 1.75f * characterScale;
		float characterWidth = 0.4f * characterScale;
		btConvexShape capsule = new btCapsuleShape(characterWidth, characterHeight);
		ghostObject.setCollisionShape(capsule);
		ghostObject.setCollisionFlags(CollisionFlags.CF_CHARACTER_OBJECT);

		float stepHeight = 0.15f * characterScale;
		character = new btKinematicCharacterController(ghostObject, capsule, stepHeight);
		character.setJumpSpeed(8.5f);
	}

	@Override
	public void load(TagCompound compound) throws NBTException {
	}

	@Override
	public TagCompound save() {
		return null;
	}

}
