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

package net.luxvacuos.lightengine.universal.network.packets;

import java.io.Serializable;
import java.util.UUID;

import net.luxvacuos.igl.vector.Vector3d;

public class UpdateBasicEntity implements Serializable {

	private static final long serialVersionUID = 7936939795436298971L;

	private UUID uuid;
	private Vector3d position, rotation, velocity;
	private float scale;

	public UpdateBasicEntity(UUID uuid, Vector3d position, Vector3d rotation, Vector3d velocity, float scale) {
		this.uuid = uuid;
		this.position = position;
		this.rotation = rotation;
		this.velocity = velocity;
		this.scale = scale;
	}

	public UUID getUUID() {
		return uuid;
	}

	public Vector3d getPosition() {
		return position;
	}

	public Vector3d getRotation() {
		return rotation;
	}

	public Vector3d getVelocity() {
		return velocity;
	}

	public float getScale() {
		return scale;
	}

}
