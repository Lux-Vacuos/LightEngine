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

package net.luxvacuos.lightengine.client.rendering.nanovg.compositor;

import static org.lwjgl.nanovg.NanoVGGL3.nvgluBindFramebuffer;
import static org.lwjgl.nanovg.NanoVGGL3.nvgluCreateFramebuffer;
import static org.lwjgl.nanovg.NanoVGGL3.nvgluDeleteFramebuffer;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import org.joml.Vector2f;
import org.lwjgl.nanovg.NVGLUFramebuffer;

import net.luxvacuos.lightengine.client.rendering.nanovg.IWindow;
import net.luxvacuos.lightengine.client.rendering.nanovg.shaders.WindowManagerShader;
import net.luxvacuos.lightengine.client.rendering.opengl.GPUProfiler;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.RawModel;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public abstract class GLCompositorEffect implements IDisposable {

	private WindowManagerShader shader;
	private String name;
	private NVGLUFramebuffer fbo;
	private long nvg;
	private int width, height;

	public GLCompositorEffect(int width, int height, String name, long nvg) {
		this.name = name;
		this.nvg = nvg;
		this.width = width;
		this.height = height;
		shader = new WindowManagerShader(name);
		shader.start();
		shader.loadResolution(new Vector2f(width, height));
		shader.stop();
		fbo = nvgluCreateFramebuffer(nvg, width, height, 0);
	}

	public void render(NVGLUFramebuffer fbos[], RawModel quad, IWindow window, int currentWindow, int accumulator) {
		GPUProfiler.start(name);
		nvgluBindFramebuffer(nvg, fbo);
		glViewport(0, 0, width, height);
		glClear(GL_COLOR_BUFFER_BIT);
		shader.start();
		shader.loadBlurBehind(window.hasBlurBehind());
		shader.loadWindowPosition(new Vector2f(window.getX(), window.getY()));
		glBindVertexArray(quad.getVaoID());
		glEnableVertexAttribArray(0);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, currentWindow);
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, accumulator);
		prepareTextures(fbos);
		glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		shader.stop();
		nvgluBindFramebuffer(nvg, null);
		GPUProfiler.end();
		fbos[0] = fbo;
	}

	protected abstract void prepareTextures(NVGLUFramebuffer fbos[]);

	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		nvgluDeleteFramebuffer(nvg, fbo);
		fbo = nvgluCreateFramebuffer(nvg, width, height, 0);
		shader.start();
		shader.loadResolution(new Vector2f(width, height));
		shader.stop();
	}

	@Override
	public void dispose() {
		shader.dispose();
		nvgluDeleteFramebuffer(nvg, fbo);
	}

}