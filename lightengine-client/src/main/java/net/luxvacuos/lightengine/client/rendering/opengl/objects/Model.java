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

package net.luxvacuos.lightengine.client.rendering.opengl.objects;

import static org.lwjgl.assimp.Assimp.aiReleaseImport;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;

import net.luxvacuos.lightengine.universal.ecs.entities.BasicEntity;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public class Model implements IDisposable {
	private AIScene scene;
	private List<Mesh> meshes;
	private List<Material> materials;
	// private List<Light> lights;
	private CollisionShape shape;
	private TriangleIndexVertexArray triangleIndexVertexArray;

	public Model(AIScene scene, String rootPath) {
		this.scene = scene;
		
		int materialCount = scene.mNumMaterials();
		PointerBuffer materialsBuffer = scene.mMaterials();
		materials = new ArrayList<>();
		for (int i = 0; i < materialCount; ++i) {
			materials.add(new Material(AIMaterial.create(materialsBuffer.get(i)), rootPath));
		}
		
		int meshCount = scene.mNumMeshes();
		PointerBuffer meshesBuffer = scene.mMeshes();
		meshes = new ArrayList<>();
		for (int i = 0; i < meshCount; ++i) {
			meshes.add(new Mesh(AIMesh.create(meshesBuffer.get(i))));
		}
		// int lightCount = scene.mNumLights();
		// PointerBuffer lightBuffer = scene.mLights();
		// lights = new ArrayList<>();
		// for (int i = 0; i < lightCount; i++) {
		// lights.add(new Light(AILight.create(lightBuffer.get(i))));
		// }
		// Renderer.getLightRenderer().addAllLights(lights);
		triangleIndexVertexArray = new TriangleIndexVertexArray();
		for (Mesh m : meshes) {
			IndexedMesh mesh = new IndexedMesh();

			int faceCount = m.getAiMesh().mNumFaces();
			int elementCount = faceCount * 3;

			ByteBuffer elementArrayBufferData = memAlloc(elementCount * 4);
			AIFace.Buffer facesBuffer = m.getAiMesh().mFaces();
			for (int i = 0; i < faceCount; ++i) {
				AIFace face = facesBuffer.get(i);
				if (face.mNumIndices() != 3)
					throw new IllegalStateException("AIFace.mNumIndices() != 3");
				for (int j = 0; j < face.mNumIndices(); ++j) {
					elementArrayBufferData.putInt(face.mIndices().get(j));
				}
			}
			elementArrayBufferData.flip();
			ByteBuffer vertices = memAlloc(m.getAiMesh().mNumVertices() * 3 * 4);
			for (int i = 0; i < m.getAiMesh().mNumVertices(); i++) {
				AIVector3D position = m.getAiMesh().mVertices().get(i);
				vertices.putFloat(position.x());
				vertices.putFloat(position.y());
				vertices.putFloat(position.z());
			}
			vertices.flip();
			mesh.numTriangles = faceCount;
			mesh.triangleIndexBase = elementArrayBufferData;
			mesh.triangleIndexStride = 3 * 4;
			mesh.numVertices = m.getAiMesh().mNumVertices();
			mesh.vertexBase = vertices;
			mesh.vertexStride = 3 * 4;

			triangleIndexVertexArray.addIndexedMesh(mesh);
		}
		List<BasicEntity> childrens = new ArrayList<>();
		
		AINode root = scene.mRootNode();
		int childrenCount = root.mNumChildren();
		for (int id = 0; id < childrenCount; id++) {
			AINode child = AINode.create(root.mChildren().get(id));
			System.out.println(child.mMeshes().get(0));
		}

		shape = new BvhTriangleMeshShape(triangleIndexVertexArray, true);
	}

	@Override
	public void dispose() {
		aiReleaseImport(scene);
		for (Material material : materials) {
			material.dispose();
		}
		for (Mesh mesh : meshes) {
			mesh.dispose();
		}
		for (IndexedMesh mesh : triangleIndexVertexArray.getIndexedMeshArray()) {
			memFree(mesh.triangleIndexBase);
			memFree(mesh.vertexBase);
		}
		// Renderer.getLightRenderer().removeAllLights(lights);
	}

	public boolean isDoneLoading() {
		for (Mesh m : meshes) {
			if (!m.isDoneLoading())
				return false;
		}
		return true;
	}

	public CollisionShape getShape() {
		return shape;
	}

	public List<Material> getMaterials() {
		return materials;
	}

	public List<Mesh> getMeshes() {
		return meshes;
	}

	// public List<Light> getLights() {
	// return lights;
	// }

}
