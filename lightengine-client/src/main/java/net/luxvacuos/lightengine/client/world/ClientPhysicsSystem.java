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

package net.luxvacuos.lightengine.client.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;

import net.luxvacuos.lightengine.client.ecs.ClientComponents;
import net.luxvacuos.lightengine.client.ecs.entities.RenderEntity;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.world.PhysicsSystem;

public class ClientPhysicsSystem extends PhysicsSystem {

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				if (ClientComponents.RENDERABLE.has(entity))
					TaskManager.addTask(() -> ClientComponents.RENDERABLE.get(entity).getModel().dispose());
				if (entity instanceof RenderEntity)
					if (Components.COLLISION.has(entity))
						((RenderEntity) entity).addedToSim = false;
			}

			@Override
			public void entityAdded(Entity entity) {
			}
		});
	}

	@Override
	protected void update(float delta, Entity entity) {
		super.update(delta, entity);
		if (entity instanceof RenderEntity)
			if (!((RenderEntity) entity).addedToSim)
				if (Components.COLLISION.has(entity)) {
					dynamicsWorld.addRigidBody(Components.COLLISION.get(entity).getDynamicObject().getBody());
					((RenderEntity) entity).addedToSim = true;
				}
	}

}
