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

package net.luxvacuos.lightengine.client.rendering.opengl.pipeline;

import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE5;

import net.luxvacuos.lightengine.client.network.IRenderingData;
import net.luxvacuos.lightengine.client.rendering.opengl.RendererData;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Texture;
import net.luxvacuos.lightengine.client.rendering.opengl.pipeline.shaders.VolumetricLightShader;
import net.luxvacuos.lightengine.client.rendering.opengl.v2.DeferredPass;
import net.luxvacuos.lightengine.client.rendering.opengl.v2.DeferredPipeline;

public class VolumetricLight extends DeferredPass<VolumetricLightShader> {

	public VolumetricLight(float scaling) {
		super("VolumetricLight", scaling);
	}

	@Override
	protected VolumetricLightShader setupShader() {
		return new VolumetricLightShader(name);
	}

	@Override
	protected void setupShaderData(RendererData rnd, IRenderingData rd, VolumetricLightShader shader) {
		shader.loadLightPosition(rd.getSun().getSunPosition());
		shader.loadCameraData(rd.getCamera());
		shader.loadSunCameraData(rd.getSun().getCamera());
		shader.loadTime(rd.getWorldSimulation().getGlobalTime());
	}

	@Override
	protected void setupTextures(RendererData rnd, DeferredPipeline dp, Texture[] auxTex) {
		super.activateTexture(GL_TEXTURE0, GL_TEXTURE_2D, dp.getPositionTex().getTexture());
		super.activateTexture(GL_TEXTURE1, GL_TEXTURE_2D, dp.getNormalTex().getTexture());
		super.activateTexture(GL_TEXTURE2, GL_TEXTURE_2D, rnd.dlsm.getShadowMaps()[0].getTexture());
		super.activateTexture(GL_TEXTURE3, GL_TEXTURE_2D, rnd.dlsm.getShadowMaps()[1].getTexture());
		super.activateTexture(GL_TEXTURE4, GL_TEXTURE_2D, rnd.dlsm.getShadowMaps()[2].getTexture());
		super.activateTexture(GL_TEXTURE5, GL_TEXTURE_2D, rnd.dlsm.getShadowMaps()[3].getTexture());
	}

}
