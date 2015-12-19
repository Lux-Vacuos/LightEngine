package net.guerra24.infinity.client.core.states;

import net.guerra24.infinity.client.core.GlobalStates;
import net.guerra24.infinity.client.core.State;
import net.guerra24.infinity.client.core.Infinity;
import net.guerra24.infinity.client.core.InfinityVariables;
import net.guerra24.infinity.client.core.GlobalStates.GameState;
import net.guerra24.infinity.client.input.Mouse;
import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.universal.util.vector.Vector3f;

/**
 * Options Menu State
 * 
 * @author danirod
 * @category Kernel
 */
public class OptionsState extends State {

	public OptionsState() {
		super(3);
	}

	@Override
	public void update(Infinity voxel, GlobalStates states, float delta) {
		GameResources gm = voxel.getGameResources();

		while (Mouse.next()) {
			if (gm.getMenuSystem().optionsMenu.getShadowsButton().pressed())
				InfinityVariables.useShadows = !InfinityVariables.useShadows;
			if (gm.getMenuSystem().optionsMenu.getDofButton().pressed())
				InfinityVariables.useDOF = !InfinityVariables.useDOF;
			if (gm.getMenuSystem().optionsMenu.getGodraysButton().pressed())
				InfinityVariables.useVolumetricLight = !InfinityVariables.useVolumetricLight;
		}
		if (gm.getMenuSystem().optionsMenu.getExitButton().pressed()) {
			gm.getMenuSystem().mainMenu.load(gm);
			gm.getCamera().setPosition(new Vector3f(0, 0, 1));
			gm.getGameSettings().updateSetting();
			gm.getGameSettings().save();
			states.setState(GameState.MAINMENU);
		}
	}

	@Override
	public void render(Infinity voxel, GlobalStates states, float delta) {
		GameResources gm = voxel.getGameResources();
		gm.getMenuSystem().optionsMenu.update(gm);
		gm.getFrustum().calculateFrustum(gm.getRenderer().getProjectionMatrix(), gm.getCamera());
		gm.getRenderer().prepare();
	}

}
