/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2018 Lux Vacuos
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

import java.util.List;

import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.ecs.entities.Sun;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.CubeMapTexture;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Light;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Texture;
import net.luxvacuos.lightengine.universal.core.IWorldSimulation;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public interface IDeferredPipeline extends IDisposable {

	public void init();

	public void begin();

	public void end();

	public void preRender(CameraEntity camera, Sun sun, IWorldSimulation clientWorldSimulation, List<Light> lights,
			CubeMapTexture irradianceCapture, CubeMapTexture environmentMap, Texture brdfLUT, ShadowFBO shadowFBO,
			float exposure);

	public void render(FBO postProcess);
	
	public void resize(int width, int height);

	public RenderingPipelineFBO getMainFBO();
	
	public int getLastTexture();

}
