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

import org.joml.Vector3f;

import com.badlogic.ashley.core.Entity;
import com.hackhalo2.nbt.stream.NBTOutputStream;

import net.luxvacuos.lightengine.universal.resources.IDisposable;
import net.luxvacuos.lightengine.universal.util.IUpdatable;

public class LEEntity extends Entity implements IUpdatable, IDisposable {

	protected Vector3f position = new Vector3f();
	protected Vector3f rotation = new Vector3f();
	protected float scale = 1;

	@Override
	public void beforeUpdate(float delta) {
	}

	@Override
	public void update(float delta) {

	}

	@Override
	public void afterUpdate(float delta) {

	}

	@Override
	public void dispose() {

	}

	public void quickSave() {

	}

	public void save(NBTOutputStream out) {

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

	public LEEntity setX(float x) {
		position.x = x;
		return this;
	}

	public LEEntity setY(float y) {
		position.y = y;
		return this;
	}

	public LEEntity setZ(float z) {
		position.z = z;
		return this;
	}

	public LEEntity setPosition(float x, float y, float z) {
		position.set(x, y, z);
		return this;
	}

	public LEEntity setPosition(Vector3f vec) {
		position.set(vec);
		return this;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public float getRX() {
		return rotation.x();
	}

	public float getRY() {
		return rotation.y();
	}

	public float getRZ() {
		return rotation.z();
	}

	public LEEntity setRX(float x) {
		rotation.x = x;
		return this;
	}

	public LEEntity setRY(float y) {
		rotation.y = y;
		return this;
	}

	public LEEntity setRZ(float z) {
		rotation.z = z;
		return this;
	}

	public LEEntity setRotation(float x, float y, float z) {
		rotation.set(x, y, z);
		return this;
	}

	public LEEntity setRotation(Vector3f vec) {
		rotation.set(vec);
		return this;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

}
