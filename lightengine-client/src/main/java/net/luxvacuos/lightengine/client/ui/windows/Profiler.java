/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2017 Lux Vacuos
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

import net.luxvacuos.lightengine.client.rendering.api.opengl.GPUProfiler;
import net.luxvacuos.lightengine.client.rendering.api.opengl.GPUTaskProfile;
import net.luxvacuos.lightengine.client.ui.Alignment;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;
import net.luxvacuos.lightengine.client.ui.Direction;
import net.luxvacuos.lightengine.client.ui.FlowLayout;
import net.luxvacuos.lightengine.client.ui.ScrollArea;
import net.luxvacuos.lightengine.client.ui.TextArea;

public class Profiler extends ComponentWindow {

	private TextArea text;
	private float timer;

	public Profiler() {
		super(0, 400, 400, 400, "Profiler");
	}

	@Override
	public void initApp() {
		super.setBackgroundColor(0.4f, 0.4f, 0.4f, 1f);

		ScrollArea area = new ScrollArea(0, 0, w, h, 0, 0);
		area.setLayout(new FlowLayout(Direction.DOWN, 0, 0));
		text = new TextArea("", 0, 0, w);
		text.setWindowAlignment(Alignment.LEFT_TOP);
		area.addComponent(text);

		super.addComponent(area);
		super.initApp();
	}

	@Override
	public void alwaysUpdateApp(float delta) {
		GPUTaskProfile tp;
		timer += delta;
		while ((tp = GPUProfiler.getFrameResults()) != null) {
			if (timer > 0.5f) {
				text.setText(tp.dumpS());
				timer = 0;
			}
			GPUProfiler.recycle(tp);
		}
		super.alwaysUpdateApp(delta);
	}

}
