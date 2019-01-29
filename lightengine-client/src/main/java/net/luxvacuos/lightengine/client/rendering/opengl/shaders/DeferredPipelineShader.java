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
import org.joml.Vector2f;
import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.opengl.RenderingSettings;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Light;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.Attribute;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformBoolean;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformFloat;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformInteger;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformLight;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformMatrix;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformSampler;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformVec2;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformVec3;

public class DeferredPipelineShader extends ShaderProgram {

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

	private UniformVec2 resolution = new UniformVec2("resolution");

	private UniformFloat exposure = new UniformFloat("exposure");
	private UniformFloat time = new UniformFloat("time");

	private UniformInteger shadowDrawDistance = new UniformInteger("shadowDrawDistance");

	private UniformBoolean useFXAA = new UniformBoolean("useFXAA");
	private UniformBoolean useDOF = new UniformBoolean("useDOF");
	private UniformBoolean useMotionBlur = new UniformBoolean("useMotionBlur");
	private UniformBoolean useReflections = new UniformBoolean("useReflections");
	private UniformBoolean useVolumetricLight = new UniformBoolean("useVolumetricLight");
	private UniformBoolean useAmbientOcclusion = new UniformBoolean("useAmbientOcclusion");
	private UniformBoolean useChromaticAberration = new UniformBoolean("useChromaticAberration");
	private UniformBoolean useLensFlares = new UniformBoolean("useLensFlares");
	private UniformBoolean useShadows = new UniformBoolean("useShadows");

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

	private Matrix4f biasM;

	public DeferredPipelineShader(String name) {
		super("deferred/" + name + ".vs", "deferred/" + name + ".fs", new Attribute(0, "position"));
		super.storeUniformArray(lights);
		projectionLightMatrix = new UniformMatrix[4];
		for (int x = 0; x < 4; x++) {
			projectionLightMatrix[x] = new UniformMatrix("projectionLightMatrix[" + x + "]");
		}
		super.storeUniformArray(projectionLightMatrix);
		shadowMap = new UniformSampler[4];
		for (int x = 0; x < 4; x++) {
			shadowMap[x] = new UniformSampler("shadowMap[" + x + "]");
		}
		super.storeUniformArray(shadowMap);
		super.storeAllUniformLocations(projectionMatrix, viewMatrix, inverseProjectionMatrix, inverseViewMatrix,
				previousViewMatrix, cameraPosition, previousCameraPosition, lightPosition, invertedLightPosition,
				skyColor, resolution, exposure, time, shadowDrawDistance, useFXAA, useDOF, useMotionBlur,
				useReflections, useVolumetricLight, useAmbientOcclusion, gDiffuse, gPosition, gNormal, gDepth, gPBR,
				gMask, composite0, composite1, composite2, totalLights, useChromaticAberration, composite3,
				useLensFlares, biasMatrix, viewLightMatrix, useShadows);
		connectTextureUnits();
		biasM = new Matrix4f();
		biasM.m00(0.5f);
		biasM.m11(0.5f);
		biasM.m22(0.5f);
		biasM.m30(0.5f);
		biasM.m31(0.5f);
		biasM.m32(0.5f);
	}

	private void connectTextureUnits() {
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
		for (int x = 0; x < lights.size(); x++) {
			this.lights[x].loadLight(lights.get(x), 14, x);
		}
		totalLights.loadInteger(lights.size());
	}

	public void loadBiasMatrix(Matrix4f[] shadowProjectionMatrix) {
		this.biasMatrix.loadMatrix(biasM);
		for (int x = 0; x < 4; x++) {
			this.projectionLightMatrix[x].loadMatrix(shadowProjectionMatrix[x]);
		}
	}

	public void loadLightMatrix(Matrix4f sunCameraViewMatrix) {
		viewLightMatrix.loadMatrix(sunCameraViewMatrix);
	}

	public void loadResolution(Vector2f res) {
		resolution.loadVec2(res);
	}

	public void loadSettings(RenderingSettings rs) {
		this.useDOF.loadBoolean(rs.depthOfFieldEnabled);
		this.useFXAA.loadBoolean(rs.fxaaEnabled);
		this.useMotionBlur.loadBoolean(rs.motionBlurEnabled);
		this.useVolumetricLight.loadBoolean(rs.volumetricLightEnabled);
		this.useReflections.loadBoolean(rs.ssrEnabled);
		this.useAmbientOcclusion.loadBoolean(rs.ambientOcclusionEnabled);
		this.shadowDrawDistance.loadInteger(rs.shadowsDrawDistance);
		this.useChromaticAberration.loadBoolean(rs.chromaticAberrationEnabled);
		this.useLensFlares.loadBoolean(rs.lensFlaresEnabled);
		this.useShadows.loadBoolean(rs.shadowsEnabled);
	}

	public void loadMotionBlurData(CameraEntity camera, Matrix4f previousViewMatrix, Vector3f previousCameraPosition) {
		this.projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		this.inverseProjectionMatrix.loadMatrix(camera.getProjectionMatrix().invert(new Matrix4f()));
		this.inverseViewMatrix.loadMatrix(camera.getViewMatrix().invert(new Matrix4f()));
		this.previousViewMatrix.loadMatrix(previousViewMatrix);
		this.cameraPosition.loadVec3(camera.getPosition());
		this.previousCameraPosition.loadVec3(previousCameraPosition);
	}

	public void loadviewMatrix(CameraEntity camera) {
		this.viewMatrix.loadMatrix(camera.getViewMatrix());
	}

}
