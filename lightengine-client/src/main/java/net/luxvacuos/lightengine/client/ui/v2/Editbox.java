/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2019 Lux Vacuos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.luxvacuos.lightengine.client.ui.v2;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;
import static org.lwjgl.glfw.GLFW.glfwGetClipboardString;
import static org.lwjgl.system.MemoryUtil.NULL;

import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Alignment;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Surface;
import net.luxvacuos.lightengine.client.ui.v2.events.IEditboxEvent;

public class Editbox extends Surface {

	private boolean inside, selected;

	private Text placeholder, input;

	private String inputText = "";

	private IEditboxEvent event = (v) -> {
	};

	public Editbox(String placeholder) {
		this.placeholder = new Text(placeholder);
		this.input = new Text(inputText);
	}

	@Override
	public void init(long ctx, MouseHandler mh, KeyboardHandler kh) {
		super.init(ctx, mh, kh);
		super.addSurface(placeholder.setForegroundColor("#606060FF").setVerticalAlignment(Alignment.CENTER).setMargin(0,
				0, 40, 0));
		super.addSurface(input.setForegroundColor("#000000FF").setVerticalAlignment(Alignment.CENTER));
		super.setBorder(1).setPadding(8, 2);
		super.setBorderColor("#3E3E3EFF").setBackgroundColor("#FFFFFFFF");
		super.setBackgroundHoverColor("#D2D2D2FF");
		super.setBackgroundPressedColor("#AAAAAAFF");
		input.setHidden(true);
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		if (selected) {
			state = SurfaceState.PRESSED;
			inputText = kh.handleInput(inputText);
			input.setText(inputText);
		}
		if (selected && !isCursorInsideSurface() && mh.isButtonPressed(0)) {
			selected = false;
			kh.clearInputData();
			kh.disableTextInput();
			if (inputText.isEmpty()) {
				placeholder.setHidden(false);
				input.setHidden(true);
			}
			event.handleEvent(inputText);
		}
		if (selected && kh.isCtrlPressed() && kh.isKeyPressed(GLFW_KEY_V)) {
			kh.ignoreKeyUntilRelease(GLFW_KEY_V);
			inputText += glfwGetClipboardString(NULL);
		}
	}

	@Override
	protected void handleInput() {
		super.handleInput();
		if (inside = isCursorInsideSurface())
			state = SurfaceState.HOVER;
		if (inside && mh.isButtonPressed(0)) {
			selected = true;
			kh.enableTextInput();
			placeholder.setHidden(true);
			input.setHidden(false);
		}
	}

	public void setEvent(IEditboxEvent event) {
		this.event = event;
	}

	public String getInputText() {
		return inputText;
	}

}
