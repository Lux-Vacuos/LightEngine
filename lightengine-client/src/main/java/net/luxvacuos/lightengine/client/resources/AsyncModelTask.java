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

package net.luxvacuos.lightengine.client.resources;

import static org.lwjgl.assimp.Assimp.AI_SCENE_FLAGS_INCOMPLETE;
import static org.lwjgl.assimp.Assimp.aiGetErrorString;
import static org.lwjgl.assimp.Assimp.aiImportFileFromMemoryWithProperties;
import static org.lwjgl.assimp.Assimp.aiProcess_CalcTangentSpace;
import static org.lwjgl.assimp.Assimp.aiProcess_FindInvalidData;
import static org.lwjgl.assimp.Assimp.aiProcess_FlipUVs;
import static org.lwjgl.assimp.Assimp.aiProcess_GenSmoothNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_ImproveCacheLocality;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_OptimizeMeshes;
import static org.lwjgl.assimp.Assimp.aiProcess_SplitLargeMeshes;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
import static org.lwjgl.assimp.Assimp.aiProcess_ValidateDataStructure;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.assimp.AIScene;

import com.badlogic.gdx.utils.async.AsyncTask;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.client.rendering.opengl.GLResourceLoader;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Model;

public class AsyncModelTask implements AsyncTask<Model> {
	private String filePath;

	public AsyncModelTask(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public Model call() throws Exception {
		Logger.log("Loading Model: " + filePath);
		String fileName = "assets/" + filePath;
		String ext = fileName.split("\\.")[1];
		ByteBuffer bFile = null;
		try {
			bFile = GLResourceLoader.ioResourceToByteBuffer(fileName, 1024 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
		AIScene scene = aiImportFileFromMemoryWithProperties(bFile,
				aiProcess_Triangulate | aiProcess_FlipUVs | aiProcess_SplitLargeMeshes | aiProcess_OptimizeMeshes
						| aiProcess_ValidateDataStructure | aiProcess_FindInvalidData | aiProcess_JoinIdenticalVertices
						| aiProcess_GenSmoothNormals | aiProcess_CalcTangentSpace | aiProcess_ImproveCacheLocality,
				ext, AssimpResourceLoader.propertyStore);
		if (scene == null || scene.mFlags() == AI_SCENE_FLAGS_INCOMPLETE || scene.mRootNode() == null) {
			Logger.error(aiGetErrorString());
		}
		Model m = new Model(scene, filePath.substring(0, filePath.lastIndexOf("/")));
		while (!m.isDoneLoading()) {
			Thread.sleep(100);
		}
		return m;
	}

}
