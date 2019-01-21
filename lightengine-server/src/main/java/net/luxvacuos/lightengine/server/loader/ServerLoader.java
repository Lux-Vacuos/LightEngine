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

package net.luxvacuos.lightengine.server.loader;

import java.io.File;
import java.io.IOException;

import net.luxvacuos.lightengine.server.core.ServerEngine;
import net.luxvacuos.lightengine.server.core.ServerTaskManager;
import net.luxvacuos.lightengine.universal.core.IEngineLoader;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.loader.EngineData;
import net.luxvacuos.lightengine.universal.loader.Loader;

public class ServerLoader extends Loader {

	public ServerLoader(IEngineLoader el, String... args) {
		super(el, args);
	}

	@Override
	protected void findUserDir(EngineData ed) {
		try {
			ed.userDir = new File(".").getCanonicalPath().toString();
		} catch (IOException e) {
		}
	}

	@Override
	protected void startEngine(IEngineLoader el, EngineData ed) {
		TaskManager.tm = new ServerTaskManager();
		new ServerEngine(el, ed);
	}

}
