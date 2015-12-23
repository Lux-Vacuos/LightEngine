package net.guerra24.infinity.client.core.states;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glReadPixels;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import net.guerra24.infinity.client.core.GlobalStates;
import net.guerra24.infinity.client.core.GlobalStates.GameState;
import net.guerra24.infinity.client.core.Infinity;
import net.guerra24.infinity.client.core.InfinityVariables;
import net.guerra24.infinity.client.core.State;
import net.guerra24.infinity.client.graphics.opengl.Display;
import net.guerra24.infinity.client.particle.ParticleMaster;
import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.client.resources.models.Tessellator;

/**
 * Single Player GameState
 * 
 * @author danirod
 * @category Kernel
 */
public class GameSPState extends State {

	public GameSPState() {
		super(2);
	}

	@Override
	public void update(Infinity voxel, GlobalStates states, float delta) {
		GameResources gm = voxel.getGameResources();
		Display display = voxel.getDisplay();

		gm.getCamera().update(delta, gm, voxel.getClient());
		gm.getPhysics().getMobManager().getPlayer().update(delta, gm);
		gm.getPhysics().getMobManager().update(delta, gm);
		gm.update(gm.getSkyboxRenderer().update(delta));
		gm.getRenderer().getWaterRenderer().update(delta);
		ParticleMaster.getInstance().update(delta, gm.getCamera());

		if (!display.isDisplayFocused() && !InfinityVariables.debug) {
			gm.getCamera().unlockMouse();
			states.setState(GameState.IN_PAUSE);
		}
	}

	@Override
	public void render(Infinity voxel, GlobalStates states, float delta) {
		GameResources gm = voxel.getGameResources();

		gm.getFrustum().calculateFrustum(gm.getRenderer().getProjectionMatrix(), gm.getCamera());
		gm.getSun_Camera().setPosition(gm.getCamera().getPosition());
		gm.getRenderer().prepare();
		if (InfinityVariables.useShadows) {
			gm.getMasterShadowRenderer().being();
			gm.getRenderer().prepare();
			for (Tessellator tess : gm.demo.getModels()) {
				tess.drawShadow(gm.getSun_Camera());
			}
			gm.getMasterShadowRenderer().renderEntity(gm.getPhysics().getMobManager().getMobs(), gm);
			gm.getMasterShadowRenderer().renderEntity(gm.demo.getMobs(), gm);
			gm.getMasterShadowRenderer().end();
		}
		gm.getDeferredShadingRenderer().getPost_fbo().begin();
		gm.getRenderer().prepare();
		for (Tessellator tess : gm.demo.getModels()) {
			tess.draw(gm);
		}
		FloatBuffer p = BufferUtils.createFloatBuffer(1);
		glReadPixels(Display.getWidth() / 2, Display.getHeight() / 2, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, p);
		gm.getCamera().depth = p.get(0);
		gm.getSkyboxRenderer().render(InfinityVariables.RED, InfinityVariables.GREEN, InfinityVariables.BLUE, delta,
				gm);
		gm.getRenderer().renderEntity(gm.getPhysics().getMobManager().getMobs(), gm);
		gm.getRenderer().renderEntity(gm.demo.getMobs(), gm);
		gm.getRenderer().getWaterRenderer().render(gm.demo.getWaters(), gm);
		ParticleMaster.getInstance().render(gm.getCamera());
		gm.getDeferredShadingRenderer().getPost_fbo().end();

		gm.getRenderer().prepare();
		gm.getDeferredShadingRenderer().render(gm);

	}

}
