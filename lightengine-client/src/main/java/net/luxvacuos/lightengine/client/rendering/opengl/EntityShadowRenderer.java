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

package net.luxvacuos.lightengine.client.rendering.opengl;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;

import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Material;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Mesh;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Material.MaterialType;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.EntityBasicShader;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.ecs.entities.BasicEntity;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public class EntityShadowRenderer implements IDisposable {

	private EntityBasicShader shader;

	public EntityShadowRenderer() {
		shader = new EntityBasicShader();
	}

	protected void renderShadow(Map<Material, List<EntityRendererObject>> entities, CameraEntity sunCamera) {
		shader.start();
		shader.loadviewMatrix(sunCamera);
		shader.loadProjectionMatrix(sunCamera.getProjectionMatrix());
		renderEntity(entities);
		shader.stop();
	}

	private void renderEntity(Map<Material, List<EntityRendererObject>> entities) {
		for (Material mat : entities.keySet()) {
			if (mat.getType() != MaterialType.OPAQUE)
				continue;
			List<EntityRendererObject> batch = entities.get(mat);
			for (EntityRendererObject obj : batch) {
				prepareInstance(obj.entity);
				prepareTexturedModel(obj.mesh, mat);
				glDrawElements(GL_TRIANGLES, obj.mesh.getMesh().getIndexCount(), GL_UNSIGNED_INT, 0);
				unbindTexturedModel(obj.mesh);
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
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRX(),
				entity.getRY(), entity.getRZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}

	@Override
	public void dispose() {
		shader.dispose();
	}

}
