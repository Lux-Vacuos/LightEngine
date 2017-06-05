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

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;
import static org.lwjgl.nanovg.NanoVG.nvgScissor;
import static org.lwjgl.nanovg.NanoVGGL3.nvgluBindFramebuffer;
import static org.lwjgl.nanovg.NanoVGGL3.nvgluCreateFramebuffer;
import static org.lwjgl.nanovg.NanoVGGL3.nvgluDeleteFramebuffer;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGLUFramebuffer;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.input.Mouse;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Sync;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.glfw.WindowManager;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.themes.Theme;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.themes.Theme.BackgroundStyle;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.themes.Theme.ButtonStyle;
import net.luxvacuos.lightengine.client.rendering.api.opengl.Renderer;
import net.luxvacuos.lightengine.client.ui.Alignment;
import net.luxvacuos.lightengine.client.ui.Direction;
import net.luxvacuos.lightengine.client.ui.FlowLayout;
import net.luxvacuos.lightengine.client.ui.ITitleBar;
import net.luxvacuos.lightengine.client.ui.TitleBar;
import net.luxvacuos.lightengine.client.ui.TitleBarButton;
import net.luxvacuos.lightengine.client.ui.TitleBarText;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public abstract class NanoWindow implements IWindow {

	private boolean draggable = true, decorations = true, resizable = true, maximized, hidden, exit, alwaysOnTop,
			background, blurBehind = true, running = true, minimized, closeButton = true;
	private boolean resizingRight, resizingRightBottom, resizingBottom, resizingTop, resizingLeft;
	private float ft, fb, fr, fl;
	private BackgroundStyle backgroundStyle = BackgroundStyle.SOLID;
	private NVGColor backgroundColor = Theme.rgba(0, 0, 0, 255);
	protected float x, y, w, h, minW = 300, minH = 300;
	private float oldX, oldY, oldW, oldH;
	private WindowClose windowClose = WindowClose.DISPOSE;
	private ITitleBar titleBar;
	private NVGLUFramebuffer fbo;
	private String title;
	private double lastLoopTime;
	private Thread thread;
	private int UPS = 60;

	public NanoWindow(float x, float y, float w, float h, String title) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.title = title;
		titleBar = new TitleBar(this);
	}

	@Override
	public void init(Window wind) {
		fbo = nvgluCreateFramebuffer(wind.getNVGID(), (int) (wind.getWidth() * wind.getPixelRatio()),
				(int) (wind.getHeight() * wind.getPixelRatio()), 0);
		titleBar.getLeft().setLayout(new FlowLayout(Direction.RIGHT, 1, 0));
		titleBar.getRight().setLayout(new FlowLayout(Direction.LEFT, 1, 0));
		initApp(wind);
		TitleBarButton closeBtn = new TitleBarButton(0, -1, 28, 28);
		closeBtn.setOnButtonPress(() -> {
			onClose();
			closeWindow();
		});
		closeBtn.setWindowAlignment(Alignment.RIGHT_TOP);
		closeBtn.setAlignment(Alignment.LEFT_BOTTOM);
		closeBtn.setStyle(ButtonStyle.CLOSE);

		TitleBarButton maximizeBtn = new TitleBarButton(0, -1, 28, 28);
		maximizeBtn.setOnButtonPress(() -> {
			toggleMaximize();
		});
		maximizeBtn.setWindowAlignment(Alignment.RIGHT_TOP);
		maximizeBtn.setAlignment(Alignment.LEFT_BOTTOM);
		maximizeBtn.setStyle(ButtonStyle.MAXIMIZE);

		TitleBarButton minimizeBtn = new TitleBarButton(0, -1, 28, 28);
		minimizeBtn.setOnButtonPress(() -> {
			minimized = true;
		});
		minimizeBtn.setWindowAlignment(Alignment.RIGHT_TOP);
		minimizeBtn.setAlignment(Alignment.LEFT_BOTTOM);
		minimizeBtn.setStyle(ButtonStyle.MINIMIZE);

		TitleBarText titleText = new TitleBarText(title, 0, 0);
		titleText.setWindowAlignment(Alignment.CENTER);
		titleText.setAlign(NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
		if (closeButton)
			titleBar.getRight().addComponent(closeBtn);
		if (resizable)
			titleBar.getRight().addComponent(maximizeBtn);
		if (GraphicalSubsystem.getWindowManager().isShellEnabled())
			titleBar.getRight().addComponent(minimizeBtn);
		titleBar.getCenter().addComponent(titleText);

		titleBar.setOnDrag((window) -> {
			if (draggable && !maximized) {
				this.x += Mouse.getDX();
				this.y += Mouse.getDY();
			}
		});

		thread = new Thread(() -> {
			lastLoopTime = WindowManager.getTime();
			float delta = 0;
			float accumulator = 0f;
			float interval = 1f / UPS;
			Sync sync = new Sync();
			while (running) {
				delta = getDelta();
				accumulator += delta;
				while (accumulator >= interval) {
					updateApp(delta, wind);
					accumulator -= interval;
				}
				sync.sync(UPS);
			}
		});
		// thread.start();
	}

	@Override
	public void render(Window window, IWindowManager nanoWindowManager) {
		if (!hidden && !minimized) {
			nvgluBindFramebuffer(window.getNVGID(), fbo);
			Renderer.clearBuffer(GL_COLOR_BUFFER_BIT);
			Renderer.clearColors(0, 0, 0, 0);
			window.beingNVGFrame();
			Theme.renderWindow(window.getNVGID(), x, window.getHeight() - y, w, h, backgroundStyle, backgroundColor,
					decorations, titleBar.isEnabled(), maximized, ft, fb, fr, fl);
			if (decorations)
				titleBar.render(window);
			nvgScissor(window.getNVGID(), x, window.getHeight() - y, w, h);
			renderApp(window);
			window.endNVGFrame();
			nvgluBindFramebuffer(window.getNVGID(), null);
		}
	}

	@Override
	public void update(float delta, Window window, IWindowManager nanoWindowManager) {
		if (decorations && !hidden && !minimized) {
			if (!isResizing() && !minimized)
				titleBar.update(delta, window);
			float borderSize = (float) REGISTRY
					.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/borderSize"));
			if ((Mouse.isButtonDown(0) && canResizeRightBottom(borderSize) && !isResizing()) || resizingRightBottom) {
				resizingRightBottom = Mouse.isButtonDown(0);
				w = Maths.clamp(Mouse.getX() - x, minW);
				h = Maths.clamp(-Mouse.getY() + y, minH);
			}
			if ((Mouse.isButtonDown(0) && canResizeRight(borderSize) && !isResizing()) || resizingRight) {
				resizingRight = Mouse.isButtonDown(0);
				w = Maths.clamp(Mouse.getX() - x, minW);
			}
			if ((Mouse.isButtonDown(0) && canResizeLeft(borderSize) && !isResizing()) || resizingLeft) {
				resizingLeft = Mouse.isButtonDown(0);
				x = Mouse.getX();
				w = Maths.clamp(w - Mouse.getDX(), minW);
			}
			if ((Mouse.isButtonDown(0) && canResizeBottom(borderSize) && !isResizing()) || resizingBottom) {
				resizingBottom = Mouse.isButtonDown(0);
				h = Maths.clamp(-Mouse.getY() + y, minH);
			}
			if ((Mouse.isButtonDown(0) && canResizeTop(borderSize) && !isResizing()) || resizingTop) {
				resizingTop = Mouse.isButtonDown(0);
				// h = Maths.clamp(-Mouse.getY() + y, minH);
				y = Mouse.getY() - (float) REGISTRY
						.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"));
				h = Maths.clamp(h + Mouse.getDY(), minH);
			}
		}
		if (!isResizing() && !minimized)
			updateApp(delta, window);
	}

	@Override
	public void alwaysUpdate(float delta, Window window, IWindowManager nanoWindowManager) {
		titleBar.alwaysUpdate(delta, window);
		alwaysUpdateApp(delta, window);
	}

	@Override
	public void dispose(Window window) {
		running = false;
		nvgluDeleteFramebuffer(window.getNVGID(), fbo);
		disposeApp(window);
	}

	@Override
	public void onClose() {
	}

	private boolean canResizeBottom(float borderSize) {
		return Mouse.getX() > x && Mouse.getY() < y - h && Mouse.getX() < x + w && Mouse.getY() > y - h - borderSize
				&& resizable && !maximized && !minimized;
	}

	private boolean canResizeTop(float borderSize) {
		if (titleBar.isEnabled())
			return Mouse.getX() > x
					&& Mouse.getY() < y
							+ (float) REGISTRY
									.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"))
							+ borderSize
					&& Mouse.getX() < x + w
					&& Mouse.getY() > y + (float) REGISTRY
							.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"))
					&& resizable && !maximized && !minimized;
		else
			return Mouse.getX() > x && Mouse.getY() < y + borderSize && Mouse.getX() < x + w && Mouse.getY() > y
					&& resizable && !maximized && !minimized;
	}

	private boolean canResizeRightBottom(float borderSize) {
		return Mouse.getX() > x + w && Mouse.getY() < y - h && Mouse.getX() < x + w + borderSize
				&& Mouse.getY() > y - h - borderSize && resizable && !maximized && !minimized;
	}

	private boolean canResizeRight(float borderSize) {
		return Mouse.getX() > x + w && Mouse.getY() < y && Mouse.getX() < x + w + borderSize && Mouse.getY() > y - h
				&& resizable && !maximized && !minimized;
	}

	private boolean canResizeLeft(float borderSize) {
		return Mouse.getX() > x - borderSize && Mouse.getY() < y && Mouse.getX() < x + borderSize
				&& Mouse.getY() > y - h && resizable && !maximized && !minimized;
	}

	@Override
	public boolean insideWindow() {
		float borderSize = (float) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/borderSize"));
		if (titleBar.isEnabled() && decorations)
			if ((boolean) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarBorder")))
				return Mouse.getX() > x - borderSize && Mouse.getX() < x + w + borderSize
						&& Mouse.getY() > y - h - borderSize
						&& Mouse.getY() < y
								+ (float) REGISTRY
										.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"))
								+ borderSize;
			else
				return Mouse.getX() > x - borderSize && Mouse.getX() < x + w + borderSize
						&& Mouse.getY() > y - h - borderSize && Mouse.getY() < y + (float) REGISTRY
								.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"));
		else if (!decorations)
			return Mouse.getX() > x && Mouse.getX() < x + w && Mouse.getY() > y - h && Mouse.getY() < y;
		else
			return Mouse.getX() > x - borderSize && Mouse.getX() < x + w + borderSize * 2f
					&& Mouse.getY() > y - h - borderSize * 2f && Mouse.getY() < y + borderSize;
	}

	@Override
	public void setDraggable(boolean draggable) {
		this.draggable = draggable;
	}

	@Override
	public void setDecorations(boolean decorations) {
		this.decorations = decorations;
	}

	@Override
	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	@Override
	public void setCloseButton(boolean closeButton) {
		this.closeButton = closeButton;
	}

	@Override
	public void setBackgroundStyle(BackgroundStyle backgroundStyle) {
		this.backgroundStyle = backgroundStyle;
	}

	@Override
	public void setWindowClose(WindowClose windowClose) {
		this.windowClose = windowClose;
	}

	@Override
	public void setBackgroundColor(float r, float g, float b, float a) {
		backgroundColor.r(r);
		backgroundColor.g(g);
		backgroundColor.b(b);
		backgroundColor.a(a);
	}

	@Override
	public void setBackgroundColor(String hex) {
		backgroundColor.r(Integer.valueOf(hex.substring(1, 3), 16) / 255f);
		backgroundColor.g(Integer.valueOf(hex.substring(3, 5), 16) / 255f);
		backgroundColor.b(Integer.valueOf(hex.substring(5, 7), 16) / 255f);
		backgroundColor.a(Integer.valueOf(hex.substring(7, 9), 16) / 255f);
	}

	@Override
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public void setAlwaysOnTop(boolean alwaysOnTop) {
		this.alwaysOnTop = alwaysOnTop;
	}

	@Override
	public void setAsBackground(boolean background) {
		this.background = background;
	}

	@Override
	public void setBlurBehind(boolean blur) {
		blurBehind = blur;
	}

	@Override
	public void setMinWidth(int width) {
		this.minW = width;
	}

	@Override
	public void setMinHeight(int height) {
		this.minH = height;
	}

	@Override
	public void toggleTitleBar() {
		this.titleBar.setEnabled(!this.titleBar.isEnabled());
	}

	@Override
	public void toggleMaximize() {
		if (resizable) {
			maximized = !maximized;
			if (maximized) {
				int height = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height"));
				oldX = this.x;
				oldY = this.y;
				oldW = this.w;
				oldH = this.h;
				this.x = 0;
				this.y = height - (float) REGISTRY
						.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"));
				this.w = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width"));
				this.h = height
						- (float) REGISTRY
								.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"))
						- (float) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/shellHeight"));
			} else {
				this.x = oldX;
				this.y = oldY;
				this.w = oldW;
				this.h = oldH;
			}
		}
	}

	@Override
	public void extendFrame(float t, float b, float r, float l) {
		this.ft = t;
		this.fb = b;
		this.fr = r;
		this.fl = l;
	}

	@Override
	public BackgroundStyle getBackgroundStyle() {
		return backgroundStyle;
	}

	@Override
	public float getWidth() {
		return w;
	}

	@Override
	public float getHeight() {
		return h;
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public Thread getThread() {
		return thread;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public NVGLUFramebuffer getFBO() {
		return fbo;
	}

	@Override
	public void toggleMinimize() {
		minimized = !minimized;
	}

	@Override
	public boolean hasDecorations() {
		return decorations;
	}

	@Override
	public boolean isResizable() {
		return resizable;
	}

	@Override
	public boolean isDraggable() {
		return draggable;
	}

	@Override
	public boolean hasBlurBehind() {
		return blurBehind;
	}

	@Override
	public boolean shouldClose() {
		return exit;
	}

	@Override
	public boolean isBackground() {
		return background;
	}

	@Override
	public boolean isAlwaysOnTop() {
		return alwaysOnTop;
	}

	@Override
	public boolean isDragging() {
		if (minimized || hidden)
			return false;
		else
			return titleBar.isDragging();
	}

	@Override
	public boolean isResizing() {
		if (minimized || hidden)
			return false;
		else
			return resizingRight || resizingRightBottom || resizingBottom || resizingTop || resizingLeft;
	}

	@Override
	public boolean isMinimized() {
		return minimized;
	}

	@Override
	public boolean isHidden() {
		return hidden;
	}

	@Override
	public boolean isMaximized() {
		return maximized;
	}

	@Override
	public ITitleBar getTitleBar() {
		return titleBar;
	}

	@Override
	public void closeWindow() {
		switch (windowClose) {
		case DISPOSE:
			exit = true;
			break;
		case DO_NOTHING:
			break;
		case HIDE:
			hidden = true;
			break;
		}
	}

	public float getDelta() {
		double time = WindowManager.getTime();
		float delta = (float) (time - this.lastLoopTime);
		this.lastLoopTime = time;
		return delta;
	}

}
