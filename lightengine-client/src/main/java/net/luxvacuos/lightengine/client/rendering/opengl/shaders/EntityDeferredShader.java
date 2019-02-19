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

import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Material;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.Attribute;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformMaterial;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformMatrix;

public class EntityDeferredShader extends ShaderProgram {

	private UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
	private UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	private UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	private UniformMaterial material = new UniformMaterial("material");

	public EntityDeferredShader() {
		super(ClientVariables.VERTEX_FILE_ENTITY_DEFERRED, ClientVariables.FRAGMENT_FILE_ENTITY_DEFERRED,
				new Attribute(0, "position"), new Attribute(1, "textureCoords"), new Attribute(2, "normals"),
				new Attribute(3, "tangent"));
		super.storeUniforms(transformationMatrix, projectionMatrix, viewMatrix, material);
		super.validate();
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		transformationMatrix.loadMatrix(matrix);
	}

	public void loadMaterial(Material mat) {
		material.loadMaterial(mat);
	}

	public void loadCamera(CameraEntity camera) {
		projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		viewMatrix.loadMatrix(camera.getViewMatrix());
	}
}