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

import static org.lwjgl.nanovg.NanoVG.NVG_CCW;
import static org.lwjgl.nanovg.NanoVG.NVG_CW;
import static org.lwjgl.nanovg.NanoVG.NVG_PI;
import static org.lwjgl.nanovg.NanoVG.nvgArc;
import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgClosePath;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVG.nvgFillColor;
import static org.lwjgl.nanovg.NanoVG.nvgFillPaint;
import static org.lwjgl.nanovg.NanoVG.nvgLinearGradient;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;

import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Surface;

public class Spinner extends Surface {

	private NVGPaint paint;
	private NVGColor color, color1, color2;

	private float time;
	private float r;

	public Spinner(float rad) {
		r = rad;
	}

	@Override
	public void init(long ctx, MouseHandler mh, KeyboardHandler kh) {
		super.init(ctx, mh, kh);
		super.setWidth(r * 2f).setHeight(r * 2f);
		color = Theme.setColor("#FFFFFFFF");
		color1 = Theme.setColor("#00000000");
		color2 = Theme.setColor("#000000FF");
		paint = NVGPaint.malloc();
	}

	@Override
	protected void renderSurface(float delta) {
		super.renderSurface(delta);
		time += 1 * delta;
		float a0 = time * 6;
		float a1 = NVG_PI + time * 6;
		float r0 = r;
		float r1 = r * 0.75f;
		float ax, ay, bx, by;
		nvgBeginPath(ctx);
		nvgArc(ctx, elementPos.x + r, elementPos.y + r, r0, a0, a1, NVG_CW);
		nvgArc(ctx, elementPos.x + r, elementPos.y + r, r1, a1, a0, NVG_CCW);
		nvgClosePath(ctx);
		ax = elementPos.x + r + (float) Math.cos(a0) * (r0 + r1) * 0.5f;
		ay = elementPos.y + r + (float) Math.sin(a0) * (r0 + r1) * 0.5f;
		bx = elementPos.x + r + (float) Math.cos(a1) * (r0 + r1) * 0.5f;
		by = elementPos.y + r + (float) Math.sin(a1) * (r0 + r1) * 0.5f;
		nvgLinearGradient(ctx, ax, ay, bx, by, color1, color2, paint);
		nvgFillColor(ctx, color);
		nvgFillPaint(ctx, paint);
		nvgFill(ctx);
	}

	@Override
	public void dispose() {
		super.dispose();
		paint.free();
	}

}
