package net.luxvacuos.lightengine.server.core.states;

import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.states.StateNames;

public class Splash extends AbstractState {

	public Splash() {
		super(StateNames.SPLASH_SCREEN);
	}

	@Override
	public void update(float delta) {
		if (TaskManager.tm.isEmpty())
			StateMachine.setCurrentState(StateNames.MAIN);
	}

}
