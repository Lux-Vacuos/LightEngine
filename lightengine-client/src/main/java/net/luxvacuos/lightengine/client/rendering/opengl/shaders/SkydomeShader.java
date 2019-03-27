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

import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.Attribute;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformBoolean;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformFloat;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformMatrix;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformVec3;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.core.subsystems.ResManager;
import net.luxvacuos.lightengine.universal.resources.ResourceType;

public class SkydomeShader extends ShaderProgram {

	private UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	private UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
	private UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	private UniformFloat time = new UniformFloat("time");
	private UniformVec3 lightPosition = new UniformVec3("lightPosition");
	private UniformBoolean renderSun = new UniformBoolean("renderSun");
	private UniformVec3 cameraPosition = new UniformVec3("cameraPosition");

	private Matrix4f temp = new Matrix4f();

	public SkydomeShader() {
		super(ResManager.getResourceOfType("ENGINE_RND_Skydome_VS", ResourceType.SHADER).get(),
				ResManager.getResourceOfType("ENGINE_RND_Skydome_FS", ResourceType.SHADER).get(),
				new Attribute(0, "position"), new Attribute(1, "textureCoords"), new Attribute(2, "normal"));
		super.storeUniforms(projectionMatrix, transformationMatrix, viewMatrix, time, lightPosition, renderSun,
				cameraPosition);
		super.validate();
	}

	public void loadCamera(CameraEntity camera) {
		projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		viewMatrix.loadMatrix(Maths.createViewMatrixRot(camera.getRotation().x(), camera.getRotation().y(),
				camera.getRotation().z(), temp.identity()));
		cameraPosition.loadVec3(camera.getPosition());
	}

	public void loadTransformationMatrix(Matrix4f mat) {
		transformationMatrix.loadMatrix(mat);
	}

	public void loadLightPosition(Vector3f pos) {
		lightPosition.loadVec3(pos);
	}

	public void renderSun(boolean val) {
		renderSun.loadBoolean(val);
	}

	public void loadTime(float time) {
		this.time.loadFloat(time);
	}

}
