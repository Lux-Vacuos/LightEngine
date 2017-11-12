package net.luxvacuos.lightengine.demo.ui;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.LANG;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;

import java.util.Arrays;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.NanoWindow;
import net.luxvacuos.lightengine.client.ui.Alignment;
import net.luxvacuos.lightengine.client.ui.Button;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;
import net.luxvacuos.lightengine.client.ui.DropDown;
import net.luxvacuos.lightengine.client.ui.EditBox;
import net.luxvacuos.lightengine.client.ui.Text;
import net.luxvacuos.lightengine.demo.Global;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;

public class MultiplayerWindow extends ComponentWindow {

	private NanoWindow root;
	private String level;

	public MultiplayerWindow(int x, int y, int w, int h, NanoWindow root) {
		super(x, y, w, h, LANG.getRegistryItem("lightengine.mpwindow.name"));
		this.root = root;
	}

	@Override
	public void initApp() {
		super.setBackgroundColor(0.4f, 0.4f, 0.4f, 1f);
		super.setResizable(false);

		Button playButton = new Button(0, 40, 200, 40, LANG.getRegistryItem("lightengine.mpwindow.btnplay"));
		playButton.setAlignment(Alignment.CENTER);
		playButton.setWindowAlignment(Alignment.BOTTOM);
		EditBox address = new EditBox(0, -30, 300, 30, "");
		address.setAlignment(Alignment.CENTER);
		address.setWindowAlignment(Alignment.CENTER);
		address.setFontSize(25);
		Text text = new Text(LANG.getRegistryItem("lightengine.mpwindow.txtlvl"), 0, 80);
		text.setAlign(NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
		text.setWindowAlignment(Alignment.CENTER);
		Text textAdd = new Text(LANG.getRegistryItem("lightengine.mpwindow.txtadd"), 0, 0);
		textAdd.setAlign(NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
		textAdd.setWindowAlignment(Alignment.CENTER);

		playButton.setOnButtonPress(() -> {
			if (level == "Select Level")
				return;
			String ip = address.getText();
			Global.ip = ip;
			address.setText("");
			GraphicalSubsystem.getWindowManager().toggleShell();
			super.closeWindow();
			root.setWindowClose(WindowClose.DISPOSE);
			root.closeWindow();
			TaskManager.addTaskUpdate(() -> StateMachine.setCurrentState(level));
		});

		DropDown<String> levels = new DropDown<>(0, 50, 300, 30, "Select Level",
				Arrays.asList("Level0", "Level1", "Level2", "Level3"));
		levels.setAlignment(Alignment.CENTER);
		levels.setWindowAlignment(Alignment.CENTER);

		levels.setOnButtonPress(() -> {
			level = levels.getValue();
		});

		super.addComponent(address);
		super.addComponent(text);
		super.addComponent(playButton);
		super.addComponent(textAdd);
		super.addComponent(levels);
		super.initApp();
	}

}