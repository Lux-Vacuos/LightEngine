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
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;

import net.luxvacuos.lightengine.client.ecs.components.WaterTileComp;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.ecs.entities.WaterTileEnt;
import net.luxvacuos.lightengine.client.rendering.IResourceLoader;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.CachedAssets;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.RawModel;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Texture;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.WaterShader;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public class WaterRenderer implements IDisposable {

	private RawModel quad;
	private WaterShader shader;
	private Texture dudv, foamMask;;

	public WaterRenderer(IResourceLoader loader) {
		shader = new WaterShader();
		quad = loader.loadObjModel("water");
		dudv = CachedAssets.loadTextureMisc("textures/waterDUDV.png");
		foamMask = CachedAssets.loadTextureMisc("textures/foamMask.png");
	}

	public void render(ImmutableArray<Entity> water, CameraEntity camera, float time, Frustum frustum) {
		prepareRender(camera, time);
		for (Entity ent : water) {
			WaterTileComp tile = ent.getComponent(WaterTileComp.class);
			// float halfTileSize = WaterTileEnt.TILE_SIZE * 2;
			// if (!frustum.cubeInFrustum(tile.getPosition().x() + halfTileSize,
			// tile.getPosition().y(),
			// tile.getPosition().z() - halfTileSize, halfTileSize))
			// continue;
			shader.loadTransformationMatrix(
					Maths.createTransformationMatrix(tile.getPosition(), 0, 0, 0, WaterTileEnt.TILE_SIZE));
			glDrawElements(GL_TRIANGLES, quad.getVertexCount(), GL_UNSIGNED_INT, 0);
		}
		unbind();
	}

	private void prepareRender(CameraEntity camera, float time) {
		shader.start();
		shader.loadViewMatrix(camera);
		shader.loadProjectionMatrix(camera.getProjectionMatrix());
		shader.loadCameraPosition(camera.getPosition());
		shader.loadTime(time);
		glBindVertexArray(quad.getVaoID());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glActiveTexture(GL_TEXTURE2);
		glBindTexture(GL_TEXTURE_2D, dudv.getID());
		glActiveTexture(GL_TEXTURE4);
		glBindTexture(GL_TEXTURE_2D, foamMask.getID());
	}

	private void unbind() {
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
		shader.stop();
	}

	@Override
	public void dispose() {
		shader.dispose();
		dudv.dispose();
		foamMask.dispose();
	}

}
