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

package net.luxvacuos.lightengine.universal.ecs.entities;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.linearmath.Transform;

import net.luxvacuos.lightengine.universal.ecs.components.Collision;
import net.luxvacuos.lightengine.universal.ecs.components.Player;
import net.luxvacuos.lightengine.universal.world.DynamicObject;

public class PlayerEntity extends BasicEntity {

	public PlayerEntity(String name) {
		super(name);
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(new Vector3f(0, 45, 5));
		this.add(new Player(transform));
		BoxShape shape = new BoxShape(new Vector3f(0.25f, 1.7f, 0.25f));
		this.add(new Collision(new DynamicObject(shape, transform, 1)));
	}

	public PlayerEntity(String name, String uuid) {
		super(name, uuid);
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(new Vector3f(0, 45, 5));
		this.add(new Player(transform));
		BoxShape shape = new BoxShape(new Vector3f(0.25f, 1.7f, 0.25f));
		this.add(new Collision(new DynamicObject(shape, transform, 1)));
	}

}
