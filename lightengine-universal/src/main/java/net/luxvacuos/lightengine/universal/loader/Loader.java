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

package net.luxvacuos.lightengine.universal.loader;

import net.luxvacuos.lightengine.universal.core.IEngineLoader;
import net.luxvacuos.lightengine.universal.util.PerRunLog;

/**
 * 
 * Main class, early init and identification of the system.
 * 
 * There are two entry points, either the game starts the engine or the engine
 * loads a .jar
 * 
 * @author Guerra24
 *
 */
public abstract class Loader {

	private IEngineLoader el;
	private EngineData ed;

	public Loader(IEngineLoader el, String... args) {
		this.el = el;
		init(args);
	}

	private void init(String... args) {
		System.out.println("Loader - Starting...");
		ed = new EngineData();
		parseArgs(ed, args);
		findPlatform(ed);
		findProject(ed);
		findUserDir(ed);
		PerRunLog.userDir = ed.userDir;
		startEngine(el, ed);
	}

	private void findPlatform(EngineData ed) {
		// Convert os.name and os.arch to lower case
		String os = System.getProperty("os.name").toLowerCase();
		String arch = System.getProperty("os.arch").toLowerCase();

		// Find what os is running
		boolean isWindows = os.contains("windows");
		boolean isLinux = os.contains("linux");
		boolean isMac = os.contains("mac");
		boolean is64Bit = arch.equals("amd64") || arch.equals("x86_64");

		// Set platform to unknown before setting the real os
		ed.platform = Platform.UNKNOWN;

		// Check booleans and architecture
		if (isWindows)
			ed.platform = is64Bit ? Platform.WINDOWS_64 : Platform.WINDOWS_32;
		if (isLinux)
			ed.platform = is64Bit ? Platform.LINUX_64 : Platform.LINUX_32;
		if (isMac)
			ed.platform = Platform.MACOS;
	}

	private void findProject(EngineData ed) {
		/*
		 * try (BufferedReader br = new BufferedReader( new
		 * InputStreamReader(getClass().getClassLoader().getResourceAsStream("project"))
		 * )) { ed.project = br.lines().reduce("", String::concat); } catch (Exception
		 * e) { }
		 */
	}

	protected abstract void findUserDir(EngineData ed);

	private void parseArgs(EngineData ed, String... args) {
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "-systemDir":
				ed.systemDir = args[++i];
				break;
			case "-r":
				ed.registry.put(args[++i], args[++i]);
				break;
			case "-p":
				ed.project = args[++i];
				break;
			default:
				if (args[i].startsWith("-"))
					System.out.println("Loader - Unknown argument: " + args[i].substring(1));
				else
					System.out.println("Loader - Unknown token: " + args[i]);

			}
		}
	}

	protected abstract void startEngine(IEngineLoader el, EngineData ed);

}
