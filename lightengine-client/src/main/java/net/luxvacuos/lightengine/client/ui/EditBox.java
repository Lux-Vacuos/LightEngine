/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2018 Lux Vacuos
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

package net.luxvacuos.lightengine.client.ui;

import org.lwjgl.glfw.GLFW;

import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme;

public class EditBox extends Component {
	private String text, font = "Poppins-Medium";
	private float fontSize = 20f;
	private boolean selected = false;
	private OnAction onAction, onEnterFress;

	public EditBox(float x, float y, float width, float height, String text) {
		this.x = x;
		this.y = y;
		this.w = width;
		this.h = height;
		this.text = text;
	}

	@Override
	public void render(Window window) {
		Theme.renderEditBox(window.getNVGID(), componentState, text, font, rootComponent.rootX + alignedX,
				window.getHeight() - rootComponent.rootY - alignedY - h, w, h, fontSize, selected);
	}

	@Override
	public void update(float delta, Window window) {
		super.update(delta, window);
		KeyboardHandler kb = window.getKeyboardHandler();
		MouseHandler mh = window.getMouseHandler();
		if (insideBox(mh))
			componentState = ComponentState.HOVER;
		if (mh.isButtonPressed(0)) {
			if (insideBox(mh)) {
				kb.enableTextInput();
				kb.clearInputData();
				selected = true;
			} else {
				if (selected && onAction != null)
					onAction.onAction();
				selected = false;
			}
		}
		if (selected) {
			text = kb.handleInput(text);
			componentState = ComponentState.SELECTED;
			if(kb.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) && kb.isKeyPressed(GLFW.GLFW_KEY_V)) {
				kb.ignoreKeyUntilRelease(GLFW.GLFW_KEY_LEFT_CONTROL);
				kb.ignoreKeyUntilRelease(GLFW.GLFW_KEY_V);
				text += GLFW.glfwGetClipboardString(window.getID());
			}
		}
		if (kb.isKeyPressed(GLFW.GLFW_KEY_ENTER)) {
			kb.ignoreKeyUntilRelease(GLFW.GLFW_KEY_ENTER);
			if (onEnterFress != null)
				onEnterFress.onAction();
			text = "";
		}
	}

	public boolean insideBox(MouseHandler mh) {
		return mh.getX() > rootComponent.rootX + alignedX && mh.getY() > rootComponent.rootY + alignedY
				&& mh.getX() < rootComponent.rootX + alignedX + w && mh.getY() < rootComponent.rootY + alignedY + h;
	}

	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setOnUnselect(OnAction onAction) {
		this.onAction = onAction;
	}

	public void setOnEnterFress(OnAction onEnterFress) {
		this.onEnterFress = onEnterFress;
	}
}
