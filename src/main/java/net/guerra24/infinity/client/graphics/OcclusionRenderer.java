package net.guerra24.infinity.client.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.guerra24.infinity.client.graphics.shaders.EntityBasicShader;
import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.client.resources.models.TexturedModel;
import net.guerra24.infinity.client.world.entities.Entity;
import net.guerra24.infinity.client.world.entities.IEntity;
import net.guerra24.infinity.universal.util.vector.Matrix4f;

public class OcclusionRenderer {
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
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

	public void renderEntity(List<IEntity> list, GameResources gm) {
		for (IEntity entity : list) {
			if (entity != null)
				if (entity.getEntity() != null)
					if (gm.getFrustum().pointInFrustum(entity.getEntity().getPosition().x,
							entity.getEntity().getPosition().y, entity.getEntity().getPosition().z))
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
		shader.start();
		shader.loadviewMatrix(gm.getCamera());
		shader.loadProjectionMatrix(gm.getRenderer().getProjectionMatrix());
		renderer.renderBlockEntity(entities, gm);
		shader.stop();
		entities.clear();
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

	/**
	 * Clear the Shader
	 */
	public void cleanUp() {
		shader.cleanUp();
	}

}
