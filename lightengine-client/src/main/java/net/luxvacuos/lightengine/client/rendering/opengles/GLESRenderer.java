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

package net.luxvacuos.lightengine.client.rendering.opengles;

import static org.lwjgl.opengles.GLES20.GL_BACK;
import static org.lwjgl.opengles.GLES20.GL_CULL_FACE;
import static org.lwjgl.opengles.GLES20.GL_DEPTH_TEST;
import static org.lwjgl.opengles.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengles.GLES20.GL_SRC_ALPHA;
import static org.lwjgl.opengles.GLES20.glBlendFunc;
import static org.lwjgl.opengles.GLES20.glCullFace;
import static org.lwjgl.opengles.GLES20.glEnable;

import net.luxvacuos.lightengine.client.network.IRenderData;
import net.luxvacuos.lightengine.client.rendering.IRenderer;
import net.luxvacuos.lightengine.client.rendering.nanovg.IWindow;
import net.luxvacuos.lightengine.client.rendering.opengl.Frustum;
import net.luxvacuos.lightengine.client.rendering.opengl.IRenderPass;
import net.luxvacuos.lightengine.client.rendering.opengl.LightRenderer;

public class GLESRenderer implements IRenderer {

	public GLESRenderer() {
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		// glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
	}

	@Override
	public void init() {
	}

	@Override
	public void render(IRenderData renderData, float delta) {
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void resetState() {
	}

	@Override
	public void setShadowPass(IRenderPass shadowPass) {
	}

	@Override
	public void setDeferredPass(IRenderPass deferredPass) {
	}

	@Override
	public void setForwardPass(IRenderPass forwardPass) {
	}

	@Override
	public void setOcclusionPass(IRenderPass occlusionPass) {
	}

	@Override
	public Frustum getFrustum() {
		return null;
	}

	@Override
	public LightRenderer getLightRenderer() {
		return null;
	}

	@Override
	public IWindow getWindow() {
		return null;
	}

}
