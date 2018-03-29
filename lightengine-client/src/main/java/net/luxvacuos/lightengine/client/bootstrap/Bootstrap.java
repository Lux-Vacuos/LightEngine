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

package net.luxvacuos.lightengine.client.bootstrap;

import net.luxvacuos.lightengine.client.core.ClientTaskManager;
import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.core.LightEngineClient;
import net.luxvacuos.lightengine.universal.bootstrap.AbstractBootstrap;
import net.luxvacuos.lightengine.universal.bootstrap.Platform;
import net.luxvacuos.lightengine.universal.core.GlobalVariables;
import net.luxvacuos.lightengine.universal.core.IEngineLoader;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.TempVariables;

public class Bootstrap extends AbstractBootstrap {

	public Bootstrap(String[] args, IEngineLoader loader) {
		super(args, loader);
	}

	@Override
	public void init() {
		Thread.currentThread().setName("Light Engine-Client");
		String prefix = "";
		if (getPlatform().equals(Platform.WINDOWS_32) || getPlatform().equals(Platform.WINDOWS_64))
			prefix = System.getenv("AppData");
		else if (getPlatform().equals(Platform.LINUX_32) || getPlatform().equals(Platform.LINUX_64))
			prefix = System.getProperty("user.home");
		else if (getPlatform().equals(Platform.MACOSX)) {
			prefix = System.getProperty("user.home");
			prefix += "/Library/Application Support";
		}
		prefix += "/." + GlobalVariables.PROJECT;
		TempVariables.userDir = prefix;
		
		TaskManager.tm = new ClientTaskManager();
		TaskManager.tm.init();
		loader.loadExternal();
		new LightEngineClient();
	}

	@Override
	public void parseArgs(String[] args) {
		// Booleans to prevent setting a previously set value
		boolean gaveWidth = false, gaveHeight = false, gaveSysDir = false;
		// Iterate through array
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			// Check for window width
			case "-width":
				if (gaveWidth)
					throw new IllegalStateException("Width already given");
				// Convert and set the width
				ClientVariables.WIDTH = Integer.parseInt(args[++i]);
				if (ClientVariables.WIDTH <= 0)
					throw new IllegalArgumentException("Width must be positive");
				gaveWidth = true;
				break;
			// Check for height
			case "-height":
				if (gaveHeight)
					throw new IllegalStateException("Height already given");
				// Convert and set height
				ClientVariables.HEIGHT = Integer.parseInt(args[++i]);
				if (ClientVariables.HEIGHT <= 0)
					throw new IllegalArgumentException("Height must be positive");
				gaveHeight = true;
				break;
			case "-systemDir":
				if(gaveSysDir)
					throw new IllegalStateException("SystemDir already given");
				TempVariables.systemDir = args[++i];
				gaveSysDir = true;
				break;
			default:
				// If there is an unknown arg throw exception
				if (args[i].startsWith("-")) {
					throw new IllegalArgumentException("Unknown argument: " + args[i].substring(1));
				} else {
					throw new IllegalArgumentException("Unknown token: " + args[i]);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new Bootstrap(args, null);
	}

}
