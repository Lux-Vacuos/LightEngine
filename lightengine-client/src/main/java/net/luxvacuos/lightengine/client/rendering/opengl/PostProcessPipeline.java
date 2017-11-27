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

package net.luxvacuos.lightengine.client.rendering.opengl;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.nanovg.NanoVG.NVG_IMAGE_FLIPY;
import static org.lwjgl.nanovg.NanoVG.nvgDeleteImage;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL13.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.RawModel;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.DeferredShadingShader;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.util.registry.KeyCache;

public abstract class PostProcessPipeline implements IPostProcessPipeline {

	protected FBO fbo;
	protected int width, height;
	protected List<IPostProcessPass> imagePasses = new ArrayList<>();;
	private Matrix4f previousViewMatrix;
	private Vector3f previousCameraPosition;
	private RawModel quad;
	private FBO[] auxs = new FBO[3];
	private String name;
	private int texture = -1;
	private DeferredShadingShader finalShader;
	private Window window;

	public PostProcessPipeline(String name, Window window) {
		this.name = name;
		this.window = window;
		width = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/width"));
		height = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/height"));

		if (width > GLUtil.GL_MAX_TEXTURE_SIZE)
			width = GLUtil.GL_MAX_TEXTURE_SIZE;
		if (height > GLUtil.GL_MAX_TEXTURE_SIZE)
			height = GLUtil.GL_MAX_TEXTURE_SIZE;
		float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
		if (quad == null)
			quad = window.getResourceLoader().loadToVAO(positions, 2);
		
		fbo = new FBO(width, height, GL_RGB, GL_RGB, GL_UNSIGNED_BYTE);

		previousCameraPosition = new Vector3f();
		previousViewMatrix = new Matrix4f();
		finalShader = new DeferredShadingShader("Final");
		finalShader.start();
		finalShader.loadResolution(new Vector2f(window.getWidth(), window.getHeight()));
		finalShader.stop();
		init();
		for (IPostProcessPass deferredPass : imagePasses) {
			deferredPass.init();
		}
		texture = Theme.generateImageFromTexture(window.getNVGID(), fbo.getTexture(), width, height, NVG_IMAGE_FLIPY);
	}

	@Override
	public void begin() {
		fbo.begin();
	}

	@Override
	public void end() {
		fbo.end();
	}

	@Override
	public void preRender(CameraEntity camera) {
		auxs[0] = fbo;
		glBindVertexArray(quad.getVaoID());
		glEnableVertexAttribArray(0);
		for (IPostProcessPass deferredPass : imagePasses) {
			deferredPass.process(camera, previousViewMatrix, previousCameraPosition, auxs, quad);
		}
		fbo.begin();
		Renderer.clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		finalShader.start();
		glActiveTexture(GL_TEXTURE6);
		glBindTexture(GL_TEXTURE_2D, auxs[0].getTexture());
		glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		finalShader.stop();
		fbo.end();
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		previousViewMatrix = Maths.createViewMatrix(camera);
		previousCameraPosition = camera.getPosition();
	}
	
	@Override
	public void resize() {
		width = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/width"));
		height = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/height"));
		if (texture != -1)
			nvgDeleteImage(window.getNVGID(), texture);
		fbo.dispose();
		fbo = new FBO(width, height, GL_RGB, GL_RGB, GL_UNSIGNED_BYTE);
		finalShader.start();
		finalShader.loadResolution(new Vector2f(width, height));
		finalShader.stop();
		for (IPostProcessPass deferredPass : imagePasses) {
			deferredPass.resize(width, height);
		}
		texture = Theme.generateImageFromTexture(window.getNVGID(), fbo.getTexture(), width, height, NVG_IMAGE_FLIPY);
	}

	@Override
	public void dispose() {
		if (texture != -1)
			nvgDeleteImage(window.getNVGID(), texture);
		fbo.dispose();
		for (IPostProcessPass deferredPass : imagePasses) {
			deferredPass.dispose();
		}
	}

	@Override
	public FBO getFBO() {
		return fbo;
	}

	@Override
	public int getNVGImage() {
		return texture;
	}

	public String getName() {
		return name;
	}

}
