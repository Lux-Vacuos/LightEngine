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

package net.luxvacuos.lightengine.client.rendering.opengl.v2;

import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_LINEAR;
import static org.lwjgl.opengl.GL11C.GL_RGBA;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glDrawArrays;
import static org.lwjgl.opengl.GL12C.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30C.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30C.GL_RGBA16F;

import org.joml.Vector2f;

import net.luxvacuos.lightengine.client.rendering.opengl.GPUProfiler;
import net.luxvacuos.lightengine.client.rendering.opengl.RenderingSettings;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Framebuffer;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.FramebufferBuilder;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Texture;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.TextureBuilder;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.VAO;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.DeferredPipelineShader;

public class DeferredPass {

	private Framebuffer mainBuf;
	private Texture mainTex;

	private DeferredPipelineShader shader;

	private String name;

	public DeferredPass(String name) {
		this.name = name;
	}

	public void init(int width, int height) {
		generateFramebuffer(width, height);
		shader = new DeferredPipelineShader(name);
		shader.start();
		shader.loadResolution(new Vector2f(width, height));
		shader.stop();
	}

	public void process(RenderingSettings rs, VAO quad) {
		GPUProfiler.start(name);
		mainBuf.bind();
		glClear(GL_COLOR_BUFFER_BIT);
		shader.start();

		// TODO: Load the other stuff
		shader.loadSettings(rs);
		// TODO: Setup image buffers
		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
		shader.stop();
		mainBuf.unbind();
		GPUProfiler.end();
	}

	public void resize(int width, int height) {
		disposeFramebuffer();
		generateFramebuffer(width, height);
		shader.start();
		shader.loadResolution(new Vector2f(width, height));
		shader.stop();
	}

	public void dispose() {
		disposeFramebuffer();
	}

	private void generateFramebuffer(int width, int height) {
		var tb = new TextureBuilder();

		tb.genTexture(GL_TEXTURE_2D).bindTexture();
		tb.sizeTexture(width, height).texImage2D(0, GL_RGBA16F, 0, GL_RGBA, GL_FLOAT, 0);
		tb.texParameteri(GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		tb.texParameteri(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		tb.texParameteri(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		tb.texParameteri(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		var fb = new FramebufferBuilder();

		fb.genFramebuffer().bindFramebuffer().sizeFramebuffer(width, height);
		fb.framebufferTexture(GL_COLOR_ATTACHMENT0, mainTex, 0);
		mainBuf = fb.endFramebuffer();
	}

	private void disposeFramebuffer() {
		mainBuf.dispose();
		mainTex.dispose();
	}

}
