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

package net.luxvacuos.lightengine.client.core.subsystems;

import java.io.File;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import net.luxvacuos.lightengine.client.core.ClientGameSettings;
import net.luxvacuos.lightengine.universal.core.GlobalVariables;
import net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class ClientCoreSubsystem extends CoreSubsystem {

	@Override
	public void init() {
		super.init();
		try {
			Manifest manifest = new Manifest(
					getClass().getClassLoader().getResourceAsStream("lightengine-client-version.mf"));
			Attributes attr = manifest.getMainAttributes();
			String version = attr.getValue("LV-Version");
			String branch = attr.getValue("LV-Branch");
			String build = attr.getValue("LV-Build");
			if (version != null)
				GlobalVariables.version = version;
			if (branch != null)
				GlobalVariables.branch = branch;
			if (build != null)
				GlobalVariables.build = Integer.parseInt(build);
		} catch (Exception e) {
		}
		REGISTRY.register(new Key("/Light Engine/version"),
				GlobalVariables.version + "-" + GlobalVariables.branch + "-" + GlobalVariables.build);
		gameSettings = new ClientGameSettings();
		gameSettings.read();
		REGISTRY.load(new File((String) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/file"))));
		REGISTRY.save();
		LANG.load(
				"assets/langs/" + REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Regional/lang")) + ".json");
	}

}
