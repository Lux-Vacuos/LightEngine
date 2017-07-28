package net.luxvacuos.lightengine.demo.ui;

import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgCircle;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVG.nvgFillColor;

import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.themes.Theme;
import net.luxvacuos.lightengine.client.ui.Button;

public class Ball extends Button {

	private int type;

	public Ball(float x, float y, float w, float h, int type) {
		super(x, y, w, h, "");
		this.type = type;
	}

	@Override
	public void render(Window window) {
		if (!enabled)
			return;
		nvgSave(window.getNVGID());
		nvgBeginPath(window.getNVGID());
		nvgCircle(window.getNVGID(), rootComponent.rootX + alignedX + w / 2f,
				window.getHeight() - rootComponent.rootY - alignedY - h / 2f, h / 2f);
		switch (type) {
		case 0:
			nvgFillColor(window.getNVGID(), Theme.setColor(0.8f, 0, 0, 1));
			break;
		case 1:
			nvgFillColor(window.getNVGID(), Theme.setColor(0, 0.8f, 0, 1));
			break;
		case 2:
			nvgFillColor(window.getNVGID(), Theme.setColor(0, 0, 0.8f, 1));
			break;
		case 3:
			nvgFillColor(window.getNVGID(), Theme.setColor(0.8f, 0.8f, 0, 1));
			break;
		}
		nvgFill(window.getNVGID());
		nvgRestore(window.getNVGID());
		
		nvgSave(window.getNVGID());
		nvgBeginPath(window.getNVGID());
		nvgCircle(window.getNVGID(), rootComponent.rootX + alignedX + w / 2f - 8,
				window.getHeight() - rootComponent.rootY - alignedY - h / 2f - 8, h / 5f);
		switch (type) {
		case 0:
			nvgFillColor(window.getNVGID(), Theme.setColor(1, 0.2f, 0.2f, 1f));
			break;
		case 1:
			nvgFillColor(window.getNVGID(), Theme.setColor(0.2f, 1, 0.2f, 1f));
			break;
		case 2:
			nvgFillColor(window.getNVGID(), Theme.setColor(0.2f, 0.2f, 1, 1f));
			break;
		case 3:
			nvgFillColor(window.getNVGID(), Theme.setColor(1, 1, 0.2f, 1f));
			break;
		}
		nvgFill(window.getNVGID());
		nvgRestore(window.getNVGID());
	}
	
	public int getType() {
		return type;
	}

}
