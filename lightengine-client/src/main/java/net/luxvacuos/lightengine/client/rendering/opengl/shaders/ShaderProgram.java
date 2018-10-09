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

package net.luxvacuos.lightengine.client.rendering.opengl.shaders;

import static org.lwjgl.opengl.GL11C.GL_FALSE;
import static org.lwjgl.opengl.GL20C.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20C.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20C.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20C.glAttachShader;
import static org.lwjgl.opengl.GL20C.glBindAttribLocation;
import static org.lwjgl.opengl.GL20C.glCompileShader;
import static org.lwjgl.opengl.GL20C.glCreateProgram;
import static org.lwjgl.opengl.GL20C.glCreateShader;
import static org.lwjgl.opengl.GL20C.glDeleteProgram;
import static org.lwjgl.opengl.GL20C.glDeleteShader;
import static org.lwjgl.opengl.GL20C.glDetachShader;
import static org.lwjgl.opengl.GL20C.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20C.glGetShaderi;
import static org.lwjgl.opengl.GL20C.glLinkProgram;
import static org.lwjgl.opengl.GL20C.glShaderSource;
import static org.lwjgl.opengl.GL20C.glUseProgram;
import static org.lwjgl.opengl.GL20C.glValidateProgram;
import static org.lwjgl.opengl.GL32C.GL_GEOMETRY_SHADER;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.client.core.exception.CompileShaderException;
import net.luxvacuos.lightengine.client.core.exception.LoadShaderException;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.Attribute;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.data.IUniform;
import net.luxvacuos.lightengine.client.resources.ShaderIncludes;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public abstract class ShaderProgram implements IDisposable {
	private int programID;
	private boolean loaded;
	private List<IUniform> uniforms = new ArrayList<>();

	private static boolean bound = false;

	public ShaderProgram(String vertexFile, String fragmentFile, Attribute... inVariables) {
		int vertexShaderID = loadShader(vertexFile, GL_VERTEX_SHADER);
		int fragmentShaderID = loadShader(fragmentFile, GL_FRAGMENT_SHADER);
		programID = glCreateProgram();
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, fragmentShaderID);
		bindAttributes(inVariables);
		glLinkProgram(programID);
		glDetachShader(programID, vertexShaderID);
		glDetachShader(programID, fragmentShaderID);
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);
		loaded = true;
	}

	public ShaderProgram(String vertexFile, String fragmentFile, String geometryFile, Attribute... inVariables) {
		int vertexShaderID = loadShader(vertexFile, GL_VERTEX_SHADER);
		int geometryShaderID = loadShader(geometryFile, GL_GEOMETRY_SHADER);
		int fragmentShaderID = loadShader(fragmentFile, GL_FRAGMENT_SHADER);
		programID = glCreateProgram();
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, geometryShaderID);
		glAttachShader(programID, fragmentShaderID);
		bindAttributes(inVariables);
		glLinkProgram(programID);
		glDetachShader(programID, vertexShaderID);
		glDetachShader(programID, geometryShaderID);
		glDetachShader(programID, fragmentShaderID);
		glDeleteShader(vertexShaderID);
		glDeleteShader(geometryShaderID);
		glDeleteShader(fragmentShaderID);
		loaded = true;
	}

	/**
	 * Loads All Uniforms and validate the program.
	 * 
	 * @param uniforms Array of Uniforms
	 */
	protected void storeAllUniformLocations(IUniform... uniforms) {
		for (IUniform uniform : uniforms) {
			uniform.storeUniformLocation(programID);
		}
		this.uniforms.addAll(Arrays.asList(uniforms));
		glValidateProgram(programID);
	}

	/**
	 * Loads All Uniforms.
	 * 
	 * @param uniforms
	 */
	protected void storeUniformArray(IUniform... uniforms) {
		for (IUniform uniform : uniforms) {
			uniform.storeUniformLocation(programID);
		}
		this.uniforms.addAll(Arrays.asList(uniforms));
	}

	/**
	 * Starts the Shader
	 * 
	 */
	public void start() {
		if (bound)
			throw new RuntimeException("A Shader Program is already bound");
		glUseProgram(programID);
		bound = true;
	}

	/**
	 * Stops the Shader
	 * 
	 */
	public void stop() {
		glUseProgram(0);
		bound = false;
	}

	/**
	 * Clear all loaded data
	 * 
	 */
	@Override
	public void dispose() {
		if (!loaded)
			return;
		stop();
		glDeleteProgram(programID);
		loaded = false;
		for (IUniform uniform : uniforms) {
			uniform.dispose();
		}
		uniforms.clear();
	}

	/**
	 * Bind array of attributes
	 * 
	 * @param inVariables Array
	 */
	private void bindAttributes(Attribute[] att) {
		for (int i = 0; i < att.length; i++) {
			glBindAttribLocation(programID, att[i].getId(), att[i].getName());
		}
	}

	private int loadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();
		InputStream filet = getClass().getClassLoader().getResourceAsStream("assets/shaders/" + file);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(filet));
			Logger.log("Loading Shader: " + file);

			shaderSource.append("#version 330 core").append("//\n");
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#include")) {
					String[] split = line.split(" ");
					String name = split[2];
					if (split[1].equalsIgnoreCase("variable")) {
						shaderSource.append(ShaderIncludes.getVariable(name)).append("//\n");
					} else if (split[1].equalsIgnoreCase("struct")) {
						shaderSource.append(ShaderIncludes.getStruct(name)).append("//\n");
					} else if (split[1].equalsIgnoreCase("function")) {
						shaderSource.append(ShaderIncludes.getFunction(name)).append("//\n");
					}
					continue;
				}
				shaderSource.append(line).append("//\n");
			}
			reader.close();
		} catch (IOException e) {
			throw new LoadShaderException(e);
		}
		int shaderID = glCreateShader(type);
		glShaderSource(shaderID, shaderSource);
		glCompileShader(shaderID);
		if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			Logger.error(glGetShaderInfoLog(shaderID, 500));
			throw new CompileShaderException(glGetShaderInfoLog(shaderID, 500));
		}
		return shaderID;
	}

}