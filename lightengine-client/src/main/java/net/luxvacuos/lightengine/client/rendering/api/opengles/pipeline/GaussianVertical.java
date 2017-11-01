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

package net.luxvacuos.lightengine.client.rendering.api.opengles.pipeline;

import static org.lwjgl.opengles.GLES20.GL_TEXTURE6;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE_2D;
import static org.lwjgl.opengles.GLES20.glActiveTexture;
import static org.lwjgl.opengles.GLES20.glBindTexture;

import net.luxvacuos.lightengine.client.rendering.api.opengles.DeferredPass;
import net.luxvacuos.lightengine.client.rendering.api.opengles.FBO;
import net.luxvacuos.lightengine.client.rendering.api.opengles.IDeferredPipeline;
import net.luxvacuos.lightengine.client.rendering.api.opengles.ShadowFBO;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.CubeMapTexture;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.Texture;

public class GaussianVertical extends DeferredPass {

	public GaussianVertical(String name, int width, int height) {
		super(name, width, height);
	}

	@Override
	public void render(FBO[] auxs, IDeferredPipeline pipe, CubeMapTexture irradianceCapture,
			CubeMapTexture environmentMap, Texture brdfLUT, ShadowFBO shadow) {
		glActiveTexture(GL_TEXTURE6);
		glBindTexture(GL_TEXTURE_2D, auxs[0].getTexture());
	}

}
