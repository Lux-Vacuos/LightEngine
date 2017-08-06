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
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgScissor;
import static org.lwjgl.nanovg.NanoVGGL3.nvgluBindFramebuffer;
import static org.lwjgl.nanovg.NanoVGGL3.nvgluCreateFramebuffer;
import static org.lwjgl.nanovg.NanoVGGL3.nvgluDeleteFramebuffer;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGLUFramebuffer;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
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
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public abstract class NanoWindow implements IWindow {

	private static final float MAX_ANIM_SPEED = 4000;
	private static final float MIN_ANIM_SPEED = 4000;

	private boolean draggable = true, decorations = true, resizable = true, maximized, hidden, exit, toggleExit,
			alwaysOnTop, background, blurBehind = true, minimized, closeButton = true, fadeOut, fadeIn, maxFadeIn,
			maxFadeOut;
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
	private float reY, reX;
	private float oldMinY;

	private Queue<WindowMessage> messageQueue = new ConcurrentLinkedQueue<>();

	private boolean compositor = true;
	protected Window window;

	public NanoWindow(float x, float y, float w, float h, String title) {
		this.x = x;
		this.y = y;
		oldMinY = y;
		this.w = w;
		this.h = h;
		this.title = title;
		titleBar = new TitleBar(this);
	}

	@Override
	public void init(Window wind) {
		this.window = wind;
		titleBar.init(window);
		initApp();
		compositor = (boolean) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/compositor"));
		if (compositor)
			fbo = nvgluCreateFramebuffer(wind.getNVGID(), (int) (wind.getWidth() * wind.getPixelRatio()),
					(int) (wind.getHeight() * wind.getPixelRatio()), 0);
		titleBar.getLeft().setLayout(new FlowLayout(Direction.RIGHT, 1, 0));
		titleBar.getRight().setLayout(new FlowLayout(Direction.LEFT, 1, 0));
		TitleBarButton closeBtn = new TitleBarButton(0, 0, 28, 28);
		closeBtn.setOnButtonPress(() -> {
			notifyWindow(WindowMessage.WM_CLOSE, windowClose);
		});
		closeBtn.setWindowAlignment(Alignment.RIGHT_TOP);
		closeBtn.setAlignment(Alignment.LEFT_BOTTOM);
		closeBtn.setStyle(ButtonStyle.CLOSE);

		TitleBarButton maximizeBtn = new TitleBarButton(0, 0, 28, 28);
		maximizeBtn.setOnButtonPress(() -> {
			toggleMaximize();
		});
		maximizeBtn.setWindowAlignment(Alignment.RIGHT_TOP);
		maximizeBtn.setAlignment(Alignment.LEFT_BOTTOM);
		maximizeBtn.setStyle(ButtonStyle.MAXIMIZE);

		TitleBarButton minimizeBtn = new TitleBarButton(0, 0, 28, 28);
		minimizeBtn.setOnButtonPress(() -> {
			toggleMinimize();
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
				this.x += window.getMouseHandler().getDX();
				this.y += window.getMouseHandler().getDY();
			}
		});
		if (decorations) {
			fadeIn = true;
			y = -(float) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"));
		}
	}

	@Override
	public void render(IWindowManager nanoWindowManager) {
		if (!hidden && !minimized) {
			if (compositor) {
				nvgluBindFramebuffer(window.getNVGID(), fbo);
				Renderer.clearBuffer(GL_COLOR_BUFFER_BIT);
				Renderer.clearColors(0, 0, 0, 0);
				window.beingNVGFrame();
			}
			nvgSave(window.getNVGID());
			Theme.renderWindow(window.getNVGID(), x, window.getHeight() - y, w, h, backgroundStyle, backgroundColor,
					decorations, titleBar.isEnabled(), maximized, ft, fb, fr, fl);
			if (decorations)
				titleBar.render();
			nvgScissor(window.getNVGID(), x, window.getHeight() - y, w, h);
			renderApp();
			nvgRestore(window.getNVGID());
			if (compositor) {
				window.endNVGFrame();
				nvgluBindFramebuffer(window.getNVGID(), null);
			}
		}
	}

	@Override
	public void update(float delta, IWindowManager nanoWindowManager) {
		if (decorations && !hidden && !minimized && !fadeIn && !fadeOut && !maxFadeIn && !maxFadeOut) {
			if (!isResizing() && !minimized)
				titleBar.update(delta, window);
			float borderSize = (float) REGISTRY
					.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/borderSize"));
			borderSize = Maths.clamp(borderSize, 8);
			float tileBarHeight = (float) REGISTRY
					.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"));
			MouseHandler mh = window.getMouseHandler();
			if ((mh.isButtonPressed(0) && canResizeRightBottom(borderSize, mh) && !isResizing())
					|| resizingRightBottom) {
				resizingRightBottom = mh.isButtonPressed(0);
				w = Maths.clamp(mh.getX() - x, minW);
				h = Maths.clamp(-mh.getY() + y, minH);
			}
			if ((mh.isButtonPressed(0) && canResizeRight(borderSize, mh) && !isResizing()) || resizingRight) {
				resizingRight = mh.isButtonPressed(0);
				w = Maths.clamp(mh.getX() - x, minW);
			}
			if ((mh.isButtonPressed(0) && canResizeLeft(borderSize, mh) && !isResizing()) || resizingLeft) {
				if (!resizingLeft)
					reX = x + w - minW;
				resizingLeft = mh.isButtonPressed(0);
				x = Maths.min(mh.getX(), reX);
				if (mh.getX() <= x)
					w = Maths.clamp(w - mh.getDX(), minW);
			}
			if ((mh.isButtonPressed(0) && canResizeBottom(borderSize, mh) && !isResizing()) || resizingBottom) {
				resizingBottom = mh.isButtonPressed(0);
				h = Maths.clamp(-mh.getY() + y, minH);
			}
			if ((mh.isButtonPressed(0) && canResizeTop(borderSize, mh) && !isResizing()) || resizingTop) {
				if (!resizingTop)
					reY = y - h + minH;
				resizingTop = mh.isButtonPressed(0);
				// h = Maths.clamp(-mh.getY() + y, minH);
				if (titleBar.isEnabled()) {
					y = Maths.clamp(mh.getY() - tileBarHeight, reY);
					if (mh.getY() >= y + tileBarHeight)
						h = Maths.clamp(h + mh.getDY(), minH);
				} else {
					y = Maths.clamp(mh.getY(), reY);
					if (mh.getY() >= y)
						h = Maths.clamp(h + mh.getDY(), minH);
				}
			}
		}
		if (!isResizing() && !minimized && !titleBar.isDragging() && !fadeIn && !fadeOut && !maxFadeIn && !maxFadeOut)
			updateApp(delta);
	}

	@Override
	public void alwaysUpdate(float delta, IWindowManager nanoWindowManager) {
		if (fadeOut) {
			y -= MIN_ANIM_SPEED * delta;
			if (y < -(float) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"))) {
				fadeOut = false;
				minimized = true;
				if (toggleExit)
					exit = true;
			}
		}
		if (fadeIn) {
			minimized = false;
			y += MIN_ANIM_SPEED * delta;
			if (y >= oldMinY) {
				y = oldMinY;
				fadeIn = false;
				if (maximized) {
					int height = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height"));
					this.x = 0;
					this.y = height - (float) REGISTRY
							.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"));
					this.w = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width"));
					this.h = height
							- (float) REGISTRY
									.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"))
							- (float) REGISTRY
									.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/shellHeight"));
				}
			}
		}
		if (maxFadeIn) {
			int height = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height"));
			int width = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width"));
			float titlebarHeight = (float) REGISTRY
					.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"));
			float shellHeight = (float) REGISTRY
					.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/shellHeight"));
			boolean ready = true;
			x -= MAX_ANIM_SPEED * delta;
			if (x <= 0)
				x = 0;
			else
				ready = false;

			y += MAX_ANIM_SPEED * delta;
			if (y >= height - titlebarHeight)
				y = height - titlebarHeight;
			else
				ready = false;

			w += MAX_ANIM_SPEED * delta;
			if (w >= width)
				w = width;
			else
				ready = false;
			h += MAX_ANIM_SPEED * delta;
			if (h >= height - titlebarHeight - shellHeight)
				h = height - titlebarHeight - shellHeight;
			else
				ready = false;
			if (ready) {
				maxFadeIn = false;
				maximized = true;
			}
		}
		if (maxFadeOut) {
			maximized = false;
			boolean ready = true;
			x += MAX_ANIM_SPEED * delta;
			if (x >= oldX)
				x = oldX;
			else
				ready = false;

			y -= MAX_ANIM_SPEED * delta;
			if (y <= oldY)
				y = oldY;
			else
				ready = false;

			w -= MAX_ANIM_SPEED * delta;
			if (w <= oldW)
				w = oldW;
			else
				ready = false;
			h -= MAX_ANIM_SPEED * delta;
			if (h <= oldH)
				h = oldH;
			else
				ready = false;
			if (ready) {
				maxFadeOut = false;
			}
		}
		titleBar.alwaysUpdate(delta, window);
		while (!messageQueue.isEmpty()) {
			WindowMessage ms = messageQueue.poll();
			processWindowMessage(ms.message, ms.param);
		}
		alwaysUpdateApp(delta);
	}

	@Override
	public void dispose() {
		if (compositor)
			nvgluDeleteFramebuffer(window.getNVGID(), fbo);
		disposeApp();
	}

	@Override
	public void notifyWindow(int message, Object param) {
		messageQueue.add(new WindowMessage(message, param));
	}

	@Override
	public void processWindowMessage(int message, Object param) {
		switch (message) {
		case WindowMessage.WM_CLOSE:
			WindowClose wc = (WindowClose) param;
			switch (wc) {
			case DISPOSE:
				if (decorations) {
					fadeOut = true;
					toggleExit = true;
				} else {
					exit = true;
				}
				break;
			case DO_NOTHING:
				break;
			}
			break;
		case WindowMessage.WM_MAXIMIZE:
			if (resizable && !maximized) {
				maxFadeIn = true;
				maxFadeOut = false;
				oldX = this.x;
				oldY = this.y;
				oldW = this.w;
				oldH = this.h;
			}
			break;
		case WindowMessage.WM_RESTORE:
			if (resizable && maximized && !minimized) {
				maxFadeIn = false;
				maxFadeOut = true;
			}
			if (minimized) {
				fadeIn = true;
				fadeOut = false;
			}
			break;
		case WindowMessage.WM_MINIMIZE:
			if (!minimized) {
				fadeOut = true;
				if (!fadeIn)
					oldMinY = y;
				fadeIn = false;
			}
			break;
		case WindowMessage.WM_COMPOSITOR_DISABLED:
			compositor = false;
			TaskManager.addTask(() -> {
				nvgluDeleteFramebuffer(window.getNVGID(), fbo);
				fbo = null;
			});
			break;
		case WindowMessage.WM_COMPOSITOR_ENABLED:
			compositor = true;
			TaskManager.addTask(() -> fbo = nvgluCreateFramebuffer(window.getNVGID(),
					(int) (window.getWidth() * window.getPixelRatio()),
					(int) (window.getHeight() * window.getPixelRatio()), 0));
			break;
		}
	}

	private boolean canResizeBottom(float borderSize, MouseHandler mh) {
		return mh.getX() > x && mh.getY() < y - h && mh.getX() < x + w && mh.getY() > y - h - borderSize && resizable
				&& !maximized && !minimized;
	}

	private boolean canResizeTop(float borderSize, MouseHandler mh) {
		if (titleBar.isEnabled())
			return mh.getX() > x
					&& mh.getY() < y + (float) REGISTRY.getRegistryItem(
							new Key("/Light Engine/Settings/WindowManager/titleBarHeight")) + borderSize
					&& mh.getX() < x + w
					&& mh.getY() > y + (float) REGISTRY
							.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"))
					&& resizable && !maximized && !minimized;
		else
			return mh.getX() > x && mh.getY() < y + borderSize && mh.getX() < x + w && mh.getY() > y && resizable
					&& !maximized && !minimized;
	}

	private boolean canResizeRightBottom(float borderSize, MouseHandler mh) {
		return mh.getX() > x + w && mh.getY() < y - h && mh.getX() < x + w + borderSize
				&& mh.getY() > y - h - borderSize && resizable && !maximized && !minimized;
	}

	private boolean canResizeRight(float borderSize, MouseHandler mh) {
		return mh.getX() > x + w && mh.getY() < y && mh.getX() < x + w + borderSize && mh.getY() > y - h && resizable
				&& !maximized && !minimized;
	}

	private boolean canResizeLeft(float borderSize, MouseHandler mh) {
		return mh.getX() > x - borderSize && mh.getY() < y && mh.getX() < x + borderSize && mh.getY() > y - h
				&& resizable && !maximized && !minimized;
	}

	@Override
	public boolean insideWindow() {
		float borderSize = (float) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/borderSize"));
		MouseHandler mh = GraphicalSubsystem.getMainWindow().getMouseHandler();
		borderSize = Maths.clamp(borderSize, 8);
		if (titleBar.isEnabled() && decorations)
			return mh.getX() > x - borderSize && mh.getX() < x + w + borderSize && mh.getY() > y - h - borderSize
					&& mh.getY() < y
							+ (float) REGISTRY
									.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"))
							+ borderSize;
		else if (!decorations)
			return mh.getX() > x && mh.getX() < x + w && mh.getY() > y - h && mh.getY() < y;
		else
			return mh.getX() > x - borderSize && mh.getX() < x + w + borderSize * 2f
					&& mh.getY() > y - h - borderSize * 2f && mh.getY() < y + borderSize;
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
		if (maximized)
			this.notifyWindow(WindowMessage.WM_RESTORE, null);
		else
			this.notifyWindow(WindowMessage.WM_MAXIMIZE, null);
	}

	@Override
	public void extendFrame(float t, float b, float r, float l) {
		this.ft = t;
		this.fb = b;
		this.fr = r;
		this.fl = l;
	}

	@Override
	public void closeWindow() {
		notifyWindow(WindowMessage.WM_CLOSE, windowClose);
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
	public String getTitle() {
		return title;
	}

	@Override
	public NVGLUFramebuffer getFBO() {
		return fbo;
	}

	@Override
	public void toggleMinimize() {
		if (minimized)
			this.notifyWindow(WindowMessage.WM_RESTORE, null);
		else
			this.notifyWindow(WindowMessage.WM_MINIMIZE, null);
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
	public void reloadFBO(Window window) {
		if (fbo != null || compositor) {
			nvgluDeleteFramebuffer(window.getNVGID(), fbo);
			fbo = nvgluCreateFramebuffer(window.getNVGID(), (int) (window.getWidth() * window.getPixelRatio()),
					(int) (window.getHeight() * window.getPixelRatio()), 0);
		}
	}

	@Override
	public void onMainResize() {
	}

}
