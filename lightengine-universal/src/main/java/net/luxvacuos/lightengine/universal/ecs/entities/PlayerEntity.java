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

package net.luxvacuos.lightengine.universal.ecs.entities;

import org.joml.Vector3f;

import com.badlogic.gdx.physics.bullet.linearmath.btTransform;

import net.luxvacuos.lightengine.universal.ecs.components.Player;
import net.luxvacuos.lightengine.universal.util.VectoVec;

public class PlayerEntity extends BasicEntity {

	public PlayerEntity(String name, Vector3f pos) {
		super(name);
		btTransform transform = new btTransform();
		transform.setIdentity();
		transform.setOrigin(VectoVec.toVec3(pos));
		this.add(new Player(transform));
	}

	public PlayerEntity(String name, String uuid, Vector3f pos) {
		super(name, uuid);
		btTransform transform = new btTransform();
		transform.setIdentity();
		transform.setOrigin(VectoVec.toVec3(pos));
		this.add(new Player(transform));
	}

	public PlayerEntity(String name) {
		super(name);
		btTransform transform = new btTransform();
		transform.setIdentity();
		transform.setOrigin(VectoVec.toVec3(new Vector3f(0, 0, 0)));
		this.add(new Player(transform));
	}

	public PlayerEntity(String name, String uuid) {
		super(name, uuid);
		btTransform transform = new btTransform();
		transform.setIdentity();
		transform.setOrigin(VectoVec.toVec3(new Vector3f(0, 0, 0)));
		this.add(new Player(transform));
	}

}
