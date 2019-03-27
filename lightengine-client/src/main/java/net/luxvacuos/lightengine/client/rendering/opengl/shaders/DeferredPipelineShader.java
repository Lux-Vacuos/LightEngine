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

package net.luxvacuos.lightengine.client.rendering.opengl.shaders;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.ecs.entities.SunCamera;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Light;
import net.luxvacuos.lightengine.client.rendering.opengl.pipeline.shaders.BasePipelineShader;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformFloat;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformInteger;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformLight;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformMatrix;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformSampler;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformVec3;

public class DeferredPipelineShader extends BasePipelineShader {

	private UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	private UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	private UniformMatrix inverseProjectionMatrix = new UniformMatrix("inverseProjectionMatrix");
	private UniformMatrix inverseViewMatrix = new UniformMatrix("inverseViewMatrix");
	private UniformMatrix previousViewMatrix = new UniformMatrix("previousViewMatrix");

	private UniformVec3 cameraPosition = new UniformVec3("cameraPosition");
	private UniformVec3 previousCameraPosition = new UniformVec3("previousCameraPosition");
	private UniformVec3 lightPosition = new UniformVec3("lightPosition");
	private UniformVec3 invertedLightPosition = new UniformVec3("invertedLightPosition");
	private UniformVec3 skyColor = new UniformVec3("skyColor");

	private UniformLight lights[];
	private UniformInteger totalLights = new UniformInteger("totalLights");

	private UniformFloat exposure = new UniformFloat("exposure");
	private UniformFloat time = new UniformFloat("time");

	private UniformSampler gDiffuse = new UniformSampler("gDiffuse");
	private UniformSampler gPosition = new UniformSampler("gPosition");
	private UniformSampler gNormal = new UniformSampler("gNormal");
	private UniformSampler gDepth = new UniformSampler("gDepth");
	private UniformSampler gPBR = new UniformSampler("gPBR");
	private UniformSampler gMask = new UniformSampler("gMask");
	private UniformSampler composite0 = new UniformSampler("composite0");
	private UniformSampler composite1 = new UniformSampler("composite1");
	private UniformSampler composite2 = new UniformSampler("composite2");
	private UniformSampler composite3 = new UniformSampler("composite3");

	private UniformMatrix projectionLightMatrix[];
	private UniformMatrix viewLightMatrix = new UniformMatrix("viewLightMatrix");
	private UniformMatrix biasMatrix = new UniformMatrix("biasMatrix");
	private UniformSampler shadowMap[];

	public DeferredPipelineShader(String name) {
		super("DFR_" + name);
		lights = new UniformLight[18];
		for (int x = 0; x < 18; x++) {
			lights[x] = new UniformLight("lights[" + x + "]");
		}
		super.storeUniforms(lights);
		projectionLightMatrix = new UniformMatrix[4];
		for (int x = 0; x < 4; x++) {
			projectionLightMatrix[x] = new UniformMatrix("projectionLightMatrix[" + x + "]");
		}
		super.storeUniforms(projectionLightMatrix);
		shadowMap = new UniformSampler[4];
		for (int x = 0; x < 4; x++) {
			shadowMap[x] = new UniformSampler("shadowMap[" + x + "]");
		}
		super.storeUniforms(shadowMap);
		super.storeUniforms(projectionMatrix, viewMatrix, inverseProjectionMatrix, inverseViewMatrix,
				previousViewMatrix, cameraPosition, previousCameraPosition, lightPosition, invertedLightPosition,
				skyColor, exposure, time, gDiffuse, gPosition, gNormal, gDepth, gPBR, gMask, composite0, composite1,
				composite2, totalLights, composite3, biasMatrix, viewLightMatrix);
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
		composite0.loadTexUnit(6);
		composite1.loadTexUnit(7);
		composite2.loadTexUnit(8);
		composite3.loadTexUnit(9);
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

	public void loadExposure(float exposure) {
		this.exposure.loadFloat(exposure);
	}

	public void loadLightPosition(Vector3f pos, Vector3f invertPos) {
		lightPosition.loadVec3(pos);
		invertedLightPosition.loadVec3(invertPos);
	}

	public void loadTime(float time) {
		this.time.loadFloat(time);
	}

	public void loadPointLightsPos(List<Light> lights) {
		for (int x = 0; x < lights.size(); x++)
			this.lights[x].loadLight(lights.get(x), 14, x);
		totalLights.loadInteger(lights.size());
	}

	public void loadSunCameraData(SunCamera camera) {
		for (int x = 0; x < 4; x++)
			this.projectionLightMatrix[x].loadMatrix(camera.getProjectionArray()[x]);
		viewLightMatrix.loadMatrix(camera.getViewMatrix());
	}

	public void loadCameraData(CameraEntity camera, Matrix4f previousViewMatrix, Vector3f previousCameraPosition) {
		this.projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		this.viewMatrix.loadMatrix(camera.getViewMatrix());
		this.cameraPosition.loadVec3(camera.getPosition());
		this.inverseProjectionMatrix.loadMatrix(camera.getProjectionMatrix().invert(new Matrix4f()));
		this.inverseViewMatrix.loadMatrix(camera.getViewMatrix().invert(new Matrix4f()));
		// this.previousViewMatrix.loadMatrix(previousViewMatrix);
		// this.previousCameraPosition.loadVec3(previousCameraPosition);
	}

}
