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

package net.luxvacuos.lightengine.client.core;
import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import net.luxvacuos.lightengine.universal.core.AbstractGameSettings;
import net.luxvacuos.lightengine.universal.util.registry.Key;

/**
 * 
 * Here all Light Engine's settings are stored to a file.
 * 
 * @author Guerra24 <pablo230699@hotmail.com>
 * @author HACKhalo2 <hackhalotwo@gmail.com>
 *
 */
public final class ClientGameSettings extends AbstractGameSettings {

	/**
	 * Create a GameSettings instance that will set the path, create the
	 * Properties object and check for existing file.
	 */
	public ClientGameSettings() {
	}

	@Override
	public void read() {
		REGISTRY.register(new Key("/Light Engine/Settings/Core/fps", true), 						60);
		REGISTRY.register(new Key("/Light Engine/Settings/Core/ups", true), 						60);
		REGISTRY.register(new Key("/Light Engine/Settings/Core/fov", true), 						90);
		
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/shadows", true),				false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/shadowsResolution", true), 		1024);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/shadowsDrawDistance", true), 	200);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/volumetricLight", true), 		false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/fxaa", true), 					false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/vsync", true), 					false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/motionBlur", true), 			false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/dof", true), 					false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/reflections", true), 			false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/parallax", true),				false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/ambientOcclusion", true), 		false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/chromaticAberration", true), 	false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/lensFlares", true), 			false);
		REGISTRY.register(new Key("/Light Engine/Settings/Graphics/pipeline", true), 				"MultiPass");
		
		REGISTRY.register(new Key("/Light Engine/Settings/WindowManager/invertButtons", true), 	false);
		REGISTRY.register(new Key("/Light Engine/Settings/WindowManager/borderSize", true), 		10f);
		REGISTRY.register(new Key("/Light Engine/Settings/WindowManager/titleBarHeight", true), 	30f);
		REGISTRY.register(new Key("/Light Engine/Settings/WindowManager/scrollBarSize", true), 	16f);
		REGISTRY.register(new Key("/Light Engine/Settings/WindowManager/titleBarBorder", true), 	true);
		REGISTRY.register(new Key("/Light Engine/Settings/WindowManager/theme", true), 			"Nano");
		
		REGISTRY.register(new Key("/Light Engine/Settings/Regional/lang", true), 					"en_US");
	}

	/**
	 * Update values from runtime data
	 */
	@Override
	public void update() {
	}

}
