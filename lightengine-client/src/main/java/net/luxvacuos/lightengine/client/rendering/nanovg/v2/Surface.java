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
import static org.lwjgl.nanovg.NanoVG.nvgIntersectScissor;
import static org.lwjgl.nanovg.NanoVG.nvgPathWinding;
import static org.lwjgl.nanovg.NanoVG.nvgRect;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgText;
import static org.lwjgl.nanovg.NanoVG.nvgTextAlign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.nanovg.NVGColor;

import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.layouts.EmptyLayout;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.layouts.FlowLayout;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.layouts.ILayout;
import net.luxvacuos.lightengine.universal.core.Task;
import net.luxvacuos.lightengine.universal.core.TaskManager;

public class Surface {

	private List<Surface> surfaces = Collections.synchronizedList(new ArrayList<>());

	private Queue<ScheduledSurface> toAdd = new ConcurrentLinkedQueue<>(), toRemove = new ConcurrentLinkedQueue<>();

	protected Vector4f initialPos = new Vector4f(0);
	private Vector4f calcPos = new Vector4f(0);
	protected Vector4f marginPos = new Vector4f(0);
	protected Vector4f borderPos = new Vector4f(0);
	protected Vector4f elementPos = new Vector4f(0);
	private Vector4f paddingPos = new Vector4f(0);
	private Vector4f margin = new Vector4f(0);
	private Vector4f padding = new Vector4f(0);
	private Vector4f border = new Vector4f(0);

	private Alignment horizontal = Alignment.LEFT, vertical = Alignment.TOP;

	protected SurfaceState state = SurfaceState.NONE;

	private boolean hidden, noInput;

	private ILayout layout = new EmptyLayout();

	private Surface root;

	protected NVGColor backgroundCurrentColor, borderCurrentColor;
	protected NVGColor foregroundCurrentColor;

	protected NVGColor backgroundColor = Theme.setColor("#00000000"), borderColor = Theme.setColor("#000000FF");
	protected NVGColor foregroundColor = Theme.setColor("#FFFFFFFF");

	protected NVGColor backgroundHoverColor = Theme.setColor("#00000000"),
			borderHoverColor = Theme.setColor("#000000FF");
	protected NVGColor foregroundHoverColor = Theme.setColor("#FFFFFFFF");

	protected NVGColor backgroundPressedColor = Theme.setColor("#00000000"),
			borderPressedColor = Theme.setColor("#000000FF");
	protected NVGColor foregroundPressedColor = Theme.setColor("#FFFFFFFF");

	protected long ctx;
	protected MouseHandler mh;
	protected KeyboardHandler kh;

	protected static final boolean DEBUG = false;

	public void init(long ctx, MouseHandler mh, KeyboardHandler kh) {
		this.ctx = ctx;
		this.mh = mh;
		this.kh = kh;
		backgroundCurrentColor = backgroundColor;
		borderCurrentColor = borderColor;
		foregroundCurrentColor = foregroundColor;
	}

	protected void renderSurface(float delta) {
		nvgSave(ctx);

		switch (state) {
		case NONE:
			backgroundCurrentColor = backgroundColor;
			borderCurrentColor = borderColor;
			foregroundCurrentColor = foregroundColor;
			break;
		case HOVER:
			backgroundCurrentColor = backgroundHoverColor;
			borderCurrentColor = borderHoverColor;
			foregroundCurrentColor = foregroundHoverColor;
			break;
		case PRESSED:
			backgroundCurrentColor = backgroundPressedColor;
			borderCurrentColor = borderPressedColor;
			foregroundCurrentColor = foregroundPressedColor;
			break;
		default:
			break;
		}

		if (border.length() != 0) {
			nvgBeginPath(ctx);
			nvgRect(ctx, elementPos.x, elementPos.y, elementPos.z, elementPos.w);
			nvgPathWinding(ctx, NVG_HOLE);
			nvgRect(ctx, borderPos.x, borderPos.y, borderPos.z, borderPos.w);
			nvgFillColor(ctx, borderCurrentColor);
			nvgFill(ctx);
		}

		nvgBeginPath(ctx);
		nvgRect(ctx, elementPos.x, elementPos.y, elementPos.z, elementPos.w);
		nvgFillColor(ctx, backgroundCurrentColor);
		nvgFill(ctx);

		nvgRestore(ctx);
	}

	protected void renderDebug() {
		if (DEBUG) {
			nvgSave(ctx);
			nvgBeginPath(ctx);
			nvgRect(ctx, marginPos.x, marginPos.y, marginPos.z, marginPos.w);
			nvgFillColor(ctx, Theme.rgba(0, 100, 0, 255, colorA));
			nvgFill(ctx);

			if (border.length() != 0) {
				nvgBeginPath(ctx);
				nvgRect(ctx, elementPos.x, elementPos.y, elementPos.z, elementPos.w);
				nvgPathWinding(ctx, NVG_HOLE);
				nvgRect(ctx, borderPos.x, borderPos.y, borderPos.z, borderPos.w);
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
	}

	public void render(float delta) {
		if (hidden)
			return;
		nvgSave(ctx);
		renderSurface(delta);
		nvgRestore(ctx);

		nvgSave(ctx);
		renderDebug();
		nvgRestore(ctx);

		nvgIntersectScissor(ctx, paddingPos.x, paddingPos.y, paddingPos.z, paddingPos.w);
		synchronized (surfaces) {
			for (var surface : surfaces) {
				nvgSave(ctx);
				surface.render(delta);
				nvgRestore(ctx);
			}
		}
	}

	public void update(float delta) {
		removeSurfaceI();
		addSurfaceI();
		for (var surface : surfaces)
			surface.update(delta);
	}

	public boolean handleInputI() {
		// TODO: Improve
		state = SurfaceState.NONE;
		if (surfaces.size() > 0) {
			var list = surfaces.listIterator(surfaces.size());
			var ret = false;
			while (list.hasPrevious()) {
				var srf = list.previous();
				if (!ret) {
					if (srf.handleInputI())
						ret = true;
				} else {
					srf.state = SurfaceState.NONE;
				}
			}
			if (ret)
				return true;
		}
		boolean handle = isCursorInsideSurface() && !noInput;
		if (handle)
			handleInput();
		return handle;
	}

	protected void handleInput() {

	}

	public void preLayout(float delta) {
		synchronized (surfaces) {
			for (var srf : surfaces) {
				nvgSave(ctx);
				srf.preLayout(delta);
				nvgRestore(ctx);
			}
		}
	}

	public Vector2f updateSize() {

		// TODO: Hack! needs a better way to calculate children's size
		var size = new Vector2f();
		var tmpSize = new Vector2f();
		if (layout instanceof FlowLayout) {
			var tmpMarginPos = new Vector4f(0);
			var tmpLayout = new Vector4f(0);
			for (int i = 0; i < surfaces.size(); i++) {
				var srf = surfaces.get(i);
				tmpSize = srf.updateSize();
				size.max(tmpSize);
				tmpMarginPos.set(0, 0, tmpSize.x, tmpSize.y);
				tmpLayout = layout.calculateLayout(i, tmpMarginPos);
			}
			size.max(new Vector2f(tmpLayout.x + tmpSize.x, tmpLayout.y + tmpSize.y));
		} else {
			for (int i = 0; i < surfaces.size(); i++) {
				var srf = surfaces.get(i);
				tmpSize.max(srf.updateSize());
			}
			size.x = tmpSize.x;
			size.y = tmpSize.y;
		}
		calcPos.z = size.x;
		calcPos.w = size.y;

		calcPos.max(initialPos);

		marginPos.z = calcPos.z + margin.x + margin.z + border.x + border.z + padding.x + padding.z;
		marginPos.w = calcPos.w + margin.y + margin.w + border.y + border.w + padding.y + padding.w;
		return new Vector2f(marginPos.z, marginPos.w);
	}

	public void updateLayout(Vector4f root) {

		updateSize();

		switch (horizontal) {
		case LEFT:
			marginPos.x = initialPos.x + root.x;
			break;
		case CENTER:
			marginPos.x = initialPos.x + root.x + root.z / 2f - marginPos.z / 2f;
			break;
		case RIGHT:
			marginPos.x = initialPos.x + root.x + root.z - marginPos.z;
			break;
		case STRETCH:
			marginPos.x = root.x;
			marginPos.z = root.z;
			break;
		default:
			throw new UnsupportedOperationException("Only LEFT, CENTER, RIGHT and STRETCH are supported.");
		}

		switch (vertical) {
		case TOP:
			marginPos.y = initialPos.y + root.y;
			break;
		case CENTER:
			marginPos.y = initialPos.y + root.y + root.w / 2f - marginPos.w / 2f;
			break;
		case BOTTOM:
			marginPos.y = initialPos.y + root.y + root.w - marginPos.w;
			break;
		case STRETCH:
			marginPos.y = root.y;
			marginPos.w = root.w;
			break;
		default:
			throw new UnsupportedOperationException("Only TOP, CENTER, BOTTOM and STRETCH are supported.");
		}

		borderPos.x = marginPos.x + margin.x;
		borderPos.y = marginPos.y + margin.y;
		borderPos.z = marginPos.z - margin.x - margin.z;
		borderPos.w = marginPos.w - margin.y - margin.w;

		elementPos.x = borderPos.x + border.x;
		elementPos.y = borderPos.y + border.y;
		elementPos.z = borderPos.z - border.x - border.z;
		elementPos.w = borderPos.w - border.y - border.w;

		paddingPos.x = elementPos.x + padding.x;
		paddingPos.y = elementPos.y + padding.y;
		paddingPos.z = elementPos.z - padding.x - padding.z;
		paddingPos.w = elementPos.w - padding.y - padding.w;

		for (int i = 0; i < surfaces.size(); i++) {
			Surface srf = surfaces.get(i);
			srf.updateLayout(paddingPos.add(layout.calculateLayout(i, srf.marginPos), new Vector4f(0)));
		}
	}

	public void dispose() {
		for (Surface srf : surfaces) {
			srf.dispose();
		}
		surfaces.clear();
	}

	private void addSurfaceI() {
		while (!toAdd.isEmpty()) {
			var ss = toAdd.poll();
			var srf = ss.srf;
			TaskManager.tm.submitRenderThread(new Task<Void>() {
				@Override
				protected Void call() {
					srf.init(ctx, mh, kh);
					return null;
				}
			}).get();
			srf.root = this;
			synchronized (this.surfaces) {
				this.surfaces.add(srf);
			}
			this.layout.addSurface(srf, ss.params);
		}
	}

	private void removeSurfaceI() {
		while (!toRemove.isEmpty()) {
			var ss = toRemove.poll();
			var srf = ss.srf;
			this.layout.removeSurface(srf);
			synchronized (this.surfaces) {
				this.surfaces.remove(srf);
			}
			TaskManager.tm.submitRenderThread(new Task<Void>() {
				@Override
				protected Void call() {
					srf.dispose();
					return null;
				}
			}).get();
		}
	}

	public void addSurface(Surface srf, Object... params) {
		var ss = new ScheduledSurface();
		ss.srf = srf;
		ss.params = params;
		toAdd.add(ss);
	}

	public void removeSurface(Surface srf) {
		var ss = new ScheduledSurface();
		ss.srf = srf;
		toRemove.add(ss);
	}

	public boolean isCursorInsideSurface() {
		return mh.getX() >= elementPos.x() && mh.getY() >= elementPos.y() && mh.getX() < elementPos.x() + elementPos.z()
				&& mh.getY() < elementPos.y() + elementPos.w();
	}

	public void removeSurfaceFromRoot() {
		root.removeSurface(this);
	}

	public List<Surface> getSurfaces() {
		return surfaces;
	}

	public Surface setX(float x) {
		this.initialPos.x = x;
		return this;
	}

	public Surface setY(float y) {
		this.initialPos.y = y;
		return this;
	}

	public Surface setWidth(float width) {
		this.initialPos.z = width;
		return this;
	}

	public Surface setHeight(float height) {
		this.initialPos.w = height;
		return this;
	}

	public Surface setMargin(float margin) {
		this.margin.set(margin);
		return this;
	}

	public Surface setMargin(float h, float v) {
		this.margin.set(h, v, h, v);
		return this;
	}

	public Surface setMargin(float l, float t, float r, float b) {
		this.margin.set(l, t, r, b);
		return this;
	}

	public Surface setPadding(float padding) {
		this.padding.set(padding);
		return this;
	}

	public Surface setPadding(float h, float v) {
		this.padding.set(h, v, h, v);
		return this;
	}

	public Surface setPadding(float l, float t, float r, float b) {
		this.padding.set(l, t, r, b);
		return this;
	}

	public Surface setBorder(float border) {
		this.border.set(border);
		return this;
	}

	public Surface setBorder(float h, float v) {
		this.border.set(h, v, h, v);
		return this;
	}

	public Surface setBorder(float l, float t, float r, float b) {
		this.border.set(l, t, r, b);
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

	public Surface setBackgroundColor(String color) {
		this.backgroundColor = Theme.setColor(color, backgroundColor);
		return this;
	}

	public Surface setBorderColor(String color) {
		this.borderColor = Theme.setColor(color, borderColor);
		return this;
	}

	public Surface setForegroundColor(String color) {
		this.foregroundColor = Theme.setColor(color, foregroundColor);
		return this;
	}

	public Surface setBackgroundHoverColor(String color) {
		this.backgroundHoverColor = Theme.setColor(color, backgroundHoverColor);
		return this;
	}

	public Surface setBorderHoverColor(String color) {
		this.borderHoverColor = Theme.setColor(color, borderHoverColor);
		return this;
	}

	public Surface setForegroundHoverColor(String color) {
		this.foregroundHoverColor = Theme.setColor(color, foregroundHoverColor);
		return this;
	}

	public Surface setBackgroundPressedColor(String color) {
		this.backgroundPressedColor = Theme.setColor(color, backgroundPressedColor);
		return this;
	}

	public Surface setBorderPressedColor(String color) {
		this.borderPressedColor = Theme.setColor(color, borderPressedColor);
		return this;
	}

	public Surface setForegroundPressedColor(String color) {
		this.foregroundPressedColor = Theme.setColor(color, foregroundPressedColor);
		return this;
	}

	public Surface setHidden(boolean hidden) {
		this.hidden = hidden;
		return this;
	}

	public Surface setNoInput(boolean noInput) {
		this.noInput = noInput;
		return this;
	}

	public enum SurfaceState {
		NONE, HOVER, PRESSED
	}

	class ScheduledSurface {
		Surface srf;
		Object params;
	}
}
