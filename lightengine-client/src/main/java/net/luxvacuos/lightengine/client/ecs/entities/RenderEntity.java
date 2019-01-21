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

package net.luxvacuos.lightengine.client.ecs.entities;

import com.bulletphysics.linearmath.Transform;

import net.luxvacuos.lightengine.client.ecs.ClientComponents;
import net.luxvacuos.lightengine.client.ecs.components.ModelLoader;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Model;
import net.luxvacuos.lightengine.universal.ecs.components.Collision;
import net.luxvacuos.lightengine.universal.ecs.entities.BasicEntity;
import net.luxvacuos.lightengine.universal.util.VectoVec;
import net.luxvacuos.lightengine.universal.world.DynamicObject;

public class RenderEntity extends BasicEntity {
	private boolean loadedColl;
	public boolean addedToSim;

	public RenderEntity(String name, Model model) {
		super(name);
		add(new ModelLoader(model));
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(VectoVec.toVec3(localPosition));
		add(new Collision(new DynamicObject(model.getShape(), transform, 0)));
	}

	public RenderEntity(String name, String uuid, Model model) {
		super(name, uuid);
		add(new ModelLoader(model));
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(VectoVec.toVec3(localPosition));
		add(new Collision(new DynamicObject(model.getShape(), transform, 0)));
	}

	public RenderEntity(String name, String path) {
		super(name);
		add(new ModelLoader(path));
	}

	public RenderEntity(String name, String uuid, String path) {
		super(name, uuid);
		add(new ModelLoader(path));
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		if (!loadedColl) {
			ModelLoader re = ClientComponents.RENDERABLE.get(this);
			if (re.isLoaded()) {
				loadedColl = true;
				Transform transform = new Transform();
				transform.setIdentity();
				transform.origin.set(VectoVec.toVec3(localPosition));
				add(new Collision(new DynamicObject(re.getModel().getShape(), transform, 0)));
			}
		}

	}

	public boolean isLoadedColl() {
		return loadedColl;
	}

}
