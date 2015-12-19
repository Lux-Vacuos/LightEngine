package net.guerra24.infinity.client.core.states;

import net.guerra24.infinity.client.core.GlobalStates;
import net.guerra24.infinity.client.core.GlobalStates.GameState;
import net.guerra24.infinity.client.core.State;
import net.guerra24.infinity.client.core.Infinity;
import net.guerra24.infinity.client.core.InfinityVariables;
import net.guerra24.infinity.client.input.Mouse;
import net.guerra24.infinity.client.particle.ParticleMaster;
import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.universal.util.vector.Vector3f;

/**
 * In Pause State
 * 
 * @author danirod
 * @category Kernel
 */
public class InPauseState extends State {

	public InPauseState() {
		super(4);
	}

	@Override
	public void update(Infinity voxel, GlobalStates states, float delta) {
		GameResources gm = voxel.getGameResources();
		while (Mouse.next()) {
			if (gm.getMenuSystem().pauseMenu.getBackToMain().pressed()) {
				gm.getMenuSystem().mainMenu.load(gm);
				gm.getCamera().setPosition(new Vector3f(0, 0, 1));
				gm.getCamera().setPitch(0);
				gm.getCamera().setYaw(0);
				states.setState(GameState.MAINMENU);
			}
		}
	}

	@Override
	public void render(Infinity voxel, GlobalStates states, float delta) {
		GameResources gm = voxel.getGameResources();
		gm.getFrustum().calculateFrustum(gm.getRenderer().getProjectionMatrix(), gm.getCamera());
		gm.getRenderer().prepare();
		if (InfinityVariables.useShadows) {
			gm.getMasterShadowRenderer().being();
			gm.getRenderer().prepare();
			gm.getMasterShadowRenderer().end();
		}
		gm.getDeferredShadingRenderer().getPost_fbo().begin();
		gm.getRenderer().prepare();
		gm.getSkyboxRenderer().render(InfinityVariables.RED, InfinityVariables.GREEN, InfinityVariables.BLUE, delta, gm);
		gm.getRenderer().renderEntity(gm.getPhysics().getMobManager().getMobs(), gm);
		ParticleMaster.getInstance().render(gm.getCamera());
		gm.getDeferredShadingRenderer().getPost_fbo().end();
		gm.getRenderer().prepare();
		gm.getDeferredShadingRenderer().render(gm);
	}

}
