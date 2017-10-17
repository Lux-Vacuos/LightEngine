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

public class DummyShell implements IShell {

	DummyShell() {
	}

	@Override
	public void initApp() {
	}

	@Override
	public void renderApp() {
	}

	@Override
	public void updateApp(float delta) {
	}

	@Override
	public void alwaysUpdateApp(float delta) {
	}

	@Override
	public void disposeApp() {
	}

	@Override
	public void toggleShell() {
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public IWindow getNotificationsWindow() {
		return null;
	}

	@Override
	public void init(Window window) {
	}

	@Override
	public void render(IWindowManager nanoWindowManager) {
	}

	@Override
	public void update(float delta, IWindowManager nanoWindowManager) {
	}

	@Override
	public void alwaysUpdate(float delta, IWindowManager nanoWindowManager) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void closeWindow() {
	}

	@Override
	public boolean insideWindow() {
		return false;
	}

	@Override
	public void setDraggable(boolean draggable) {
	}

	@Override
	public void setDecorations(boolean decorations) {
	}

	@Override
	public void setResizable(boolean resizable) {
	}

	@Override
	public void setCloseButton(boolean closeButton) {
	}

	@Override
	public void setBackgroundStyle(BackgroundStyle backgroundStyle) {
	}

	@Override
	public void setWindowClose(WindowClose windowClose) {
	}

	@Override
	public void setBackgroundColor(float r, float g, float b, float a) {
	}

	@Override
	public void setBackgroundColor(String hex) {
	}

	@Override
	public void setHidden(boolean hidden) {
	}

	@Override
	public void setAsBackground(boolean background) {
	}

	@Override
	public void setAlwaysOnTop(boolean alwaysOnTop) {
	}

	@Override
	public void setBlurBehind(boolean blur) {
	}

	@Override
	public void setMinWidth(int width) {
	}

	@Override
	public void setMinHeight(int height) {
	}

	@Override
	public void extendFrame(int t, int b, int r, int l) {
	}

	@Override
	public void setAnimationState(AnimationState animationState) {
	}

	@Override
	public void toggleMinimize() {
	}

	@Override
	public void toggleTitleBar() {
	}

	@Override
	public void toggleMaximize() {
	}

	@Override
	public BackgroundStyle getBackgroundStyle() {
		return null;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getX() {
		return 0;
	}

	@Override
	public int getY() {
		return 0;
	}

	@Override
	public int getFX() {
		return 0;
	}

	@Override
	public int getFY() {
		return 0;
	}

	@Override
	public int getFH() {
		return 0;
	}

	@Override
	public int getFW() {
		return 0;
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public NVGLUFramebuffer getFBO() {
		return null;
	}

	@Override
	public AnimationState getAnimationState() {
		return null;
	}

	@Override
	public boolean hasDecorations() {
		return false;
	}

	@Override
	public boolean isResizable() {
		return false;
	}

	@Override
	public boolean isBackground() {
		return false;
	}

	@Override
	public boolean isDraggable() {
		return false;
	}

	@Override
	public boolean isDragging() {
		return false;
	}

	@Override
	public boolean isResizing() {
		return false;
	}

	@Override
	public boolean isMinimized() {
		return false;
	}

	@Override
	public boolean isHidden() {
		return true; // Hardcoded Hidden
	}

	@Override
	public boolean isMaximized() {
		return false;
	}

	@Override
	public boolean isCompositor() {
		return false;
	}

	@Override
	public boolean isAnimating() {
		return false;
	}

	@Override
	public ITitleBar getTitleBar() {
		return null;
	}

	@Override
	public boolean shouldClose() {
		return false;
	}

	@Override
	public boolean hasBlurBehind() {
		return false;
	}

	@Override
	public boolean isAlwaysOnTop() {
		return false;
	}

	@Override
	public void notifyWindow(int message, Object param) {
	}

	@Override
	public void processWindowMessage(int message, Object param) {
	}

	@Override
	public void setTransparentInput(boolean transparentInput) {
	}

	@Override
	public boolean hasTransparentInput() {
		return false;
	}

}
