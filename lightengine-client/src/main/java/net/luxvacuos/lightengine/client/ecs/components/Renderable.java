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

package net.luxvacuos.lightengine.client.ecs.components;

import com.hackhalo2.nbt.exceptions.NBTException;
import com.hackhalo2.nbt.tags.TagCompound;

import net.luxvacuos.lightengine.client.rendering.opengl.EntityRenderer;
import net.luxvacuos.lightengine.universal.ecs.components.LEComponent;

public class Renderable implements LEComponent {
	
	private int rendererID = EntityRenderer.ENTITY_RENDERER_ID;

	@Override
	public void load(TagCompound compound) throws NBTException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TagCompound save() {
		// TODO Auto-generated method stub
		return null;
	}

}
