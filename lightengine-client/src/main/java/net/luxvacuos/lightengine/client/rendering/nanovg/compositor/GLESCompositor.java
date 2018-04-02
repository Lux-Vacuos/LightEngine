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

import static org.lwjgl.nanovg.NanoVGGLES3.nvgluBindFramebuffer;
import static org.lwjgl.nanovg.NanoVGGLES3.nvgluCreateFramebuffer;
import static org.lwjgl.nanovg.NanoVGGLES3.nvgluDeleteFramebuffer;
import static org.lwjgl.opengles.GLES20.GL_BLEND;
import static org.lwjgl.opengles.GLES20.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE0;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE2;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE_2D;
import static org.lwjgl.opengles.GLES20.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengles.GLES20.glActiveTexture;
import static org.lwjgl.opengles.GLES20.glBindTexture;
import static org.lwjgl.opengles.GLES20.glClear;
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
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.nanovg.AnimationState;
import net.luxvacuos.lightengine.client.rendering.nanovg.IWindow;
import net.luxvacuos.lightengine.client.rendering.nanovg.shaders.Window3DShader;
import net.luxvacuos.lightengine.client.rendering.nanovg.shaders.WindowManagerShader;
import net.luxvacuos.lightengine.client.rendering.opengl.GPUProfiler;
import net.luxvacuos.lightengine.client.rendering.opengl.Renderer;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.RawModel;
import net.luxvacuos.lightengine.client.util.Maths;

public class GLESCompositor implements ICompositor {

	private static RawModel quad, quadFull;
	private CameraEntity camera;
	private NVGLUFramebuffer fbos[];
	private NVGLUFramebuffer currentWindow, accumulator;

	private Window3DShader shader;
	private WindowManagerShader accumulatorShader;
	private List<GLESCompositorEffect> effects = new ArrayList<>();
	private Map<IWindow, AnimationData> animationData = new HashMap<>();
	private int width, height;
	private long nvg;

	public GLESCompositor(Window window, int width, int height) {
		this.width = width;
		this.height = height;
		this.nvg = window.getNVGID();
		float[] positions = { -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f };
		if (quad == null)
			quad = window.getResourceLoader().loadToVAO(positions, 2);
		float[] positionsFull = { -1, 1, -1, -1, 1, 1, 1, -1 };
		if (quadFull == null)
			quadFull = window.getResourceLoader().loadToVAO(positionsFull, 2);

		fbos = new NVGLUFramebuffer[2];
		accumulator = nvgluCreateFramebuffer(nvg, width, height, 0);
		currentWindow = nvgluCreateFramebuffer(nvg, width, height, 0);
		fbos[0] = accumulator;

		shader = new Window3DShader();
		accumulatorShader = new WindowManagerShader("Accumulator");
		camera = new CameraEntity("");

		// Orthographic mode
		/*
		 * float aspectY = (float) width / (float) height; float aspectX = (float)
		 * height / (float) width; camera.setProjectionMatrix(
		 * Maths.orthographic(-aspectY, aspectY, -aspectY * aspectX, aspectY * aspectX,
		 * 0.1f, 100, true));
		 */
		camera.setProjectionMatrix(Renderer.createProjectionMatrix(width, height, 45, 0.1f, 1000f));
		effects.add(new GLESCompositorEffect(width, height, "GaussianV", nvg) {
			@Override
			protected void prepareTextures(NVGLUFramebuffer[] fbos) {
			}

			@Override
			public void resize(int width, int height) {
				super.resize(width / 4, height / 4);
			}

		});
		effects.add(new GLESCompositorEffect(width / 2, height / 2, "GaussianH", nvg) {
			@Override
			protected void prepareTextures(NVGLUFramebuffer[] fbos) {
				glActiveTexture(GL_TEXTURE2);
				glBindTexture(GL_TEXTURE_2D, fbos[0].texture());
			}

			@Override
			public void resize(int width, int height) {
				super.resize(width / 4, height / 4);
			}

		});
		effects.add(new GLESCompositorEffect(width / 2, height / 2, "Final", nvg) {
			@Override
			protected void prepareTextures(NVGLUFramebuffer[] fbos) {
				glActiveTexture(GL_TEXTURE2);
				glBindTexture(GL_TEXTURE_2D, fbos[0].texture());
			}

		});
	}

	@Override
	public void render(List<IWindow> windows, float delta) {
		camera.setProjectionMatrix(Renderer.createProjectionMatrix(width, height, 45, 0.1f, 1000f));
		camera.afterUpdate(delta);
		GPUProfiler.start("Compositing");
		glDisable(GL_BLEND);
		nvgluBindFramebuffer(nvg, accumulator);
		glClear(GL_COLOR_BUFFER_BIT);
		nvgluBindFramebuffer(nvg, null);
		for (int z = 0; z < windows.size(); z++) {
			IWindow window = windows.get(z);
			if (window.getFBO() != null) {
				GPUProfiler.start(window.getTitle());
				if (!window.isHidden() && !window.isMinimized()) {
					render(window, windows.size() - z, delta);
					for (GLESCompositorEffect compositorEffect : effects) {
						compositorEffect.render(fbos, quadFull, window, currentWindow.texture(), accumulator.texture());
					}
					nvgluBindFramebuffer(nvg, accumulator);
					glClear(GL_COLOR_BUFFER_BIT);
					accumulatorShader.start();
					glBindVertexArray(quadFull.getVaoID());
					glEnableVertexAttribArray(0);
					glActiveTexture(GL_TEXTURE2);
					glBindTexture(GL_TEXTURE_2D, fbos[0].texture());
					glDrawArrays(GL_TRIANGLE_STRIP, 0, quadFull.getVertexCount());
					glDisableVertexAttribArray(0);
					glBindVertexArray(0);
					accumulatorShader.stop();
					nvgluBindFramebuffer(nvg, null);
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
		float aspect = (float) width / (float) height;
		float x = 0, y = 0;
		float divX = (float) window.getFW() / 2f;
		float divY = (float) window.getFH() / 2f;
		x = (float) (window.getFX() + divX) / (float) width;
		y = (float) (window.getFY() - divY) / (float) height;
		x *= aspect;
		x *= 2f;
		y -= 0.5f;
		y *= 2;
		float scaleY = (float) window.getFH() / (float) height;
		float scaleX = (float) window.getFW() / (float) width;
		scaleX *= aspect;
		scaleX *= 2;
		scaleX *= offsetScaleX;
		scaleY *= 2;
		scaleY *= offsetScaleY;
		nvgluBindFramebuffer(nvg, currentWindow);
		glClear(GL_COLOR_BUFFER_BIT);
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
		nvgluBindFramebuffer(nvg, null);
	}

	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		nvgluDeleteFramebuffer(nvg, accumulator);
		nvgluDeleteFramebuffer(nvg, currentWindow);
		accumulator = nvgluCreateFramebuffer(nvg, width, height, 0);
		currentWindow = nvgluCreateFramebuffer(nvg, width, height, 0);
		for (GLESCompositorEffect compositorEffect : effects) {
			compositorEffect.resize(width, height);
		}
	}

	@Override
	public void dispose() {
		nvgluDeleteFramebuffer(nvg, accumulator);
		nvgluDeleteFramebuffer(nvg, currentWindow);
		for (GLESCompositorEffect compositorEffect : effects) {
			compositorEffect.dispose();
		}
		effects.clear();
		shader.dispose();
		accumulatorShader.dispose();
	}

	@Override
	public NVGLUFramebuffer getFinal() {
		return accumulator;
	}

}
