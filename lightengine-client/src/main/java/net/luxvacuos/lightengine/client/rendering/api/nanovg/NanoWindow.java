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
import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgScissor;
import static org.lwjgl.nanovg.NanoVGGL3.nvgluBindFramebuffer;
import static org.lwjgl.nanovg.NanoVGGL3.nvgluCreateFramebuffer;
import static org.lwjgl.nanovg.NanoVGGL3.nvgluDeleteFramebuffer;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glViewport;

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
import net.luxvacuos.lightengine.universal.util.registry.KeyCache;

public abstract class NanoWindow implements IWindow {

	private boolean draggable = true, decorations = true, resizable = true, maximized, hidden, exit, alwaysOnTop,
			background, blurBehind = true, minimized, closeButton = true, afterResize, exiting, transparentInput;
	private boolean resizingRight, resizingRightBottom, resizingBottom, resizingTop, resizingLeft;
	private int ft, fb, fr, fl;
	private BackgroundStyle backgroundStyle = BackgroundStyle.SOLID;
	private NVGColor backgroundColor = Theme.rgba(0, 0, 0, 255);
	protected int x, y, w, h, minW = 300, minH = 300;
	protected int lfx, lfy, fx, fy, fh, fw;
	private int oldX, oldY, oldW, oldH;
	private WindowClose windowClose = WindowClose.DISPOSE;
	private ITitleBar titleBar;
	private NVGLUFramebuffer fbo;
	private String title;
	private int reY, reX;
	private AnimationState animationState = AnimationState.NONE;

	private Queue<WindowMessage> messageQueue = new ConcurrentLinkedQueue<>();

	protected boolean compositor = true;
	protected Window window;

	public NanoWindow(int x, int y, int w, int h, String title) {
		this.x = x;
		this.y = y;
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
		compositor = (boolean) REGISTRY
				.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/compositor"));
		if (compositor) {
			updateRenderSize();
			fbo = nvgluCreateFramebuffer(window.getNVGID(), fw, fh, 0);
		}
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
		if (decorations && compositor)
			animationState = AnimationState.OPEN;
	}

	@Override
	public void render(IWindowManager nanoWindowManager) {
		if (!hidden && !minimized) {
			if (compositor) {
				nvgluBindFramebuffer(window.getNVGID(), fbo);
				glViewport(0, 0, fw, fh);
				Renderer.clearColors(0, 0, 0, 0);
				Renderer.clearBuffer(GL_COLOR_BUFFER_BIT);
				nvgBeginFrame(window.getNVGID(), fw, fh, 1);
				nvgSave(window.getNVGID());
				Theme.renderWindow(window.getNVGID(), lfx, lfy, w, h, backgroundStyle, backgroundColor, decorations,
						titleBar.isEnabled(), maximized, ft, fb, fr, fl);
				if (decorations)
					titleBar.render(window);
				nvgScissor(window.getNVGID(), lfx, lfy, w, h);
				renderApp();
				nvgRestore(window.getNVGID());
				nvgEndFrame(window.getNVGID());
				nvgluBindFramebuffer(window.getNVGID(), null);
				window.resetViewport();
			} else {
				nvgSave(window.getNVGID());
				Theme.renderWindow(window.getNVGID(), x, window.getHeight() - y, w, h, backgroundStyle, backgroundColor,
						decorations, titleBar.isEnabled(), maximized, ft, fb, fr, fl);
				if (decorations)
					titleBar.render(window);
				nvgScissor(window.getNVGID(), x, window.getHeight() - y, w, h);
				renderApp();
				nvgRestore(window.getNVGID());
			}
		}
	}

	@Override
	public void update(float delta, IWindowManager nanoWindowManager) {
		if (decorations && !hidden && !minimized) {
			if (!isResizing() && !minimized)
				titleBar.update(delta, window);
			int borderSize = (int) REGISTRY
					.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/borderSize"));
			borderSize = Maths.clampInt(borderSize, 8);
			int tileBarHeight = (int) REGISTRY
					.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/titleBarHeight"));
			MouseHandler mh = window.getMouseHandler();
			if ((mh.isButtonPressed(0) && canResizeRightBottom(borderSize, mh) && !isResizing())
					|| resizingRightBottom) {
				resizingRightBottom = mh.isButtonPressed(0);
				w = Maths.clampInt(mh.getXI() - x, minW);
				h = Maths.clampInt(-mh.getYI() + y, minH);
			}
			if ((mh.isButtonPressed(0) && canResizeRight(borderSize, mh) && !isResizing()) || resizingRight) {
				resizingRight = mh.isButtonPressed(0);
				w = Maths.clampInt(mh.getXI() - x, minW);
			}
			if ((mh.isButtonPressed(0) && canResizeLeft(borderSize, mh) && !isResizing()) || resizingLeft) {
				if (!resizingLeft)
					reX = x + w - minW;
				resizingLeft = mh.isButtonPressed(0);
				x = Maths.minInt(mh.getXI(), reX);
				if (mh.getX() <= x)
					w = Maths.clampInt(w - (int) mh.getDX(), minW);
			}
			if ((mh.isButtonPressed(0) && canResizeBottom(borderSize, mh) && !isResizing()) || resizingBottom) {
				resizingBottom = mh.isButtonPressed(0);
				h = Maths.clampInt(-mh.getYI() + y, minH);
			}
			if ((mh.isButtonPressed(0) && canResizeTop(borderSize, mh) && !isResizing()) || resizingTop) {
				if (!resizingTop)
					reY = y - h + minH;
				resizingTop = mh.isButtonPressed(0);
				// h = Maths.clampInt(-mh.getY() + y, minH);
				if (titleBar.isEnabled()) {
					y = Maths.clampInt(mh.getYI() - tileBarHeight, reY);
					if (mh.getY() >= y + tileBarHeight)
						h = Maths.clampInt(h + (int) mh.getDY(), minH);
				} else {
					y = Maths.clampInt(mh.getYI(), reY);
					if (mh.getY() >= y)
						h = Maths.clampInt(h + (int) mh.getDY(), minH);
				}
			}
		}
		if (isResizing() || afterResize) {
			if (!isResizing() && afterResize)
				notifyWindow(WindowMessage.WM_COMPOSITOR_RELOAD, null);
			afterResize = isResizing();
		}
		if (!isResizing() && !minimized && !titleBar.isDragging() && !isAnimating())
			updateApp(delta);
	}

	@Override
	public void alwaysUpdate(float delta, IWindowManager nanoWindowManager) {
		updateRenderSize();
		titleBar.alwaysUpdate(delta, window);
		if (compositor) {
			if (animationState.equals(AnimationState.AFTER_CLOSE) && exiting)
				exit = true;
			if (animationState.equals(AnimationState.AFTER_MINIMIZE) && !minimized)
				minimized = true;
		}

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
				if (decorations && compositor) {
					animationState = AnimationState.CLOSE;
					exiting = true;
				} else
					exit = true;
				break;
			case DO_NOTHING:
				break;
			}
			break;
		case WindowMessage.WM_MAXIMIZE:
			if (resizable && !maximized) {
				maximized = true;
				int height = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/height"));
				oldX = this.x;
				oldY = this.y;
				oldW = this.w;
				oldH = this.h;
				this.x = 0;
				this.y = height - (int) REGISTRY
						.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/titleBarHeight"));
				this.w = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/width"));
				this.h = height
						- (int) REGISTRY
								.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/titleBarHeight"))
						- (int) REGISTRY
								.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/shellHeight"));
				updateRenderSize();
				if (compositor)
					TaskManager.addTask(() -> {
						nvgluDeleteFramebuffer(window.getNVGID(), fbo);
						fbo = nvgluCreateFramebuffer(window.getNVGID(), fw, fh, 0);
					});
			}
			break;
		case WindowMessage.WM_RESTORE:
			if (resizable && maximized && !minimized) {
				maximized = false;
				this.x = oldX;
				this.y = oldY;
				this.w = oldW;
				this.h = oldH;
				updateRenderSize();
				if (compositor)
					TaskManager.addTask(() -> {
						nvgluDeleteFramebuffer(window.getNVGID(), fbo);
						fbo = nvgluCreateFramebuffer(window.getNVGID(), fw, fh, 0);
					});
			}
			if (minimized) {
				if (compositor) {
					animationState = AnimationState.RESTORE_MINIMIZE;
				}
				minimized = false;
			}
			break;
		case WindowMessage.WM_MINIMIZE:
			if (!minimized) {
				if (compositor) {
					animationState = AnimationState.MINIMIZE;
				} else
					minimized = true;
			}
			break;
		case WindowMessage.WM_RESIZE:
			updateRenderSize();
			if (maximized) {
				int height = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/height"));
				this.x = 0;
				this.y = height - (int) REGISTRY
						.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/titleBarHeight"));
				this.w = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/width"));
				this.h = height
						- (int) REGISTRY
								.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/titleBarHeight"))
						- (int) REGISTRY
								.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/shellHeight"));
				if (compositor)
					TaskManager.addTask(() -> {
						nvgluDeleteFramebuffer(window.getNVGID(), fbo);
						fbo = nvgluCreateFramebuffer(window.getNVGID(), fw, fh, 0);
					});
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
			TaskManager.addTask(() -> fbo = nvgluCreateFramebuffer(window.getNVGID(), fw, fh, 0));
			break;
		case WindowMessage.WM_COMPOSITOR_RELOAD:
			if (compositor) {
				TaskManager.addTask(() -> {
					nvgluDeleteFramebuffer(window.getNVGID(), fbo);
					fbo = nvgluCreateFramebuffer(window.getNVGID(), fw, fh, 0);
				});
			}
			break;
		}
	}

	private boolean canResizeBottom(int borderSize, MouseHandler mh) {
		return mh.getX() > x && mh.getY() < y - h && mh.getX() < x + w && mh.getY() > y - h - borderSize && resizable
				&& !maximized && !minimized;
	}

	private boolean canResizeTop(int borderSize, MouseHandler mh) {
		if (titleBar.isEnabled())
			return mh.getX() > x
					&& mh.getY() < y + (int) REGISTRY.getRegistryItem(
							KeyCache.getKey("/Light Engine/Settings/WindowManager/titleBarHeight")) + borderSize
					&& mh.getX() < x + w
					&& mh.getY() > y + (int) REGISTRY
							.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/titleBarHeight"))
					&& resizable && !maximized && !minimized;
		else
			return mh.getX() > x && mh.getY() < y + borderSize && mh.getX() < x + w && mh.getY() > y && resizable
					&& !maximized && !minimized;
	}

	private boolean canResizeRightBottom(int borderSize, MouseHandler mh) {
		return mh.getX() > x + w && mh.getY() < y - h && mh.getX() < x + w + borderSize
				&& mh.getY() > y - h - borderSize && resizable && !maximized && !minimized;
	}

	private boolean canResizeRight(int borderSize, MouseHandler mh) {
		return mh.getX() > x + w && mh.getY() < y && mh.getX() < x + w + borderSize && mh.getY() > y - h && resizable
				&& !maximized && !minimized;
	}

	private boolean canResizeLeft(int borderSize, MouseHandler mh) {
		return mh.getX() > x - borderSize && mh.getY() < y && mh.getX() < x + borderSize && mh.getY() > y - h
				&& resizable && !maximized && !minimized;
	}

	@Override
	public boolean insideWindow() {
		if (transparentInput)
			return false;
		int borderSize = (int) REGISTRY
				.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/borderSize"));
		MouseHandler mh = GraphicalSubsystem.getMainWindow().getMouseHandler();
		borderSize = Maths.clampInt(borderSize, 8);
		if (titleBar.isEnabled() && decorations)
			return mh.getX() > x - borderSize && mh.getX() < x + w + borderSize && mh.getY() > y - h - borderSize
					&& mh.getY() < y
							+ (int) REGISTRY.getRegistryItem(
									KeyCache.getKey("/Light Engine/Settings/WindowManager/titleBarHeight"))
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
	public void extendFrame(int t, int b, int r, int l) {
		this.ft = t;
		this.fb = b;
		this.fr = r;
		this.fl = l;
	}

	@Override
	public void setAnimationState(AnimationState animationState) {
		this.animationState = animationState;
	}

	@Override
	public void setTransparentInput(boolean transparentInput) {
		this.transparentInput = transparentInput;
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
	public int getWidth() {
		return w;
	}

	@Override
	public int getHeight() {
		return h;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	public void updateRenderSize() {
		int borderSize = (int) REGISTRY
				.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/borderSize"));
		int titleBarHeight = (int) REGISTRY
				.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/titleBarHeight"));
		boolean titleBarBorder = (boolean) REGISTRY
				.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/titleBarBorder"));
		if (titleBar.isEnabled() && decorations && !maximized)
			if (titleBarBorder) {
				lfx = borderSize;
				lfy = titleBarHeight + borderSize;
				fx = x - borderSize;
				fy = y + titleBarHeight + borderSize;
				fw = (int) (w + borderSize * 2f);
				fh = (int) (h + titleBarHeight + borderSize * 2f);
			} else {
				lfx = borderSize;
				lfy = titleBarHeight;
				fx = x - borderSize;
				fy = y + titleBarHeight;
				fw = (int) (w + borderSize * 2f);
				fh = h + titleBarHeight + borderSize;
			}
		else if (!decorations) {
			lfx = 0;
			lfy = 0;
			fx = x;
			fy = y;
			fw = w;
			fh = h;
		} else if (maximized) {
			lfx = 0;
			lfy = titleBarHeight;
			fx = x;
			fy = y + titleBarHeight;
			fw = w;
			fh = h + titleBarHeight;
		} else {
			lfx = borderSize;
			lfy = borderSize;
			fx = x + borderSize;
			fy = y + borderSize;
			fw = (int) (w + borderSize * 2f);
			fh = (int) (h + borderSize * 2f);
		}
	}

	@Override
	public int getFX() {
		return fx;
	}

	@Override
	public int getFY() {
		return fy;
	}

	@Override
	public int getFH() {
		return fh;
	}

	@Override
	public int getFW() {
		return fw;
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
	public AnimationState getAnimationState() {
		return animationState;
	}

	@Override
	public boolean hasTransparentInput() {
		return transparentInput;
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
	public boolean isCompositor() {
		return compositor;
	}

	@Override
	public boolean isAnimating() {
		return !animationState.equals(AnimationState.NONE);
	}

	@Override
	public ITitleBar getTitleBar() {
		return titleBar;
	}

}
