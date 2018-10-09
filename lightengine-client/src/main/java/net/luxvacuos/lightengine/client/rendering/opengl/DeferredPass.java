/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2018 Lux Vacuos
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

package net.luxvacuos.lightengine.client.rendering.opengl;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.ecs.entities.Sun;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.CubeMapTexture;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Light;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.RawModel;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Texture;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.DeferredShadingShader;
import net.luxvacuos.lightengine.universal.core.IWorldSimulation;

public abstract class DeferredPass implements IDeferredPass {

	/**
	 * Deferred Shader
	 */
	protected DeferredShadingShader shader;
	/**
	 * FBO
	 */
	protected FBO fbo;

	/**
	 * Width and Height of the FBO
	 */
	protected int width, height;
	/**
	 * Name
	 */
	protected String name;

	protected static Matrix4f tmp;

	/**
	 * 
	 * @param width  Width
	 * @param height Height
	 */
	public DeferredPass(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
	}

	/**
	 * Initializes the FBO, Shader and loads information to the shader.
	 */
	@Override
	public void init() {
		fbo = new FBO(width, height, GL_RGBA16F, GL_RGBA, GL_FLOAT);
		shader = new DeferredShadingShader(name);
		shader.start();
		shader.loadResolution(new Vector2f(width, height));
		shader.stop();
	}

	@Override
	public void process(CameraEntity camera, Sun sun, Matrix4f previousViewMatrix, Vector3f previousCameraPosition,
			IWorldSimulation clientWorldSimulation, List<Light> lights, FBO[] auxs, IDeferredPipeline pipe,
			RawModel quad, CubeMapTexture irradianceCapture, CubeMapTexture environmentMap, Texture brdfLUT,
			ShadowFBO shadowFBO, float exposure) {
		RenderingSettings rs = GraphicalSubsystem.getRenderingSettings();
		GPUProfiler.start(name);
		fbo.begin();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		shader.start();
		shader.loadMotionBlurData(camera, previousViewMatrix, previousCameraPosition);
		shader.loadLightPosition(sun.getSunPosition(), sun.getInvertedSunPosition());
		shader.loadviewMatrix(camera);
		shader.loadSettings(rs.depthOfFieldEnabled, rs.fxaaEnabled, rs.motionBlurEnabled, rs.volumetricLightEnabled,
				rs.ssrEnabled, rs.ambientOcclusionEnabled, rs.shadowsDrawDistance, rs.chromaticAberrationEnabled,
				rs.lensFlaresEnabled, rs.shadowsEnabled);
		shader.loadExposure(exposure);
		shader.loadTime(clientWorldSimulation.getGlobalTime());
		shader.loadLightMatrix(sun.getCamera().getViewMatrix());
		shader.loadBiasMatrix(sun.getCamera().getProjectionArray());
		render(auxs, pipe, irradianceCapture, environmentMap, brdfLUT, shadowFBO);
		glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		shader.stop();
		fbo.end();
		auxs[0] = fbo;
		GPUProfiler.end();
	}

	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		fbo.dispose();
		fbo = new FBO(width, height, GL_RGBA16F, GL_RGBA, GL_FLOAT);
		shader.start();
		shader.loadResolution(new Vector2f(width, height));
		shader.stop();
	}

	/**
	 * Dispose shader and FBO
	 */
	@Override
	public void dispose() {
		shader.dispose();
		fbo.dispose();
	}

}
