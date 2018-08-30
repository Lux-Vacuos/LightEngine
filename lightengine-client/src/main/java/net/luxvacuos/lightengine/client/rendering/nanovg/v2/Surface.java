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

import static net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme.colorA;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BOTTOM;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_RIGHT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_TOP;
import static org.lwjgl.nanovg.NanoVG.NVG_HOLE;
import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVG.nvgFillColor;
import static org.lwjgl.nanovg.NanoVG.nvgFontFace;
import static org.lwjgl.nanovg.NanoVG.nvgFontSize;
import static org.lwjgl.nanovg.NanoVG.nvgPathWinding;
import static org.lwjgl.nanovg.NanoVG.nvgRect;
import static org.lwjgl.nanovg.NanoVG.nvgResetScissor;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgScissor;
import static org.lwjgl.nanovg.NanoVG.nvgText;
import static org.lwjgl.nanovg.NanoVG.nvgTextAlign;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.nanovg.NVGColor;

import net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.layouts.EmptyLayout;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.layouts.ILayout;

public class Surface {

	private List<Surface> surfaces = new ArrayList<>();

	protected Vector2f initialPos = new Vector2f(0);
	protected Vector4f marginPos = new Vector4f(0);
	protected Vector4f elementPos = new Vector4f(0);
	protected Vector4f paddingPos = new Vector4f(0);
	protected Vector4f margin = new Vector4f(0);
	protected Vector4f padding = new Vector4f(0);
	protected Vector4f border = new Vector4f(0);

	protected Alignment horizontal = Alignment.LEFT, vertical = Alignment.TOP;

	private long ctx;

	private ILayout layout = new EmptyLayout();

	private NVGColor backgroundColor = Theme.setColor("#7F7F7FFF"), borderColor = Theme.setColor("#FFFFFFFF");

	private static final boolean DEBUG = true;

	public Surface() {
	}

	public void init(long ctx) {
		this.ctx = ctx;
	}

	public void render(float delta) {
		nvgSave(ctx);

		if (border.length() != 0) {
			nvgBeginPath(ctx);
			nvgRect(ctx, marginPos.x + border.x, marginPos.y + border.y, marginPos.z - border.x - border.z,
					marginPos.w - border.y - border.w);
			nvgPathWinding(ctx, NVG_HOLE);
			nvgRect(ctx, marginPos.x, marginPos.y, marginPos.z, marginPos.w);
			nvgFillColor(ctx, borderColor);
			nvgFill(ctx);
		}

		nvgBeginPath(ctx);
		nvgRect(ctx, elementPos.x, elementPos.y, elementPos.z, elementPos.w);
		nvgFillColor(ctx, backgroundColor);
		nvgFill(ctx);

		nvgRestore(ctx);

		if (DEBUG) {

			nvgSave(ctx);
			nvgScissor(ctx, marginPos.x + border.x, marginPos.y + border.y, marginPos.z - border.x - border.z,
					marginPos.w - border.y - border.w);
			nvgBeginPath(ctx);
			nvgRect(ctx, marginPos.x, marginPos.y, marginPos.z, marginPos.w);
			nvgFillColor(ctx, Theme.rgba(0, 100, 0, 255, colorA));
			nvgFill(ctx);
			nvgResetScissor(ctx);

			if (border.length() != 0) {
				nvgBeginPath(ctx);
				nvgRect(ctx, marginPos.x + border.x, marginPos.y + border.y, marginPos.z - border.x - border.z,
						marginPos.w - border.y - border.w);
				nvgPathWinding(ctx, NVG_HOLE);
				nvgRect(ctx, marginPos.x, marginPos.y, marginPos.z, marginPos.w);
				nvgFillColor(ctx, Theme.rgba(0, 0, 100, 255, colorA));
				nvgFill(ctx);
			}

			nvgBeginPath(ctx);
			nvgRect(ctx, elementPos.x, elementPos.y, elementPos.z, elementPos.w);
			nvgFillColor(ctx, Theme.rgba(100, 100, 100, 255, colorA));
			nvgFill(ctx);

			nvgBeginPath(ctx);
			nvgRect(ctx, paddingPos.x, paddingPos.y, paddingPos.z, paddingPos.w);
			nvgFillColor(ctx, Theme.rgba(100, 0, 0, 255, colorA));
			nvgFill(ctx);

			nvgRestore(ctx);

			nvgSave(ctx);
			nvgFontSize(ctx, 20);
			nvgFontFace(ctx, "Poppins-Regular");
			nvgFillColor(ctx, Theme.rgba(255, 255, 255, 255, colorA));
			nvgTextAlign(ctx, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
			nvgText(ctx, elementPos.x + 4, elementPos.y + elementPos.w / 2f, String.format("X %.2f", elementPos.x));
			nvgTextAlign(ctx, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
			nvgText(ctx, elementPos.x + elementPos.z / 2f, elementPos.y + 4, String.format("Y %.2f", elementPos.y));

			nvgTextAlign(ctx, NVG_ALIGN_RIGHT | NVG_ALIGN_MIDDLE);
			nvgText(ctx, elementPos.x + elementPos.z - 4f, elementPos.y + elementPos.w / 2f,
					String.format("W %.2f", elementPos.z));
			nvgTextAlign(ctx, NVG_ALIGN_CENTER | NVG_ALIGN_BOTTOM);
			nvgText(ctx, elementPos.x + elementPos.z / 2f, elementPos.y + elementPos.w - 4f,
					String.format("H %.2f", elementPos.w));
			nvgRestore(ctx);

		}

		for (Surface surface : surfaces) {
			surface.render(delta);
		}
	}

	public void update(float delta) {

	}

	public void updateLayout(Vector4f root) {
		marginPos.z = initialPos.x + margin.x + margin.z + border.x + border.z;
		switch (horizontal) {
		case LEFT:
			marginPos.x = root.x;
			break;
		case CENTER:
			marginPos.x = root.x + root.z / 2f - marginPos.z / 2f;
			break;
		case RIGHT:
			marginPos.x = root.x + root.z - marginPos.z;
			break;
		case STRETCH:
			marginPos.x = root.x;
			marginPos.z = root.x + root.z;
			break;
		default:
			throw new UnsupportedOperationException("Only LEFT, CENTER, RIGHT and STRETCH are supported.");
		}
		elementPos.x = marginPos.x + margin.x + border.x;
		elementPos.z = marginPos.z - margin.x - margin.z - border.x - border.z;

		marginPos.w = initialPos.y + margin.y + margin.w + border.y + border.w;
		switch (vertical) {
		case TOP:
			marginPos.y = root.y;
			break;
		case CENTER:
			marginPos.y = root.y + root.w / 2f - marginPos.w / 2f;
			break;
		case BOTTOM:
			marginPos.y = root.y + root.w - marginPos.w;
			break;
		case STRETCH:
			marginPos.y = root.y;
			marginPos.w = root.y + root.w;
			break;
		default:
			throw new UnsupportedOperationException("Only TOP, CENTER, BOTTOM and STRETCH are supported.");
		}
		elementPos.y = marginPos.y + margin.y + border.y;
		elementPos.w = marginPos.w - margin.y - margin.w - border.y - border.w;

		paddingPos.x = elementPos.x + padding.x;
		paddingPos.y = elementPos.y + padding.y;
		paddingPos.z = elementPos.z - padding.z - padding.x;
		paddingPos.w = elementPos.w - padding.w - padding.y;

		for (int i = 0; i < surfaces.size(); i++) {
			Surface srf = surfaces.get(i);
			srf.updateLayout(paddingPos.add(layout.calculateLayout(i, srf.marginPos), new Vector4f()));
		}
	}

	public void dispose() {
	}

	public void addSurface(Surface srf, Object... params) {
		srf.init(ctx);
		this.surfaces.add(srf);
		this.layout.addSurface(srf, params);
	}

	public List<Surface> getSurfaces() {
		return surfaces;
	}

	public Surface setWidth(float width) {
		this.initialPos.x = width;
		return this;
	}

	public Surface setHeight(float height) {
		this.initialPos.y = height;
		return this;
	}

	public Surface setMargin(float margin) {
		this.margin.set(margin);
		return this;
	}

	public Surface setPadding(float padding) {
		this.padding.set(padding);
		return this;
	}

	public Surface setBorder(float border) {
		this.border.set(border);
		return this;
	}

	public Surface setHorizontalAlignment(Alignment al) {
		this.horizontal = al;
		return this;
	}

	public Surface setVerticalAlignment(Alignment al) {
		this.vertical = al;
		return this;
	}

	public Surface setLayout(ILayout layout) {
		this.layout = layout;
		return this;
	}
}
