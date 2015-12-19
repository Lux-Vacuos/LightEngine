package net.guerra24.infinity.client.core.states;

import net.guerra24.infinity.client.core.GlobalStates;
import net.guerra24.infinity.client.core.GlobalStates.GameState;
import net.guerra24.infinity.client.core.Infinity;
import net.guerra24.infinity.client.core.State;
import net.guerra24.infinity.client.resources.GameResources;

/**
 * Loading Screen State
 * 
 * @author danirod
 * @category Kernel
 */
public class LoadingState extends State {

	public LoadingState() {
		super(5);
	}

	@Override
	public void update(Infinity voxel, GlobalStates states, float delta) {
		GameResources gm = voxel.getGameResources();
			gm.getCamera().setMouse();
		gm.getSoundSystem().rewind("menu1");
		gm.getSoundSystem().stop("menu1");
		gm.getSoundSystem().rewind("menu2");
		gm.getSoundSystem().stop("menu2");
		states.setState(GameState.GAME_SP);
	}

	@Override
	public void render(Infinity voxel, GlobalStates states, float delta) {
		GameResources gm = voxel.getGameResources();
		gm.getRenderer().prepare();
	}

}
