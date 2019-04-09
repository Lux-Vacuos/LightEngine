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

package net.luxvacuos.lightengine.server.core.subsystems;

import java.io.File;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import net.luxvacuos.lightengine.server.core.states.Splash;
import net.luxvacuos.lightengine.universal.core.GlobalVariables;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem;
import net.luxvacuos.lightengine.universal.loader.EngineData;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class ServerCoreSubsystem extends CoreSubsystem {

	@Override
	public void init(EngineData ed) {
		super.init(ed);
		try {
			Manifest manifest = new Manifest(
					getClass().getClassLoader().getResourceAsStream("lightengine-server-version.mf"));
			Attributes attr = manifest.getMainAttributes();
			String version = attr.getValue("LV-Version");
			String branch = attr.getValue("LV-Branch");
			String build = attr.getValue("LV-Build");
			if (version != null)
				GlobalVariables.version = version;
			if (branch != null)
				GlobalVariables.branch = branch;
			if (build != null)
				GlobalVariables.build = build;
		} catch (Exception e) {
		}
		REGISTRY.register(new Key("/Light Engine/version"),
				GlobalVariables.version + "-" + GlobalVariables.branch + "-" + GlobalVariables.build);
		initDefaultRegistry();
		REGISTRY.load(new File((String) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/file"))));
		REGISTRY.save();
		LANG.load(
				"assets/langs/" + REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Regional/lang")) + ".json");
		StateMachine.registerState(new Splash());
	}

	private void initDefaultRegistry() {
		REGISTRY.register(new Key("/Light Engine/Server/port", true), 44454);
		
		REGISTRY.register(new Key("/Light Engine/Settings/Core/ups", true), 20);
		
		REGISTRY.register(new Key("/Light Engine/Settings/Regional/lang", true), "en_US");
	}

}
