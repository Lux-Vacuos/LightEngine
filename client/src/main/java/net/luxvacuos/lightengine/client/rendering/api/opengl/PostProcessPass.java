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

package net.luxvacuos.lightengine.client.rendering.api.opengl;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glDrawArrays;

import net.luxvacuos.igl.vector.Matrix4d;
import net.luxvacuos.igl.vector.Vector2d;
import net.luxvacuos.igl.vector.Vector3d;
import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.RawModel;
import net.luxvacuos.lightengine.client.rendering.api.opengl.shaders.DeferredShadingShader;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public abstract class PostProcessPass implements IPostProcessPass {

	/**
	 * Deferred Shader
	 */
	private DeferredShadingShader shader;
	/**
	 * FBO
	 */
	private FBO fbo;

	/**
	 * Width and Height of the FBO
	 */
	private int width, height;
	/**
	 * Name
	 */
	private String name;

	/**
	 * 
	 * @param width
	 *            Width
	 * @param height
	 *            Height
	 */
	public PostProcessPass(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
	}

	/**
	 * Initializes the FBO, Shader and loads information to the shader.
	 */
	@Override
	public void init() {
		fbo = new FBO(width, height);
		shader = new DeferredShadingShader(name);
		shader.start();
		shader.loadResolution(new Vector2d(width, height));
		shader.loadSkyColor(ClientVariables.skyColor);
		shader.stop();
	}

	@Override
	public void process(CameraEntity camera, Matrix4d previousViewMatrix, Vector3d previousCameraPosition, FBO[] auxs,
			RawModel quad) {
		GPUProfiler.start(name);
		fbo.begin();
		Renderer.clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		Renderer.clearColors(0, 0, 0, 1);
		shader.start();
		shader.loadUnderWater(false);
		shader.loadMotionBlurData(camera, previousViewMatrix, previousCameraPosition);
		shader.loadviewMatrix(camera);
		shader.loadSettings((boolean) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Graphics/dof")),
				(boolean) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Graphics/fxaa")),
				(boolean) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Graphics/motionBlur")), false, false, false, 0,
				(boolean) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Graphics/chromaticAberration")), false, false);

		render(auxs);
		glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		shader.stop();
		fbo.end();
		auxs[0] = getFbo();
		GPUProfiler.end();
	}

	/**
	 * Dispose shader and FBO
	 */
	@Override
	public void dispose() {
		shader.dispose();
		fbo.dispose();
	}

	public FBO getFbo() {
		return fbo;
	}

}
