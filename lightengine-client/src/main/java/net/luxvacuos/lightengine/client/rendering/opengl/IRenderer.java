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

package net.luxvacuos.lightengine.client.rendering.opengl;

import java.util.List;

import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.ecs.entities.Sun;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.CubeMapTexture;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Texture;
import net.luxvacuos.lightengine.universal.ecs.entities.BasicEntity;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public interface IRenderer extends IDisposable {

	public void preProcess(List<BasicEntity> entities);

	public void render(CameraEntity camera);

	public void renderReflections(CameraEntity camera, Sun sun, ShadowFBO shadow, CubeMapTexture irradiance,
			CubeMapTexture environmentMap, Texture brdfLUT);

	public void renderForward(CameraEntity camera, Sun sun, ShadowFBO shadow, CubeMapTexture irradiance,
			CubeMapTexture environmentMap, Texture brdfLUT);

	public void renderShadow(CameraEntity sun);

	public void end();

	public int getID();

}
