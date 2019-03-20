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

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Material;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.Attribute;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformBoolean;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformMaterial;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformMatrix;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformSampler;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformVec3;

public class EntityFowardShader extends ShaderProgram {

	private UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
	private UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	private UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	private UniformMaterial material = new UniformMaterial("material");
	private UniformVec3 cameraPosition = new UniformVec3("cameraPosition");
	private UniformVec3 lightPosition = new UniformVec3("lightPosition");
	private UniformSampler irradianceMap = new UniformSampler("irradianceMap");
	private UniformSampler preFilterEnv = new UniformSampler("preFilterEnv");
	private UniformSampler brdfLUT = new UniformSampler("brdfLUT");
	private UniformBoolean colorCorrect = new UniformBoolean("colorCorrect");

	private UniformBoolean useShadows = new UniformBoolean("useShadows");

	private UniformMatrix projectionLightMatrix[];
	private UniformMatrix viewLightMatrix = new UniformMatrix("viewLightMatrix");
	private UniformMatrix biasMatrix = new UniformMatrix("biasMatrix");
	private UniformSampler shadowMap[];

	private Matrix4f biasM;

	public EntityFowardShader() {
		super(ClientVariables.VERTEX_FILE_ENTITY_FORWARD, ClientVariables.FRAGMENT_FILE_ENTITY_FORWARD,
				new Attribute(0, "position"), new Attribute(1, "textureCoords"), new Attribute(2, "normals"),
				new Attribute(3, "tangent"));
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
		super.storeUniforms(transformationMatrix, projectionMatrix, viewMatrix, material, cameraPosition, lightPosition,
				irradianceMap, preFilterEnv, brdfLUT, colorCorrect, biasMatrix, viewLightMatrix, useShadows);
		super.validate();
		biasM = new Matrix4f();
		biasM.m00(0.5f);
		biasM.m11(0.5f);
		biasM.m22(0.5f);
		biasM.m30(0.5f);
		biasM.m31(0.5f);
		biasM.m32(0.5f);
		this.loadInitialData();
	}

	@Override
	protected void loadInitialData() {
		super.start();
		irradianceMap.loadTexUnit(4);
		preFilterEnv.loadTexUnit(5);
		brdfLUT.loadTexUnit(6);
		shadowMap[0].loadTexUnit(7);
		shadowMap[1].loadTexUnit(8);
		shadowMap[2].loadTexUnit(9);
		shadowMap[3].loadTexUnit(10);
		super.stop();
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		transformationMatrix.loadMatrix(matrix);
	}

	public void loadMaterial(Material mat) {
		this.material.loadMaterial(mat);
	}

	public void loadLightPosition(Vector3f lightPos) {
		lightPosition.loadVec3(lightPos);
	}

	public void colorCorrect(boolean val) {
		colorCorrect.loadBoolean(val);
	}

	public void loadCamera(CameraEntity camera) {
		projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		viewMatrix.loadMatrix(camera.getViewMatrix());
		cameraPosition.loadVec3(camera.getPosition());
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

	public void loadSettings(boolean useShadows) {
		this.useShadows.loadBoolean(useShadows);
	}

}