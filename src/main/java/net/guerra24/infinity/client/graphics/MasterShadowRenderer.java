package net.guerra24.infinity.client.graphics;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.glCullFace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.guerra24.infinity.client.graphics.shaders.EntityBasicShader;
import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.client.resources.models.TexturedModel;
import net.guerra24.infinity.client.util.Maths;
import net.guerra24.infinity.client.world.entities.Entity;
import net.guerra24.infinity.client.world.entities.IEntity;
import net.guerra24.infinity.universal.util.vector.Matrix4f;

public class MasterShadowRenderer {

	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private EntityBasicShader shader;
	private ShadowRenderer renderer;
	private FrameBuffer fbo;
	private Matrix4f projectionMatrix;

	/**
	 * Constructor, Initializes the OpenGL code, creates the projection matrix,
	 * entity renderer and skybox renderer
	 * 
	 * @param loader
	 *            Game Loader
	 */
	public MasterShadowRenderer() {
		shader = new EntityBasicShader();
		projectionMatrix = Maths.orthographic(-80, 80, -80, 80, -400, 400);
		renderer = new ShadowRenderer(shader, projectionMatrix);
		fbo = new FrameBuffer(true, 4096, 4096);
	}

	public void being() {
		fbo.begin(4096, 4096);
	}

	public void end() {
		fbo.end();
	}

	public void renderEntity(List<IEntity> list, GameResources gm) {
		for (IEntity entity : list) {
			if (entity != null)
				if (entity.getEntity() != null)
					processEntity(entity.getEntity());
		}
		renderEntity(gm);
	}

	/**
	 * Chunk Rendering PipeLine
	 * 
	 * @param lights
	 *            A list of lights
	 * @param camera
	 *            A Camera
	 */
	private void renderEntity(GameResources gm) {
		glCullFace(GL_FRONT);
		shader.start();
		shader.loadviewMatrix(gm.getSun_Camera());
		renderer.renderBlockEntity(entities, gm);
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
	private void processEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
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
