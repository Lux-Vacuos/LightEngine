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

package net.luxvacuos.lightengine.client.rendering.nanovg.themes;

import static net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme.colorA;
import static net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme.colorB;
import static net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme.paintA;
import static net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme.paintB;
import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;
import static org.lwjgl.nanovg.NanoVG.NVG_CCW;
import static org.lwjgl.nanovg.NanoVG.NVG_CW;
import static org.lwjgl.nanovg.NanoVG.NVG_HOLE;
import static org.lwjgl.nanovg.NanoVG.NVG_PI;
import static org.lwjgl.nanovg.NanoVG.nnvgText;
import static org.lwjgl.nanovg.NanoVG.nnvgTextBreakLines;
import static org.lwjgl.nanovg.NanoVG.nvgArc;
import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgClosePath;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVG.nvgFillColor;
import static org.lwjgl.nanovg.NanoVG.nvgFillPaint;
import static org.lwjgl.nanovg.NanoVG.nvgFontBlur;
import static org.lwjgl.nanovg.NanoVG.nvgFontFace;
import static org.lwjgl.nanovg.NanoVG.nvgFontSize;
import static org.lwjgl.nanovg.NanoVG.nvgImagePattern;
import static org.lwjgl.nanovg.NanoVG.nvgImageSize;
import static org.lwjgl.nanovg.NanoVG.nvgIntersectScissor;
import static org.lwjgl.nanovg.NanoVG.nvgLineTo;
import static org.lwjgl.nanovg.NanoVG.nvgLinearGradient;
import static org.lwjgl.nanovg.NanoVG.nvgMoveTo;
import static org.lwjgl.nanovg.NanoVG.nvgPathWinding;
import static org.lwjgl.nanovg.NanoVG.nvgRect;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgRoundedRectVarying;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgStroke;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeColor;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeWidth;
import static org.lwjgl.nanovg.NanoVG.nvgText;
import static org.lwjgl.nanovg.NanoVG.nvgTextAlign;
import static org.lwjgl.nanovg.NanoVG.nvgTextBounds;
import static org.lwjgl.nanovg.NanoVG.nvgTextMetrics;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memUTF8;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NVGTextRow;

import net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme.BackgroundStyle;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme.ButtonStyle;
import net.luxvacuos.lightengine.client.ui.ComponentState;
import net.luxvacuos.lightengine.universal.util.registry.KeyCache;

public class NanoTheme implements ITheme {

	private final FloatBuffer lineh = BufferUtils.createFloatBuffer(1);
	private final NVGTextRow.Buffer rows = NVGTextRow.create(3);

	protected NVGColor buttonColor = Theme.rgba(255, 255, 255, 255), buttonHighlight = Theme.rgba(210, 210, 210, 255),
			buttonPress = Theme.rgba(170, 170, 170, 255), buttonTextColor = Theme.rgba(0, 0, 0, 255);
	protected NVGColor toggleButtonColor = Theme.setColor(1f, 1f, 1f, 1f),
			toggleButtonHighlight = Theme.setColor(0.5f, 1f, 0.5f, 1f);
	protected NVGColor titleBarButtonColor = Theme.setColor("#646464C8"),
			titleBarButtonHighlight = Theme.setColor("#BEBEBEC8"), titleBarButtonPress = Theme.setColor("#FFFFFFC8"),
			titleBarButtonCloseHighlight = Theme.setColor("#BE0000C8"),
			titleBarButtonClosePress = Theme.setColor("#FF0000C8");
	protected NVGColor contextButtonColor = Theme.setColor("#646464C8"),
			contextButtonHighlight = Theme.setColor("#BEBEBEC8"), contextButtonPress = Theme.setColor("#A0A0A0C8");

	@Override
	public void renderWindow(long vg, int x, int y, int w, int h, BackgroundStyle backgroundStyle,
			NVGColor backgroundColor, boolean decorations, boolean titleBar, boolean maximized, int ft, int fb, int fr,
			int fl) {
		NVGPaint shadowPaint = paintA;
		int borderSize = (int) REGISTRY
				.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/borderSize"));
		int titleBarHeight = (int) REGISTRY
				.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/titleBarHeight"));
		nvgSave(vg);
		if (Theme.DEBUG)
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);

		if (decorations) {
			if (maximized) {
				// Window
				nvgBeginPath(vg);
				nvgRect(vg, x + fl, y + ft, w - fr - fl, h - fb - ft);
				nvgPathWinding(vg, NVG_HOLE);
				nvgRect(vg, x, y - titleBarHeight, w, h + titleBarHeight);
				nvgFillColor(vg, Theme.rgba(31, 31, 31, 120, colorA));
				nvgFill(vg);
				if (Theme.DEBUG) {
					nvgStrokeColor(vg, Theme.debugA);
					nvgStroke(vg);
				}
			} else {
				// Window
				nvgBeginPath(vg);
				nvgRect(vg, x + fl, y + ft, w - fr - fl, h - fb - ft);
				nvgPathWinding(vg, NVG_HOLE);
				if (titleBar) {
					nvgRect(vg, x - borderSize, y - titleBarHeight - borderSize, w + borderSize * 2f,
							h + titleBarHeight + borderSize * 2f);
					nvgFillColor(vg, Theme.rgba(31, 31, 31, 120, colorA));
					nvgFill(vg);
					if (Theme.DEBUG) {
						nvgStrokeColor(vg, Theme.debugA);
						nvgStroke(vg);
					}
				} else {
					nvgRect(vg, x - borderSize, y - borderSize, w + borderSize * 2f, h + borderSize * 2f);
					nvgFillColor(vg, Theme.rgba(31, 31, 31, 120, colorA));
					nvgFill(vg);
					if (Theme.DEBUG) {
						nvgStrokeColor(vg, Theme.debugA);
						nvgStroke(vg);
					}
				}
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

		if (Theme.DEBUG) {
			nvgBeginPath(vg);
			nvgRect(vg, x + fl, y + ft, w - fr - fl, h - fb - ft);
			nvgStrokeColor(vg, Theme.debugA);
			nvgStroke(vg);
		}

		nvgRestore(vg);
	}

	@Override
	public float renderTitleBarText(long vg, String text, String font, int align, float x, float y, float fontSize) {
		nvgSave(vg);
		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		nvgTextAlign(vg, align);

		nvgFontBlur(vg, 4);
		nvgFillColor(vg, Theme.rgba(0, 0, 0, 255, colorA));
		nvgText(vg, x, y + 1, text);

		nvgFontBlur(vg, 0);
		nvgFillColor(vg, Theme.rgba(255, 255, 255, 255, colorA));
		nvgText(vg, x, y, text);
		float[] bounds = new float[4];
		nvgTextBounds(vg, x, y, text, bounds);
		if (Theme.DEBUG) {
			nvgIntersectScissor(vg, bounds[0], bounds[1], bounds[2] - bounds[0], bounds[3] - bounds[1]);
			nvgBeginPath(vg);
			nvgRect(vg, bounds[0], bounds[1], bounds[2] - bounds[0], bounds[3] - bounds[1]);
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugB);
			nvgStroke(vg);
		}
		nvgRestore(vg);
		return bounds[2];
	}

	@Override
	public void renderTitleBarButton(long vg, ComponentState componentState, float x, float y, float w, float h,
			ButtonStyle style, boolean highlight) {
		nvgSave(vg);
		nvgIntersectScissor(vg, x, y, w, h);
		nvgBeginPath(vg);
		nvgRect(vg, x, y, w, h);
		switch (componentState) {
		case HOVER:
			if (style.equals(ButtonStyle.CLOSE))
				nvgFillColor(vg, titleBarButtonCloseHighlight);
			else
				nvgFillColor(vg, titleBarButtonHighlight);
			break;
		case NONE:
			nvgFillColor(vg, titleBarButtonColor);
			break;
		case PRESSED:
			if (style.equals(ButtonStyle.CLOSE))
				nvgFillColor(vg, titleBarButtonClosePress);
			else
				nvgFillColor(vg, titleBarButtonPress);
			break;
		case SELECTED:
			break;
		}
		nvgFill(vg);

		if (Theme.DEBUG) {
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugB);
			nvgStroke(vg);
		}

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
	public float renderText(long vg, String text, String font, int align, float x, float y, float fontSize,
			NVGColor color) {
		nvgSave(vg);
		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		nvgTextAlign(vg, align);
		nvgFillColor(vg, color);
		nvgText(vg, x, y, text);
		float[] bounds = new float[4];
		nvgTextBounds(vg, x, y, text, bounds);
		if (Theme.DEBUG) {
			nvgIntersectScissor(vg, bounds[0], bounds[1], bounds[2] - bounds[0], bounds[3] - bounds[1]);
			nvgBeginPath(vg);
			nvgRect(vg, bounds[0], bounds[1], bounds[2] - bounds[0], bounds[3] - bounds[1]);
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugB);
			nvgStroke(vg);
		}
		nvgRestore(vg);
		return bounds[2];
	}

	@Override
	public void renderImage(long vg, float x, float y, float w, float h, int image, float alpha) {
		NVGPaint imgPaint = paintB;
		nvgSave(vg);
		nvgIntersectScissor(vg, x, y, w, h);
		nvgImagePattern(vg, x, y, w, h, 0, image, alpha, imgPaint);
		nvgBeginPath(vg);
		nvgRect(vg, x, y, w, h);
		nvgFillPaint(vg, imgPaint);
		nvgFill(vg);
		if (Theme.DEBUG) {
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugB);
			nvgStroke(vg);
		}
		nvgRestore(vg);
	}

	@Override
	public void renderImage(long vg, float x, float y, int image, float alpha) {
		NVGPaint imgPaint = paintB;
		int[] iw = new int[1], ih = new int[1];
		nvgSave(vg);
		nvgImageSize(vg, image, iw, ih);
		nvgIntersectScissor(vg, x, y, iw[0], ih[0]);
		nvgImagePattern(vg, x, y, iw[0], ih[0], 0, image, alpha, imgPaint);
		nvgBeginPath(vg);
		nvgRect(vg, x, y, iw[0], iw[0]);
		nvgFillPaint(vg, imgPaint);
		nvgFill(vg);
		if (Theme.DEBUG) {
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugB);
			nvgStroke(vg);
		}
		nvgRestore(vg);
	}

	@Override
	public void renderEditBoxBase(long vg, ComponentState componentState, float x, float y, float w, float h,
			boolean selected) {
		nvgSave(vg);
		nvgIntersectScissor(vg, x, y, w, h);
		nvgBeginPath(vg);
		nvgRect(vg, x + 1, y + 1, w - 2, h - 2);
		switch (componentState) {
		case HOVER:
			nvgFillColor(vg, buttonHighlight);
			break;
		case NONE:
			nvgFillColor(vg, buttonColor);
			break;
		case PRESSED:
			break;
		case SELECTED:
			nvgFillColor(vg, buttonPress);
			break;
		}
		nvgFill(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x + 0.5f, y + 0.5f, w - 1, h - 1);
		nvgStrokeColor(vg, Theme.rgba(0, 0, 0, 100, colorA));
		nvgStroke(vg);
		if (Theme.DEBUG) {
			nvgBeginPath(vg);
			nvgRect(vg, x, y, w, h);
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugB);
			nvgStroke(vg);
		}
		nvgRestore(vg);
	}

	@Override
	public void renderEditBox(long vg, ComponentState componentState, String text, String font, float x, float y,
			float w, float h, float fontSize, boolean selected) {
		float[] bounds = new float[4];
		renderEditBoxBase(vg, componentState, x, y, w, h, selected);
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
		if (Theme.DEBUG) {
			nvgBeginPath(vg);
			nvgRect(vg, x, y, w, h);
			nvgIntersectScissor(vg, bounds[0], bounds[1], bounds[2] - bounds[0], bounds[3] - bounds[1]);
			nvgRect(vg, bounds[0], bounds[1], bounds[2] - bounds[0], bounds[3] - bounds[1]);
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugB);
			nvgStroke(vg);
		}
		nvgRestore(vg);
	}

	@Override
	public void renderButton(long vg, ComponentState componentState, String preicon, String text, String font,
			String entypo, float x, float y, float w, float h, boolean highlight, float fontSize) {
		float tw, iw = 0;
		nvgSave(vg);
		nvgIntersectScissor(vg, x, y, w, h);
		nvgBeginPath(vg);
		nvgRect(vg, x + 1, y + 1, w - 2, h - 2);
		switch (componentState) {
		case HOVER:
			nvgFillColor(vg, buttonHighlight);
			break;
		case NONE:
			nvgFillColor(vg, buttonColor);
			break;
		case PRESSED:
			nvgFillColor(vg, buttonPress);
			break;
		case SELECTED:
			break;
		}
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
		float[] preiconBounds = new float[4];
		if (preicon != null) {
			nvgFontSize(vg, h * 1.3f);
			nvgFontFace(vg, entypo);
			nvgFillColor(vg, buttonTextColor);
			if (text.isEmpty()) {
				nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
				nvgText(vg, x + w * 0.5f, y + h * 0.5f, preicon);
				nvgTextBounds(vg, x + w * 0.5f, y + h * 0.5f, preicon, preiconBounds);
			} else {
				nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
				nvgText(vg, x + w * 0.5f - tw * 0.5f - iw * 0.75f, y + h * 0.5f, preicon);
				nvgTextBounds(vg, x + w * 0.5f - tw * 0.5f - iw * 0.75f, y + h * 0.5f, preicon, preiconBounds);
			}
		}
		float[] bounds = new float[4];

		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
		nvgFillColor(vg, buttonTextColor);
		nvgTextBounds(vg, x + w * 0.5f - tw * 0.5f + iw * 0.25f, y + h * 0.5f, text, bounds);
		nvgText(vg, x + w * 0.5f - tw * 0.5f + iw * 0.25f, y + h * 0.5f, text);
		if (Theme.DEBUG) {
			nvgBeginPath(vg);
			nvgRect(vg, x, y, w, h);
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugB);
			nvgStroke(vg);

			nvgBeginPath(vg);
			nvgMoveTo(vg, bounds[0], bounds[1]);
			nvgLineTo(vg, bounds[2], bounds[1]);
			nvgLineTo(vg, bounds[2], bounds[3]);
			nvgLineTo(vg, bounds[0], bounds[3]);
			nvgLineTo(vg, bounds[0], bounds[1]);
			if (preicon != null) {
				nvgMoveTo(vg, preiconBounds[0], preiconBounds[1]);
				nvgLineTo(vg, preiconBounds[2], preiconBounds[1]);
				nvgLineTo(vg, preiconBounds[2], preiconBounds[3]);
				nvgLineTo(vg, preiconBounds[0], preiconBounds[3]);
				nvgLineTo(vg, preiconBounds[0], preiconBounds[1]);
			}
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugC);
			nvgStroke(vg);
		}
		nvgRestore(vg);
	}

	@Override
	public void renderContexMenuButton(long vg, ComponentState componentState, String text, String font, float x,
			float y, float w, float h, float fontSize, boolean highlight) {
		nvgSave(vg);
		nvgIntersectScissor(vg, x, y, w, h);
		nvgBeginPath(vg);
		nvgRect(vg, x, y, w, h);
		switch (componentState) {
		case HOVER:
			nvgFillColor(vg, contextButtonHighlight);
			break;
		case NONE:
			nvgFillColor(vg, contextButtonColor);
			break;
		case PRESSED:
			nvgFillColor(vg, contextButtonPress);
			break;
		case SELECTED:
			break;
		}
		nvgFill(vg);

		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
		nvgFillColor(vg, Theme.rgba(255, 255, 255, 255, colorA));
		float[] bounds = new float[4];
		nvgTextBounds(vg, x + 10f, y + h * 0.5f, text, bounds);
		nvgText(vg, x + 10f, y + h * 0.5f, text);
		if (Theme.DEBUG) {
			nvgBeginPath(vg);
			nvgRect(vg, x, y, w, h);
			nvgMoveTo(vg, bounds[0], bounds[1]);
			nvgLineTo(vg, bounds[2], bounds[1]);
			nvgLineTo(vg, bounds[2], bounds[3]);
			nvgLineTo(vg, bounds[0], bounds[3]);
			nvgLineTo(vg, bounds[0], bounds[1]);
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugB);
			nvgStroke(vg);
		}
		nvgRestore(vg);
	}

	@Override
	public void renderToggleButton(long vg, ComponentState componentState, String text, String font, float x, float y,
			float w, float h, float fontSize, boolean status) {
		nvgSave(vg);
		nvgIntersectScissor(vg, x, y, w, h);
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
		if (Theme.DEBUG) {
			nvgBeginPath(vg);
			nvgRect(vg, x, y, w, h);
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugB);
			nvgStroke(vg);
		}
		nvgRestore(vg);
	}

	@Override
	public void renderSpinner(long vg, float cx, float cy, float r, float t) {
		float a0 = t * 6;
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
		nvgLinearGradient(vg, ax, ay, bx, by, Theme.rgba(0, 0, 0, 0, colorA), Theme.rgba(0, 0, 0, 255, colorB), paint);
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

		if (Theme.DEBUG) {
			nvgIntersectScissor(vg, x, y, width, yy - y);
			nvgBeginPath(vg);
			nvgRect(vg, x, y, width, yy - y);
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugB);
			nvgStroke(vg);
		}
		memFree(paragraph);
		nvgRestore(vg);
		return yy - y;
	}

	@Override
	public void renderBox(long vg, float x, float y, float w, float h, NVGColor color, float rt, float lt, float rb,
			float lb) {
		nvgSave(vg);
		nvgIntersectScissor(vg, x, y, w, h);
		nvgBeginPath(vg);
		nvgRoundedRectVarying(vg, x, y, w, h, lt, rt, lb, rb);
		nvgFillColor(vg, color);
		nvgFill(vg);
		if (Theme.DEBUG) {
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugB);
			nvgStroke(vg);
		}
		nvgRestore(vg);
	}

	@Override
	public void renderSlider(long vg, ComponentState componentState, float pos, float x, float y, float w, float h) {

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

		if (Theme.DEBUG) {
			nvgBeginPath(vg);
			nvgRect(vg, x, y, w, h);
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugB);
			nvgStroke(vg);
		}

		nvgRestore(vg);
	}

	@Override
	public void renderScrollBarV(long vg, ComponentState componentState, float x, float y, float w, float h, float pos,
			float sizeV) {
		int scrollBarSize = (int) REGISTRY
				.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/scrollBarSize"));

		nvgSave(vg);
		nvgIntersectScissor(vg, x, y, w, h);

		nvgSave(vg);
		nvgIntersectScissor(vg, x + w - scrollBarSize, y + scrollBarSize, scrollBarSize, h - scrollBarSize * 2f);
		nvgBeginPath(vg);
		nvgRect(vg, x + w - scrollBarSize, y + scrollBarSize, scrollBarSize, h - scrollBarSize * 2f);
		nvgFillColor(vg, Theme.rgba(128, 128, 128, 140, colorB));
		nvgFill(vg);

		if (Theme.DEBUG) {
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugB);
			nvgStroke(vg);
		}
		nvgRestore(vg);

		nvgSave(vg);
		nvgIntersectScissor(vg, x + w - scrollBarSize, y + scrollBarSize + pos * (h - scrollBarSize * 2f - sizeV),
				scrollBarSize, sizeV);
		nvgBeginPath(vg);
		nvgRect(vg, x + w - scrollBarSize, y + scrollBarSize + pos * (h - scrollBarSize * 2f - sizeV), scrollBarSize,
				sizeV);
		nvgFillColor(vg, Theme.rgba(220, 220, 220, 255, colorB));
		nvgFill(vg);
		if (Theme.DEBUG) {
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugB);
			nvgStroke(vg);
		}
		nvgRestore(vg);

		nvgSave(vg);
		nvgIntersectScissor(vg, x + w - scrollBarSize, y, scrollBarSize, scrollBarSize);
		nvgBeginPath(vg);
		nvgRect(vg, x + w - scrollBarSize, y, scrollBarSize, scrollBarSize);
		nvgFillColor(vg, Theme.rgba(80, 80, 80, 140, colorB));
		nvgFill(vg);
		if (Theme.DEBUG) {
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugB);
			nvgStroke(vg);
		}
		nvgRestore(vg);

		nvgSave(vg);
		nvgIntersectScissor(vg, x + w - scrollBarSize, y + h - scrollBarSize, scrollBarSize, scrollBarSize);
		nvgBeginPath(vg);
		nvgRect(vg, x + w - scrollBarSize, y + h - scrollBarSize, scrollBarSize, scrollBarSize);
		nvgFillColor(vg, Theme.rgba(80, 80, 80, 140, colorB));
		nvgFill(vg);

		if (Theme.DEBUG) {
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugB);
			nvgStroke(vg);
		}
		nvgRestore(vg);

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
	public void renderDropDownButton(long vg, ComponentState componentState, float x, float y, float w, float h,
			float fontSize, String font, String entypo, String text, boolean inside) {
		nvgSave(vg);
		nvgIntersectScissor(vg, x, y, w, h);
		nvgBeginPath(vg);
		nvgRect(vg, x + 1, y + 1, w - 2, h - 2);
		switch (componentState) {
		case HOVER:
			nvgFillColor(vg, buttonHighlight);
			break;
		case NONE:
			nvgFillColor(vg, buttonColor);
			break;
		case PRESSED:
			nvgFillColor(vg, buttonPress);
			break;
		case SELECTED:
			nvgFillColor(vg, buttonPress);
			break;
		}
		nvgFill(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x + 0.5f, y + 0.5f, w - 1, h - 1);
		nvgStrokeColor(vg, Theme.rgba(0, 0, 0, 100, colorA));
		nvgStroke(vg);

		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		nvgFillColor(vg, buttonTextColor);
		nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
		float[] bounds = new float[4];
		nvgTextBounds(vg, x + h * 0.3f, y + h * 0.5f, text, bounds);
		nvgText(vg, x + h * 0.3f, y + h * 0.5f, text);

		nvgFontSize(vg, h * 1.3f);
		nvgFontFace(vg, entypo);
		nvgFillColor(vg, Theme.rgba(100, 100, 100, 96, colorA));
		nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
		nvgText(vg, x + w - h * 0.5f, y + h * 0.5f, Theme.ICON_CHEVRON_RIGHT);
		if (Theme.DEBUG) {
			nvgBeginPath(vg);
			nvgRect(vg, x, y, w, h);
			nvgMoveTo(vg, bounds[0], bounds[1]);
			nvgLineTo(vg, bounds[2], bounds[1]);
			nvgLineTo(vg, bounds[2], bounds[3]);
			nvgLineTo(vg, bounds[0], bounds[3]);
			nvgLineTo(vg, bounds[0], bounds[1]);
			nvgStrokeWidth(vg, Theme.DEBUG_STROKE);
			nvgStrokeColor(vg, Theme.debugB);
			nvgStroke(vg);
		}
		nvgRestore(vg);
	}

	@Override
	public String getName() {
		return "Nano";
	}

}
