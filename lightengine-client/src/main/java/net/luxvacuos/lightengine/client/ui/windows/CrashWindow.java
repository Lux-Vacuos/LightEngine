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

package net.luxvacuos.lightengine.client.ui.windows;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;

import org.lwjgl.glfw.GLFW;

import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.WindowMessage;
import net.luxvacuos.lightengine.client.ui.Alignment;
import net.luxvacuos.lightengine.client.ui.Box;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;
import net.luxvacuos.lightengine.client.ui.Text;
import net.luxvacuos.lightengine.client.ui.TextArea;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.util.registry.Key;
import net.luxvacuos.lightengine.universal.util.registry.KeyCache;

public class CrashWindow extends ComponentWindow {

	private Throwable t;

	public CrashWindow(Throwable t) {
		super(0, (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")), "BSOD");
		this.t = t;
	}

	@Override
	public void initApp() {
		super.setDecorations(false);
		super.setAlwaysOnTop(true);
		super.toggleTitleBar();
		super.setBackgroundColor(0, 0.5f, 1, 1);
		super.setBlurBehind(false);

		window.getResourceLoader().loadNVGFont("Px437_IBM_VGA8", "Px437_IBM_VGA8");

		Box titleB = new Box(0, -40, 180, 25);
		titleB.setAlignment(Alignment.CENTER);
		titleB.setWindowAlignment(Alignment.TOP);

		Text title = new Text("Light Engine", 0, -40);
		title.setAlign(NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
		title.setFont("Px437_IBM_VGA8");
		title.setColor(0, 0.5f, 1, 1);
		title.setWindowAlignment(Alignment.TOP);

		TextArea text = new TextArea(
				"An error has ocurred and unfortunately Light Engine is unable to recover from it and continue. Some information might have been lost. \n\nPress \"Enter\" to exit.",
				-400, -100, 800);
		text.setAlign(NVG_ALIGN_MIDDLE);
		text.setFont("Px437_IBM_VGA8");
		text.setWindowAlignment(Alignment.TOP);

		Text error = new Text("Kernel Panic - " + t.getLocalizedMessage(), -400, -200);
		error.setAlign(NVG_ALIGN_MIDDLE);
		error.setFont("Px437_IBM_VGA8");
		error.setWindowAlignment(Alignment.TOP);

		TextArea errorMessage = new TextArea(stackTraceToString(t), -w / 2 + 40, -220, w - 80);
		errorMessage.setFont("Px437_IBM_VGA8");
		errorMessage.setFontSize(20);
		errorMessage.setWindowAlignment(Alignment.TOP);

		super.addComponent(titleB);
		super.addComponent(title);
		super.addComponent(text);
		super.addComponent(error);
		super.addComponent(errorMessage);
		super.initApp();
	}
	
	@Override
	public void updateApp(float delta) {
		super.updateApp(delta);
		KeyboardHandler kb = window.getKeyboardHandler();
		if(kb.isKeyPressed(GLFW.GLFW_KEY_ENTER))
			StateMachine.stop();
	}

	@Override
	public void processWindowMessage(int message, Object param) {
		if (message == WindowMessage.WM_RESIZE) {
			y = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/height"));
			w = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/width"));
			h = (int) REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/Display/height"));
		}
		super.processWindowMessage(message, param);
	}

	public String stackTraceToString(Throwable e) {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement element : e.getStackTrace()) {
			sb.append(element.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

}
