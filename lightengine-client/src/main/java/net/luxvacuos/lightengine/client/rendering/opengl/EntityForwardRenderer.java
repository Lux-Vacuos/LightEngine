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

import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE10;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE7;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE8;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE9;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13C.glActiveTexture;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.network.IRenderingData;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Material;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Material.MaterialType;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Mesh;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.EntityFowardShader;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.ecs.entities.BasicEntity;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public class EntityForwardRenderer implements IDisposable {

	private EntityFowardShader shader;

	public EntityForwardRenderer() {
		shader = new EntityFowardShader();
	}

	public void render(Map<Material, List<EntityRendererObject>> entities, IRenderingData rd, RendererData rnd,
			CameraEntity cubeCamera, boolean colorCorrect, MaterialType materialType) {
		shader.start();
		if (cubeCamera == null) // TODO: Improve
			shader.loadCamera(rd.getCamera());
		else
			shader.loadCamera(cubeCamera);
		shader.loadLightPosition(rd.getSun().getSunPosition());
		shader.colorCorrect(colorCorrect);
		shader.loadSettings(GraphicalSubsystem.getRenderingSettings().shadowsEnabled);
		shader.loadBiasMatrix(rd.getSun().getCamera().getProjectionArray());
		shader.loadLightMatrix(rd.getSun().getCamera().getViewMatrix());
		glActiveTexture(GL_TEXTURE4);
		glBindTexture(GL_TEXTURE_CUBE_MAP, rnd.irradianceCapture.getTexture());
		glActiveTexture(GL_TEXTURE5);
		glBindTexture(GL_TEXTURE_CUBE_MAP, rnd.environmentMap.getTexture());
		glActiveTexture(GL_TEXTURE6);
		glBindTexture(GL_TEXTURE_2D, rnd.brdfLUT.getTexture());
		glActiveTexture(GL_TEXTURE7);
		glBindTexture(GL_TEXTURE_2D, rnd.dlsm.getShadowMaps()[0].getTexture());
		glActiveTexture(GL_TEXTURE8);
		glBindTexture(GL_TEXTURE_2D, rnd.dlsm.getShadowMaps()[1].getTexture());
		glActiveTexture(GL_TEXTURE9);
		glBindTexture(GL_TEXTURE_2D, rnd.dlsm.getShadowMaps()[2].getTexture());
		glActiveTexture(GL_TEXTURE10);
		glBindTexture(GL_TEXTURE_2D, rnd.dlsm.getShadowMaps()[3].getTexture());
		renderEntity(entities, materialType);
		shader.stop();
	}

	private void renderEntity(Map<Material, List<EntityRendererObject>> entities, MaterialType materialType) {
		for (Material mat : entities.keySet()) {
			if (mat.getType() != materialType)
				continue;
			List<EntityRendererObject> batch = entities.get(mat);
			for (EntityRendererObject obj : batch) {
				prepareInstance(obj.entity);
				prepareTexturedModel(obj.mesh, mat);
				shader.loadMaterial(mat);
				glDrawElements(GL_TRIANGLES, obj.mesh.getMesh().getIndexCount(), GL_UNSIGNED_INT, 0);
				unbindTexturedModel(obj.mesh);
			}
		}
	}

	private void prepareTexturedModel(Mesh mesh, Material material) {
		mesh.getMesh().bind(0, 1, 2, 3);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, material.getDiffuseTexture().getTexture());
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, material.getNormalTexture().getTexture());
		glActiveTexture(GL_TEXTURE2);
		glBindTexture(GL_TEXTURE_2D, material.getRoughnessTexture().getTexture());
		glActiveTexture(GL_TEXTURE3);
		glBindTexture(GL_TEXTURE_2D, material.getMetallicTexture().getTexture());
	}

	private void unbindTexturedModel(Mesh mesh) {
		mesh.getMesh().unbind(0, 1, 2, 3);
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
