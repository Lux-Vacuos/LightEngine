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

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_TOP;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import net.luxvacuos.lightengine.client.commands.TestCommand;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.ui.Alignment;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;
import net.luxvacuos.lightengine.client.ui.Direction;
import net.luxvacuos.lightengine.client.ui.EditBox;
import net.luxvacuos.lightengine.client.ui.FlowLayout;
import net.luxvacuos.lightengine.client.ui.TextArea;
import net.luxvacuos.lightengine.universal.commands.CommandManager;
import net.luxvacuos.lightengine.universal.commands.ICommandManager;

public class Console extends ComponentWindow {

	private ICommandManager manager;
	private TextArea text;
	private EditBox command;
	private String textBuffer = "";
	private Interceptor inter = new Interceptor();

	public Console(float x, float y, float w, float h) {
		super(x, y, w, h, "Console");
	}

	@Override
	public void initApp(Window window) {
		super.setBackgroundColor("#1F1F1F78");
		super.setLayout(new FlowLayout(Direction.UP, 0, -30));
		manager = new CommandManager(inter);
		manager.registerCommand(new TestCommand());

		text = new TextArea(textBuffer, 0, 0, w);
		text.setWindowAlignment(Alignment.LEFT_BOTTOM);
		text.setAlign( NVG_ALIGN_LEFT | NVG_ALIGN_BOTTOM);
		text.setResizeH(true);
		text.setFontSize(20);

		command = new EditBox(0, 0, 0, 30, "");
		command.setResizeH(true);
		command.setOnEnterFress(() -> {
			textBuffer += command.getText() + "\n";
			manager.command(command.getText());
			textBuffer += inter.getLastText();
		});

		super.addComponent(command);
		super.addComponent(text);
		super.initApp(window);
		textBuffer = "Light Engine Console (WIP) \n";
	}

	@Override
	public void alwaysUpdateApp(float delta, Window window) {
		text.setText(textBuffer);
		super.alwaysUpdateApp(delta, window);
	}

	private class Interceptor extends PrintStream {

		private String lastText = "";

		public Interceptor() {
			super(new ByteArrayOutputStream(), true);
		}

		@Override
		public void println(String x) {
			lastText += x + "\n";
		}

		public String getLastText() {
			String tmp = new String(lastText);
			lastText = "";
			return tmp;
		}
	}

}
