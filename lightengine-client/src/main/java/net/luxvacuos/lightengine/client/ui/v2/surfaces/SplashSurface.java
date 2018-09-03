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

package net.luxvacuos.lightengine.client.ui.v2.surfaces;

import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Alignment;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Surface;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.layouts.FlowLayout;
import net.luxvacuos.lightengine.client.ui.v2.Button;
import net.luxvacuos.lightengine.client.ui.v2.Text;

public class SplashSurface extends Surface {

	@Override
	public void init(long ctx) {
		super.init(ctx);
		this.setHorizontalAlignment(Alignment.STRETCH).setVerticalAlignment(Alignment.STRETCH);
		this.setBackgroundColor("#000000FF");
		this.setLayout(new FlowLayout());
		this.addSurface(new Text("Hello World!"));
		this.addSurface(new Button("Well, it works"));
	}
}
