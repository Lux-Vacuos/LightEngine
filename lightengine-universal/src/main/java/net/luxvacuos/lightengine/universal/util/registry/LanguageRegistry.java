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

package net.luxvacuos.lightengine.universal.util.registry;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;

import com.google.gson.reflect.TypeToken;

public class LanguageRegistry extends PersistentRegistry<String, String> {

	public void load(String filename) {
		InputStream file = getClass().getClassLoader().getResourceAsStream(filename);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file));) {
			Type type = new TypeToken<HashMap<String, String>>() {
			}.getType();
			registry = gson.fromJson(reader, type);
		} catch (Exception e) {
		}
	}

	@Override
	public void load(File database) {
		throw new UnsupportedOperationException("Use load(String)");
	}

	@Override
	public void save() {
		throw new UnsupportedOperationException("Languages can't be saved");
	}

	@Override
	/* @Nullable */
	public String getRegistryItem(/* @Nonnull */ String key) {
		String value = super.getRegistryItem(key);
		return (value == null ? key : value);
	}

}
