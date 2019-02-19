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
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE7;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE8;
import static org.lwjgl.opengl.GL13C.glActiveTexture;

import net.luxvacuos.lightengine.client.network.IRenderingData;
import net.luxvacuos.lightengine.client.rendering.opengl.RendererData;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Texture;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.DeferredPipelineShader;
import net.luxvacuos.lightengine.client.rendering.opengl.v2.DeferredPass;
import net.luxvacuos.lightengine.client.rendering.opengl.v2.DeferredPipeline;
import net.luxvacuos.lightengine.client.resources.ResourcesManager;

public class LensFlares extends DeferredPass<DeferredPipelineShader> {

	private Texture lensColor;

	public LensFlares() {
		super("LensFlares");
	}

	@Override
	public void init(int width, int height) {
		super.init(width, height);
		lensColor = ResourcesManager.loadTextureMisc("textures/lens/lens_color.png", null).get();
	}

	@Override
	protected DeferredPipelineShader setupShader() {
		return new DeferredPipelineShader(name);
	}

	@Override
	protected void setupShaderData(RendererData rnd, IRenderingData rd, DeferredPipelineShader shader) {
		shader.loadLightPosition(rd.getSun().getSunPosition(), rd.getSun().getInvertedSunPosition());
		shader.loadCameraData(rd.getCamera(), null, null);// TODO: Use previous data
		shader.loadExposure(rnd.exposure);
		shader.loadTime(rd.getWorldSimulation().getGlobalTime());
		shader.loadSunCameraData(rd.getSun().getCamera());
	}

	@Override
	protected void setupTextures(RendererData rnd, DeferredPipeline dp, Texture[] auxTex) {
		glActiveTexture(GL_TEXTURE6);
		glBindTexture(GL_TEXTURE_2D, auxTex[0].getTexture());
		glActiveTexture(GL_TEXTURE7);
		glBindTexture(GL_TEXTURE_2D, auxTex[1].getTexture());
		glActiveTexture(GL_TEXTURE8);
		glBindTexture(GL_TEXTURE_2D, lensColor.getTexture());
		auxTex[1] = auxTex[0];
	}

	@Override
	public void dispose() {
		super.dispose();
		lensColor.dispose();
	}

}