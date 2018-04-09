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

package net.luxvacuos.lightengine.universal.core;

public class GlobalVariables {

	/** Client/Server Version */
	public static String version = "devver";

	/** Client/Server Branch */
	public static String branch = "devbranch";

	/** Client/Server Build */
	public static int build = 0;

	/** Universal Version */
	public static String versionUniversal = "devver";

	/** * Universal Branch */
	public static String branchUniversal = "devbranch";

	/** * Universal Build */
	public static int buildUniversal = 0;

	/** Flag to enable debug mode */
	public static boolean debug = false;

	/** Enable Test Mode */
	public static boolean TEST_MODE = false;

	public static String PROJECT;

	protected GlobalVariables() {
	}

}
