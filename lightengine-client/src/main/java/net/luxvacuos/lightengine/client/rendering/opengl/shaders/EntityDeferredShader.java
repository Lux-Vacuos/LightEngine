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
import org.joml.Vector2f;

import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Material;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.Attribute;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformMaterial;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.UniformMatrix;
import net.luxvacuos.lightengine.universal.core.subsystems.ResManager;
import net.luxvacuos.lightengine.universal.resources.ResourceType;

public class EntityDeferredShader extends ShaderProgram {

	private UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
	private UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	private UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	private UniformMatrix jitterMatrix = new UniformMatrix("jitterMatrix");
	private UniformMaterial material = new UniformMaterial("material");

	private Matrix4f jitter = new Matrix4f();

	private int frameCont;

	// TODO: Move this
	private Vector2f sampleLocs[] = { new Vector2f(-7.0f, 1.0f).mul(1.0f / 8.0f),
			new Vector2f(-5.0f, -5.0f).mul(1.0f / 8.0f), new Vector2f(-1.0f, -3.0f).mul(1.0f / 8.0f),
			new Vector2f(3.0f, -7.0f).mul(1.0f / 8.0f), new Vector2f(5.0f, -1.0f).mul(1.0f / 8.0f),
			new Vector2f(7.0f, 7.0f).mul(1.0f / 8.0f), new Vector2f(1.0f, 3.0f).mul(1.0f / 8.0f),
			new Vector2f(-3.0f, 5.0f).mul(1.0f / 8.0f) };
	
	private Vector2f tmp = new Vector2f();

	public EntityDeferredShader() {
		super(ResManager.getResourceOfType("ENGINE_RND_EntityDeferred_VS", ResourceType.SHADER).get(),
				ResManager.getResourceOfType("ENGINE_RND_EntityDeferred_FS", ResourceType.SHADER).get(),
				new Attribute(0, "position"), new Attribute(1, "textureCoords"), new Attribute(2, "normals"),
				new Attribute(3, "tangent"));
		super.storeUniforms(transformationMatrix, material, projectionMatrix, viewMatrix, jitterMatrix);
		super.validate();
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		transformationMatrix.loadMatrix(matrix);
	}

	public void loadMaterial(Material mat) {
		material.loadMaterial(mat);
	}

	public void loadCamera(CameraEntity camera, Vector2f resolution) {
		projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		viewMatrix.loadMatrix(camera.getViewMatrix());
		frameCont++;
		frameCont %= 8;

		Vector2f texSize = new Vector2f(1.0f / resolution.x, 1.0f / resolution.y);

		Vector2f subsampleSize = texSize.mul(2.0f, new Vector2f());

		Vector2f S = sampleLocs[frameCont];

		Vector2f subsample = S.mul(subsampleSize, tmp);
		subsample.mul(0.5f);
		jitter.translation(subsample.x, subsample.y, 0);
		jitterMatrix.loadMatrix(jitter);
	}
}