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

package net.luxvacuos.lightengine.universal.world;

import javax.vecmath.Vector3f;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.CollisionFilterGroups;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.dispatch.GhostPairCallback;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;

import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.ecs.components.Collision;
import net.luxvacuos.lightengine.universal.ecs.components.Player;
import net.luxvacuos.lightengine.universal.ecs.entities.LEEntity;
import net.luxvacuos.lightengine.universal.ecs.entities.RootEntity;
import net.luxvacuos.lightengine.universal.util.VectoVec;

public class PhysicsSystem extends EntitySystem {
	protected ImmutableArray<Entity> entities;

	protected DiscreteDynamicsWorld dynamicsWorld;

	private RootEntity rootEntity;

	public PhysicsSystem() {
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		Vector3f worldMin = new Vector3f(-10000, -10000, -10000);
		Vector3f worldMax = new Vector3f(10000, 10000, 10000);
		AxisSweep3 sweepBP = new AxisSweep3(worldMin, worldMax);
		BroadphaseInterface overlappingPairCache = sweepBP;
		SequentialImpulseConstraintSolver constraintSolver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, constraintSolver,
				collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0, -9.8f, 0));
		overlappingPairCache.getOverlappingPairCache().setInternalGhostPairCallback(new GhostPairCallback());
		rootEntity = new RootEntity();
	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		entities = engine.getEntities();
		engine.addEntityListener(new EntityListener() {

			@Override
			public void entityAdded(Entity entity) {
				if (entity instanceof LEEntity) {
					LEEntity ent = (LEEntity) entity;
					ent.setRootEntity(rootEntity);
					ent.setEngine(engine);
					ent.init();
				}

				if (Components.PLAYER.has(entity)) {
					Player p = Components.PLAYER.get(entity);
					dynamicsWorld.addCollisionObject(p.ghostObject, CollisionFilterGroups.CHARACTER_FILTER,
							(short) (CollisionFilterGroups.STATIC_FILTER | CollisionFilterGroups.DEFAULT_FILTER));
					dynamicsWorld.addAction(p.character);
					return;
				}
				if (Components.COLLISION.has(entity))
					dynamicsWorld.addRigidBody(Components.COLLISION.get(entity).getDynamicObject().getBody());
			}

			@Override
			public void entityRemoved(Entity entity) {
				if (entity instanceof LEEntity)
					((LEEntity) entity).dispose();

				if (Components.PLAYER.has(entity)) {
					Player p = Components.PLAYER.get(entity);
					dynamicsWorld.removeAction(p.character);
					dynamicsWorld.removeCollisionObject(p.ghostObject);
					return;
				}
				if (Components.COLLISION.has(entity))
					dynamicsWorld.removeRigidBody(Components.COLLISION.get(entity).getDynamicObject().getBody());

			}

		});
	}

	@Override
	public void update(float delta) {
		for (Entity entity : entities) {
			if (entity instanceof LEEntity) {
				LEEntity leEntity = (LEEntity) entity;
				leEntity.beforeUpdate(delta);
				leEntity.update(delta);

				if (Components.COLLISION.has(entity)) {
					Collision coll = Components.COLLISION.get(entity);
					if (!Components.PLAYER.has(entity)) {
						Transform trans = new Transform();
						coll.getDynamicObject().getBody().getMotionState().getWorldTransform(trans);
						leEntity.setPosition(VectoVec.toVec3(trans.origin));
					}
				}
			}
			update(delta, entity);
		}
		dynamicsWorld.stepSimulation(delta, 0);
		for (Entity entity : entities)
			if (entity instanceof LEEntity)
				((LEEntity) entity).afterUpdate(delta);

	}

	protected void update(float delta, Entity entity) {
	}

	public void addCollision(DynamicObject dyo) {
		dynamicsWorld.addRigidBody(dyo.getBody());
	}

}