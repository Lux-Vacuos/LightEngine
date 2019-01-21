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

import org.joml.Vector3f;

import com.hackhalo2.nbt.CompoundBuilder;
import com.hackhalo2.nbt.exceptions.NBTException;
import com.hackhalo2.nbt.tags.TagCompound;

@Deprecated
public class Position implements LEComponent {

	private Vector3f position;

	public Position() {
		position = new Vector3f();
	}

	public Position(float x, float y, float z) {
		position = new Vector3f(x, y, z);
	}

	public Position(Vector3f vec) {
		position = new Vector3f(vec);
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getX() {
		return position.x();
	}

	public float getY() {
		return position.y();
	}

	public float getZ() {
		return position.z();
	}

	public Position setX(float x) {
		position.x = x;
		return this;
	}

	public Position setY(float y) {
		position.y = y;
		return this;
	}

	public Position setZ(float z) {
		position.z = z;
		return this;
	}

	public Position set(float x, float y, float z) {
		position.set(x, y, z);
		return this;
	}

	public Position set(Vector3f vec) {
		position.set(vec);
		return this;
	}

	@Override
	public String toString() {
		return "[x:" + this.position.x() + "]" + "[y:" + this.position.y() + "]" + "[z:" + this.position.z() + "]";
	}

	@Override
	public void load(TagCompound compound) throws NBTException {
		this.position.x = compound.getFloat("PosX");
		this.position.y = compound.getFloat("PosY");
		this.position.z = compound.getFloat("PosZ");
	}

	@Override
	public TagCompound save() {
		CompoundBuilder builder = new CompoundBuilder().start("PositionComponent");
		builder.addDouble("PosX", this.position.x()).addDouble("PosY", this.position.y()).addDouble("PosZ",
				this.position.z());
		return builder.build();
	}
}
