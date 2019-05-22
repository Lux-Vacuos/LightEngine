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

import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Alignment;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Surface;
import net.luxvacuos.lightengine.client.ui.v2.events.IButtonEvent;

public class Button extends Surface {

	private boolean pressed, inside, insideGlobal;

	protected Text text;

	private IButtonEvent event = () -> {
	};

	public Button(String text) {
		this.text = new Text(text);
	}

	@Override
	public void init(long ctx, MouseHandler mh, KeyboardHandler kh) {
		super.init(ctx, mh, kh);
		super.addSurface(text.setForegroundColor("#000000FF").setVerticalAlignment(Alignment.CENTER)
				.setHorizontalAlignment(Alignment.CENTER));
		super.setBorder(1).setPadding(8, 2);
		super.setBorderColor("#3E3E3EFF").setBackgroundColor("#FFFFFFFF");
		super.setBackgroundHoverColor("#D2D2D2FF");
		super.setBackgroundPressedColor("#AAAAAAFF");
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		insideGlobal = isCursorInsideSurface();
	}

	@Override
	protected void handleInput() {
		super.handleInput();
		if (inside = isCursorInsideSurface()) {
			state = SurfaceState.HOVER;
			if (pressed)
				state = SurfaceState.PRESSED;
		}
		if ((inside && mh.isButtonPressed(0)) || pressed) {
			if (!mh.isButtonPressed(0) && inside && insideGlobal && pressed)
				event.handleEvent();
			pressed = mh.isButtonPressed(0);
		}
	}

	public void setButtonEvent(IButtonEvent event) {
		this.event = event;
	}

}
