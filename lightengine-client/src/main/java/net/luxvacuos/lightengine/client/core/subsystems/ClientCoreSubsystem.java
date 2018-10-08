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

import net.luxvacuos.lightengine.client.core.states.SplashScreenState;
import net.luxvacuos.lightengine.universal.core.GlobalVariables;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem;
import net.luxvacuos.lightengine.universal.loader.EngineData;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class ClientCoreSubsystem extends CoreSubsystem {

	@Override
	public void init(EngineData ed) {
		super.init(ed);
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
		initDefaultRegistry();
		REGISTRY.load(new File((String) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/file"))));
		REGISTRY.save();
		LANG.load(
				"assets/langs/" + REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Regional/lang")) + ".json");
	}

	@Override
	public void run() {
		StateMachine.registerState(new SplashScreenState());
	}

	private void initDefaultRegistry() {
		REGISTRY.register(new Key("/Light Engine/Settings/Core/fps", true), 60);
		REGISTRY.register(new Key("/Light Engine/Settings/Core/ups", true), 60);
		REGISTRY.register(new Key("/Light Engine/Settings/Core/fov", true), 90);

		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/shadows", true), false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/shadowsResolution", true), 1024);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/shadowsDrawDistance", true), 200);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/volumetricLight", true), false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/fxaa", true), false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/vsync", true), false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/motionBlur", true), false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/dof", true), false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/reflections", true), false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/parallax", true), false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/ambientOcclusion", true), false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/chromaticAberration", true), false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/lensFlares", true), false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/pipeline", true), "MultiPass");

		REGISTRY.register(new Key("/Light Engine/Settings/WindowManager/invertButtons", true), false);
		REGISTRY.register(new Key("/Light Engine/Settings/WindowManager/borderSize", true), 4);
		REGISTRY.register(new Key("/Light Engine/Settings/WindowManager/titleBarHeight", true), 29);
		REGISTRY.register(new Key("/Light Engine/Settings/WindowManager/scrollBarSize", true), 16);
		REGISTRY.register(new Key("/Light Engine/Settings/WindowManager/theme", true), "Nano");
		REGISTRY.register(new Key("/Light Engine/Settings/WindowManager/compositor", true), true);

		REGISTRY.register(new Key("/Light Engine/Settings/Regional/lang", true), "en_US");
	}

}
