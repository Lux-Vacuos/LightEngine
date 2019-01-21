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

import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Surface;

public class Button extends Surface {

	private Text text;

	public Button(String text) {
		this.text = new Text(text);
	}

	@Override
	public void init(long ctx) {
		super.init(ctx);
		this.addSurface(text);
		this.setBorder(1).setPadding(10, 4);
		this.setBorderColor("#3E3E3EFF").setBackgroundColor("#FFFFFFFF");
		this.setForegroundColor("#000000FF");
	}

}
