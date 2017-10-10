import net.luxvacuos.lightengine.demo.MainMenuState;
import net.luxvacuos.lightengine.universal.core.IEngineLoader;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;

public class EngineLoader implements IEngineLoader {

	@Override
	public void loadExternal() {
		TaskManager.addTaskAsync(() -> StateMachine.registerState(new MainMenuState()));
	}

}
