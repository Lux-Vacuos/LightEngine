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

package net.luxvacuos.lightengine.universal.core.subsystems;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.universal.core.exception.CompileGroovyException;

public class ScriptSubsystem implements ISubsystem {

	private static ScriptEngineManager scriptEngineManager;
	private static ScriptEngine scriptEngine;
	private static Compilable compilableEngine;

	@Override
	public void init() {
		scriptEngineManager = new ScriptEngineManager();
		scriptEngine = scriptEngineManager.getEngineByName("groovy");
		if (scriptEngine == null)
			throw new NullPointerException("Can't setup Groovy engine");
		if (scriptEngine instanceof Compilable) {
			compilableEngine = (Compilable) scriptEngine;
		}
	}

	@Override
	public void restart() {
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void render(float delta) {
	}

	@Override
	public void dispose() {
	}

	public static CompiledScript compile(String file) {
		Logger.log("Compiling: " + file);
		InputStream filet = ScriptSubsystem.class.getClassLoader().getResourceAsStream("assets/" + file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(filet));
		try {
			return compilableEngine.compile(reader);
		} catch (ScriptException e) {
			throw new CompileGroovyException("Unable to compile: " + file, e);
		}
	}

	public static ScriptEngine getScriptEngine() {
		return scriptEngine;
	}
}
