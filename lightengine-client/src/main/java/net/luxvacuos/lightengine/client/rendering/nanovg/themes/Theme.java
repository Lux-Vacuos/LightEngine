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

package net.luxvacuos.lightengine.client.rendering.nanovg.themes;

import static org.lwjgl.nanovg.NanoVGGL3.nvglCreateImageFromHandle;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;

import net.luxvacuos.lightengine.client.ui.ComponentState;

public class Theme {

	private static ITheme theme;

	static void setTheme(ITheme theme) {
		Theme.theme = theme;
	}

	public static final String ICON_SEARCH = cpToUTF8(0x1F50D);
	public static final String ICON_CIRCLED_CROSS = cpToUTF8(0x2716);
	public static final String ICON_CHEVRON_RIGHT = cpToUTF8(0xE75E);
	public static final String ICON_CHECK = cpToUTF8(0x2713);
	public static final String ICON_LOGIN = cpToUTF8(0xE740);
	public static final String ICON_TRASH = cpToUTF8(0xE729);
	public static final String ICON_INFORMATION_SOURCE = cpToUTF8(0x2139);
	public static final String ICON_GEAR = cpToUTF8(0x2699);
	public static final String ICON_BLACK_RIGHT_POINTING_TRIANGLE = cpToUTF8(0x25B6);

	public static final NVGPaint paintA = NVGPaint.create();
	public static final NVGPaint paintB = NVGPaint.create();
	public static final NVGPaint paintC = NVGPaint.create();
	public static final NVGColor colorA = NVGColor.create();
	public static final NVGColor colorB = NVGColor.create();
	public static final NVGColor colorC = NVGColor.create();

	public static final boolean DEBUG = false;
	public static final float DEBUG_STROKE = 2;

	public static final NVGColor debugA = setColor(1, 0, 0, 1);
	public static final NVGColor debugB = setColor(0, 1, 0, 1);
	public static final NVGColor debugC = setColor(0, 0, 1, 1);
	public static final NVGColor debugE = setColor(1, 1, 1, 1);

	public enum ButtonStyle {
		CLOSE, MAXIMIZE, MINIMIZE, NONE, LEFT_ARROW, RIGHT_ARROW
	};

	public enum BackgroundStyle {
		SOLID, TRANSPARENT
	};

	public static String cpToUTF8(int cp) {
		return new String(Character.toChars(cp));
	}

	public static int generateImageFromTexture(long vg, int texID, int w, int h, int flags) {
		return nvglCreateImageFromHandle(vg, texID, w, h, flags);
	}

	public static NVGColor rgba(int r, int g, int b, int a, NVGColor color) {
		return setColor(r / 255f, g / 255f, b / 255f, a / 255f, color);
	}

	public static NVGColor rgba(int r, int g, int b, int a) {
		return setColor(r / 255f, g / 255f, b / 255f, a / 255f);
	}

	public static NVGColor setColor(float r, float g, float b, float a, NVGColor color) {
		color.r(r);
		color.g(g);
		color.b(b);
		color.a(a);
		return color;
	}

	public static NVGColor setColor(float r, float g, float b, float a) {
		return setColor(r, g, b, a, NVGColor.create());
	}

	public static NVGColor setColor(String hex, NVGColor color) {
		color.r(Integer.valueOf(hex.substring(1, 3), 16) / 255f);
		color.g(Integer.valueOf(hex.substring(3, 5), 16) / 255f);
		color.b(Integer.valueOf(hex.substring(5, 7), 16) / 255f);
		color.a(Integer.valueOf(hex.substring(7, 9), 16) / 255f);
		return color;
	}

	public static NVGColor setColor(String hex) {
		return setColor(hex, NVGColor.create());
	}

	public static void renderWindow(long vg, int x, int y, int w, int h, BackgroundStyle backgroundStyle,
			NVGColor backgroundColor, boolean decorations, boolean titleBar, boolean maximized, int ft, int fb, int fr,
			int fl) {
		theme.renderWindow(vg, x, y, w, h, backgroundStyle, backgroundColor, decorations, titleBar, maximized, ft, fb,
				fr, fl);

	}

	public static float renderTitleBarText(long vg, String text, String font, int align, float x, float y,
			float fontSize) {
		return theme.renderTitleBarText(vg, text, font, align, x, y, fontSize);

	}

	public static void renderTitleBarButton(long vg, ComponentState componentState, float x, float y, float w, float h,
			ButtonStyle style, boolean highlight) {
		theme.renderTitleBarButton(vg, componentState, x, y, w, h, style, highlight);

	}

	public static float renderText(long vg, String text, String font, int align, float x, float y, float fontSize,
			NVGColor color) {
		return theme.renderText(vg, text, font, align, x, y, fontSize, color);

	}

	public static void renderImage(long vg, float x, float y, float w, float h, int image, float alpha) {
		theme.renderImage(vg, x, y, w, h, image, alpha);
	}

	public static void renderImage(long vg, float x, float y, int image, float alpha) {
		theme.renderImage(vg, x, y, image, alpha);
	}

	public static void renderEditBoxBase(long vg, ComponentState componentState, float x, float y, float w, float h,
			boolean selected) {
		theme.renderEditBoxBase(vg, componentState, x, y, w, h, selected);
	}

	public static void renderEditBox(long vg, ComponentState componentState, String text, String font, float x, float y,
			float w, float h, float fontSize, boolean selected) {
		theme.renderEditBox(vg, componentState, text, font, x, y, w, h, fontSize, selected);
	}

	public static void renderButton(long vg, ComponentState componentState, String preicon, String text, String font,
			String entypo, float x, float y, float w, float h, boolean highlight, float fontSize) {
		theme.renderButton(vg, componentState, preicon, text, font, entypo, x, y, w, h, highlight, fontSize);
	}

	public static void renderContexMenuButton(long vg, ComponentState componentState, String text, String font, float x,
			float y, float w, float h, float fontSize, boolean highlight) {
		theme.renderContexMenuButton(vg, componentState, text, font, x, y, w, h, fontSize, highlight);
	}

	public static void renderToggleButton(long vg, ComponentState componentState, String text, String font, float x,
			float y, float w, float h, float fontSize, boolean status) {
		theme.renderToggleButton(vg, componentState, text, font, x, y, w, h, fontSize, status);
	}

	public static void renderSpinner(long vg, float cx, float cy, float r, float t) {
		theme.renderSpinner(vg, cx, cy, r, t);
	}

	public static float renderParagraph(long vg, float x, float y, float width, float fontSize, String font,
			String text, int align, NVGColor color) {
		return theme.renderParagraph(vg, x, y, width, fontSize, font, text, align, color);
	}

	public static void renderBox(long vg, float x, float y, float w, float h, NVGColor color, float rt, float lt,
			float rb, float lb) {
		theme.renderBox(vg, x, y, w, h, color, rt, lt, rb, lb);
	}

	public static void renderSlider(long vg, ComponentState componentState, float pos, float x, float y, float w,
			float h) {
		theme.renderSlider(vg, componentState, pos, x, y, w, h);
	}

	public static void renderScrollBarV(long vg, ComponentState componentState, float x, float y, float w, float h,
			float pos, float sizeV) {
		theme.renderScrollBarV(vg, componentState, x, y, w, h, pos, sizeV);
	}

	public static void renderDropDownButton(long vg, ComponentState componentState, float x, float y, float w, float h,
			float fontSize, String font, String entypo, String text, boolean inside) {
		theme.renderDropDownButton(vg, componentState, x, y, w, h, fontSize, font, entypo, text, inside);
	}

}
