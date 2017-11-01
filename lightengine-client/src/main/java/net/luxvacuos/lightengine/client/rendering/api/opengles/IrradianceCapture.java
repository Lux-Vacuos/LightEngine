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

package net.luxvacuos.lightengine.client.rendering.api.opengles;

import static org.lwjgl.opengles.EXTDrawBuffers.glDrawBuffersEXT;
import static org.lwjgl.opengles.GLES20.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengles.GLES20.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengles.GLES20.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengles.GLES20.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengles.GLES20.GL_FRAMEBUFFER;
import static org.lwjgl.opengles.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengles.GLES20.GL_RENDERBUFFER;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE0;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengles.GLES20.GL_TRIANGLES;
import static org.lwjgl.opengles.GLES20.glActiveTexture;
import static org.lwjgl.opengles.GLES20.glBindFramebuffer;
import static org.lwjgl.opengles.GLES20.glBindRenderbuffer;
import static org.lwjgl.opengles.GLES20.glBindTexture;
import static org.lwjgl.opengles.GLES20.glCheckFramebufferStatus;
import static org.lwjgl.opengles.GLES20.glDeleteFramebuffers;
import static org.lwjgl.opengles.GLES20.glDeleteRenderbuffers;
import static org.lwjgl.opengles.GLES20.glDisableVertexAttribArray;
import static org.lwjgl.opengles.GLES20.glDrawArrays;
import static org.lwjgl.opengles.GLES20.glEnableVertexAttribArray;
import static org.lwjgl.opengles.GLES20.glFramebufferTexture2D;
import static org.lwjgl.opengles.GLES20.glGenFramebuffers;
import static org.lwjgl.opengles.GLES20.glGenRenderbuffers;
import static org.lwjgl.opengles.GLES20.glRenderbufferStorage;
import static org.lwjgl.opengles.GLES30.glBindVertexArray;

import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.core.exception.FrameBufferException;
import net.luxvacuos.lightengine.client.ecs.entities.CubeMapCamera;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.CubeMapTexture;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.RawModel;
import net.luxvacuos.lightengine.client.rendering.api.opengles.shaders.IrradianceCaptureShader;
import net.luxvacuos.lightengine.client.resources.ResourceLoader;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public class IrradianceCapture implements IDisposable {

	private final float SIZE = 1;

	private final float[] VERTICES = { -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE,
			SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE,
			SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE,
			SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE,
			SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE,
			SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, -SIZE, -SIZE,
			SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE };

	private IrradianceCaptureShader shader;
	private int fbo, depthBuffer;
	private CubeMapTexture cubeMapTexture;
	private CubeMapCamera camera;
	private RawModel cube;

	public IrradianceCapture(ResourceLoader loader) {
		shader = new IrradianceCaptureShader();
		camera = new CubeMapCamera(new Vector3f());
		cube = loader.loadToVAO(VERTICES, 3);
		cubeMapTexture = loader.createEmptyCubeMap(32, true, false);
		fbo = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);

		depthBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, cubeMapTexture.getSize(), cubeMapTexture.getSize());

		glBindTexture(GL_TEXTURE_CUBE_MAP, cubeMapTexture.getID());
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X,
				cubeMapTexture.getID(), 0);

		int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		if (status != GL_FRAMEBUFFER_COMPLETE)
			throw new FrameBufferException("Incomplete FrameBuffer ");

		glDrawBuffersEXT(GL_COLOR_ATTACHMENT0);

		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public void render(Window window, int envMap) {
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		window.setViewport(0, 0, cubeMapTexture.getSize(), cubeMapTexture.getSize());
		shader.start();
		glBindVertexArray(cube.getVaoID());
		glEnableVertexAttribArray(0);
		shader.loadProjectionMatrix(camera.getProjectionMatrix());
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_CUBE_MAP, envMap);
		for (int i = 0; i < 6; i++) {
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
					cubeMapTexture.getID(), 0);
			camera.switchToFace(i);
			shader.loadviewMatrix(camera);
			Renderer.clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glDrawArrays(GL_TRIANGLES, 0, cube.getVertexCount());
		}
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		shader.stop();
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		window.resetViewport();
	}

	@Override
	public void dispose() {
		shader.dispose();
		cubeMapTexture.dispose();
		glDeleteRenderbuffers(depthBuffer);
		glDeleteFramebuffers(fbo);
	}

	public CubeMapTexture getCubeMapTexture() {
		return cubeMapTexture;
	}

}
