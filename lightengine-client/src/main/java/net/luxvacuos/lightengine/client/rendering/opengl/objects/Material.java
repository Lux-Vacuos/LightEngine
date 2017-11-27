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

import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_DIFFUSE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_EMISSIVE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_SPECULAR;
import static org.lwjgl.assimp.Assimp.aiGetMaterialColor;
import static org.lwjgl.assimp.Assimp.aiGetMaterialTexture;
import static org.lwjgl.assimp.Assimp.aiGetMaterialTextureCount;
import static org.lwjgl.assimp.Assimp.aiReturn_SUCCESS;
import static org.lwjgl.assimp.Assimp.aiTextureType_AMBIENT;
import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;
import static org.lwjgl.assimp.Assimp.aiTextureType_NONE;
import static org.lwjgl.assimp.Assimp.aiTextureType_REFLECTION;
import static org.lwjgl.assimp.Assimp.aiTextureType_SPECULAR;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector4f;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIString;

import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

/**
 * Material
 * 
 */
public class Material implements IDisposable {

	private Vector4f diffuse, emissive;
	private float roughness, metallic;
	private Texture diffuseTexture, normalTexture, roughnessTexture, metallicTexture;

	/**
	 * 
	 * @param diffuse Diffuse color
	 * @param emissive Emissive Color
	 * @param roughness Roughness
	 * @param metallic Metallic
	 */
	public Material(Vector4f diffuse, Vector4f emissive, float roughness, float metallic) {
		this.diffuse = diffuse;
		this.emissive = emissive;
		this.roughness = roughness;
		this.metallic = metallic;
		this.diffuseTexture = DefaultData.diffuse;
		this.normalTexture = DefaultData.normal;
		this.roughnessTexture = DefaultData.roughness;
		this.metallicTexture = DefaultData.metallic;
	}

	/**
	 * 
	 * @param material Assimp Material
	 * @param rootPath internal
	 */
	public Material(AIMaterial material, String rootPath) {
		this.diffuse = new Vector4f(1, 1, 1, 1);
		this.emissive = new Vector4f(0, 0, 0, 0);
		this.roughness = 0.5f;
		this.metallic = 0;
		this.diffuseTexture = DefaultData.diffuse;
		this.normalTexture = DefaultData.normal;
		this.roughnessTexture = DefaultData.roughness;
		this.metallicTexture = DefaultData.metallic;

		AIColor4D diffuse = AIColor4D.create();
		AIColor4D emissive = AIColor4D.create();
		AIColor4D pbr = AIColor4D.create();
		if (aiGetMaterialColor(material, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, diffuse) == aiReturn_SUCCESS)
			this.diffuse.set(diffuse.r(), diffuse.g(), diffuse.b(), diffuse.a());
		if (aiGetMaterialColor(material, AI_MATKEY_COLOR_EMISSIVE, aiTextureType_NONE, 0, emissive) == aiReturn_SUCCESS)
			this.emissive.set(emissive.r(), emissive.g(), emissive.b(), emissive.a());
		if (aiGetMaterialColor(material, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, pbr) == aiReturn_SUCCESS) {
			this.roughness = pbr.r();
			this.metallic = pbr.g();
		}
		if (aiGetMaterialTextureCount(material, aiTextureType_DIFFUSE) > 0) {
			AIString path = AIString.create();
			if (aiGetMaterialTexture(material, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, (IntBuffer) null,
					(FloatBuffer) null, (IntBuffer) null, (IntBuffer) null, (IntBuffer) null) == aiReturn_SUCCESS) {
				TaskManager.addTaskAsync(() -> this.diffuseTexture = loadTexture(path, rootPath));
				this.diffuse.set(1, 1, 1, 1);
			}
		}
		if (aiGetMaterialTextureCount(material, aiTextureType_AMBIENT) > 0) {
			AIString path = AIString.create();
			if (aiGetMaterialTexture(material, aiTextureType_AMBIENT, 0, path, (IntBuffer) null, (IntBuffer) null,
					(FloatBuffer) null, (IntBuffer) null, (IntBuffer) null, (IntBuffer) null) == aiReturn_SUCCESS)
				TaskManager.addTaskAsync(() -> this.normalTexture = loadTextureMisc(path, rootPath));

		}
		if (aiGetMaterialTextureCount(material, aiTextureType_SPECULAR) > 0) {
			AIString path = AIString.create();
			if (aiGetMaterialTexture(material, aiTextureType_SPECULAR, 0, path, (IntBuffer) null, (IntBuffer) null,
					(FloatBuffer) null, (IntBuffer) null, (IntBuffer) null, (IntBuffer) null) == aiReturn_SUCCESS) {
				TaskManager.addTaskAsync(() -> this.roughnessTexture = loadTextureMisc(path, rootPath));
				this.roughness = 1f;
			}

		}
		if (aiGetMaterialTextureCount(material, aiTextureType_REFLECTION) > 0) {
			AIString path = AIString.create();
			if (aiGetMaterialTexture(material, aiTextureType_REFLECTION, 0, path, (IntBuffer) null, (IntBuffer) null,
					(FloatBuffer) null, (IntBuffer) null, (IntBuffer) null, (IntBuffer) null) == aiReturn_SUCCESS) {
				TaskManager.addTaskAsync(() -> this.metallicTexture = loadTextureMisc(path, rootPath));
				this.metallic = 1f;
			}

		}
	}

	public void setDiffuseTexture(Texture diffuseTexture) {
		this.diffuseTexture = diffuseTexture;
	}

	public void setNormalTexture(Texture normalTexture) {
		this.normalTexture = normalTexture;
	}

	public void setRoughnessTexture(Texture roughnessTexture) {
		this.roughnessTexture = roughnessTexture;
	}

	public void setMetallicTexture(Texture metallicTexture) {
		this.metallicTexture = metallicTexture;
	}

	public Vector4f getDiffuse() {
		return diffuse;
	}

	public Vector4f getEmissive() {
		return emissive;
	}

	public float getMetallic() {
		return metallic;
	}

	public float getRoughness() {
		return roughness;
	}

	public Texture getDiffuseTexture() {
		return diffuseTexture;
	}

	public Texture getMetallicTexture() {
		return metallicTexture;
	}

	public Texture getNormalTexture() {
		return normalTexture;
	}

	public Texture getRoughnessTexture() {
		return roughnessTexture;
	}

	@Override
	public void dispose() {
		if (diffuseTexture != null)
			diffuseTexture.dispose();
		if (metallicTexture != null)
			metallicTexture.dispose();
		if (normalTexture != null)
			normalTexture.dispose();
		if (roughnessTexture != null)
			roughnessTexture.dispose();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Material))
			return false;
		Material t = (Material) obj;
		if (t.getRoughness() != roughness)
			return false;
		if (t.getMetallic() != metallic)
			return false;
		if (!t.getDiffuseTexture().equals(diffuseTexture))
			return false;
		if (!t.getNormalTexture().equals(normalTexture))
			return false;
		if (!t.getRoughnessTexture().equals(roughnessTexture))
			return false;
		if (!t.getMetallicTexture().equals(metallicTexture))
			return false;
		if (!t.getDiffuse().equals(diffuse))
			return false;
		if (!t.getEmissive().equals(emissive))
			return false;
		return true;
	}

	private static Texture loadTexture(AIString path, String rootPath) {
		String file = path.dataString();
		file = file.replace("\\", "/");
		file = file.replace("//", "");
		int count = file.split("\\.").length;
		if (count > 2) {
			count--;
			count /= 2;
			for (int i = 0; i < count; i++)
				rootPath = rootPath.substring(0, rootPath.lastIndexOf("/"));
			file = file.substring(2);
		} else
			rootPath += "/";

		return CachedAssets.loadTexture(rootPath + file);
	}

	private static Texture loadTextureMisc(AIString path, String rootPath) {
		String file = path.dataString();
		file = file.replace("\\", "/");
		file = file.replace("//", "");
		int count = file.split("\\.").length;
		if (count > 2) {
			count--;
			count /= 2;
			for (int i = 0; i < count; i++)
				rootPath = rootPath.substring(0, rootPath.lastIndexOf("/"));
			file = file.substring(2);
		} else
			rootPath += "/";
		return CachedAssets.loadTextureMisc(rootPath + file);
	}

}
