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

import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformMatrix;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformSampler;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformVec3;

public class ReflectionsShader extends BasePipelineShader {

	private UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	private UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	private UniformMatrix inverseProjectionMatrix = new UniformMatrix("inverseProjectionMatrix");
	private UniformMatrix inverseViewMatrix = new UniformMatrix("inverseViewMatrix");

	private UniformVec3 cameraPosition = new UniformVec3("cameraPosition");

	private UniformSampler gDiffuse = new UniformSampler("gDiffuse");
	private UniformSampler gPosition = new UniformSampler("gPosition");
	private UniformSampler gNormal = new UniformSampler("gNormal");
	private UniformSampler gDepth = new UniformSampler("gDepth");
	private UniformSampler gPBR = new UniformSampler("gPBR");
	private UniformSampler gMask = new UniformSampler("gMask");

	private UniformSampler environmentCube = new UniformSampler("environmentCube");
	private UniformSampler brdfLUT = new UniformSampler("brdfLUT");
	private UniformSampler pass = new UniformSampler("pass");
	
	private Matrix4f projInv = new Matrix4f(), viewInv = new Matrix4f();

	public ReflectionsShader(String name) {
		super("DFR_" + name);
		super.storeUniforms(projectionMatrix, viewMatrix, cameraPosition, gDiffuse, gPosition, gNormal, gDepth, gPBR,
				gMask, environmentCube, brdfLUT, inverseProjectionMatrix, inverseViewMatrix, pass);
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
		environmentCube.loadTexUnit(6);
		brdfLUT.loadTexUnit(7);
		pass.loadTexUnit(8);
	}
	
	public void loadCameraData(CameraEntity camera) {
		this.projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		this.viewMatrix.loadMatrix(camera.getViewMatrix());
		this.cameraPosition.loadVec3(camera.getPosition());
		this.inverseProjectionMatrix.loadMatrix(camera.getProjectionMatrix().invert(projInv));
		this.inverseViewMatrix.loadMatrix(camera.getViewMatrix().invert(viewInv));
	}

}
