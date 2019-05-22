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

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.esotericsoftware.kryonet.util.ObjectIntMap;

import net.luxvacuos.lightengine.client.ecs.ClientComponents;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.network.IRenderingData;
import net.luxvacuos.lightengine.universal.ecs.entities.BasicEntity;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public class RenderingManager implements IDisposable {

	private final IntMap<IObjectRenderer> objectRenderers = new IntMap<>();
	private final ObjectIntMap<List<BasicEntity>> entitiesToRenderers = new ObjectIntMap<>();

	public RenderingManager() {
	}

	public void addRenderer(IObjectRenderer objectRenderer) {
		objectRenderers.put(objectRenderer.getID(), objectRenderer);
	}

	public void preProcess(ImmutableArray<Entity> entities) {
		for (Entity entity : entities)
			if (ClientComponents.RENDERABLE.get(entity).isLoaded())
				process((BasicEntity) entity);
		for (Entry<IObjectRenderer> rendererEntry : objectRenderers) {
			IObjectRenderer objectRenderer = rendererEntry.value;
			List<BasicEntity> batch = entitiesToRenderers.findKey(objectRenderer.getID());
			if (batch != null)
				objectRenderer.preProcess(batch);
		}
	}

	public void render(CameraEntity camera) {
		for (Entry<IObjectRenderer> rendererEntry : objectRenderers)
			rendererEntry.value.render(camera);
	}

	public void renderReflections(IRenderingData rd, RendererData rnd, CameraEntity cubeCamera) {
		for (Entry<IObjectRenderer> rendererEntry : objectRenderers)
			rendererEntry.value.renderReflections(rd, rnd, cubeCamera);
	}

	public void renderForward(IRenderingData rd, RendererData rnd) {
		glEnable(GL_BLEND);
		for (Entry<IObjectRenderer> rendererEntry : objectRenderers)
			rendererEntry.value.renderForward(rd, rnd);
		glDisable(GL_BLEND);
	}

	public void renderShadow(CameraEntity camera) {
		for (Entry<IObjectRenderer> rendererEntry : objectRenderers)
			rendererEntry.value.renderShadow(camera);
	}

	public void end() {
		for (Entry<IObjectRenderer> rendererEntry : objectRenderers) {
			IObjectRenderer objectRenderer = rendererEntry.value;
			List<BasicEntity> batch = entitiesToRenderers.findKey(objectRenderer.getID());
			if (batch != null)
				batch.clear();
			objectRenderer.end();
		}
	}

	private void process(BasicEntity entity) {
		int id = ClientComponents.RENDERABLE.get(entity).getRendererID();
		List<BasicEntity> batch = entitiesToRenderers.findKey(id);
		if (batch != null)
			batch.add(entity);
		else {
			List<BasicEntity> newBatch = new ArrayList<BasicEntity>();
			newBatch.add(entity);
			entitiesToRenderers.put(newBatch, id);
		}
	}

	@Override
	public void dispose() {
		for (Entry<IObjectRenderer> rendererEntry : objectRenderers)
			rendererEntry.value.dispose();
	}

}
