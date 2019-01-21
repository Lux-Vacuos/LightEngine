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

import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;

import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public class Mesh implements IDisposable {

	private VAO mesh;
	private AIMesh aiMesh;
	private boolean doneLoading;

	public Mesh(AIMesh aiMesh) {
		this.aiMesh = aiMesh;

		List<Vector3f> pos = new ArrayList<>();
		List<Vector2f> tex = new ArrayList<>();
		List<Vector3f> nor = new ArrayList<>();
		List<Vector3f> tan = new ArrayList<>();
		for (int i = 0; i < aiMesh.mNumVertices(); i++) {
			AIVector3D position = aiMesh.mVertices().get(i);
			pos.add(new Vector3f(position.x(), position.y(), position.z()));

			AIVector3D texcoord = null;
			if (aiMesh.mTextureCoords(0) != null) {
				texcoord = aiMesh.mTextureCoords(0).get(i);
				tex.add(new Vector2f(texcoord.x(), texcoord.y()));
			} else
				tex.add(new Vector2f(0, 0));

			AIVector3D normal = null;
			if (aiMesh.mNormals() != null) {
				normal = aiMesh.mNormals().get(i);
				nor.add(new Vector3f(normal.x(), normal.y(), normal.z()));
			} else
				nor.add(new Vector3f(0, 1, 0));

			AIVector3D tangent = null;
			if (aiMesh.mTangents() != null) {
				tangent = aiMesh.mTangents().get(i);
				tan.add(new Vector3f(tangent.x(), tangent.y(), tangent.z()));
			} else
				tan.add(new Vector3f(0, 0, 0));
		}
		int faceCount = aiMesh.mNumFaces();
		int elementCount = faceCount * 3;
		IntBuffer elementArrayBufferData = memAllocInt(elementCount);
		AIFace.Buffer facesBuffer = aiMesh.mFaces();
		for (int i = 0; i < faceCount; ++i) {
			AIFace face = facesBuffer.get(i);
			if (face.mNumIndices() != 3)
				throw new IllegalStateException("AIFace.mNumIndices() != 3");
			elementArrayBufferData.put(face.mIndices());
		}
		elementArrayBufferData.flip();
		int[] ind = new int[elementCount];
		elementArrayBufferData.get(ind);
		memFree(elementArrayBufferData);
		TaskManager.tm.addTaskRenderThread(() -> {
			mesh = VAO.create();
			mesh.bind();
			loadData(pos, tex, nor, tan);
			mesh.createIndexBuffer(ind, GL_STATIC_DRAW);
			mesh.unbind();
			doneLoading = true;
		});
	}

	@Override
	public void dispose() {
		mesh.dispose();
	}

	public AIMesh getAiMesh() {
		return aiMesh;
	}

	public VAO getMesh() {
		return mesh;
	}

	public boolean isDoneLoading() {
		return doneLoading;
	}

	private void loadData(List<Vector3f> positions, List<Vector2f> texcoords, List<Vector3f> normals,
			List<Vector3f> tangets) {
		FloatBuffer pos = memAllocFloat(positions.size() * 3);
		FloatBuffer tex = memAllocFloat(texcoords.size() * 2);
		FloatBuffer nor = memAllocFloat(normals.size() * 3);
		FloatBuffer tan = memAllocFloat(tangets.size() * 3);
		for (int i = 0; i < positions.size(); i++) {
			pos.put(positions.get(i).x);
			pos.put(positions.get(i).y);
			pos.put(positions.get(i).z);
		}
		for (int i = 0; i < texcoords.size(); i++) {
			tex.put(texcoords.get(i).x);
			tex.put(texcoords.get(i).y);
		}
		for (int i = 0; i < normals.size(); i++) {
			nor.put(normals.get(i).x);
			nor.put(normals.get(i).y);
			nor.put(normals.get(i).z);
		}
		for (int i = 0; i < tangets.size(); i++) {
			tan.put(tangets.get(i).x);
			tan.put(tangets.get(i).y);
			tan.put(tangets.get(i).z);
		}
		pos.flip();
		tex.flip();
		nor.flip();
		tan.flip();
		mesh.createAttribute(0, pos, 3, GL_STATIC_DRAW);
		mesh.createAttribute(1, tex, 2, GL_STATIC_DRAW);
		mesh.createAttribute(2, nor, 3, GL_STATIC_DRAW);
		mesh.createAttribute(3, tan, 3, GL_STATIC_DRAW);
		memFree(pos);
		memFree(tex);
		memFree(nor);
		memFree(tan);
	}

}
