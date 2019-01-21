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

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.hackhalo2.nbt.stream.NBTOutputStream;

import net.luxvacuos.lightengine.universal.resources.IDisposable;
import net.luxvacuos.lightengine.universal.util.IUpdatable;

public class LEEntity extends Entity implements IUpdatable, IDisposable {

	protected Vector3f transformPosition = new Vector3f(), localPosition = new Vector3f(),
			finalPosition = new Vector3f();
	protected Vector3f localRotation = new Vector3f(), finalRotation = new Vector3f();
	protected float scale = 1;
	protected LEEntity rootEntity;
	/**
	 * <b>INTERNAL USE ONLY</b>
	 */
	private Engine engine;

	protected Matrix4f matrix = new Matrix4f();

	public void init() {
	}

	@Override
	public void beforeUpdate(float delta) {
	}

	@Override
	public void update(float delta) {
		matrix.identity();
		matrix.rotate((float) Math.toRadians(rootEntity.getRotation().x()), new Vector3f(1, 0, 0));
		matrix.rotate((float) Math.toRadians(rootEntity.getRotation().y()), new Vector3f(0, 1, 0));
		matrix.rotate((float) Math.toRadians(rootEntity.getRotation().z()), new Vector3f(0, 0, 1));

		localPosition.mulTransposeDirection(matrix, transformPosition);

		rootEntity.getPosition().add(transformPosition, finalPosition);
		rootEntity.getRotation().add(localRotation, finalRotation);
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

	public void addEntity(LEEntity entity) {
		entity.rootEntity = this;
		entity.engine = this.engine;
		this.engine.addEntity(entity);
	}

	public Vector3f getPosition() {
		return finalPosition;
	}

	public float getX() {
		return localPosition.x();
	}

	public float getY() {
		return localPosition.y();
	}

	public float getZ() {
		return localPosition.z();
	}

	public LEEntity setX(float x) {
		localPosition.x = x;
		return this;
	}

	public LEEntity setY(float y) {
		localPosition.y = y;
		return this;
	}

	public LEEntity setZ(float z) {
		localPosition.z = z;
		return this;
	}

	public LEEntity setPosition(float x, float y, float z) {
		localPosition.set(x, y, z);
		return this;
	}

	public LEEntity setPosition(Vector3f vec) {
		localPosition.set(vec);
		return this;
	}

	public Vector3f getRotation() {
		return finalRotation;
	}

	public float getRX() {
		return localRotation.x();
	}

	public float getRY() {
		return localRotation.y();
	}

	public float getRZ() {
		return localRotation.z();
	}

	public LEEntity setRX(float x) {
		localRotation.x = x;
		return this;
	}

	public LEEntity setRY(float y) {
		localRotation.y = y;
		return this;
	}

	public LEEntity setRZ(float z) {
		localRotation.z = z;
		return this;
	}

	public LEEntity setRotation(float x, float y, float z) {
		localRotation.set(x, y, z);
		return this;
	}

	public LEEntity setRotation(Vector3f vec) {
		localRotation.set(vec);
		return this;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	/**
	 * <b>INTERNAL USE ONLY</b>
	 */
	public void setRootEntity(LEEntity rootEntity) {
		if (this.rootEntity == null)
			this.rootEntity = rootEntity;
	}

	/**
	 * <b>INTERNAL USE ONLY</b>
	 */
	public void setEngine(Engine engine) {
		if (this.engine == null)
			this.engine = engine;
	}

}
