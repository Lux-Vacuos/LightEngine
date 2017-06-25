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

package net.luxvacuos.lightengine.client.rendering.api.nanovg.themes;

import static net.luxvacuos.lightengine.client.rendering.api.nanovg.themes.Theme.colorA;
import static net.luxvacuos.lightengine.client.rendering.api.nanovg.themes.Theme.colorB;
import static net.luxvacuos.lightengine.client.rendering.api.nanovg.themes.Theme.paintA;
import static net.luxvacuos.lightengine.client.rendering.api.nanovg.themes.Theme.paintB;
import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;
import static org.lwjgl.nanovg.NanoVG.NVG_CCW;
import static org.lwjgl.nanovg.NanoVG.NVG_CW;
import static org.lwjgl.nanovg.NanoVG.NVG_HOLE;
import static org.lwjgl.nanovg.NanoVG.NVG_PI;
import static org.lwjgl.nanovg.NanoVG.nnvgText;
import static org.lwjgl.nanovg.NanoVG.nnvgTextBreakLines;
import static org.lwjgl.nanovg.NanoVG.nvgArc;
import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgBoxGradient;
import static org.lwjgl.nanovg.NanoVG.nvgClosePath;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVG.nvgFillColor;
import static org.lwjgl.nanovg.NanoVG.nvgFillPaint;
import static org.lwjgl.nanovg.NanoVG.nvgFontBlur;
import static org.lwjgl.nanovg.NanoVG.nvgFontFace;
import static org.lwjgl.nanovg.NanoVG.nvgFontSize;
import static org.lwjgl.nanovg.NanoVG.nvgImagePattern;
import static org.lwjgl.nanovg.NanoVG.nvgImageSize;
import static org.lwjgl.nanovg.NanoVG.nvgLineTo;
import static org.lwjgl.nanovg.NanoVG.nvgLinearGradient;
import static org.lwjgl.nanovg.NanoVG.nvgMoveTo;
import static org.lwjgl.nanovg.NanoVG.nvgPathWinding;
import static org.lwjgl.nanovg.NanoVG.nvgRect;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgRoundedRectVarying;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgScissor;
import static org.lwjgl.nanovg.NanoVG.nvgStroke;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeColor;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeWidth;
import static org.lwjgl.nanovg.NanoVG.nvgText;
import static org.lwjgl.nanovg.NanoVG.nvgTextAlign;
import static org.lwjgl.nanovg.NanoVG.nvgTextBounds;
import static org.lwjgl.nanovg.NanoVG.nvgTextMetrics;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memUTF8;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NVGTextRow;

import net.luxvacuos.lightengine.client.rendering.api.nanovg.themes.Theme.BackgroundStyle;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.themes.Theme.ButtonStyle;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class NanoTheme implements ITheme {

	private final FloatBuffer lineh = BufferUtils.createFloatBuffer(1);
	private final NVGTextRow.Buffer rows = NVGTextRow.create(3);

	protected NVGColor buttonColor = Theme.rgba(255, 255, 255, 255), buttonHighlight = Theme.rgba(190, 190, 190, 255),
			buttonTextColor = Theme.rgba(60, 60, 60, 255);
	protected NVGColor toggleButtonColor = Theme.setColor(1f, 1f, 1f, 1f),
			toggleButtonHighlight = Theme.setColor(0.5f, 1f, 0.5f, 1f);
	protected NVGColor titleBarButtonColor = Theme.setColor("#646464C8"),
			titleBarButtonHighlight = Theme.setColor("#FFFFFFC8"),
			titleBarButtonCloseHighlight = Theme.setColor("#FF0000C8");
	protected NVGColor contextButtonColor = Theme.setColor("#646464C8"),
			contextButtonHighlight = Theme.setColor("#FFFFFFC8");

	@Override
	public void renderWindow(long vg, float x, float y, float w, float h, BackgroundStyle backgroundStyle,
			NVGColor backgroundColor, boolean decorations, boolean titleBar, boolean maximized, float ft, float fb,
			float fr, float fl) {
		NVGPaint shadowPaint = paintA;
		float borderSize = (float) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/borderSize"));
		float titleBarHeight = (float) REGISTRY
				.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"));
		boolean titleBarBorder = (boolean) REGISTRY
				.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarBorder"));

		nvgSave(vg);
		if (decorations) {
			if (maximized) {
				nvgBeginPath(vg);
				nvgRect(vg, x, y - titleBarHeight, w, titleBarHeight);
				nvgFillColor(vg, Theme.rgba(31, 31, 31, 120, colorA));
				nvgFill(vg);
			} else {
				// Window
				nvgBeginPath(vg);
				nvgRect(vg, x + fl, y + ft, w - fr - fl, h - fb - ft);
				nvgPathWinding(vg, NVG_HOLE);
				if (titleBar)
					if (titleBarBorder)
						nvgRect(vg, x - borderSize, y - titleBarHeight - borderSize, w + borderSize * 2f,
								h + titleBarHeight + borderSize * 2f);
					else
						nvgRect(vg, x - borderSize, y - titleBarHeight, w + borderSize * 2f,
								h + titleBarHeight + borderSize);
				else
					nvgRect(vg, x - borderSize, y - borderSize, w + borderSize * 2f, h + borderSize * 2f);
				nvgFillColor(vg, Theme.rgba(31, 31, 31, 120, colorA));
				nvgFill(vg);
			}
		}

		// Background
		switch (backgroundStyle) {
		case SOLID:
			nvgBeginPath(vg);
			nvgRect(vg, x + fl, y + ft, w - fr - fl, h - fb - ft);
			nvgFillColor(vg, backgroundColor);
			nvgFill(vg);
			break;
		case TRANSPARENT:
			break;
		}
		if (decorations && !maximized) {
			// Drop shadow
			if (titleBar) {
				if (titleBarBorder) {
					nvgBoxGradient(vg, x - borderSize, y + 10 - titleBarHeight - borderSize, w + borderSize * 2f,
							h + titleBarHeight + borderSize * 2f, 0, 20, Theme.rgba(0, 0, 0, 80, colorA),
							Theme.rgba(0, 0, 0, 0, colorB), shadowPaint);
					nvgBeginPath(vg);
					nvgRect(vg, x - 10 - borderSize, y - 10 - titleBarHeight - borderSize, w + 20 + borderSize * 2f,
							h + 30 + titleBarHeight + borderSize * 2f);
					nvgRect(vg, x - borderSize, y - titleBarHeight - borderSize, w + borderSize * 2f,
							h + titleBarHeight + borderSize * 2f);
				} else {
					nvgBoxGradient(vg, x - borderSize, y + 10 - titleBarHeight, w + borderSize * 2f,
							h + titleBarHeight + borderSize, 0, 20, Theme.rgba(0, 0, 0, 80, colorA),
							Theme.rgba(0, 0, 0, 0, colorB), shadowPaint);
					nvgBeginPath(vg);
					nvgRect(vg, x - 10 - borderSize, y - 10 - titleBarHeight, w + 20 + borderSize * 2f,
							h + 30 + titleBarHeight + borderSize);
					nvgRect(vg, x - borderSize, y - titleBarHeight, w + borderSize * 2f,
							h + titleBarHeight + borderSize);

				}
			} else {
				nvgBoxGradient(vg, x - borderSize, y + 10 - borderSize, w + borderSize * 2f, h + borderSize * 2f, 0, 20,
						Theme.rgba(0, 0, 0, 80, colorA), Theme.rgba(0, 0, 0, 0, colorB), shadowPaint);
				nvgBeginPath(vg);
				nvgRect(vg, x - 10 - borderSize, y - 10 - borderSize, w + 20 + borderSize * 2f,
						h + 30 + borderSize * 2f);
				nvgRect(vg, x - borderSize, y - titleBarHeight, w + borderSize * 2f, h + titleBarHeight + borderSize);
			}
			nvgPathWinding(vg, NVG_HOLE);
			nvgFillPaint(vg, shadowPaint);
			nvgFill(vg);

		}

		nvgRestore(vg);
	}

	@Override
	public void renderTitleBarText(long vg, String text, String font, int align, float x, float y, float fontSize) {
		nvgSave(vg);
		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);

		nvgFontBlur(vg, 4);
		nvgFillColor(vg, Theme.rgba(0, 0, 0, 255, colorA));
		nvgText(vg, x, y + 1, text);

		nvgFontBlur(vg, 0);
		nvgFillColor(vg, Theme.rgba(255, 255, 255, 255, colorA));
		nvgText(vg, x, y, text);
		nvgRestore(vg);
	}

	@Override
	public void renderTitleBarButton(long vg, float x, float y, float w, float h, ButtonStyle style,
			boolean highlight) {
		nvgSave(vg);
		nvgBeginPath(vg);
		nvgRect(vg, x, y, w, h);
		if (highlight)
			if (style.equals(ButtonStyle.CLOSE))
				nvgFillColor(vg, titleBarButtonCloseHighlight);
			else
				nvgFillColor(vg, titleBarButtonHighlight);
		else
			nvgFillColor(vg, titleBarButtonColor);
		nvgFill(vg);

		switch (style) {
		case CLOSE:
			nvgBeginPath(vg);
			nvgMoveTo(vg, x + w / 2 - 6, y + h / 2 - 6);
			nvgLineTo(vg, x + w / 2 + 6, y + h / 2 + 6);
			nvgMoveTo(vg, x + w / 2 - 6, y + h / 2 + 6);
			nvgLineTo(vg, x + w / 2 + 6, y + h / 2 - 6);
			nvgStrokeColor(vg, Theme.rgba(0, 0, 0, 255, colorA));
			nvgStroke(vg);
			break;
		case MAXIMIZE:
			nvgBeginPath(vg);
			nvgMoveTo(vg, x + w / 2 - 6, y + h / 2 - 6);
			nvgLineTo(vg, x + w / 2 + 6, y + h / 2 - 6);
			nvgLineTo(vg, x + w / 2 + 6, y + h / 2 + 6);
			nvgLineTo(vg, x + w / 2 - 6, y + h / 2 + 6);
			nvgLineTo(vg, x + w / 2 - 6, y + h / 2 - 6);
			nvgStrokeColor(vg, Theme.rgba(0, 0, 0, 255, colorA));
			nvgStroke(vg);
			break;
		case MINIMIZE:
			nvgBeginPath(vg);
			nvgMoveTo(vg, x + w / 2 - 6, y + h / 2);
			nvgLineTo(vg, x + w / 2 + 6, y + h / 2);
			nvgStrokeColor(vg, Theme.rgba(0, 0, 0, 255, colorA));
			nvgStroke(vg);
			break;
		case LEFT_ARROW:
			nvgBeginPath(vg);
			nvgMoveTo(vg, x + w / 2, y + h / 2 + 6);
			nvgLineTo(vg, x + w / 2 - 6, y + h / 2);
			nvgLineTo(vg, x + w / 2, y + h / 2 - 6);
			nvgMoveTo(vg, x + w / 2 - 6, y + h / 2);
			nvgLineTo(vg, x + w / 2 + 6, y + h / 2);
			nvgStrokeColor(vg, Theme.rgba(0, 0, 0, 255, colorA));
			nvgStroke(vg);
			break;
		case RIGHT_ARROW:
			nvgBeginPath(vg);
			nvgMoveTo(vg, x + w / 2, y + h / 2 + 6);
			nvgLineTo(vg, x + w / 2 + 6, y + h / 2);
			nvgLineTo(vg, x + w / 2, y + h / 2 - 6);
			nvgMoveTo(vg, x + w / 2 + 6, y + h / 2);
			nvgLineTo(vg, x + w / 2 - 6, y + h / 2);
			nvgStrokeColor(vg, Theme.rgba(0, 0, 0, 255, colorA));
			nvgStroke(vg);
			break;
		case NONE:
			break;
		}
		nvgRestore(vg);
	}

	@Override
	public void renderText(long vg, String text, String font, int align, float x, float y, float fontSize,
			NVGColor color) {
		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		nvgTextAlign(vg, align);
		nvgFillColor(vg, color);
		nvgText(vg, x, y, text);
	}

	@Override
	public void renderImage(long vg, float x, float y, float w, float h, int image, float alpha) {
		NVGPaint imgPaint = paintB;
		nvgSave(vg);
		nvgImagePattern(vg, x, y, w, h, 0, image, alpha, imgPaint);
		nvgBeginPath(vg);
		nvgRect(vg, x, y, w, h);
		nvgFillPaint(vg, imgPaint);
		nvgFill(vg);
		nvgRestore(vg);
	}

	@Override
	public void renderImage(long vg, float x, float y, int image, float alpha) {
		NVGPaint imgPaint = paintB;
		IntBuffer imgw = memAllocInt(1), imgh = memAllocInt(1);
		nvgSave(vg);
		nvgImageSize(vg, image, imgw, imgh);
		nvgImagePattern(vg, x, y, imgw.get(0), imgh.get(0), 0, image, alpha, imgPaint);
		nvgBeginPath(vg);
		nvgRect(vg, x, y, imgw.get(0), imgh.get(0));
		nvgFillPaint(vg, imgPaint);
		nvgFill(vg);
		nvgRestore(vg);
		memFree(imgh);
		memFree(imgw);
	}

	@Override
	public void renderEditBoxBase(long vg, float x, float y, float w, float h, boolean selected) {
		nvgSave(vg);
		nvgBeginPath(vg);
		nvgRect(vg, x + 1, y + 1, w - 2, h - 2);
		if (selected)
			nvgFillColor(vg, Theme.rgba(255, 255, 255, 255, colorA));
		else
			nvgFillColor(vg, Theme.rgba(150, 150, 150, 255, colorA));
		nvgFill(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x + 0.5f, y + 0.5f, w - 1, h - 1);
		if (selected)
			nvgStrokeColor(vg, Theme.rgba(50, 50, 50, 255, colorA));
		else
			nvgStrokeColor(vg, Theme.rgba(70, 70, 70, 255, colorA));
		nvgStrokeWidth(vg, 1);
		nvgStroke(vg);
		nvgRestore(vg);
	}

	@Override
	public void renderEditBox(long vg, String text, String font, float x, float y, float w, float h, float fontSize,
			boolean selected) {
		float[] bounds = new float[4];
		renderEditBoxBase(vg, x, y, w, h, selected);
		nvgSave(vg);
		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
		nvgFillColor(vg, buttonTextColor);
		nvgText(vg, x + h * 0.3f, y + h * 0.5f, text);
		nvgTextBounds(vg, x + h * 0.3f, y + h * 0.5f, text, bounds);
		nvgBeginPath(vg);
		if (selected) {
			nvgMoveTo(vg, bounds[2], y + 5f);
			nvgLineTo(vg, bounds[2], y + h - 5f);
			nvgStrokeColor(vg, Theme.rgba(0, 0, 0, 255, colorA));
			nvgStroke(vg);
		}
		nvgRestore(vg);
	}

	@Override
	public void renderButton(long vg, ByteBuffer preicon, String text, String font, String entypo, float x, float y,
			float w, float h, boolean highlight, float fontSize) {
		float tw, iw = 0;
		nvgSave(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x + 1, y + 1, w - 2, h - 2);
		if (highlight)
			nvgFillColor(vg, buttonHighlight);
		else
			nvgFillColor(vg, buttonColor);
		nvgFill(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x + 0.5f, y + 0.5f, w - 1, h - 1);
		nvgStrokeColor(vg, Theme.rgba(0, 0, 0, 100, colorA));
		nvgStroke(vg);

		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		tw = nvgTextBounds(vg, 0, 0, text, (FloatBuffer) null);
		if (preicon != null) {
			nvgFontSize(vg, h * 1.3f);
			nvgFontFace(vg, entypo);
			iw = nvgTextBounds(vg, 0, 0, preicon, (FloatBuffer) null);
			iw += h * 0.15f;
		}

		if (preicon != null) {
			nvgFontSize(vg, h * 1.3f);
			nvgFontFace(vg, entypo);
			nvgFillColor(vg, Theme.rgba(100, 100, 100, 96, colorA));
			nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
			nvgText(vg, x + w * 0.5f - tw * 0.5f - iw * 0.75f, y + h * 0.5f, preicon);
		}

		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
		nvgFillColor(vg, buttonTextColor);
		nvgText(vg, x + w * 0.5f - tw * 0.5f + iw * 0.25f, y + h * 0.5f, text);
		nvgRestore(vg);
	}

	@Override
	public void renderContexMenuButton(long vg, String text, String font, float x, float y, float w, float h,
			float fontSize, boolean highlight) {
		nvgSave(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x, y, w, h);
		if (highlight)
			nvgFillColor(vg, contextButtonHighlight);
		else
			nvgFillColor(vg, contextButtonColor);
		nvgFill(vg);

		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
		nvgFillColor(vg, Theme.rgba(255, 255, 255, 255, colorA));
		nvgText(vg, x + 10f, y + h * 0.5f, text);
		nvgRestore(vg);
	}

	@Override
	public void renderToggleButton(long vg, String text, String font, float x, float y, float w, float h,
			float fontSize, boolean status) {
		nvgSave(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x, y, w, h);
		if (status)
			nvgFillColor(vg, Theme.setColor(toggleButtonHighlight.r() - 0.4f, toggleButtonHighlight.g() - 0.4f,
					toggleButtonHighlight.b() - 0.4f, 1f, colorA));
		else
			nvgFillColor(vg, Theme.rgba(0, 0, 0, 255, colorA));
		nvgFill(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x + 3, y + 3, w - 6, h - 6);
		nvgPathWinding(vg, NVG_HOLE);
		nvgRect(vg, x, y, w, h);
		if (status)
			nvgFillColor(vg, toggleButtonHighlight);
		else
			nvgFillColor(vg, toggleButtonColor);
		nvgFill(vg);

		nvgBeginPath(vg);
		if (status)
			nvgRect(vg, x + w - h + 5, y + 5, h - 10, h - 10);
		else
			nvgRect(vg, x + 5, y + 5, h - 10, h - 10);
		nvgFillColor(vg, toggleButtonColor);
		nvgFill(vg);

		nvgRestore(vg);
	}

	@Override
	public void renderSpinner(long vg, float cx, float cy, float r, float t) {
		float a0 = 0.0f + t * 6;
		float a1 = NVG_PI + t * 6;
		float r0 = r;
		float r1 = r * 0.75f;
		float ax, ay, bx, by;
		NVGPaint paint = paintA;

		nvgSave(vg);
		nvgBeginPath(vg);
		nvgArc(vg, cx, cy, r0, a0, a1, NVG_CW);
		nvgArc(vg, cx, cy, r1, a1, a0, NVG_CCW);
		nvgClosePath(vg);
		ax = cx + (float) Math.cos(a0) * (r0 + r1) * 0.5f;
		ay = cy + (float) Math.sin(a0) * (r0 + r1) * 0.5f;
		bx = cx + (float) Math.cos(a1) * (r0 + r1) * 0.5f;
		by = cy + (float) Math.sin(a1) * (r0 + r1) * 0.5f;
		nvgLinearGradient(vg, ax, ay, bx, by, Theme.rgba(0, 0, 0, 0, colorA), Theme.rgba(0, 0, 0, 128, colorB), paint);
		nvgFillPaint(vg, paint);
		nvgFill(vg);

		nvgRestore(vg);
	}

	@Override
	public float renderParagraph(long vg, float x, float y, float width, float fontSize, String font, String text,
			int align, NVGColor color) {
		if (text == null)
			text = "";
		ByteBuffer paragraph = memUTF8(text);

		nvgSave(vg);

		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		nvgTextAlign(vg, align);
		nvgTextMetrics(vg, null, null, lineh);

		long start = memAddress(paragraph);
		long end = start + paragraph.remaining();
		int nrows;
		float yy = y;
		while ((nrows = nnvgTextBreakLines(vg, start, end, width, memAddress(rows), 3)) != 0) {
			for (int i = 0; i < nrows; i++) {
				NVGTextRow row = rows.get(i);
				nvgFillColor(vg, color);
				nnvgText(vg, x, yy, row.start(), row.end());
				yy += lineh.get(0);
			}
			start = rows.get(nrows - 1).next();
		}

		nvgRestore(vg);
		return yy - y;
	}

	@Override
	public void renderBox(long vg, float x, float y, float w, float h, NVGColor color, float rt, float lt, float rb,
			float lb) {
		nvgBeginPath(vg);
		nvgRoundedRectVarying(vg, x, y, w, h, lt, rt, lb, rb);
		nvgFillColor(vg, color);
		nvgFill(vg);
	}

	@Override
	public void renderSlider(long vg, float pos, float x, float y, float w, float h) {

		nvgSave(vg);
		// Slot
		nvgBeginPath(vg);
		nvgRect(vg, x - 6, y, w + 12, h);
		nvgFillColor(vg, Theme.rgba(71, 71, 71, 255, colorA));
		nvgFill(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x + 0.5f - 6, y + 0.5f, w - 1 + 12, h - 1);
		nvgStrokeColor(vg, Theme.rgba(0, 0, 0, 255, colorA));
		nvgStroke(vg);

		// Knob
		nvgBeginPath(vg);
		nvgRect(vg, x + (int) (pos * w) - 5, y + 1, 10, h - 2);
		nvgFillColor(vg, Theme.rgba(200, 200, 200, 255, colorB));
		nvgFill(vg);

		nvgRestore(vg);
	}

	@Override
	public void renderScrollBarV(long vg, float x, float y, float w, float h, float pos, float sizeV) {
		float scrollv;
		float scrollBarSize = (float) REGISTRY
				.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/scrollBarSize"));

		nvgSave(vg);
		// Scroll bar
		nvgBeginPath(vg);
		nvgRect(vg, x + w - scrollBarSize, y + scrollBarSize, scrollBarSize, h - scrollBarSize * 2f);
		nvgFillColor(vg, Theme.rgba(128, 128, 128, 140, colorB));
		nvgFill(vg);

		scrollv = (h / sizeV) * (h / 2);
		nvgBeginPath(vg);
		nvgRect(vg, x + w - scrollBarSize, y + scrollBarSize + (h - 8 - scrollv) * pos, scrollBarSize,
				scrollv - scrollBarSize * 2f + 8);
		nvgFillColor(vg, Theme.rgba(220, 220, 220, 255, colorB));
		nvgFill(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x + w - scrollBarSize, y, scrollBarSize, scrollBarSize);
		nvgRect(vg, x + w - scrollBarSize, y + h - scrollBarSize, scrollBarSize, scrollBarSize);
		nvgFillColor(vg, Theme.rgba(80, 80, 80, 140, colorB));
		nvgFill(vg);

		nvgBeginPath(vg);
		nvgMoveTo(vg, x + w - scrollBarSize / 2 - 6, y + scrollBarSize / 2 + 4);
		nvgLineTo(vg, x + w - scrollBarSize / 2, y + scrollBarSize / 2 - 4);
		nvgLineTo(vg, x + w - scrollBarSize / 2 + 6, y + scrollBarSize / 2 + 4);
		nvgMoveTo(vg, x + w - scrollBarSize / 2 - 6, y + h - scrollBarSize / 2 - 4);
		nvgLineTo(vg, x + w - scrollBarSize / 2, y + h - scrollBarSize / 2 + 4);
		nvgLineTo(vg, x + w - scrollBarSize / 2 + 6, y + h - scrollBarSize / 2 - 4);
		nvgStrokeColor(vg, Theme.rgba(0, 0, 0, 255, colorA));
		nvgStroke(vg);

		nvgRestore(vg);
	}

	@Override
	public void renderDropDownButton(long vg, float x, float y, float w, float h, float fontSize, String font,
			String entypo, String text, boolean inside) {
		nvgBeginPath(vg);
		nvgRect(vg, x + 1, y + 1, w - 2, h - 2);
		if (inside)
			nvgFillColor(vg, buttonHighlight);
		else
			nvgFillColor(vg, buttonColor);
		nvgFill(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x + 0.5f, y + 0.5f, w - 1, h - 1);
		nvgStrokeColor(vg, Theme.rgba(0, 0, 0, 100, colorA));
		nvgStroke(vg);

		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		nvgFillColor(vg, buttonTextColor);
		nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
		nvgText(vg, x + h * 0.3f, y + h * 0.5f, text);

		nvgFontSize(vg, h * 1.3f);
		nvgFontFace(vg, entypo);
		nvgFillColor(vg, Theme.rgba(100, 100, 100, 96, colorA));
		nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
		nvgText(vg, x + w - h * 0.5f, y + h * 0.5f, Theme.ICON_CHEVRON_RIGHT);
	}

	@Override
	public String getName() {
		return "Nano";
	}

}
