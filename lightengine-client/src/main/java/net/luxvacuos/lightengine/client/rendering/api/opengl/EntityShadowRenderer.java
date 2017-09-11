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

package net.luxvacuos.lightengine.client.rendering.api.opengl;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;

import net.luxvacuos.igl.vector.Matrix4d;
import net.luxvacuos.lightengine.client.ecs.ClientComponents;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Material;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Mesh;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Model;
import net.luxvacuos.lightengine.client.rendering.api.opengl.shaders.EntityBasicShader;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.ecs.components.Position;
import net.luxvacuos.lightengine.universal.ecs.components.Rotation;
import net.luxvacuos.lightengine.universal.ecs.components.Scale;
import net.luxvacuos.lightengine.universal.ecs.entities.BasicEntity;

public class EntityShadowRenderer {

	private EntityBasicShader shader;

	private Map<Model, List<BasicEntity>> entities = new HashMap<Model, List<BasicEntity>>();

	public EntityShadowRenderer() {
		shader = new EntityBasicShader();
	}

	public void cleanUp() {
		shader.dispose();
	}

	public void renderEntity(ImmutableArray<Entity> immutableArray, CameraEntity sunCamera) {
		for (Entity entity : immutableArray) {
			if (ClientComponents.RENDERABLE.has(entity))
				if (ClientComponents.RENDERABLE.get(entity).isLoaded())
					processEntity((BasicEntity) entity);
		}
		renderEntity(sunCamera);
	}

	private void renderEntity(CameraEntity sunCamera) {
		glCullFace(GL_FRONT);
		shader.start();
		shader.loadviewMatrix(sunCamera);
		shader.loadProjectionMatrix(sunCamera.getProjectionMatrix());
		renderEntity(entities);
		shader.stop();
		entities.clear();
		glCullFace(GL_BACK);
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

	private void renderEntity(Map<Model, List<BasicEntity>> blockEntities) {
		for (Model model : blockEntities.keySet()) {
			List<BasicEntity> batch = blockEntities.get(model);
			for (BasicEntity entity : batch) {
				prepareInstance(entity);
				for (Mesh mesh : model.getMeshes()) {
					Material mat = model.getMaterials().get(mesh.getAiMesh().mMaterialIndex());
					prepareTexturedModel(mesh, mat);
					glDrawElements(GL_TRIANGLES, mesh.getMesh().getIndexCount(), GL_UNSIGNED_INT, 0);
					unbindTexturedModel(mesh);
				}
			}
		}
	}

	private void prepareTexturedModel(Mesh mesh, Material material) {
		mesh.getMesh().bind(0, 1);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, material.getDiffuseTexture().getID());
	}

	private void unbindTexturedModel(Mesh mesh) {
		mesh.getMesh().unbind(0, 1);
	}

	private void prepareInstance(BasicEntity entity) {
		Position pos = Components.POSITION.get(entity);
		Rotation rot = Components.ROTATION.get(entity);
		Scale scale = Components.SCALE.get(entity);
		Matrix4d transformationMatrix = Maths.createTransformationMatrix(pos.getPosition(), rot.getX(), rot.getY(),
				rot.getZ(), scale.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}

}
