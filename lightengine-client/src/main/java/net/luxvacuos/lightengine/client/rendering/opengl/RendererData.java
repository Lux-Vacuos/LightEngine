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

import net.luxvacuos.lightengine.client.rendering.opengl.objects.Texture;
import net.luxvacuos.lightengine.client.rendering.opengl.v2.lights.DirectionalLightShadowMap;
import net.luxvacuos.lightengine.client.rendering.opengl.v2.lights.Light;

public class RendererData {

	public List<Light> lights;
	public Texture irradianceCapture, environmentMap;
	public Texture brdfLUT;
	public DirectionalLightShadowMap dlsm;
	public float exposure;
	public Matrix4f previousViewMatrix = new Matrix4f();
	public Vector3f previousCameraPosition = new Vector3f();

}
