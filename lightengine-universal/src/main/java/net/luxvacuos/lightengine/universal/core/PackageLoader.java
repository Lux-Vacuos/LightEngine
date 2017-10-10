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

package net.luxvacuos.lightengine.universal.core;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static net.luxvacuos.lightengine.universal.util.registry.KeyCache.getKey;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public final class PackageLoader {

	private PackageLoader() {
	}

	private static IEngineLoader loader;

	public static File searchPackage() {
		File pak = new File(REGISTRY.getRegistryItem(getKey("/Light Engine/System/systemDir")) + "/data/core.jar");
		return pak;
	}

	public static boolean loadPackage(File pak) {
		if (pak.getAbsolutePath().endsWith(".jar")) {
			try {
				URLClassLoader child = new URLClassLoader(new URL[] { pak.toURI().toURL() },
						PackageLoader.class.getClassLoader());
				Class<?> classToLoad = Class.forName("EngineLoader", true, child);
				loader = (IEngineLoader) classToLoad.newInstance();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static void initPackage() {
		loader.loadExternal();
	}

}
