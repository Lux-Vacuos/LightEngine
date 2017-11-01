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
import static org.lwjgl.opengles.GLES20.GL_TEXTURE_2D;
import static org.lwjgl.opengles.GLES20.GL_TRIANGLES;
import static org.lwjgl.opengles.GLES20.GL_UNSIGNED_INT;
import static org.lwjgl.opengles.GLES20.glActiveTexture;
import static org.lwjgl.opengles.GLES20.glBindTexture;
import static org.lwjgl.opengles.GLES20.glDrawElements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.badlogic.ashley.core.Entity;

import net.luxvacuos.lightengine.client.ecs.ClientComponents;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.CubeMapTexture;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.Material;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.Mesh;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.Model;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.Texture;
import net.luxvacuos.lightengine.client.rendering.api.opengles.shaders.EntityDeferredShader;
import net.luxvacuos.lightengine.client.resources.ResourceLoader;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.ecs.components.Position;
import net.luxvacuos.lightengine.universal.ecs.components.Rotation;
import net.luxvacuos.lightengine.universal.ecs.components.Scale;
import net.luxvacuos.lightengine.universal.ecs.entities.BasicEntity;

public class EntityRenderer implements IRenderer {

	public static final int ENTITY_RENDERER_ID = 10;
	/**
	 * Entity Shader
	 */
	private EntityDeferredShader shader;
	private Map<Model, List<BasicEntity>> entities = new HashMap<>();
	private EntityShadowRenderer shadowRenderer;
	private EntityForwardRenderer forwardRenderer;

	public EntityRenderer(ResourceLoader loader) {
		shader = new EntityDeferredShader();
		shadowRenderer = new EntityShadowRenderer();
		forwardRenderer = new EntityForwardRenderer();
	}

	@Override
	public void preProcess(List<BasicEntity> entities) {
		for (Entity entity : entities) {
			processEntity((BasicEntity) entity);
		}
	}

	@Override
	public void render(CameraEntity camera) {
		shader.start();
		shader.loadCamera(camera);
		renderEntity(entities);
		shader.stop();
	}

	@Override
	public void renderReflections(CameraEntity camera, Vector3f lightPosition, CubeMapTexture irradiance,
			CubeMapTexture environmentMap, Texture brdfLUT) {
		forwardRenderer.render(entities, camera, lightPosition, irradiance, environmentMap, brdfLUT, false);
	}

	@Override
	public void renderForward(CameraEntity camera, Vector3f lightPosition, CubeMapTexture irradiance,
			CubeMapTexture environmentMap, Texture brdfLUT) {
	}

	@Override
	public void renderShadow(CameraEntity sun) {
		shadowRenderer.renderShadow(entities, sun);
	}

	@Override
	public void end() {
		entities.clear();
	}

	private void processEntity(BasicEntity entity) {
		Model entityModel = ClientComponents.RENDERABLE.get(entity).getModel();
		List<BasicEntity> batch = entities.get(entityModel);
		if (batch != null)
			batch.add(entity);
		else {
			List<BasicEntity> newBatch = new ArrayList<BasicEntity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
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
		shadowRenderer.dispose();
		forwardRenderer.dispose();
	}

	@Override
	public int getID() {
		return ENTITY_RENDERER_ID;
	}

}
