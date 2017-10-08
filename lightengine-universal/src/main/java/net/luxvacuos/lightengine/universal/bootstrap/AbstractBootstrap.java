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

package net.luxvacuos.lightengine.universal.bootstrap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.universal.core.GlobalVariables;

public abstract class AbstractBootstrap implements IBootstrap {

	private static Platform platform;

	public AbstractBootstrap(String[] args) {
		try (BufferedReader br = new BufferedReader(new FileReader("project"))) {
			GlobalVariables.PROJECT = br.readLine();
		} catch (IOException e1) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					getClass().getClassLoader().getResourceAsStream("project")));
			GlobalVariables.PROJECT = br.lines().reduce("", String::concat);
		}
		try {
			parseArgs(args);
		} catch (ArrayIndexOutOfBoundsException aioe) {
			Logger.error("Error: Arguments were wrong", aioe);
			System.exit(1);
		} catch (Exception ex) {
			Logger.error(ex);
			System.exit(1);
		}
		init();
	}

	public static Platform getPlatform() {
		if (platform == null) {
			// Convert os.name and os.arch to lower case
			final String OS = System.getProperty("os.name").toLowerCase();
			final String ARCH = System.getProperty("os.arch").toLowerCase();

			// Find what OS is running
			boolean isWindows = OS.contains("windows");
			boolean isLinux = OS.contains("linux");
			boolean isMac = OS.contains("mac");
			boolean is64Bit = ARCH.equals("amd64") || ARCH.equals("x86_64");

			// Set platform to unknown before setting the real OS
			platform = Platform.UNKNOWN;

			// Check booleans and architecture
			if (isWindows)
				platform = is64Bit ? Platform.WINDOWS_64 : Platform.WINDOWS_32;
			if (isLinux)
				platform = is64Bit ? Platform.LINUX_64 : Platform.LINUX_32;
			if (isMac)
				platform = Platform.MACOSX;
		}

		return platform;
	}

}
