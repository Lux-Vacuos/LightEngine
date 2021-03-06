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

package net.luxvacuos.lightengine.universal.ecs;

import com.badlogic.ashley.core.ComponentMapper;

import net.luxvacuos.lightengine.universal.ecs.components.*;

public class Components {

	protected Components() {
		throw new RuntimeException("Cannot Initialize the Components class!");
	}

	public static final ComponentMapper<Position> POSITION = ComponentMapper.getFor(Position.class);

	public static final ComponentMapper<Rotation> ROTATION = ComponentMapper.getFor(Rotation.class);

	public static final ComponentMapper<Scale> SCALE = ComponentMapper.getFor(Scale.class);

	public static final ComponentMapper<Health> HEALTH = ComponentMapper.getFor(Health.class);

	public static final ComponentMapper<Collision> COLLISION = ComponentMapper.getFor(Collision.class);

	public static final ComponentMapper<NBTComponent> NBT = ComponentMapper.getFor(NBTComponent.class);

	public static final ComponentMapper<Player> PLAYER = ComponentMapper.getFor(Player.class);

	public static final ComponentMapper<Name> NAME = ComponentMapper.getFor(Name.class);

	public static final ComponentMapper<UUIDComponent> UUID = ComponentMapper.getFor(UUIDComponent.class);

}
