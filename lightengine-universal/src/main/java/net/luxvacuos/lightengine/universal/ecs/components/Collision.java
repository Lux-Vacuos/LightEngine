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

import java.util.List;

import com.hackhalo2.nbt.exceptions.NBTException;
import com.hackhalo2.nbt.tags.TagCompound;

import net.luxvacuos.lightengine.universal.world.DynamicObject;

public class Collision implements LEComponent {

	private DynamicObject dynamicObject;
	private List<DynamicObject> dynamicObjects;

	public Collision(DynamicObject dynamicObject) {
		this.dynamicObject = dynamicObject;
	}

	public Collision(List<DynamicObject> dynamicObjects) {
		this.dynamicObjects = dynamicObjects;
	}

	public DynamicObject getDynamicObject() {
		return dynamicObject;
	}

	public List<DynamicObject> getDynamicObjects() {
		return dynamicObjects;
	}

	@Override
	public void load(TagCompound compound) throws NBTException {
		// TODO Auto-generated method stub

	}

	@Override
	public TagCompound save() {
		// TODO Auto-generated method stub
		return null;
	}

}
