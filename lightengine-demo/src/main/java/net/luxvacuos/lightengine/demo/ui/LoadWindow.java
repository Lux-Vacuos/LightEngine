package net.luxvacuos.lightengine.demo.ui;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.WindowMessage;
import net.luxvacuos.lightengine.client.ui.Alignment;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;
import net.luxvacuos.lightengine.client.ui.Spinner;
import net.luxvacuos.lightengine.universal.util.registry.Key;
import net.luxvacuos.lightengine.universal.util.registry.KeyCache;

public class LoadWindow extends ComponentWindow {

	private float timerOnTop;

	public LoadWindow() {
		super(0, (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")), "Loader");
	}

	@Override
	public void initApp() {
		super.setDecorations(false);
		super.setBlurBehind(false);
		super.setBackgroundColor("#FFFFFFFF");
		super.setAlwaysOnTop(true);
		Spinner load = new Spinner(-22, 2, 20);
		load.setWindowAlignment(Alignment.RIGHT_BOTTOM);
		super.addComponent(load);
		super.initApp();
	}

	@Override
	public void alwaysUpdateApp(float delta) {
		timerOnTop += delta;
		if (timerOnTop > 1) {
			GraphicalSubsystem.getWindowManager().bringToFront(this);
			timerOnTop = 0;
		}
		super.alwaysUpdateApp(delta);
	}

	@Override
	public void processWindowMessage(int message, Object param) {
		if (message == WindowMessage.WM_RESIZE) {
			y = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/height"));
			w = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/width"));
			h = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/height"));
		}
		super.processWindowMessage(message, param);
	}

}
