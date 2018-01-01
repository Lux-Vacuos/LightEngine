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

package net.luxvacuos.lightengine.client.ui.windows;

import static net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme.colorA;
import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgLineTo;
import static org.lwjgl.nanovg.NanoVG.nvgMoveTo;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgStroke;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeColor;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeWidth;

import net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;
import net.luxvacuos.lightengine.client.ui.RenderArea;

public class BackgroundWindow extends ComponentWindow {

	private float time1, time2, time3, time4, time5;

	public BackgroundWindow() {
		super("background");
	}

	@Override
	public void initApp() {
		super.setAsBackground(true);
		super.setBlurBehind(false);
		super.setBackgroundColor("#FFFFFFFF");
		RenderArea area = new RenderArea(0, 0, w, h);
		area.setResizeH(true);
		area.setResizeV(true);
		area.setOnRender((vg, x, y, w, h) -> {
			nvgSave(vg);
			nvgBeginPath(vg);
			nvgMoveTo(vg, x, (float) Math.sin(time5 * 0.003f) * h / 2.02f + h / 2);
			for (int s = 0; s <= w; s++)
				nvgLineTo(vg, s, (float) Math.sin((time5 + s) * 0.003f) * h / 2.02f + h / 2);

			nvgMoveTo(vg, x, (float) Math.sin(time4 * 0.0025f) * h / 2.02f + h / 2);
			for (int s = 0; s <= w; s++)
				nvgLineTo(vg, s, (float) Math.sin((time4 + s) * 0.0025f) * h / 2.02f + h / 2);

			nvgMoveTo(vg, x, (float) Math.sin(time3 * 0.002f) * h / 2.02f + h / 2);
			for (int s = 0; s <= w; s++)
				nvgLineTo(vg, s, (float) Math.sin((time3 + s) * 0.002f) * h / 2.02f + h / 2);

			nvgMoveTo(vg, x, (float) Math.sin(time2 * 0.0015f) * h / 2.02f + h / 2);
			for (int s = 0; s <= w; s++)
				nvgLineTo(vg, s, (float) Math.sin((time2 + s) * 0.0015f) * h / 2.02f + h / 2);

			nvgMoveTo(vg, x, (float) Math.sin(time1 * 0.001f) * h / 2.02f + h / 2);
			for (int s = 0; s <= w; s++)
				nvgLineTo(vg, s, (float) Math.sin((time1 + s) * 0.001f) * h / 2.02f + h / 2);

			nvgStrokeWidth(vg, 2f);
			nvgStrokeColor(vg, Theme.rgba(0, 0, 0, 255, colorA));
			nvgStroke(vg);
			nvgRestore(vg);
		});
		area.setOnAlwaysUpdate((delta) -> {
			time1 += 48f * delta;
			time2 += 64f * delta;
			time3 += 80f * delta;
			time4 += 96f * delta;
			time5 += 112f * delta;
		});
		super.addComponent(area);
		super.initApp();
	}
}
