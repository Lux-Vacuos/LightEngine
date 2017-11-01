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

import static org.lwjgl.opengles.GLES20.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengles.GLES20.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengles.GLES20.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengles.GLES20.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengles.GLES20.GL_FLOAT;
import static org.lwjgl.opengles.GLES20.GL_FRAMEBUFFER;
import static org.lwjgl.opengles.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengles.GLES20.GL_LINEAR;
import static org.lwjgl.opengles.GLES20.GL_RGB;
import static org.lwjgl.opengles.GLES20.GL_RGBA;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE_2D;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengles.GLES20.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengles.GLES20.glBindFramebuffer;
import static org.lwjgl.opengles.GLES20.glBindTexture;
import static org.lwjgl.opengles.GLES20.glCheckFramebufferStatus;
import static org.lwjgl.opengles.GLES20.glDeleteFramebuffers;
import static org.lwjgl.opengles.GLES20.glDeleteTextures;
import static org.lwjgl.opengles.GLES20.glGenFramebuffers;
import static org.lwjgl.opengles.GLES20.glGenTextures;
import static org.lwjgl.opengles.GLES20.glTexImage2D;
import static org.lwjgl.opengles.GLES20.glTexParameteri;
import static org.lwjgl.opengles.GLES20.glViewport;
import static org.lwjgl.opengles.GLES30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengles.GLES30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengles.GLES30.GL_COLOR_ATTACHMENT3;
import static org.lwjgl.opengles.GLES30.GL_COLOR_ATTACHMENT4;
import static org.lwjgl.opengles.GLES30.GL_RG;
import static org.lwjgl.opengles.GLES30.GL_RGB16F;
import static org.lwjgl.opengles.GLES30.GL_RGB32F;
import static org.lwjgl.opengles.GLES30.GL_RGBA16F;
import static org.lwjgl.opengles.GLES30.glDrawBuffers;
import static org.lwjgl.opengles.GLES32.glFramebufferTexture;

import net.luxvacuos.lightengine.client.core.exception.FrameBufferException;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;

public class RenderingPipelineFBO {

	private int fbo;

	private int diffuseTex, positionTex, normalTex, pbrTex, maskTex, depthTex;

	private int width, height;

	public RenderingPipelineFBO(int width, int height) {
		this.width = width;
		this.height = height;
		init(width, height);
	}

	private void init(int width, int height) {

		fbo = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);

		diffuseTex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, diffuseTex);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, diffuseTex, 0);

		positionTex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, positionTex);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGB, GL_FLOAT, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, positionTex, 0);

		normalTex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, normalTex);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB16F, width, height, 0, GL_RGB, GL_FLOAT, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT2, normalTex, 0);

		pbrTex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, pbrTex);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RG, width, height, 0, GL_RG, GL_UNSIGNED_BYTE, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT3, pbrTex, 0);

		maskTex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, maskTex);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT4, maskTex, 0);

		depthTex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, depthTex);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthTex, 0);

		int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		if (status != GL_FRAMEBUFFER_COMPLETE)
			throw new FrameBufferException("Incomplete FrameBuffer ");

		int buffer[] = { GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2, GL_COLOR_ATTACHMENT3,
				GL_COLOR_ATTACHMENT4 };
		glDrawBuffers(buffer);

		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public void cleanUp() {
		glDeleteTextures(diffuseTex);
		glDeleteTextures(positionTex);
		glDeleteTextures(normalTex);
		glDeleteTextures(depthTex);
		glDeleteTextures(pbrTex);
		glDeleteTextures(maskTex);
		glDeleteFramebuffers(fbo);
	}

	public void begin() {
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glViewport(0, 0, width, height);
	}

	public void end() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		GraphicalSubsystem.getMainWindow().resetViewport();
	}

	public int getDiffuseTex() {
		return diffuseTex;
	}

	public int getPositionTex() {
		return positionTex;
	}

	public int getNormalTex() {
		return normalTex;
	}

	public int getPbrTex() {
		return pbrTex;
	}

	public int getMaskTex() {
		return maskTex;
	}

	public int getFbo() {
		return fbo;
	}

	public int getDepthTex() {
		return depthTex;
	}
}
