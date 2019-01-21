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

package net.luxvacuos.lightengine.client.rendering.nanovg.v2.layouts;

import org.joml.Vector4f;

import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Surface;

public class FlowLayout implements ILayout {

	private float relative = 0;

	private Direction direction = Direction.HORIZONTAL;

	@Override
	public Vector4f calculateLayout(int srf, Vector4f marginPos) {
		Vector4f layout = new Vector4f();
		if (srf == 0)
			relative = 0;
		switch (direction) {
		case HORIZONTAL:
			layout.x += relative;
			relative += marginPos.z;
			break;
		case VERTICAL:
			layout.y += relative;
			relative += marginPos.w;
			break;
		}
		return layout;
	}

	@Override
	public void addSurface(Surface srf, Object... params) {
	}

	@Override
	public void removeSurface(Surface srf) {
	}

	enum Direction {
		HORIZONTAL, VERTICAL
	}

}
