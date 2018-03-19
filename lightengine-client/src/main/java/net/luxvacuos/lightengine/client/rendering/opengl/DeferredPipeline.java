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

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL13.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.ecs.entities.Sun;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.CubeMapTexture;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Light;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.RawModel;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Texture;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.DeferredShadingShader;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.core.IWorldSimulation;
import net.luxvacuos.lightengine.universal.util.registry.KeyCache;

public abstract class DeferredPipeline implements IDeferredPipeline {

	protected RenderingPipelineFBO mainFBO;
	protected int width, height;
	protected List<IDeferredPass> imagePasses = new ArrayList<>();
	private Matrix4f previousViewMatrix;
	private Vector3f previousCameraPosition;
	private RawModel quad;
	private FBO[] auxs = new FBO[3];
	private DeferredShadingShader finalShader;
	private String name;

	public DeferredPipeline(String name, Window window) {
		this.name = name;
		width = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/width"));
		height = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/height"));

		float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
		if (quad == null)
			quad = window.getResourceLoader().loadToVAO(positions, 2);

		mainFBO = new RenderingPipelineFBO(width, height);

		previousCameraPosition = new Vector3f();
		previousViewMatrix = new Matrix4f();
		finalShader = new DeferredShadingShader("Final");
		finalShader.start();
		finalShader.loadResolution(new Vector2f(width, height));
		finalShader.stop();
		init();
		for (IDeferredPass deferredPass : imagePasses) {
			deferredPass.init();
		}
	}

	/**
	 * Begin Rendering
	 */
	@Override
	public void begin() {
		mainFBO.begin();
	}

	/**
	 * End rendering
	 */
	@Override
	public void end() {
		mainFBO.end();
	}

	@Override
	public void preRender(CameraEntity camera, Sun sun, IWorldSimulation clientWorldSimulation, List<Light> lights,
			CubeMapTexture irradianceCapture, CubeMapTexture environmentMap, Texture brdfLUT, ShadowFBO shadowFBO,
			float exposure) {
		glBindVertexArray(quad.getVaoID());
		glEnableVertexAttribArray(0);
		for (IDeferredPass deferredPass : imagePasses) {
			deferredPass.process(camera, sun, previousViewMatrix, previousCameraPosition, clientWorldSimulation, lights,
					auxs, this, quad, irradianceCapture, environmentMap, brdfLUT, shadowFBO, exposure);
		}
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		previousViewMatrix = Maths.createViewMatrix(camera);
		previousCameraPosition.set(camera.getPosition());
	}

	@Override
	public void render(FBO postProcess) {
		finalShader.start();
		glBindVertexArray(quad.getVaoID());
		glEnableVertexAttribArray(0);
		glActiveTexture(GL_TEXTURE6);
		glBindTexture(GL_TEXTURE_2D, auxs[0].getTexture());
		glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		finalShader.stop();
		glBindFramebuffer(GL_READ_FRAMEBUFFER, mainFBO.getFbo());
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, postProcess.getFbo());
		glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_DEPTH_BUFFER_BIT, GL_NEAREST);
	}

	@Override
	public void resize() {
		width = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/width"));
		height = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/height"));
		mainFBO.dispose();
		mainFBO = new RenderingPipelineFBO(width, height);
		finalShader.start();
		finalShader.loadResolution(new Vector2f(width, height));
		finalShader.stop();
		for (IDeferredPass deferredPass : imagePasses) {
			deferredPass.resize(width, height);
		}
	}

	@Override
	public void dispose() {
		mainFBO.dispose();
		for (IDeferredPass deferredPass : imagePasses) {
			deferredPass.dispose();
		}
		finalShader.dispose();
	}

	public RenderingPipelineFBO getMainFBO() {
		return mainFBO;
	}

	@Override
	public int getLastTexture() {
		return auxs[0].getTexture();
	}

	public String getName() {
		return name;
	}

}
