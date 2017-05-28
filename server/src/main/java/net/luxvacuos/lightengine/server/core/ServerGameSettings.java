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

package net.luxvacuos.lightengine.server.core;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import net.luxvacuos.lightengine.universal.core.AbstractGameSettings;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class ServerGameSettings extends AbstractGameSettings {

	@Override
	public void read() {
		REGISTRY.register(new Key("/Light Engine/Settings/Core/ups", true), 					Integer.parseInt(getValue("UPS", "20")));

		REGISTRY.register(new Key("/Light Engine/Server/port", true),						 	Integer.parseInt(getValue("port", "44454")));

		REGISTRY.register(new Key("/Light Engine/Settings/World/chunkRadius", true), 			Integer.parseInt(getValue("chunkRadius", "6")));
		REGISTRY.register(new Key("/Light Engine/Settings/World/chunkManagerThreads", true), 	Integer.parseInt(getValue("chunkManagerThreads", "3")));

		REGISTRY.register(new Key("/Light Engine/Simulation/World/name", true), 				getValue("worldName", "world"));
	}

	@Override
	public void update() {
	}

}
