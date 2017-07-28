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

package net.luxvacuos.lightengine.client.rendering.api.opengl.objects;

import net.luxvacuos.lightengine.universal.resources.IDisposable;

/**
 * Textured Model
 * 
 * @author Guerra24 <pablo230699@hotmail.com>
 * @category Assets
 */
public class TexturedModel implements IDisposable {

	/**
	 * Raw Model
	 */
	private RawModel rawModel;
	/**
	 * Material
	 */
	private Material material;

	/**
	 * Constructor, Create a Textured Model
	 * 
	 * @param model
	 *            RawModel
	 * @param texture
	 *            Texture
	 */
	public TexturedModel(RawModel model, Material material) {
		this.rawModel = model;
		this.material = material;
	}
	
	@Override
	public void dispose() {
		material.dispose();
	}

	/**
	 * Get Raw Model
	 * 
	 * @return RawModel
	 */
	public RawModel getRawModel() {
		return rawModel;
	}

	public Material getMaterial() {
		return material;
	}
}
