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

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glDeleteRenderbuffers;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;

import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.core.exception.FrameBufferException;
import net.luxvacuos.lightengine.client.ecs.entities.CubeMapCamera;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.CubeMapTexture;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Texture;
import net.luxvacuos.lightengine.universal.core.IWorldSimulation;

public class EnvironmentRenderer {

	private int fbo, depthBuffer;
	private CubeMapTexture cubeMapTexture;
	private CubeMapCamera camera;

	public EnvironmentRenderer(CubeMapTexture cubeMap) {
		this.cubeMapTexture = cubeMap;
		fbo = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);

		depthBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, cubeMapTexture.getSize(), cubeMapTexture.getSize());
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);

		glBindTexture(GL_TEXTURE_CUBE_MAP, cubeMapTexture.getID());
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X,
				cubeMapTexture.getID(), 0);

		int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		if (status != GL_FRAMEBUFFER_COMPLETE)
			throw new FrameBufferException("Incomplete FrameBuffer ");

		glDrawBuffer(GL_COLOR_ATTACHMENT0);

		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		camera = new CubeMapCamera(new Vector3f());
	}

	public void renderEnvironmentMap(Vector3f center, SkyboxRenderer skyboxRenderer,
			IWorldSimulation clientWorldSimulation, Vector3f lightPosition, Window window) {
		camera.setPosition(center);
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		window.setViewport(0, 0, cubeMapTexture.getSize(), cubeMapTexture.getSize());
		for (int i = 0; i < 6; i++) {
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
					cubeMapTexture.getID(), 0);
			camera.switchToFace(i);
			Renderer.clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			skyboxRenderer.render(camera, clientWorldSimulation, lightPosition, false);
		}
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		window.resetViewport();
	}

	public void renderEnvironmentMap(Vector3f center, SkyboxRenderer skyboxRenderer, RenderingManager renderingManager,
			IWorldSimulation clientWorldSimulation, Vector3f lightPosition, CubeMapTexture irradiance,
			CubeMapTexture environmentMap, Texture brdfLUT, Window window) {
		camera.setPosition(center);
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		window.setViewport(0, 0, cubeMapTexture.getSize(), cubeMapTexture.getSize());
		for (int i = 0; i < 6; i++) {
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
					cubeMapTexture.getID(), 0);
			camera.switchToFace(i);
			Renderer.clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			skyboxRenderer.render(camera, clientWorldSimulation, lightPosition, false);
			renderingManager.renderReflections(camera, lightPosition, irradiance, environmentMap, brdfLUT);
		}
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		window.resetViewport();
	}

	public void cleanUp() {
		glDeleteRenderbuffers(depthBuffer);
		glDeleteFramebuffers(fbo);
	}

	public CubeMapTexture getCubeMapTexture() {
		return cubeMapTexture;
	}

}
