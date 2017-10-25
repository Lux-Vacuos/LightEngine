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

public class Position implements LEComponent {

	private float x, y, z;

	public Position() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public Position(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Position(Vector3f vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}

	public Vector3f getPosition() {
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

	public Position setX(float x) {
		this.x = x;

		return this;
	}

	public Position setY(float y) {
		this.y = y;

		return this;
	}

	public Position setZ(float z) {
		this.z = z;

		return this;
	}

	public Position set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;

		return this;
	}

	public Position set(Vector3f vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;

		return this;
	}

	@Override
	public String toString() {
		return "[x:" + this.x + "]" + "[y:" + this.y + "]" + "[z:" + this.z + "]";
	}

	@Override
	public void load(TagCompound compound) throws NBTException {
		this.x = compound.getFloat("PosX");
		this.y = compound.getFloat("PosY");
		this.z = compound.getFloat("PosZ");

	}

	@Override
	public TagCompound save() {
		CompoundBuilder builder = new CompoundBuilder().start("PositionComponent");

		builder.addDouble("PosX", this.x).addDouble("PosY", this.y).addDouble("PosZ", this.z);

		return builder.build();
	}
}
