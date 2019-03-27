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

package net.luxvacuos.lightengine.client.resources.tasks;

import net.luxvacuos.lightengine.client.resources.ShaderIncludes;
import net.luxvacuos.lightengine.universal.core.Task;
import net.luxvacuos.lightengine.universal.core.subsystems.ResManager;
import net.luxvacuos.lightengine.universal.resources.ResourceType;

public class ShaderIncludeTask extends Task<Void> {

	private String key;

	public ShaderIncludeTask(String key) {
		this.key = key;
	}

	@Override
	protected Void call() {
		var osr = ResManager.getResourceOfType(key, ResourceType.ISL);
		var sr = osr.get();
		ShaderIncludes.processIncludeFile(sr);
		return null;
	}

}
