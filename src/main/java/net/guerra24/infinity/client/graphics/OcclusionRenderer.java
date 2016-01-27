/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 Guerra24
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.guerra24.infinity.client.graphics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.guerra24.infinity.client.graphics.shaders.EntityBasicShader;
import net.guerra24.infinity.client.resources.models.TexturedModel;
import net.guerra24.infinity.client.world.entities.GameEntity;
import net.guerra24.infinity.universal.util.vector.Matrix4f;

public class OcclusionRenderer {
	private Map<TexturedModel, List<GameEntity>> blockEntities = new HashMap<TexturedModel, List<GameEntity>>();
	private EntityBasicShader shader;
	private ShadowRenderer renderer;

	/**
	 * Constructor, Initializes the OpenGL code, creates the projection matrix,
	 * entity renderer and skybox renderer
	 * 
	 * @param loader
	 *            Game Loader
	 */
	public OcclusionRenderer(Matrix4f projectionMatrix) {
		shader = new EntityBasicShader();
		renderer = new ShadowRenderer(shader, projectionMatrix);
	}

	/**
	 * Clear the Shader
	 */
	public void cleanUp() {
		shader.cleanUp();
	}

}
