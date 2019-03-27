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

package net.luxvacuos.lightengine.client.rendering.opengl.pipeline.shaders;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.ecs.entities.SunCamera;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformMatrix;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformSampler;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformVec3;

public class VolumetricLightShader extends BasePipelineShader {

	private UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	private UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");

	private UniformVec3 cameraPosition = new UniformVec3("cameraPosition");
	private UniformVec3 lightPosition = new UniformVec3("lightPosition");

	private UniformSampler gPosition = new UniformSampler("gPosition");
	private UniformSampler gNormal = new UniformSampler("gNormal");

	private UniformMatrix projectionLightMatrix[];
	private UniformMatrix viewLightMatrix = new UniformMatrix("viewLightMatrix");
	private UniformMatrix biasMatrix = new UniformMatrix("biasMatrix");
	private UniformSampler shadowMap[];

	public VolumetricLightShader(String name) {
		super("DFR_" + name);
		projectionLightMatrix = new UniformMatrix[4];
		for (int x = 0; x < 4; x++)
			projectionLightMatrix[x] = new UniformMatrix("projectionLightMatrix[" + x + "]");
		super.storeUniforms(projectionLightMatrix);
		shadowMap = new UniformSampler[4];
		for (int x = 0; x < 4; x++)
			shadowMap[x] = new UniformSampler("shadowMap[" + x + "]");
		super.storeUniforms(shadowMap);
		super.storeUniforms(projectionMatrix, viewMatrix, cameraPosition, lightPosition, gPosition, gNormal, biasMatrix,
				viewLightMatrix);
		super.validate();
		this.loadInitialData();
	}

	@Override
	protected void loadInitialData() {
		super.start();
		gPosition.loadTexUnit(0);
		gNormal.loadTexUnit(1);
		shadowMap[0].loadTexUnit(2);
		shadowMap[1].loadTexUnit(3);
		shadowMap[2].loadTexUnit(4);
		shadowMap[3].loadTexUnit(5);
		Matrix4f biasM = new Matrix4f();
		biasM.m00(0.5f);
		biasM.m11(0.5f);
		biasM.m22(0.5f);
		biasM.m30(0.5f);
		biasM.m31(0.5f);
		biasM.m32(0.5f);
		biasMatrix.loadMatrix(biasM);
		super.stop();
	}

	public void loadLightPosition(Vector3f pos) {
		lightPosition.loadVec3(pos);
	}

	public void loadSunCameraData(SunCamera camera) {
		for (int x = 0; x < 4; x++)
			this.projectionLightMatrix[x].loadMatrix(camera.getProjectionArray()[x]);
		viewLightMatrix.loadMatrix(camera.getViewMatrix());
	}

	public void loadCameraData(CameraEntity camera) {
		this.projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		this.viewMatrix.loadMatrix(camera.getViewMatrix());
		this.cameraPosition.loadVec3(camera.getPosition());
	}

}
