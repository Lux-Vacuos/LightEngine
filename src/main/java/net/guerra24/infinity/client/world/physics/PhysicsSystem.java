/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 Guerra24
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.guerra24.infinity.client.world.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.guerra24.infinity.client.world.entities.CollisionComponent;
import net.guerra24.infinity.client.world.entities.PositionComponent;
import net.guerra24.infinity.client.world.entities.VelocityComponent;
import net.guerra24.infinity.universal.util.vector.Vector3f;

public class PhysicsSystem extends EntitySystem {
	private ImmutableArray<Entity> entities;

	private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
	private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
	private ComponentMapper<CollisionComponent> cm = ComponentMapper.getFor(CollisionComponent.class);
	private BoundingBox ground;

	public PhysicsSystem() {
		ground = new BoundingBox(new Vector3(-512, -1, -512), new Vector3(512, 1.5f, 512));
	}

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(
				Family.all(PositionComponent.class, VelocityComponent.class, CollisionComponent.class).get());
	}

	@Override
	public void update(float deltaTime) {
		for (Entity entity : entities) {

			PositionComponent position = pm.get(entity);
			VelocityComponent velocity = vm.get(entity);
			CollisionComponent collison = cm.get(entity);

			Vector3f positionV = position.position;
			Vector3f velocityV = velocity.velocity;

			velocityV.y += -9.8f * deltaTime;

			velocityV.x *= 0.6f - velocityV.x * 0.01f;
			velocityV.z *= 0.6f - velocityV.z * 0.01f;

			collison.boundingBox.set(new Vector3(positionV.x - 0.5f, positionV.y - 1.0f, positionV.z - 0.5f),
					new Vector3(positionV.x + 0.5f, positionV.y + 0.2f, positionV.z + 0.5f));
			if (velocityV.y < 0)
				if (collison.boundingBox.intersects(ground))
					velocityV.y = 0;
			position.position.x += velocityV.x * deltaTime;
			position.position.y += velocityV.y * deltaTime;
			position.position.z += velocityV.z * deltaTime;
		}
	}

}