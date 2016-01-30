package net.guerra24.infinity.demo;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glReadPixels;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

import net.guerra24.infinity.client.core.GlobalStates;
import net.guerra24.infinity.client.core.Infinity;
import net.guerra24.infinity.client.core.InfinityVariables;
import net.guerra24.infinity.client.core.State;
import net.guerra24.infinity.client.graphics.VectorsRendering;
import net.guerra24.infinity.client.input.Keyboard;
import net.guerra24.infinity.client.particle.ParticleMaster;
import net.guerra24.infinity.client.particle.ParticleSystem;
import net.guerra24.infinity.client.particle.ParticleTexture;
import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.client.resources.models.Tessellator;
import net.guerra24.infinity.client.resources.models.TessellatorTextureAtlas;
import net.guerra24.infinity.client.resources.models.WaterTile;
import net.guerra24.infinity.client.world.entities.PlayerCamera;
import net.guerra24.infinity.universal.util.vector.Vector2f;
import net.guerra24.infinity.universal.util.vector.Vector3f;

public class Demo implements State {
	private Tessellator tess;
	private List<WaterTile> waters;
	private ParticleTexture fireTexture;
	private ParticleSystem particleSystem;
	
	public Demo() {
		InfinityVariables.WIDTH = 854;
		InfinityVariables.HEIGHT = 480;
	}

	@Override
	public void init(GameResources gm) {
		waters = new ArrayList<WaterTile>();
		int tex = gm.getLoader().loadTextureEntity("TestGrid");
		int normal = gm.getLoader().loadTextureEntity("TestGrid_normal");
		TessellatorTextureAtlas tessAtlas = new TessellatorTextureAtlas(256, 256, 256, tex);
		tessAtlas.registerTextureCoords("None", new Vector2f(256, 256));
		// TexturedModel model = new
		// TexturedModel(gm.getLoader().getObjLoader().loadObjModel("demo"),
		// new ModelTexture(tex));
		// GameEntity demo1 = new GameEntity(model, new Vector3f(0, 0, 0), 0, 0,
		// 0, 1);
		for (int x = 0; x < 5; x++) {
			for (int z = 0; z < 5; z++) {
				waters.add(new WaterTile(x + 2, z + 2, 1.1f));
			}
		}
		// gm.getPhysicsEngine().addEntity(demo1);
		tess = new Tessellator(gm);

		tess.begin(tex, normal, tex);
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				tess.generateCube(x, 0, z, 1, true, true, false, false, false, false, tessAtlas, "None");
			}
		}
		for (int z = 0; z < 16; z++) {
			for (int y = 0; y < 16; y++) {
				tess.generateCube(-1, y + 1, z, 1, false, false, true, false, false, false, tessAtlas, "None");
			}
		}
		tess.end();
		fireTexture = new ParticleTexture(gm.getLoader().loadTextureParticle("fire0"), 4);
		particleSystem = new ParticleSystem(fireTexture, 100, 1, 0, 10, 10);
	}

	@Override
	public void render(Infinity infinity, GlobalStates states, float alpha) {
		GameResources gm = infinity.getGameResources();
		gm.getSun_Camera().setPosition(gm.getCamera().getPosition());
		gm.getFrustum().calculateFrustum(gm.getMasterShadowRenderer().getProjectionMatrix(), gm.getSun_Camera());
		if (InfinityVariables.useShadows) {
			gm.getMasterShadowRenderer().being();
			gm.getRenderer().prepare();
			gm.getMasterShadowRenderer().renderEntity(gm.getPhysicsEngine().getEntities(), gm);
			tess.drawShadow(gm.getSun_Camera());
			gm.getMasterShadowRenderer().end();
		}
		gm.getFrustum().calculateFrustum(gm.getRenderer().getProjectionMatrix(), gm.getCamera());
		gm.getRenderer().prepare();

		gm.getDeferredShadingRenderer().getPost_fbo().begin();
		gm.getRenderer().prepare();
		gm.getSkyboxRenderer().render(InfinityVariables.RED, InfinityVariables.GREEN, InfinityVariables.BLUE, alpha,
				gm);
		tess.draw(gm);
		FloatBuffer p = BufferUtils.createFloatBuffer(1);
		glReadPixels(gm.getDisplay().getDisplayWidth() / 2, gm.getDisplay().getDisplayHeight() / 2, 1, 1,
				GL_DEPTH_COMPONENT, GL_FLOAT, p);
		gm.getCamera().depth = p.get(0);
		gm.getRenderer().renderEntity(gm.getPhysicsEngine().getEntities(), gm);
		gm.getDeferredShadingRenderer().getPost_fbo().end();

		gm.getRenderer().prepare();
		gm.getDeferredShadingRenderer().render(gm);
		ParticleMaster.getInstance().render(gm.getCamera(), gm.getRenderer().getProjectionMatrix());
		gm.getDisplay().beingNVGFrame();
		VectorsRendering.renderText("Test Text", "Roboto-Regular", 10, 10, 20,
				VectorsRendering.rgba(255, 255, 255, 255, VectorsRendering.colorA),
				VectorsRendering.rgba(255, 255, 255, 255, VectorsRendering.colorB));
		gm.getDisplay().endNVGFrame();
	}

	@Override
	public void update(Infinity infinity, GlobalStates states, float delta) {
		GameResources gm = infinity.getGameResources();
		((PlayerCamera) gm.getCamera()).update(delta, gm);

		gm.getPhysicsEngine().update(delta);
		particleSystem.generateParticles(new Vector3f(5, 5, 5), delta);

		gm.update(gm.getSkyboxRenderer().update(delta));
		gm.getRenderer().getWaterRenderer().update(delta);
		ParticleMaster.getInstance().update(delta, gm.getCamera());
		while (Keyboard.next()) {
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				states.loop = false;
			}
		}
	}
}
