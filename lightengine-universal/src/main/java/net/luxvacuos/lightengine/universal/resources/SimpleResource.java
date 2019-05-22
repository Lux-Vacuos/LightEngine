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

package net.luxvacuos.lightengine.universal.resources;

public class SimpleResource implements IResource {

	protected ResourceType type;
	protected String path;

	public SimpleResource(ResourceType type, String path) {
		this.type = type;
		this.path = path;
	}

	@Override
	public ResourceType getResourceType() {
		return this.type;
	}

	@Override
	public String getResourcePath() {
		return this.path;
	}

	@Override
	public String toString() {
		return this.path;
	}

	@Override
	public int hashCode() {
		return 191 + path.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SimpleResource))
			return false;
		SimpleResource other = (SimpleResource) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
}
