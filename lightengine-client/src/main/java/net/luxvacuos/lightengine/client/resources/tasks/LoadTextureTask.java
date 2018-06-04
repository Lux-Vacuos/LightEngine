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

package net.luxvacuos.lightengine.client.resources.tasks;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_is_hdr_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryStack;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.client.core.exception.DecodeTextureException;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.RawTexture;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Texture;
import net.luxvacuos.lightengine.client.resources.ResourcesManager;
import net.luxvacuos.lightengine.universal.core.Task;

public class LoadTextureTask extends Task<Texture> {

	private String file;
	private int filter;
	private int textureWarp;
	private int format;
	private boolean textureMipMapAF;

	public LoadTextureTask(String file, int filter, int textureWarp, int format, boolean textureMipMapAF) {
		this.file = file;
		this.filter = filter;
		this.textureWarp = textureWarp;
		this.format = format;
		this.textureMipMapAF = textureMipMapAF;
	}

	@Override
	protected Texture call() {
		Logger.log("Loading: " + file);
		RawTexture data = decodeTextureFile(file);
		int id = ResourcesManager.backend.loadTexture(filter, textureWarp, format, textureMipMapAF, data);
		data.dispose();
		return new Texture(id);
	}

	private RawTexture decodeTextureFile(String file) {
		ByteBuffer imageBuffer;
		try {
			imageBuffer = ResourcesManager.ioResourceToByteBuffer(file, 1024 * 1024);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		int width = 0;
		int height = 0;
		int component = 0;
		ByteBuffer image;
		try (MemoryStack stack = stackPush()) {
			var w = stack.mallocInt(1);
			var h = stack.mallocInt(1);
			var comp = stack.mallocInt(1);

			if (!stbi_info_from_memory(imageBuffer, w, h, comp))
				throw new DecodeTextureException("Failed to read image information: " + stbi_failure_reason());

			Logger.log("Image width: " + w.get(0), "Image height: " + h.get(0), "Image components: " + comp.get(0),
					"Image HDR: " + stbi_is_hdr_from_memory(imageBuffer));

			image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
			memFree(imageBuffer);

			if (image == null)
				throw new DecodeTextureException("Failed to load image: " + stbi_failure_reason());
			width = w.get(0);
			height = h.get(0);
			component = comp.get(0);
		}
		return new RawTexture(image, width, height, component);
	}

}
