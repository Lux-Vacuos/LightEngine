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

package net.luxvacuos.lightengine.client.rendering.api.nanovg.compositor;

import static org.lwjgl.nanovg.NanoVGGLES3.nvgluBindFramebuffer;
import static org.lwjgl.nanovg.NanoVGGLES3.nvgluCreateFramebuffer;
import static org.lwjgl.nanovg.NanoVGGLES3.nvgluDeleteFramebuffer;
import static org.lwjgl.opengles.GLES20.GL_BLEND;
import static org.lwjgl.opengles.GLES20.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE0;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE_2D;
import static org.lwjgl.opengles.GLES20.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengles.GLES20.glActiveTexture;
import static org.lwjgl.opengles.GLES20.glBindTexture;
import static org.lwjgl.opengles.GLES20.glDisable;
import static org.lwjgl.opengles.GLES20.glDisableVertexAttribArray;
import static org.lwjgl.opengles.GLES20.glDrawArrays;
import static org.lwjgl.opengles.GLES20.glEnableVertexAttribArray;
import static org.lwjgl.opengles.GLES30.glBindVertexArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.lwjgl.nanovg.NVGLUFramebuffer;

import com.badlogic.gdx.math.Interpolation;

import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.AnimationState;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.IWindow;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.shaders.Window3DShader;
import net.luxvacuos.lightengine.client.rendering.api.opengles.GPUProfiler;
import net.luxvacuos.lightengine.client.rendering.api.opengles.Renderer;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.RawModel;
import net.luxvacuos.lightengine.client.util.Maths;

public class Compositor {

	private static RawModel quad, quadFull;
	private CameraEntity camera;
	private NVGLUFramebuffer fbos[];
	private NVGLUFramebuffer currentWindow;

	private Window3DShader shader;
	private Window window;
	private List<CompositorEffect> effects = new ArrayList<>();
	private Map<IWindow, AnimationData> animationData = new HashMap<>();

	public Compositor(Window window, int width, int height) {
		this.window = window;
		fbos = new NVGLUFramebuffer[2];
		fbos[0] = nvgluCreateFramebuffer(window.getNVGID(), width, height, 0);
		fbos[1] = nvgluCreateFramebuffer(window.getNVGID(), width, height, 0);
		currentWindow = nvgluCreateFramebuffer(window.getNVGID(), width, height, 0);
		float[] positions = { -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f };
		if (quad == null)
			quad = window.getResourceLoader().loadToVAO(positions, 2);
		float[] positionsFull = { -1, 1, -1, -1, 1, 1, 1, -1 };
		if (quadFull == null)
			quadFull = window.getResourceLoader().loadToVAO(positionsFull, 2);
		shader = new Window3DShader();
		camera = new CameraEntity("");
		// Orthographic mode
		/*
		 * float aspectY = (float) this.window.getWidth() / (float)
		 * this.window.getHeight(); float aspectX = (float) this.window.getHeight() /
		 * (float) this.window.getWidth(); camera.setProjectionMatrix(
		 * Maths.orthographic(-aspectY, aspectY, -aspectY * aspectX, aspectY * aspectX,
		 * 0.1f, 100, true));
		 */
		camera.setProjectionMatrix(
				Renderer.createProjectionMatrix(this.window.getWidth(), this.window.getHeight(), 45, 0.1f, 1000f));
		effects.add(new MaskBlur(width, height));
		effects.add(new GaussianV(width / 2, height / 2));
		effects.add(new GaussianH(width / 2, height / 2));
		effects.add(new Final(width, height));
	}

	public void render(List<IWindow> windows, float delta) {
		camera.setProjectionMatrix(
				Renderer.createProjectionMatrix(this.window.getWidth(), this.window.getHeight(), 45, 0.1f, 1000f));
		camera.afterUpdate(delta);
		GPUProfiler.start("Compositing");
		glDisable(GL_BLEND);
		nvgluBindFramebuffer(this.window.getNVGID(), fbos[0]);
		Renderer.clearColors(0f, 0f, 0f, 0);
		Renderer.clearBuffer(GL_COLOR_BUFFER_BIT);
		nvgluBindFramebuffer(this.window.getNVGID(), null);
		for (int z = 0; z < windows.size(); z++) {
			IWindow window = windows.get(z);
			if (window.getFBO() != null) {
				GPUProfiler.start(window.getTitle());
				if (!window.isHidden() && !window.isMinimized()) {
					render(window, windows.size() - z, delta);
					for (CompositorEffect compositorEffect : effects) {
						NVGLUFramebuffer tmp = fbos[0];
						fbos[0] = fbos[1];
						fbos[1] = tmp;
						compositorEffect.render(fbos, quadFull, this.window, window, currentWindow.texture());
					}
				}
				GPUProfiler.end();
			}
		}
		GPUProfiler.end();
	}

	private void render(IWindow window, int z, float delta) {
		float offsetX = 0, offsetY = 0, offsetScaleX = 1, offsetScaleY = 1, offsetRotX = 0, offsetRotY = 0,
				offsetRotZ = 0;
		switch (window.getAnimationState()) {
		case CLOSE:
			AnimationData data = animationData.get(window);
			if (data != null) {
				data.y -= delta * 4f;
				if (data.y <= -2) {
					data.y = -2;
					window.setAnimationState(AnimationState.AFTER_CLOSE);
					animationData.remove(window);
				}
				data.scaleX = Interpolation.exp5Out.apply(1, 0.25f, 1 - (1 + data.y / 2f));
				data.scaleY = Interpolation.exp5Out.apply(1, 0.25f, 1 - (1 + data.y / 2f));
				data.rotX = Interpolation.sineOut.apply(0, -90, 1 - (1 + data.y / 2f));
				offsetY = data.y;
				offsetScaleX = data.scaleX;
				offsetScaleY = data.scaleY;
				offsetRotX = data.rotX;
			} else {
				data = new AnimationData();
				data.y = 0;
				data.scaleX = 1;
				data.scaleY = 1;
				data.rotX = 0;
				offsetY = data.y;
				offsetScaleX = data.scaleX;
				offsetScaleY = data.scaleY;
				offsetRotX = data.rotX;
				animationData.put(window, data);
			}
			break;
		case NONE:
			break;
		case OPEN:
			data = animationData.get(window);
			if (data != null) {
				data.y -= delta * 4f;
				if (data.y <= 0) {
					data.y = 0;
					window.setAnimationState(AnimationState.NONE);
					animationData.remove(window);
				}
				data.scaleX = Interpolation.exp5In.apply(0.25f, 1, 1 - data.y / 2f);
				data.scaleY = Interpolation.exp5In.apply(0.25f, 1, 1 - data.y / 2f);
				data.rotX = Interpolation.sineIn.apply(90, 0, 1 - data.y / 2f);
				offsetY = data.y;
				offsetScaleX = data.scaleX;
				offsetScaleY = data.scaleY;
				offsetRotX = data.rotX;
			} else {
				data = new AnimationData();
				data.y = 2;
				data.scaleX = 0.25f;
				data.scaleY = 0.25f;
				data.rotX = 90;
				offsetY = data.y;
				offsetScaleX = data.scaleX;
				offsetScaleY = data.scaleY;
				offsetRotX = data.rotX;
				animationData.put(window, data);
			}
			break;
		case AFTER_CLOSE:
			offsetY = -100;
			offsetX = -100;
			break;
		case MINIMIZE:
			data = animationData.get(window);
			if (data != null) {
				data.y -= delta * 5f;
				if (data.y <= -1) {
					data.y = -1;
					data.x += delta * 3f;
					if (data.x >= 1) {
						window.setAnimationState(AnimationState.AFTER_MINIMIZE);
						animationData.remove(window);
					}
				}
				data.scaleX = Interpolation.sineIn.apply(1, 0.9f, 1 - (1 + data.y));
				data.scaleY = Interpolation.sineIn.apply(1, 0.9f, 1 - (1 + data.y));
				offsetX = Interpolation.exp5In.apply(0, -4, data.x);
				offsetScaleX = data.scaleX;
				offsetScaleY = data.scaleY;
			} else {
				data = new AnimationData();
				data.y = 0;
				data.scaleX = 1;
				data.scaleY = 1;
				offsetScaleX = data.scaleX;
				offsetScaleY = data.scaleY;
				animationData.put(window, data);
			}
			break;
		case AFTER_MINIMIZE:
			break;
		case RESTORE_MINIMIZE:
			data = animationData.get(window);
			if (data != null) {
				data.x += delta * 3f;
				if (data.x >= 1) {
					data.x = 1;
					data.y += delta * 5f;
					if (data.y >= 1) {
						window.setAnimationState(AnimationState.NONE);
						animationData.remove(window);
					}
				}
				data.scaleX = Interpolation.sineOut.apply(0.9f, 1f, data.y);
				data.scaleY = Interpolation.sineOut.apply(0.9f, 1f, data.y);
				offsetX = Interpolation.exp5Out.apply(-4, 0, data.x);
				offsetScaleX = data.scaleX;
				offsetScaleY = data.scaleY;
			} else {
				data = new AnimationData();
				data.y = 0;
				data.x = 0;
				data.scaleX = 1;
				data.scaleY = 1;
				offsetX = -4;
				offsetScaleX = data.scaleX;
				offsetScaleY = data.scaleY;
				animationData.put(window, data);
			}
			break;
		}
		float aspect = (float) this.window.getWidth() / (float) this.window.getHeight();
		float x = 0, y = 0;
		float divX = (float) window.getFW() / 2f;
		float divY = (float) window.getFH() / 2f;
		x = (float) (window.getFX() + divX) / (float) this.window.getWidth();
		y = (float) (window.getFY() - divY) / (float) this.window.getHeight();
		x *= aspect;
		x *= 2f;
		y -= 0.5f;
		y *= 2;
		float scaleY = (float) window.getFH() / (float) this.window.getHeight();
		float scaleX = (float) window.getFW() / (float) this.window.getWidth();
		scaleX *= aspect;
		scaleX *= 2;
		scaleX *= offsetScaleX;
		scaleY *= 2;
		scaleY *= offsetScaleY;
		nvgluBindFramebuffer(this.window.getNVGID(), currentWindow);
		Renderer.clearBuffer(GL_COLOR_BUFFER_BIT);
		shader.start();
		shader.loadProjectionMatrix(camera.getProjectionMatrix());
		shader.loadViewMatrix(camera);
		glBindVertexArray(quad.getVaoID());
		glEnableVertexAttribArray(0);
		shader.loadTransformationMatrix(
				Maths.createTransformationMatrix(new Vector3f(x - aspect + offsetX, y + offsetY, -2.414f), offsetRotX,
						offsetRotY, offsetRotZ, scaleX, scaleY, 1));
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, window.getFBO().texture());
		glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		shader.stop();
		nvgluBindFramebuffer(this.window.getNVGID(), null);
	}

	public void dispose() {
		nvgluDeleteFramebuffer(window.getNVGID(), fbos[0]);
		nvgluDeleteFramebuffer(window.getNVGID(), fbos[1]);
		nvgluDeleteFramebuffer(window.getNVGID(), currentWindow);
		for (CompositorEffect compositorEffect : effects) {
			compositorEffect.dispose();
		}
		effects.clear();
		shader.dispose();
	}

	public NVGLUFramebuffer[] getFbos() {
		return fbos;
	}

}
