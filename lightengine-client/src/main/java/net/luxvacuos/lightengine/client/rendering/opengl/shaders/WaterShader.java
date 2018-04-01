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

package net.luxvacuos.lightengine.client.rendering.opengl.shaders;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.shaders.ShaderProgram;
import net.luxvacuos.lightengine.client.rendering.shaders.data.Attribute;
import net.luxvacuos.lightengine.client.rendering.shaders.data.UniformFloat;
import net.luxvacuos.lightengine.client.rendering.shaders.data.UniformMatrix;
import net.luxvacuos.lightengine.client.rendering.shaders.data.UniformSampler;
import net.luxvacuos.lightengine.client.rendering.shaders.data.UniformVec3;

public class WaterShader extends ShaderProgram {

	private UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
	private UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	private UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	private UniformSampler dudv = new UniformSampler("dudv");
	private UniformSampler foamMask = new UniformSampler("foamMask");
	private UniformVec3 cameraPosition = new UniformVec3("cameraPosition");
	private UniformFloat time = new UniformFloat("time");

	public WaterShader() {
		super(ClientVariables.VERTEX_WATER, ClientVariables.FRAGMENT_WATER, ClientVariables.GEOMETRY_WATER,
				new Attribute(0, "position"), new Attribute(1, "textureCoords"));
		super.storeAllUniformLocations(transformationMatrix, projectionMatrix, viewMatrix,
				cameraPosition, time, dudv, foamMask);
		connectTextureUnits();
	}

	private void connectTextureUnits() {
		super.start();
		dudv.loadTexUnit(2);
		foamMask.loadTexUnit(4);
		super.stop();
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		transformationMatrix.loadMatrix(matrix);
	}

	public void loadViewMatrix(CameraEntity camera) {
		viewMatrix.loadMatrix(camera.getViewMatrix());
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		projectionMatrix.loadMatrix(projection);
	}

	public void loadCameraPosition(Vector3f cameraPosition) {
		this.cameraPosition.loadVec3(cameraPosition);
	}

	public void loadTime(float time) {
		this.time.loadFloat(time);
	}

}
