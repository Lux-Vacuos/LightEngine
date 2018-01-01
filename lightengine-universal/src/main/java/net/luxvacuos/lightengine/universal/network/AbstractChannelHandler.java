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

package net.luxvacuos.lightengine.universal.network;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.badlogic.ashley.core.Engine;

import net.luxvacuos.lightengine.universal.core.IWorldSimulation;
import net.luxvacuos.lightengine.universal.ecs.entities.PlayerEntity;

public abstract class AbstractChannelHandler extends SharedChannelHandler implements INetworkHandler {
	
	protected IWorldSimulation worldSimulation;
	protected Engine engine;
	protected Map<UUID, PlayerEntity> players = new HashMap<>();

}
