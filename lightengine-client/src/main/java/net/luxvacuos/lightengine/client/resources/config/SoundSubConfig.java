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

package net.luxvacuos.lightengine.client.resources.config;

import java.util.ArrayList;
import java.util.List;

import net.luxvacuos.lightengine.universal.resources.config.SubsystemConfig;

public class SoundSubConfig extends SubsystemConfig {

	private List<Class<?>> libraries;
	private List<Codec> codecs;
	private String soundFilesPackage;
	private Class<?> customLogger;

	public SoundSubConfig() {
		libraries = new ArrayList<>();
		codecs = new ArrayList<>();
	}

	public List<Class<?>> getLibraries() {
		return libraries;
	}

	public List<Codec> getCodecs() {
		return codecs;
	}

	public void setSoundFilesPackage(String soundFilesPackage) {
		this.soundFilesPackage = soundFilesPackage;
	}

	public String getSoundFilesPackage() {
		return soundFilesPackage;
	}

	public void setCustomLogger(Class<?> customLogger) {
		this.customLogger = customLogger;
	}

	public Class<?> getCustomLogger() {
		return customLogger;
	}

	public class Codec {
		private String name;
		private Class<?> implementation;

		public Codec(String name, Class<?> implementation) {
			this.name = name;
			this.implementation = implementation;
		}

		public String getName() {
			return name;
		}

		public Class<?> getImplementation() {
			return implementation;
		}

	}

}
