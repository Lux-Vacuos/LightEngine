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

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.glCullFace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;

import net.guerra24.infinity.client.graphics.opengl.Display;
import net.guerra24.infinity.client.graphics.shaders.EntityBasicShader;
import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.client.resources.models.TexturedModel;
import net.guerra24.infinity.client.util.Maths;
import net.guerra24.infinity.client.world.entities.GameEntity;
import net.guerra24.infinity.universal.util.vector.Matrix4f;

public class MasterShadowRenderer {

	private Map<TexturedModel, List<GameEntity>> entities = new HashMap<TexturedModel, List<GameEntity>>();
	private EntityBasicShader shader;
	private ShadowRenderer renderer;
	private FrameBuffer fbo;
	private Matrix4f projectionMatrix;
	private Display display;

	/**
	 * Constructor, Initializes the OpenGL code, creates the projection matrix,
	 * entity renderer and skybox renderer
	 * 
	 * @param loader
	 *            Game Loader
	 */
	public MasterShadowRenderer(Display display) {
		this.display = display;
		shader = new EntityBasicShader();
		projectionMatrix = Maths.orthographic(-80, 80, -80, 80, -100, 100);
		renderer = new ShadowRenderer(shader, projectionMatrix);
		fbo = new FrameBuffer(true, 4096, 4096, display);
	}

	public void being() {
		fbo.begin(4096, 4096);
	}

	public void end() {
		fbo.end(display);
	}

	public void renderEntity(ImmutableArray<Entity> immutableArray, GameResources gm) {
		for (Entity entity : immutableArray) {
			if (entity instanceof GameEntity)
				processEntity((GameEntity) entity);
		}
		renderEntity(gm);
	}

	private void renderEntity(GameResources gm) {
		glCullFace(GL_FRONT);
		shader.start();
		shader.loadviewMatrix(gm.getSun_Camera());
		renderer.renderEntity(entities, gm);
		shader.stop();
		entities.clear();
		glCullFace(GL_BACK);
	}

	/**
	 * Add the Entity to the batcher map
	 * 
	 * @param entity
	 *            An Entity
	 */
	private void processEntity(GameEntity entity) {
		TexturedModel entityModel = entity.getModel();
		List<GameEntity> batch = entities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<GameEntity> newBatch = new ArrayList<GameEntity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}

	public FrameBuffer getFbo() {
		return fbo;
	}

	/**
	 * Clear the Shader
	 */
	public void cleanUp() {
		shader.cleanUp();
		fbo.cleanUp();
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

}
