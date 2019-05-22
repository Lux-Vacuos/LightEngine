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

public class LightingShader extends BasePipelineShader {

	private UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	private UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	private UniformMatrix inverseProjectionMatrix = new UniformMatrix("inverseProjectionMatrix");
	private UniformMatrix inverseViewMatrix = new UniformMatrix("inverseViewMatrix");

	private UniformVec3 cameraPosition = new UniformVec3("cameraPosition");
	private UniformVec3 lightPosition = new UniformVec3("lightPosition");
	private UniformVec3 invertedLightPosition = new UniformVec3("invertedLightPosition");

	private UniformSampler gDiffuse = new UniformSampler("gDiffuse");
	private UniformSampler gPosition = new UniformSampler("gPosition");
	private UniformSampler gNormal = new UniformSampler("gNormal");
	private UniformSampler gDepth = new UniformSampler("gDepth");
	private UniformSampler gPBR = new UniformSampler("gPBR");
	private UniformSampler gMask = new UniformSampler("gMask");
	private UniformSampler volumetric = new UniformSampler("volumetric");
	private UniformSampler irradianceCube = new UniformSampler("irradianceCube");
	private UniformSampler environmentCube = new UniformSampler("environmentCube");
	private UniformSampler brdfLUT = new UniformSampler("brdfLUT");

	private UniformMatrix projectionLightMatrix[];
	private UniformMatrix viewLightMatrix = new UniformMatrix("viewLightMatrix");
	private UniformMatrix biasMatrix = new UniformMatrix("biasMatrix");
	private UniformSampler shadowMap[];

	private Matrix4f projInv = new Matrix4f(), viewInv = new Matrix4f();

	public LightingShader(String name) {
		super("DFR_" + name);
		projectionLightMatrix = new UniformMatrix[4];
		for (int x = 0; x < 4; x++)
			projectionLightMatrix[x] = new UniformMatrix("projectionLightMatrix[" + x + "]");
		super.storeUniforms(projectionLightMatrix);
		shadowMap = new UniformSampler[4];
		for (int x = 0; x < 4; x++)
			shadowMap[x] = new UniformSampler("shadowMap[" + x + "]");
		super.storeUniforms(shadowMap);
		super.storeUniforms(projectionMatrix, viewMatrix, cameraPosition, lightPosition, invertedLightPosition,
				gDiffuse, gPosition, gNormal, gDepth, gPBR, gMask, volumetric, irradianceCube, environmentCube, brdfLUT,
				biasMatrix, viewLightMatrix, inverseProjectionMatrix, inverseViewMatrix);
		super.validate();
		this.loadInitialData();
	}

	@Override
	protected void loadInitialData() {
		super.start();
		gDiffuse.loadTexUnit(0);
		gPosition.loadTexUnit(1);
		gNormal.loadTexUnit(2);
		gDepth.loadTexUnit(3);
		gPBR.loadTexUnit(4);
		gMask.loadTexUnit(5);
		volumetric.loadTexUnit(6);
		irradianceCube.loadTexUnit(7);
		environmentCube.loadTexUnit(8);
		brdfLUT.loadTexUnit(9);
		shadowMap[0].loadTexUnit(10);
		shadowMap[1].loadTexUnit(11);
		shadowMap[2].loadTexUnit(12);
		shadowMap[3].loadTexUnit(13);
		Matrix4f bias = new Matrix4f();
		bias.m00(0.5f);
		bias.m11(0.5f);
		bias.m22(0.5f);
		bias.m30(0.5f);
		bias.m31(0.5f);
		bias.m32(0.5f);
		biasMatrix.loadMatrix(bias);
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
		this.inverseProjectionMatrix.loadMatrix(camera.getProjectionMatrix().invert(projInv));
		this.inverseViewMatrix.loadMatrix(camera.getViewMatrix().invert(viewInv));
	}
}
