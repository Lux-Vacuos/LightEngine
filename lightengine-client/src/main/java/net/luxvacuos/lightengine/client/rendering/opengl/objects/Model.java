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

import static org.lwjgl.assimp.Assimp.aiReleaseImport;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memAllocShort;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;

import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btIndexedMesh;
import com.badlogic.gdx.physics.bullet.collision.btTriangleMesh;

import net.luxvacuos.lightengine.universal.ecs.entities.BasicEntity;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public class Model implements IDisposable {
	private AIScene scene;
	private List<Mesh> meshes;
	private List<Material> materials;
	// private List<Light> lights;
	private btCollisionShape shape;
	private btTriangleMesh triangleMesh = new btTriangleMesh();

	public Model(AIScene scene, String rootPath) {
		this.scene = scene;

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
		for (Mesh m : meshes) {
			btIndexedMesh mesh = new btIndexedMesh();
			int faceCount = m.getAiMesh().mNumFaces();
			int elementCount = faceCount * 3;

			ShortBuffer elementArrayBufferData = memAllocShort(elementCount * 4);
			for (int i = 0; i < faceCount; ++i) {
				AIFace face = m.getAiMesh().mFaces().get(i);
				if (face.mNumIndices() != 3)
					throw new IllegalStateException("AIFace.mNumIndices() != 3");
				for (int j = 0; j < face.mNumIndices(); ++j) {
					elementArrayBufferData.put((short) face.mIndices().get(j));
				}
			}
			elementArrayBufferData.flip();
			FloatBuffer vertices = memAllocFloat(m.getAiMesh().mNumVertices() * 3);
			for (int i = 0; i < m.getAiMesh().mNumVertices(); i++) {
				AIVector3D position = m.getAiMesh().mVertices().get(i);
				vertices.put(position.x());
				vertices.put(position.y());
				vertices.put(position.z());
			}
			vertices.flip();
			mesh.set(vertices, 3 * 4, m.getAiMesh().mNumVertices(), 0, elementArrayBufferData, 0, elementCount);

			triangleMesh.addIndexedMesh(mesh);
		}
		List<BasicEntity> childrens = new ArrayList<>();

		AINode root = scene.mRootNode();
		int childrenCount = root.mNumChildren();
		for (int id = 0; id < childrenCount; id++) {
			AINode child = AINode.create(root.mChildren().get(id));
			// System.out.println(child.mMeshes().get(0));
		}

		int materialCount = scene.mNumMaterials();
		PointerBuffer materialsBuffer = scene.mMaterials();
		materials = new ArrayList<>();
		for (int i = 0; i < materialCount; ++i) {
			materials.add(new Material(AIMaterial.create(materialsBuffer.get(i)), rootPath));
		}

		shape = new btBvhTriangleMeshShape(triangleMesh, true);
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
		triangleMesh.dispose();
		// Renderer.getLightRenderer().removeAllLights(lights);
	}

	public btCollisionShape getShape() {
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
