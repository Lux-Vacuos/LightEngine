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

import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgLineTo;
import static org.lwjgl.nanovg.NanoVG.nvgMoveTo;
import static org.lwjgl.nanovg.NanoVG.nvgStroke;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeColor;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeWidth;

import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Alignment;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Surface;

public class ScrollbarVertical extends Surface {

	@Override
	public void init(long ctx, MouseHandler mh, KeyboardHandler kh) {
		super.init(ctx, mh, kh);
		super.setVerticalAlignment(Alignment.STRETCH);
		super.setWidth(16);
		super.setBackgroundColor("#505050FF");
		super.addSurface(new ScrollbarButton() {
			@Override
			protected void renderSurface(float delta) {
				super.renderSurface(delta);
				nvgBeginPath(ctx);
				nvgMoveTo(ctx, elementPos.x + 2, elementPos.y + elementPos.w - 4);
				nvgLineTo(ctx, elementPos.x + elementPos.z / 2.0f, elementPos.y + 4);
				nvgLineTo(ctx, elementPos.x + elementPos.z - 2, elementPos.y + elementPos.w - 4);
				nvgStrokeColor(ctx, foregroundCurrentColor);
				nvgStrokeWidth(ctx, 1.5f);
				nvgStroke(ctx);
			}
		});
		super.addSurface(new ScrollbarButton() {
			@Override
			public void init(long ctx, MouseHandler mh, KeyboardHandler kh) {
				super.init(ctx, mh, kh);
				super.setVerticalAlignment(Alignment.BOTTOM);
			}

			@Override
			protected void renderSurface(float delta) {
				super.renderSurface(delta);
				nvgBeginPath(ctx);
				nvgMoveTo(ctx, elementPos.x + 2, elementPos.y + 4);
				nvgLineTo(ctx, elementPos.x + elementPos.z / 2.0f, elementPos.y + elementPos.w - 4);
				nvgLineTo(ctx, elementPos.x + elementPos.z - 2, elementPos.y + 4);
				nvgStrokeColor(ctx, foregroundCurrentColor);
				nvgStrokeWidth(ctx, 1.5f);
				nvgStroke(ctx);
			}
		});
		super.addSurface(new Surface() {
			private boolean pressed, inside;

			@Override
			public void init(long ctx, MouseHandler mh, KeyboardHandler kh) {
				super.init(ctx, mh, kh);
				super.setBackgroundColor("#404040FF");
				super.setBackgroundHoverColor("#FFFFFF20");
				super.setBackgroundPressedColor("#FFFFFF40");
				super.setWidth(16);
				super.setHeight(80);
				super.setY(16);
			}

			@Override
			protected void handleInput() {
				super.handleInput();
				if (inside = isCursorInsideSurface()) {
					state = SurfaceState.HOVER;
					if (pressed = mh.isButtonPressed(0))
						state = SurfaceState.PRESSED;
				}
				if (inside && pressed) {
				}
			}
		});
	}

	class ScrollbarButton extends Surface {

		private boolean pressed, inside;

		public void init(long ctx, MouseHandler mh, KeyboardHandler kh) {
			super.init(ctx, mh, kh);
			super.setWidth(16).setHeight(16);
			super.setBackgroundHoverColor("#FFFFFF20");
			super.setBackgroundPressedColor("#FFFFFF40");
		}

		@Override
		protected void handleInput() {
			super.handleInput();
			if (inside = isCursorInsideSurface()) {
				state = SurfaceState.HOVER;
				if (pressed = mh.isButtonPressed(0))
					state = SurfaceState.PRESSED;
			}
			if (inside && pressed) {
			}
		}

	}

}
