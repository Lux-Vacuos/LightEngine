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

package net.luxvacuos.lightengine.client.core;

import net.luxvacuos.lightengine.universal.core.GlobalVariables;

public class ClientVariables extends GlobalVariables {

	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = Float.POSITIVE_INFINITY;

	/**
	 * Shader Files
	 */
	public static final String VERTEX_FILE_ENTITY_DEFERRED = "EntityDeferred.vs";
	public static final String FRAGMENT_FILE_ENTITY_DEFERRED = "EntityDeferred.fs";
	public static final String VERTEX_FILE_ENTITY_BASIC = "EntityBasic.vs";
	public static final String FRAGMENT_FILE_ENTITY_BASIC = "EntityBasic.fs";
	public static final String VERTEX_FILE_ENTITY_FORWARD = "EntityForward.vs";
	public static final String FRAGMENT_FILE_ENTITY_FORWARD = "EntityForward.fs";
	public static final String VERTEX_FILE_SKYDOME = "Skydome.vs";
	public static final String FRAGMENT_FILE_SKYDOME= "Skydome.fs";
	public static final String VERTEX_FILE_PARTICLE = "Particle.vs";
	public static final String FRAGMENT_FILE_PARTICLE = "Particle.fs";
	public static final String VERTEX_IRRADIANCE_CAPTURE = "IrradianceCapture.vs";
	public static final String FRAGMENT_IRRADIANCE_CAPTURE = "IrradianceCapture.fs";
	public static final String VERTEX_PRE_FILTERED_ENV = "PreFilteredEnvironment.vs";
	public static final String FRAGMENT_PRE_FILTERED_ENV = "PreFilteredEnvironment.fs";
	public static final String VERTEX_BRDF_INTEGRATION_MAP = "BRDFIntegrationMap.vs";
	public static final String FRAGMENT_BRDF_INTEGRATION_MAP = "BRDFIntegrationMap.fs";
	public static final String VERTEX_WATER = "Water.vs";
	public static final String FRAGMENT_WATER = "Water.fs";
	public static final String GEOMETRY_WATER = "Water.gs";
	public static final String VERTEX_WINDOW3D = "3DWindow.vs";
	public static final String FRAGMENT_WINDOW3D = "3DWindow.fs";

}
