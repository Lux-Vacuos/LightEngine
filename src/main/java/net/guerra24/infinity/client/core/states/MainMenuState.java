package net.guerra24.infinity.client.core.states;

import net.guerra24.infinity.client.core.GlobalStates;
import net.guerra24.infinity.client.core.GlobalStates.GameState;
import net.guerra24.infinity.client.core.Infinity;
import net.guerra24.infinity.client.core.State;
import net.guerra24.infinity.client.graphics.opengl.Display;
import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.universal.util.vector.Vector3f;

/**
 * Main Menu State
 * 
 * @author danirod
 * @category Kernel
 */
public class MainMenuState extends State {

	public MainMenuState() {
		super(1);
	}

	@Override
	public void render(Infinity voxel, GlobalStates states, float delta) {
		GameResources gm = voxel.getGameResources();
		gm.getFrustum().calculateFrustum(gm.getRenderer().getProjectionMatrix(), gm.getCamera());
		gm.getRenderer().prepare();
		Display.beingNVGFrame();
		gm.getMenuSystem().mainMenu.render();
		Display.endNVGFrame();
	}

	@Override
	public void update(Infinity voxel, GlobalStates states, float delta) {
		GameResources gm = voxel.getGameResources();

		if (gm.getMenuSystem().mainMenu.getPlayButton().pressed()) {
			states.setState(GameState.LOADING_WORLD);
		} else if (gm.getMenuSystem().mainMenu.getExitButton().pressed()) {
			states.loop = false;
		} else if (gm.getMenuSystem().mainMenu.getOptionsButton().pressed()) {
			gm.getCamera().setPosition(new Vector3f(-1.4f, -3.4f, 1.4f));
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			states.setState(GameState.OPTIONS);
		}
	}

}
