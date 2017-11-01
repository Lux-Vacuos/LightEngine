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

package net.luxvacuos.lightengine.client.rendering.api.opengles.shaders;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.Material;
import net.luxvacuos.lightengine.client.rendering.api.opengles.shaders.data.Attribute;
import net.luxvacuos.lightengine.client.rendering.api.opengles.shaders.data.UniformBoolean;
import net.luxvacuos.lightengine.client.rendering.api.opengles.shaders.data.UniformMaterial;
import net.luxvacuos.lightengine.client.rendering.api.opengles.shaders.data.UniformMatrix;
import net.luxvacuos.lightengine.client.rendering.api.opengles.shaders.data.UniformSampler;
import net.luxvacuos.lightengine.client.rendering.api.opengles.shaders.data.UniformVec3;

/**
 * Entity Shader
 * 
 * @author Guerra24 <pablo230699@hotmail.com>
 * @category Rendering
 */
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

	public EntityFowardShader() {
		super(ClientVariables.VERTEX_FILE_ENTITY_FORWARD, ClientVariables.FRAGMENT_FILE_ENTITY_FORWARD,
				new Attribute(0, "position"), new Attribute(1, "textureCoords"), new Attribute(2, "normals"),
				new Attribute(3, "tangent"));
		super.storeAllUniformLocations(transformationMatrix, projectionMatrix, viewMatrix, material, cameraPosition,
				lightPosition, irradianceMap, preFilterEnv, brdfLUT, colorCorrect);
		super.start();
		irradianceMap.loadTexUnit(7);
		preFilterEnv.loadTexUnit(8);
		brdfLUT.loadTexUnit(9);
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

}