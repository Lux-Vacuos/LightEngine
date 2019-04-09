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

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_TOP;
import static org.lwjgl.nanovg.NanoVG.nvgFillColor;
import static org.lwjgl.nanovg.NanoVG.nvgFontFace;
import static org.lwjgl.nanovg.NanoVG.nvgFontSize;
import static org.lwjgl.nanovg.NanoVG.nvgText;
import static org.lwjgl.nanovg.NanoVG.nvgTextAlign;
import static org.lwjgl.nanovg.NanoVG.nvgTextBounds;

import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Surface;

public class Text extends Surface {

	private String text;

	private float fontSize = 24;
	private String font = "Poppins-Medium";
	private int align = NVG_ALIGN_LEFT | NVG_ALIGN_TOP;

	public Text(String text) {
		this.text = text;
	}
	
	@Override
	public void init(long ctx, MouseHandler mh, KeyboardHandler kh) {
		super.init(ctx, mh, kh);
		super.setNoInput(true);
	}

	@Override
	public void preLayout(float delta) {
		super.preLayout(delta);
		float[] bounds = new float[4];
		nvgFontSize(ctx, fontSize);
		nvgFontFace(ctx, font);
		nvgTextAlign(ctx, align);
		nvgFillColor(ctx, foregroundCurrentColor);
		nvgText(ctx, 0, 0, text);
		nvgTextBounds(ctx, 0, 0, text, bounds);
		initialPos.z = bounds[2];
		initialPos.w = bounds[3];
	}

	@Override
	protected void renderSurface(float delta) {
		super.renderSurface(delta);
		nvgFontSize(ctx, fontSize);
		nvgFontFace(ctx, font);
		nvgTextAlign(ctx, align);
		nvgFillColor(ctx, foregroundCurrentColor);
		nvgText(ctx, elementPos.x, elementPos.y, text);
	}

	public Text setText(String text) {
		this.text = text;
		return this;
	}

	public Text setFontSize(float fontSize) {
		this.fontSize = fontSize;
		return this;
	}

	public Text setFont(String font) {
		this.font = font;
		return this;
	}

}
