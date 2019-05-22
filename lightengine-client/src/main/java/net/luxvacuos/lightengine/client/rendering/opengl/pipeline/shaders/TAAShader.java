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
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformMatrix;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformSampler;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformVec3;

public class TAAShader extends BasePipelineShader {

	private UniformSampler image = new UniformSampler("image");
	private UniformSampler previous = new UniformSampler("previous");

	private UniformSampler depth = new UniformSampler("depth");

	private UniformVec3 cameraPosition = new UniformVec3("cameraPosition");
	private UniformVec3 previousCameraPosition = new UniformVec3("previousCameraPosition");
	private UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	private UniformMatrix inverseProjectionMatrix = new UniformMatrix("inverseProjectionMatrix");
	private UniformMatrix inverseViewMatrix = new UniformMatrix("inverseViewMatrix");
	private UniformMatrix previousViewMatrix = new UniformMatrix("previousViewMatrix");

	private Matrix4f projInv = new Matrix4f(), viewInv = new Matrix4f();

	public TAAShader(String name) {
		super("DFR_" + name);
		this.storeUniforms(image, previous, depth, cameraPosition, previousCameraPosition, projectionMatrix,
				inverseProjectionMatrix, inverseViewMatrix, previousViewMatrix);
		this.validate();
		this.loadInitialData();
	}

	@Override
	protected void loadInitialData() {
		super.start();
		image.loadTexUnit(0);
		previous.loadTexUnit(1);
		depth.loadTexUnit(2);
		super.stop();
	}

	public void loadMotionBlurData(CameraEntity camera, Matrix4f previousViewMatrix, Vector3f previousCameraPosition) {
		this.cameraPosition.loadVec3(camera.getPosition());
		this.projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		this.inverseProjectionMatrix.loadMatrix(camera.getProjectionMatrix().invert(projInv));
		this.inverseViewMatrix.loadMatrix(camera.getViewMatrix().invert(viewInv));
		this.previousViewMatrix.loadMatrix(previousViewMatrix);
		this.previousCameraPosition.loadVec3(previousCameraPosition);
	}
}
