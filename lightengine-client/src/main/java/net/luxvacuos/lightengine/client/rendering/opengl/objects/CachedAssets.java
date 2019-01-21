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

package net.luxvacuos.lightengine.client.rendering.opengl.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.universal.core.TaskManager;

public class CachedAssets {

	private static final Map<String, CachedTexture> TEXTURES = new HashMap<>();

	private static float timer;

	public static CachedTexture loadTexture(String path) {
		return loadTexture(path, false);
	}

	public static CachedTexture loadTexture(String path, boolean immortal) {
		if (TEXTURES.containsKey(path)) {
			TEXTURES.get(path).totalCached++;
			Logger.log("Texture already in cache, ignoring " + path);
			return TEXTURES.get(path);
		} else {
			CachedTexture tex = new CachedTexture(
					GraphicalSubsystem.getMainWindow().getResourceLoader().loadTexture(path), path);
			Logger.log("Texture loaded to cache: " + path);
			tex.immortal = immortal;
			TEXTURES.put(path, tex);
			return tex;
		}
	}

	public static CachedTexture loadTextureMisc(String path) {
		return loadTextureMisc(path, false);
	}

	public static CachedTexture loadTextureMisc(String path, boolean immortal) {
		if (TEXTURES.containsKey(path)) {
			TEXTURES.get(path).totalCached++;
			Logger.log("Texture already in cache, ignoring " + path);
			return TEXTURES.get(path);
		} else {
			CachedTexture tex = new CachedTexture(
					GraphicalSubsystem.getMainWindow().getResourceLoader().loadTextureMisc(path), path);
			tex.immortal = immortal;
			Logger.log("Texture loaded to cache: " + path);
			TEXTURES.put(path, tex);
			return tex;
		}
	}

	public static void update(float delta) {
		timer += delta;
		if (timer >= 30) {
			List<CachedTexture> toRemove = new ArrayList<>();
			for (CachedTexture tex : TEXTURES.values()) {
				if (!tex.immortal)
					if (tex.totalCached <= 0)
						toRemove.add(tex);
			}
			TaskManager.tm.addTaskRenderBackgroundThread(() -> {
				for (CachedTexture cachedTexture : toRemove) {
					Logger.log("Removing Texture from cache: " + cachedTexture.path);
					TEXTURES.remove(cachedTexture.path);
					cachedTexture.trueDispose();
				}
			});
			timer = 0;
		}
	}

	public static void dispose() {
		for (CachedTexture tex : TEXTURES.values()) {
			Logger.log("Removing Texture from cache: " + tex.path);
			tex.trueDispose();
		}
	}

}
