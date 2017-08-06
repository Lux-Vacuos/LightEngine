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

package net.luxvacuos.lightengine.client.rendering.api.nanovg;

import org.lwjgl.nanovg.NVGLUFramebuffer;

import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.themes.Theme.BackgroundStyle;
import net.luxvacuos.lightengine.client.ui.ITitleBar;

public interface IWindow {

	public enum WindowClose {
		DISPOSE, DO_NOTHING
	};

	public void init(Window window);

	public void initApp();

	public void renderApp();

	public void updateApp(float delta);

	public void alwaysUpdateApp(float delta);

	public void disposeApp();

	public void render(IWindowManager nanoWindowManager);

	public void update(float delta, IWindowManager nanoWindowManager);

	public void alwaysUpdate(float delta, IWindowManager nanoWindowManager);

	public void dispose();
	
	public void closeWindow();

	public boolean insideWindow();

	public void setDraggable(boolean draggable);

	public void setDecorations(boolean decorations);

	public void setResizable(boolean resizable);

	public void setCloseButton(boolean closeButton);

	public void setBackgroundStyle(BackgroundStyle backgroundStyle);

	public void setWindowClose(WindowClose windowClose);

	public void setBackgroundColor(float r, float g, float b, float a);

	public void setBackgroundColor(String hex);

	public void setHidden(boolean hidden);

	public void setAsBackground(boolean background);

	public void setAlwaysOnTop(boolean alwaysOnTop);

	public void setBlurBehind(boolean blur);

	public void setMinWidth(int width);

	public void setMinHeight(int height);

	public void extendFrame(float t, float b, float r, float l);

	public void toggleMinimize();

	public void toggleTitleBar();

	public void toggleMaximize();

	public BackgroundStyle getBackgroundStyle();

	public float getWidth();

	public float getHeight();

	public float getX();

	public float getY();

	public String getTitle();

	public NVGLUFramebuffer getFBO();

	public boolean hasDecorations();

	public boolean isResizable();

	public boolean isBackground();

	public boolean isDraggable();

	public boolean isDragging();

	public boolean isResizing();

	public boolean isMinimized();

	public boolean isHidden();

	public boolean isMaximized();

	public ITitleBar getTitleBar();

	public boolean shouldClose();

	public boolean hasBlurBehind();

	public boolean isAlwaysOnTop();

	public void reloadFBO(Window window);

	public void onMainResize();

	public void notifyWindow(int message, Object param);

	public void processWindowMessage(int message, Object param);

}
