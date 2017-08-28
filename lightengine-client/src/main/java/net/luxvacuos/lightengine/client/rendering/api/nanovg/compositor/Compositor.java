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

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.ArrayList;
import java.util.List;

import net.luxvacuos.igl.vector.Vector3d;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.IWindow;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.shaders.Window3DShader;
import net.luxvacuos.lightengine.client.rendering.api.opengl.Renderer;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.RawModel;
import net.luxvacuos.lightengine.client.util.Maths;

public class Compositor {

	private static RawModel quad;
	private List<CompositorEffect> effects;
	private CameraEntity camera;

	private Window3DShader shader;

	public Compositor(Window window, int width, int height) {
		effects = new ArrayList<>();
		float[] positions = { -0f,0f, -0f, -1f, 1f, 0f, 1f, -1f };
		if (quad == null)
			quad = window.getResourceLoader().loadToVAO(positions, 2);
		shader = new Window3DShader();
		camera = new CameraEntity("");
		camera.setProjectionMatrix(Renderer.createProjectionMatrix(width, height, 90, 0.1f, 1000f));
	}

	public void render(IWindow window, Window wnd, int z, float delta) {
		float aspect = (float) wnd.getWidth() / (float) wnd.getHeight();
		float x = 0, y = 0;
		x = (float) window.getFX() / (float) wnd.getWidth();
		y = (float) window.getFY() / (float) wnd.getHeight();
		x *= aspect;
		x *= 2f;
		y -= 0.5f;
		y *= 2;
		float scaleY = (float) window.getFH() / (float) wnd.getHeight();
		float scaleX = (float) window.getFW() / (float) wnd.getWidth();
		scaleX *= aspect;
		scaleX *= 2;
		scaleY *= 2;
		camera.afterUpdate(0);
		shader.start();
		shader.loadProjectionMatrix(camera.getProjectionMatrix());
		shader.loadTransformationMatrix(
				Maths.createTransformationMatrix(new Vector3d(x - aspect, y, -1), 0, 0, 0, scaleX, scaleY, 1));
		shader.loadViewMatrix(camera);
		glBindVertexArray(quad.getVaoID());
		glEnableVertexAttribArray(0);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, window.getFBO().texture());
		glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		shader.stop();
	}

	public void dispose(Window window) {
		for (CompositorEffect compositorEffect : effects) {
			compositorEffect.dispose();
		}
		effects.clear();
		shader.dispose();
	}

	public void addEffect(CompositorEffect effect) {
		effects.add(effect);
	}

}
