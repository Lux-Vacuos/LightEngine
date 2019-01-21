/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2019 Lux Vacuos
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

package net.luxvacuos.igl;

public final class Logger {

	private static org.apache.log4j.Logger log;

	public static void init() {
		log = org.apache.log4j.Logger.getLogger("Light Engine");
	}

	public static void log(Object... messages) {
		for (Object object : messages)
			log.info(object);
	}

	public static void warn(Object... messages) {
		for (Object object : messages)
			log.warn(object);
	}

	public static void error(Object... messages) {
		for (Object object : messages)
			log.error(object);
	}
}