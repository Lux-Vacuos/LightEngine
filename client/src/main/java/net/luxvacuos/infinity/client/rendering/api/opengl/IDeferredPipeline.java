/*
 * This file is part of Infinity
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

package net.luxvacuos.infinity.client.rendering.api.opengl;

import java.util.List;

import net.luxvacuos.igl.vector.Vector3d;
import net.luxvacuos.infinity.client.rendering.api.opengl.objects.CubeMapTexture;
import net.luxvacuos.infinity.client.rendering.api.opengl.objects.Light;
import net.luxvacuos.infinity.client.world.entities.Camera;
import net.luxvacuos.infinity.universal.core.IWorldSimulation;
import net.luxvacuos.infinity.universal.resources.IDisposable;

public interface IDeferredPipeline extends IDisposable {

	public void init();

	public void begin();

	public void end();

	public void preRender(Camera camera, Vector3d lightPosition, Vector3d invertedLightPosition,
			IWorldSimulation clientWorldSimulation, List<Light> lights, CubeMapTexture environmentMap, float exposure);
	
	public void render(FBO postProcess);
	
	public RenderingPipelineFBO getMainFBO();

}
