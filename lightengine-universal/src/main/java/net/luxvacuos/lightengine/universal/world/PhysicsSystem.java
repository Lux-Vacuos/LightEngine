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

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btAxisSweep3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy.CollisionFilterGroups;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btGhostPairCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;

import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.ecs.components.Collision;
import net.luxvacuos.lightengine.universal.ecs.components.Player;
import net.luxvacuos.lightengine.universal.ecs.entities.LEEntity;
import net.luxvacuos.lightengine.universal.ecs.entities.RootEntity;
import net.luxvacuos.lightengine.universal.util.VectoVec;

public class PhysicsSystem extends EntitySystem {
	protected ImmutableArray<Entity> entities;

	protected btDiscreteDynamicsWorld dynamicsWorld;

	private RootEntity rootEntity;

	public PhysicsSystem() {
		btCollisionConfiguration collisionConfiguration = new btDefaultCollisionConfiguration();
		btCollisionDispatcher dispatcher = new btCollisionDispatcher(collisionConfiguration);
		// Vector3 worldMin = new Vector3(-10000, -10000, -10000);
		// Vector3 worldMax = new Vector3(10000, 10000, 10000);
		// btAxisSweep3 sweepBP = new btAxisSweep3(worldMin, worldMax);
		btBroadphaseInterface overlappingPairCache = new btDbvtBroadphase();
		btSequentialImpulseConstraintSolver constraintSolver = new btSequentialImpulseConstraintSolver();
		dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, overlappingPairCache, constraintSolver,
				collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3(0, -9.8f, 0));
		overlappingPairCache.getOverlappingPairCache().setInternalGhostPairCallback(new btGhostPairCallback());
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
					dynamicsWorld.addCollisionObject(p.ghostObject, (short) CollisionFilterGroups.CharacterFilter,
							(short) (CollisionFilterGroups.StaticFilter | CollisionFilterGroups.DefaultFilter));
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
						Matrix4 trans = new Matrix4();
						coll.getDynamicObject().getBody().getWorldTransform(trans);
						leEntity.setPosition(VectoVec.toVec3(trans.getTranslation(new Vector3())));
					}
				}
			}
			update(delta, entity);
		}
		dynamicsWorld.stepSimulation(delta);
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