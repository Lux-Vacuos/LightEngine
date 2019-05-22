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

import java.io.IOException;
import java.nio.ByteBuffer;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.client.resources.ResourcesManager;
import net.luxvacuos.lightengine.universal.core.Task;

public class IOToByteBuffer extends Task<ByteBuffer> {

	private String path;
	private int size;

	public IOToByteBuffer(String path) {
		this.path = path;
		size = 64;
	}

	public IOToByteBuffer(String path, int sizeInKB) {
		this.path = path;
		this.size = sizeInKB;
	}

	@Override
	protected ByteBuffer call() {
		Logger.log("Loading Resource: " + path);
		try {
			return ResourcesManager.ioResourceToByteBuffer(path, (int) (1024f * 1024f * size));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
