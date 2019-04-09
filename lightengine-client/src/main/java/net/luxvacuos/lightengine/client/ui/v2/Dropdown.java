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

import static net.luxvacuos.lightengine.client.rendering.nanovg.v2.Alignment.CENTER;
import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgLineTo;
import static org.lwjgl.nanovg.NanoVG.nvgMoveTo;
import static org.lwjgl.nanovg.NanoVG.nvgStroke;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeColor;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeWidth;

import java.util.List;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Surface;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.layouts.FlowLayout;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.layouts.FlowLayout.Direction;
import net.luxvacuos.lightengine.client.ui.v2.events.IDropdownEvent;

public class Dropdown<T> extends Surface {

	private T value;
	private List<T> elements;

	private IDropdownEvent<T> event = (v) -> {
	};

	private boolean pressed, inside, insideGlobal;

	private Text text;

	public Dropdown(String text) {
		this.text = new Text(text);
	}

	@Override
	public void init(long ctx, MouseHandler mh, KeyboardHandler kh) {
		super.init(ctx, mh, kh);
		super.setLayout(new FlowLayout());
		super.addSurface(text.setForegroundColor("#000000FF").setVerticalAlignment(CENTER));
		super.addSurface(new Surface() {

			@Override
			public void init(long ctx, MouseHandler mh, KeyboardHandler kh) {
				super.init(ctx, mh, kh);
				super.setWidth(14).setHeight(10);
				super.setMargin(40, 0, 0, 0);
				super.setBackgroundColor("#00000000");
				super.setForegroundColor("#000000FF");
				super.setVerticalAlignment(CENTER);
			}

			@Override
			protected void renderSurface(float delta) {
				super.renderSurface(delta);
				nvgBeginPath(ctx);
				nvgMoveTo(ctx, elementPos.x, elementPos.y);
				nvgLineTo(ctx, elementPos.x + elementPos.z / 2.0f, elementPos.y + elementPos.w);
				nvgLineTo(ctx, elementPos.x + elementPos.z, elementPos.y);
				nvgStrokeColor(ctx, foregroundCurrentColor);
				nvgStrokeWidth(ctx, 1.5f);
				nvgStroke(ctx);
			}
		});
		super.setBorder(1).setPadding(8, 2, 8, 2);
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
				showDropdown();
			pressed = mh.isButtonPressed(0);
		}
	}

	private void showDropdown() {
		Context context = new Context();
		context.setWidth(elementPos.z);
		context.setX(borderPos.x);
		context.setY(borderPos.y + borderPos.w);
		context.setLayout(new FlowLayout().setDirection(Direction.VERTICAL));
		for (T element : elements) {
			ButtonContext<T> btn = new ButtonContext<>(element.toString(), element);
			btn.setButtonEvent(() -> {
				value = btn.getValue();
				text.setText(value.toString());
				context.removeSurfaceFromRoot();
				event.handleEvent(value);
			});
			context.addSurface(btn);
		}
		GraphicalSubsystem.getSurfaceManager().addSurface(context);
	}

	public void setDropdownEvent(IDropdownEvent<T> event) {
		this.event = event;
	}

	public Dropdown<T> setElements(List<T> elements) {
		this.elements = elements;
		return this;
	}

	public T getValue() {
		return value;
	}

}
