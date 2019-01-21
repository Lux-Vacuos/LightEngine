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

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.ecs.entities.Sun;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.CubeMapTexture;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Light;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.RawModel;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Texture;
import net.luxvacuos.lightengine.universal.core.IWorldSimulation;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public interface IDeferredPass extends IDisposable {

	public void init();

	public void process(CameraEntity camera, Sun sun, Matrix4f previousViewMatrix, Vector3f previousCameraPosition,
			IWorldSimulation clientWorldSimulation, List<Light> lights, FBO[] auxs, IDeferredPipeline pipe,
			RawModel quad, CubeMapTexture irradianceCapture, CubeMapTexture environmentMap, Texture brdfLUT,
			ShadowFBO shadowFBO, float exposure);

	public void render(FBO[] auxs, IDeferredPipeline pipe, CubeMapTexture irradianceCapture,
			CubeMapTexture environmentMap, Texture brdfLUT, ShadowFBO shadow);
	
	public void resize(int width, int height);
}
