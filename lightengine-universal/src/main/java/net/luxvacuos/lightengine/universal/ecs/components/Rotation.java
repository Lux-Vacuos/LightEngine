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

package net.luxvacuos.lightengine.universal.ecs.components;

import org.joml.Vector3f;

import com.hackhalo2.nbt.CompoundBuilder;
import com.hackhalo2.nbt.exceptions.NBTException;
import com.hackhalo2.nbt.tags.TagCompound;

public class Rotation implements LEComponent {

	private float x, y, z;

	public Rotation() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public Rotation(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Rotation(Vector3f vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}

	public Vector3f getRotation() {
		return new Vector3f(this.x, this.y, this.z);
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getZ() {
		return this.z;
	}

	public Rotation setX(float x) {
		this.x = x;

		return this;
	}

	public Rotation setY(float y) {
		this.y = y;

		return this;
	}

	public Rotation setZ(float z) {
		this.z = z;

		return this;
	}

	public Rotation set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;

		return this;
	}

	public Rotation set(Vector3f vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;

		return this;
	}

	@Override
	public void load(TagCompound compound) throws NBTException {
		this.x = compound.getFloat("RotX");
		this.y = compound.getFloat("RotY");
		this.z = compound.getFloat("RotZ");

	}

	@Override
	public TagCompound save() {
		CompoundBuilder builder = new CompoundBuilder().start("RotationComponent");

		builder.addDouble("RotX", this.x).addDouble("RotY", this.y).addDouble("RotZ", this.z);

		return builder.build();
	}

}
