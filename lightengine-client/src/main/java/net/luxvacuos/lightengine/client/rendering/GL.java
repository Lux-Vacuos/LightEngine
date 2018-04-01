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

package net.luxvacuos.lightengine.client.rendering;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengles.GLES20;
import org.lwjgl.opengles.GLES32;

import net.luxvacuos.lightengine.client.rendering.glfw.RenderingAPI;

public class GL {

	private static IGLAPI glAPI;

	public static int GL_FALSE;
	public static int GL_COMPILE_STATUS;
	public static int GL_FRAGMENT_SHADER;
	public static int GL_VERTEX_SHADER;
	public static int GL_GEOMETRY_SHADER;
	public static int GL_COLOR_BUFFER_BIT;
	public static int GL_DEPTH_BUFFER_BIT;

	public static void glClearColor(float red, float green, float blue, float alpha) {
		glAPI.glClearColor(red, green, blue, alpha);
	}

	public static void glClear(int mask) {
		glAPI.glClear(mask);
	}

	public static void glAttachShader(int program, int shader) {
		glAPI.glAttachShader(program, shader);
	}

	public static void glBindAttribLocation(int program, int index, String name) {
		glAPI.glBindAttribLocation(program, index, name);
	}

	public static void glCompileShader(int shader) {
		glAPI.glCompileShader(shader);
	}

	public static int glCreateProgram() {
		return glAPI.glCreateProgram();
	}

	public static int glCreateShader(int type) {
		return glAPI.glCreateShader(type);
	}

	public static void glDeleteProgram(int program) {
		glAPI.glDeleteProgram(program);
	}

	public static void glDeleteShader(int shader) {
		glAPI.glDeleteShader(shader);
	}

	public static void glDetachShader(int program, int shader) {
		glAPI.glDetachShader(program, shader);
	}

	public static String glGetShaderInfoLog(int shader, int maxLength) {
		return glAPI.glGetShaderInfoLog(shader, maxLength);
	}

	public static int glGetShaderi(int shader, int pname) {
		return glAPI.glGetShaderi(shader, pname);
	}

	public static void glLinkProgram(int program) {
		glAPI.glLinkProgram(program);
	}

	public static void glShaderSource(int shader, CharSequence string) {
		glAPI.glShaderSource(shader, string);
	}

	public static void glUseProgram(int program) {
		glAPI.glUseProgram(program);
	}

	public static void glValidateProgram(int program) {
		glAPI.glValidateProgram(program);
	}

	public static int glGetUniformLocation(int program, CharSequence name) {
		return glAPI.glGetUniformLocation(program, name);
	}

	public static void glUniform1i(int location, int v0) {
		glAPI.glUniform1i(location, v0);
	}

	public static void glUniform2i(int location, int v0, int v1) {
		glAPI.glUniform2i(location, v0, v1);
	}

	public static void glUniform3i(int location, int v0, int v1, int v2) {
		glAPI.glUniform3i(location, v0, v1, v2);
	}

	public static void glUniform4i(int location, int v0, int v1, int v2, int v3) {
		glAPI.glUniform4i(location, v0, v1, v2, v3);
	}

	public static void glUniform1f(int location, float v0) {
		glAPI.glUniform1f(location, v0);
	}

	public static void glUniform2f(int location, float v0, float v1) {
		glAPI.glUniform2f(location, v0, v1);
	}

	public static void glUniform3f(int location, float v0, float v1, float v2) {
		glAPI.glUniform3f(location, v0, v1, v2);
	}

	public static void glUniform4f(int location, float v0, float v1, float v2, float v3) {
		glAPI.glUniform4f(location, v0, v1, v2, v3);
	}

	public static void glUniformMatrix4fv(int location, boolean transpose, float[] value) {
		glAPI.glUniformMatrix4fv(location, transpose, value);
	}

	public static void init(RenderingAPI api) {
		GL r = new GL();
		switch (api) {
		case GL:
			glAPI = r.new GLAPI();
			break;
		case GLES:
			glAPI = r.new GLESAPI();
			break;
		default:
			break;
		}
	}

	private interface IGLAPI {

		public void glClearColor(float red, float green, float blue, float alpha);

		public void glClear(int mask);

		public void glAttachShader(int program, int shader);

		public void glBindAttribLocation(int program, int index, String name);

		public void glCompileShader(int shader);

		public int glCreateProgram();

		public int glCreateShader(int type);

		public void glDeleteProgram(int program);

		public void glDeleteShader(int shader);

		public void glDetachShader(int program, int shader);

		public String glGetShaderInfoLog(int shader, int maxLength);

		public int glGetShaderi(int shader, int pname);

		public void glLinkProgram(int program);

		public void glShaderSource(int shader, CharSequence string);

		public void glUseProgram(int program);

		public void glValidateProgram(int program);

		public int glGetUniformLocation(int program, CharSequence name);

		public void glUniform1i(int location, int v0);

		public void glUniform2i(int location, int v0, int v1);

		public void glUniform3i(int location, int v0, int v1, int v2);

		public void glUniform4i(int location, int v0, int v1, int v2, int v3);

		public void glUniform1f(int location, float v0);

		public void glUniform2f(int location, float v0, float v1);

		public void glUniform3f(int location, float v0, float v1, float v2);

		public void glUniform4f(int location, float v0, float v1, float v2, float v3);

		public void glUniformMatrix4fv(int location, boolean transpose, float[] value);
	}

	private class GLAPI implements IGLAPI {

		public GLAPI() {
			GL_FALSE = GL11.GL_FALSE;
			GL_COMPILE_STATUS = GL20.GL_COMPILE_STATUS;
			GL_FRAGMENT_SHADER = GL20.GL_FRAGMENT_SHADER;
			GL_VERTEX_SHADER = GL20.GL_VERTEX_SHADER;
			GL_GEOMETRY_SHADER = GL32.GL_GEOMETRY_SHADER;
			GL_COLOR_BUFFER_BIT = GL11.GL_COLOR_BUFFER_BIT;
			GL_DEPTH_BUFFER_BIT = GL11.GL_DEPTH_BUFFER_BIT;
		}

		@Override
		public void glClearColor(float red, float green, float blue, float alpha) {
			GL11.glClearColor(red, green, blue, alpha);
		}

		@Override
		public void glClear(int mask) {
			GL11.glClear(mask);
		}

		@Override
		public void glAttachShader(int program, int shader) {
			GL20.glAttachShader(program, shader);
		}

		@Override
		public void glBindAttribLocation(int program, int index, String name) {
			GL20.glBindAttribLocation(program, index, name);
		}

		@Override
		public void glCompileShader(int shader) {
			GL20.glCompileShader(shader);
		}

		@Override
		public int glCreateProgram() {
			return GL20.glCreateProgram();
		}

		@Override
		public int glCreateShader(int type) {
			return GL20.glCreateShader(type);
		}

		@Override
		public void glDeleteProgram(int program) {
			GL20.glDeleteProgram(program);
		}

		@Override
		public void glDeleteShader(int shader) {
			GL20.glDeleteShader(shader);
		}

		@Override
		public void glDetachShader(int program, int shader) {
			GL20.glDetachShader(program, shader);
		}

		@Override
		public String glGetShaderInfoLog(int shader, int maxLength) {
			return GL20.glGetShaderInfoLog(shader, maxLength);
		}

		@Override
		public int glGetShaderi(int shader, int pname) {
			return GL20.glGetShaderi(shader, pname);
		}

		@Override
		public void glLinkProgram(int program) {
			GL20.glLinkProgram(program);
		}

		@Override
		public void glShaderSource(int shader, CharSequence string) {
			GL20.glShaderSource(shader, string);
		}

		@Override
		public void glUseProgram(int program) {
			GL20.glUseProgram(program);
		}

		@Override
		public void glValidateProgram(int program) {
			GL20.glValidateProgram(program);
		}

		@Override
		public int glGetUniformLocation(int program, CharSequence name) {
			return GL20.glGetUniformLocation(program, name);
		}

		@Override
		public void glUniform1i(int location, int v0) {
			GL20.glUniform1i(location, v0);
		}

		@Override
		public void glUniform2i(int location, int v0, int v1) {
			GL20.glUniform2i(location, v0, v1);
		}

		@Override
		public void glUniform3i(int location, int v0, int v1, int v2) {
			GL20.glUniform3i(location, v0, v1, v2);
		}

		@Override
		public void glUniform4i(int location, int v0, int v1, int v2, int v3) {
			GL20.glUniform4i(location, v0, v1, v2, v3);
		}

		@Override
		public void glUniform1f(int location, float v0) {
			GL20.glUniform1f(location, v0);
		}

		@Override
		public void glUniform2f(int location, float v0, float v1) {
			GL20.glUniform2f(location, v0, v1);
		}

		@Override
		public void glUniform3f(int location, float v0, float v1, float v2) {
			GL20.glUniform3f(location, v0, v1, v2);
		}

		@Override
		public void glUniform4f(int location, float v0, float v1, float v2, float v3) {
			GL20.glUniform4f(location, v0, v1, v2, v3);
		}

		@Override
		public void glUniformMatrix4fv(int location, boolean transpose, float[] value) {
			GL20.glUniformMatrix4fv(location, transpose, value);
		}

	}

	private class GLESAPI implements IGLAPI {

		public GLESAPI() {
			GL_FALSE = GLES20.GL_FALSE;
			GL_COMPILE_STATUS = GLES20.GL_COMPILE_STATUS;
			GL_FRAGMENT_SHADER = GLES20.GL_FRAGMENT_SHADER;
			GL_VERTEX_SHADER = GLES20.GL_VERTEX_SHADER;
			GL_GEOMETRY_SHADER = GLES32.GL_GEOMETRY_SHADER;
			GL_COLOR_BUFFER_BIT = GLES20.GL_COLOR_BUFFER_BIT;
			GL_DEPTH_BUFFER_BIT = GLES20.GL_DEPTH_BUFFER_BIT;
		}

		@Override
		public void glClearColor(float red, float green, float blue, float alpha) {
			GLES20.glClearColor(red, green, blue, alpha);
		}

		@Override
		public void glClear(int mask) {
			GLES20.glClear(mask);
		}

		@Override
		public void glAttachShader(int program, int shader) {
			GLES20.glAttachShader(program, shader);
		}

		@Override
		public void glBindAttribLocation(int program, int index, String name) {
			GLES20.glBindAttribLocation(program, index, name);
		}

		@Override
		public void glCompileShader(int shader) {
			GLES20.glCompileShader(shader);
		}

		@Override
		public int glCreateProgram() {
			return GLES20.glCreateProgram();
		}

		@Override
		public int glCreateShader(int type) {
			return GLES20.glCreateShader(type);
		}

		@Override
		public void glDeleteProgram(int program) {
			GLES20.glDeleteProgram(program);
		}

		@Override
		public void glDeleteShader(int shader) {
			GLES20.glDeleteShader(shader);
		}

		@Override
		public void glDetachShader(int program, int shader) {
			GLES20.glDetachShader(program, shader);
		}

		@Override
		public String glGetShaderInfoLog(int shader, int maxLength) {
			return GLES20.glGetShaderInfoLog(shader, maxLength);
		}

		@Override
		public int glGetShaderi(int shader, int pname) {
			return GLES20.glGetShaderi(shader, pname);
		}

		@Override
		public void glLinkProgram(int program) {
			GLES20.glLinkProgram(program);
		}

		@Override
		public void glShaderSource(int shader, CharSequence string) {
			GLES20.glShaderSource(shader, string);
		}

		@Override
		public void glUseProgram(int program) {
			GLES20.glUseProgram(program);
		}

		@Override
		public void glValidateProgram(int program) {
			GLES20.glValidateProgram(program);
		}

		@Override
		public int glGetUniformLocation(int program, CharSequence name) {
			return GLES20.glGetUniformLocation(program, name);
		}

		@Override
		public void glUniform1i(int location, int v0) {
			GLES20.glUniform1i(location, v0);
		}

		@Override
		public void glUniform2i(int location, int v0, int v1) {
			GLES20.glUniform2i(location, v0, v1);
		}

		@Override
		public void glUniform3i(int location, int v0, int v1, int v2) {
			GLES20.glUniform3i(location, v0, v1, v2);
		}

		@Override
		public void glUniform4i(int location, int v0, int v1, int v2, int v3) {
			GLES20.glUniform4i(location, v0, v1, v2, v3);
		}

		@Override
		public void glUniform1f(int location, float v0) {
			GLES20.glUniform1f(location, v0);
		}

		@Override
		public void glUniform2f(int location, float v0, float v1) {
			GLES20.glUniform2f(location, v0, v1);
		}

		@Override
		public void glUniform3f(int location, float v0, float v1, float v2) {
			GLES20.glUniform3f(location, v0, v1, v2);
		}

		@Override
		public void glUniform4f(int location, float v0, float v1, float v2, float v3) {
			GLES20.glUniform4f(location, v0, v1, v2, v3);
		}

		@Override
		public void glUniformMatrix4fv(int location, boolean transpose, float[] value) {
			GLES20.glUniformMatrix4fv(location, transpose, value);
		}
	}

}
