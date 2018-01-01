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

package net.luxvacuos.lightengine.client.rendering.nanovg;

public class WindowMessage {

	protected int message;
	protected Object param;

	public WindowMessage(int message, Object param) {
		this.message = message;
		this.param = param;
	}

	public static final int WM_CLOSE = 0;
	public static final int WM_MAXIMIZE = 1;
	public static final int WM_MINIMIZE = 2;
	public static final int WM_RESTORE = 3;
	public static final int WM_RESIZE = 4;
	public static final int WM_EXTEND_FRAME = 5;
	public static final int WM_HIDDEN_WINDOW = 6;
	public static final int WM_ALWAYS_ON_TOP = 7;
	public static final int WM_BACKGROUND_WINDOW = 8;
	public static final int WM_BLUR_BEHIND = 9;
	public static final int WM_COMPOSITOR_DISABLED = 20;
	public static final int WM_COMPOSITOR_ENABLED = 21;
	public static final int WM_COMPOSITOR_RELOAD = 22;
	public static final int WM_SHELL_WINDOW_CREATED = 80;
	public static final int WM_SHELL_WINDOW_CLOSED = 81;
	public static final int WM_SHELL_WINDOW_FOCUS = 82;
	public static final int WM_SHELL_NOTIFICATION_ADD = 83;

	public static final int MSG_OFFSET = 100;

}
