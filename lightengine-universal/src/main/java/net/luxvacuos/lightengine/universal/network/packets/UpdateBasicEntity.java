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

package net.luxvacuos.lightengine.universal.network.packets;

import java.io.Serializable;
import java.util.UUID;

import org.joml.Vector3f;

public class UpdateBasicEntity implements Serializable {

	private static final long serialVersionUID = 7936939795436298971L;

	private UUID uuid;
	private Vector3f position, rotation, velocity;
	private float scale;

	public UpdateBasicEntity(UUID uuid, Vector3f position, Vector3f rotation, Vector3f velocity, float scale) {
		this.uuid = uuid;
		this.position = position;
		this.rotation = rotation;
		this.velocity = velocity;
		this.scale = scale;
	}

	public UUID getUUID() {
		return uuid;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public float getScale() {
		return scale;
	}

}
