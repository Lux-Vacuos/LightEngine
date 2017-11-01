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

package net.luxvacuos.lightengine.client.rendering.api.opengles;

import static org.lwjgl.opengles.GLES20.GL_TEXTURE0;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE1;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE2;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE3;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE7;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE8;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE9;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE_2D;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengles.GLES20.GL_TRIANGLES;
import static org.lwjgl.opengles.GLES20.GL_UNSIGNED_INT;
import static org.lwjgl.opengles.GLES20.glActiveTexture;
import static org.lwjgl.opengles.GLES20.glBindTexture;
import static org.lwjgl.opengles.GLES20.glDrawElements;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.CubeMapTexture;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.Material;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.Mesh;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.Model;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.Texture;
import net.luxvacuos.lightengine.client.rendering.api.opengles.shaders.EntityFowardShader;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.ecs.components.Position;
import net.luxvacuos.lightengine.universal.ecs.components.Rotation;
import net.luxvacuos.lightengine.universal.ecs.components.Scale;
import net.luxvacuos.lightengine.universal.ecs.entities.BasicEntity;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public class EntityForwardRenderer implements IDisposable {

	private EntityFowardShader shader;

	public EntityForwardRenderer() {
		shader = new EntityFowardShader();
	}

	public void render(Map<Model, List<BasicEntity>> entities, CameraEntity camera, Vector3f lightPosition,
			CubeMapTexture irradiance, CubeMapTexture environmentMap, Texture brdfLUT, boolean colorCorrect) {
		shader.start();
		shader.loadCamera(camera);
		shader.loadLightPosition(lightPosition);
		shader.colorCorrect(colorCorrect);
		glActiveTexture(GL_TEXTURE7);
		glBindTexture(GL_TEXTURE_CUBE_MAP, irradiance.getID());
		glActiveTexture(GL_TEXTURE8);
		glBindTexture(GL_TEXTURE_CUBE_MAP, environmentMap.getID());
		glActiveTexture(GL_TEXTURE9);
		glBindTexture(GL_TEXTURE_2D, brdfLUT.getID());
		renderEntity(entities);
		shader.stop();
	}

	private void renderEntity(Map<Model, List<BasicEntity>> entities) {
		for (Model model : entities.keySet()) {
			List<BasicEntity> batch = entities.get(model);
			for (BasicEntity entity : batch) {
				prepareInstance(entity);
				for (Mesh mesh : model.getMeshes()) {
					Material mat = model.getMaterials().get(mesh.getAiMesh().mMaterialIndex());
					prepareTexturedModel(mesh, mat);
					shader.loadMaterial(mat);
					glDrawElements(GL_TRIANGLES, mesh.getMesh().getIndexCount(), GL_UNSIGNED_INT, 0);
					unbindTexturedModel(mesh);
				}
			}
		}

	}

	private void prepareTexturedModel(Mesh mesh, Material material) {
		mesh.getMesh().bind(0, 1, 2, 3);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, material.getDiffuseTexture().getID());
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, material.getNormalTexture().getID());
		glActiveTexture(GL_TEXTURE2);
		glBindTexture(GL_TEXTURE_2D, material.getRoughnessTexture().getID());
		glActiveTexture(GL_TEXTURE3);
		glBindTexture(GL_TEXTURE_2D, material.getMetallicTexture().getID());
	}

	private void unbindTexturedModel(Mesh mesh) {
		mesh.getMesh().unbind(0, 1, 2, 3);
	}

	private void prepareInstance(BasicEntity entity) {
		Position pos = Components.POSITION.get(entity);
		Rotation rot = Components.ROTATION.get(entity);
		Scale scale = Components.SCALE.get(entity);
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(pos.getPosition(), rot.getX(), rot.getY(),
				rot.getZ(), scale.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}

	@Override
	public void dispose() {
		shader.dispose();
	}

}
