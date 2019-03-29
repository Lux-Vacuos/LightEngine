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

package net.luxvacuos.lightengine.client.core.subsystems;

import net.luxvacuos.lightengine.client.resources.config.SoundSubConfig;
import net.luxvacuos.lightengine.universal.core.subsystems.Subsystem;
import net.luxvacuos.lightengine.universal.loader.EngineData;
import paulscode.sound.Library;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.SoundSystemLogger;

public class SoundSubsystem extends Subsystem<SoundSubConfig> {

	private static SoundSystem soundSystem;

	public SoundSubsystem() {
		super(SoundSubConfig.class, "engine/config/soundSub.json");
	}

	@Override
	public void init(EngineData ed) {
		super.init(ed);
		try {
			for (var lib : config.getLibraries())
				SoundSystemConfig.addLibrary(lib.asSubclass(Library.class));
			for (var codec : config.getCodecs())
				SoundSystemConfig.setCodec(codec.getName(), codec.getImplementation());
		} catch (SoundSystemException e) {
			e.printStackTrace();
		}
		SoundSystemConfig.setSoundFilesPackage(config.getSoundFilesPackage());
		try {
			SoundSystemConfig.setLogger(
					config.getCustomLogger().asSubclass(SoundSystemLogger.class).getConstructor().newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		soundSystem = new SoundSystem();
	}

	@Override
	public void dispose() {
		soundSystem.cleanup();
	}

	public static SoundSystem getSoundSystem() {
		return soundSystem;
	}

}
