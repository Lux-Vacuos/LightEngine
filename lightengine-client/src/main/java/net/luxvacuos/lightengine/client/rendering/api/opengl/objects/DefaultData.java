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

import net.luxvacuos.lightengine.client.resources.ResourceLoader;

public final class DefaultData {

	public static CachedTexture diffuse, normal, roughness, metallic;

	public static void init(ResourceLoader loader) {
		diffuse = CachedAssets.loadTexture("textures/def/d.png", true);
		normal = CachedAssets.loadTextureMisc("textures/def/d_n.png", true);
		roughness = CachedAssets.loadTextureMisc("textures/def/d_r.png", true);
		metallic = CachedAssets.loadTextureMisc("textures/def/d_m.png", true);
	}

	public static void dispose() {
		diffuse.dispose();
		normal.dispose();
		roughness.dispose();
		metallic.dispose();
	}

}
