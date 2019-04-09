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

package net.luxvacuos.lightengine.client.ui.v2.surfaces;

import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgLineTo;
import static org.lwjgl.nanovg.NanoVG.nvgMoveTo;
import static org.lwjgl.nanovg.NanoVG.nvgStroke;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeColor;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeWidth;

import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Surface;

public class BackgroundSurface extends Surface {

	private float time1, time2, time3, time4, time5;

	@Override
	public void init(long ctx, MouseHandler mh, KeyboardHandler kh) {
		super.init(ctx, mh, kh);
		super.setBackgroundColor("#FFFFFFFF");
		super.setForegroundColor("#000000FF");
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		time1 += 48f * delta;
		time2 += 64f * delta;
		time3 += 80f * delta;
		time4 += 96f * delta;
		time5 += 112f * delta;
		nvgBeginPath(ctx);
		nvgMoveTo(ctx, elementPos.x, (float) Math.sin(time5 * 0.003f) * elementPos.w / 2.02f + elementPos.w / 2);
		for (int s = 0; s <= elementPos.z; s++)
			nvgLineTo(ctx, s, (float) Math.sin((time5 + s) * 0.003f) * elementPos.w / 2.02f + elementPos.w / 2);

		nvgMoveTo(ctx, elementPos.x, (float) Math.sin(time4 * 0.0025f) * elementPos.w / 2.02f + elementPos.w / 2);
		for (int s = 0; s <= elementPos.z; s++)
			nvgLineTo(ctx, s, (float) Math.sin((time4 + s) * 0.0025f) * elementPos.w / 2.02f + elementPos.w / 2);

		nvgMoveTo(ctx, elementPos.x, (float) Math.sin(time3 * 0.002f) * elementPos.w / 2.02f + elementPos.w / 2);
		for (int s = 0; s <= elementPos.z; s++)
			nvgLineTo(ctx, s, (float) Math.sin((time3 + s) * 0.002f) * elementPos.w / 2.02f + elementPos.w / 2);

		nvgMoveTo(ctx, elementPos.x, (float) Math.sin(time2 * 0.0015f) * elementPos.w / 2.02f + elementPos.w / 2);
		for (int s = 0; s <= elementPos.z; s++)
			nvgLineTo(ctx, s, (float) Math.sin((time2 + s) * 0.0015f) * elementPos.w / 2.02f + elementPos.w / 2);

		nvgMoveTo(ctx, elementPos.x, (float) Math.sin(time1 * 0.001f) * elementPos.w / 2.02f + elementPos.w / 2);
		for (int s = 0; s <= elementPos.z; s++)
			nvgLineTo(ctx, s, (float) Math.sin((time1 + s) * 0.001f) * elementPos.w / 2.02f + elementPos.w / 2);

		nvgStrokeWidth(ctx, 2f);
		nvgStrokeColor(ctx, foregroundCurrentColor);
		nvgStroke(ctx);
	}

}
