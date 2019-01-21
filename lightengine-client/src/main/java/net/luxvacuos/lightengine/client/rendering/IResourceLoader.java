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

package net.luxvacuos.lightengine.client.rendering;

import net.luxvacuos.lightengine.client.rendering.opengl.objects.CubeMapTexture;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.RawModel;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.RawTexture;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Texture;
import net.luxvacuos.lightengine.client.ui.Font;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public interface IResourceLoader extends IDisposable {

	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, float[] tangents,
			int[] indices);

	public int loadToVAO(float[] positions, float[] textureCoords);

	public RawModel loadToVAO(float[] positions, int dimensions);

	public int createEmptyVBO(int floatCount);

	public void updateVBO(int vbo, float[] data);

	public void addInstacedAttribute(int vao, int vbo, int attribute, int dataSize, int instancedDataLenght,
			int offset);

	public Texture loadTextureMisc(String fileName);

	public Texture loadTextureMisc(String fileName, int filter, boolean textureMipMapAF);

	public Texture loadTexture(String fileName);

	public Texture loadTexture(String fileName, int filter, boolean textureMipMapAF);

	public int createTexture(RawTexture data, int filter, int textureWarp, int format, boolean textureMipMapAF);

	public Font loadNVGFont(String filename, String name);

	public Font loadNVGFont(String filename, String name, int size);

	public int loadNVGTexture(String file);

	public int loadCubeMap(String[] textureFiles);

	public CubeMapTexture createEmptyCubeMap(int size, boolean hdr, boolean mipmap);

	public RawModel loadObjModel(String fileName);

}
