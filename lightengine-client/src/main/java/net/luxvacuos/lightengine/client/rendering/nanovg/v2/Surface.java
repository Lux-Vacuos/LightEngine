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

package net.luxvacuos.lightengine.client.rendering.nanovg.v2;

import java.util.ArrayList;
import java.util.List;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme;

import static net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme.colorA;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.util.yoga.Yoga.*;

public class Surface {

	private List<Surface> childrens = new ArrayList<>();

	private float lx, ly, lw, lh;
	private float fx, fy, fw, fh;

	private long node;

	private long ctx;

	public Surface(float w, float h) {
		node = YGNodeNew();
		this.lw = w;
		this.lh = h;
	}

	public void init(long ctx) {
		this.ctx = ctx;
		YGNodeStyleSetWidth(node, lw);
		YGNodeStyleSetHeight(node, lh);
	}

	public void render(float delta) {
		glViewport((int) fx, GraphicalSubsystem.getMainWindow().getHeight() - (int) fy - (int) fh, (int) fw, (int) fh);
		nvgBeginFrame(ctx, fw, fh, 1.0f);
		nvgBeginPath(ctx);
		nvgRect(ctx, fx, fy, fw, fh);
		nvgFillColor(ctx, Theme.rgba(31, 31, 31, 100, colorA));
		nvgFill(ctx);
		nvgEndFrame(ctx);
		for (Surface surface : childrens) {
			surface.render(delta);
		}
	}

	public void update(float delta) {

	}

	public void updateLayout(float width, float height) {
		YGNodeCalculateLayout(node, width, height, YGDirectionLTR);
	}

	public void updateLayoutData() {
		fx = YGNodeLayoutGetLeft(node);
		fy = YGNodeLayoutGetTop(node);
		fw = YGNodeLayoutGetWidth(node);
		fh = YGNodeLayoutGetHeight(node);
		for (Surface surface : childrens) {
			surface.updateLayoutData();
		}
	}

	public void dispose() {
		YGNodeFree(node);
	}

	public void addSurface(Surface srf) {
		srf.init(ctx);
		this.childrens.add(srf);
		YGNodeInsertChild(node, srf.getNode(), 0);
	}

	public long getNode() {
		return node;
	}

	public List<Surface> getChildrens() {
		return childrens;
	}
}
