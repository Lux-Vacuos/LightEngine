/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Guerra24
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

package net.guerra24.infinity.client.world.entities;

import net.guerra24.infinity.client.core.InfinityVariables;
import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.client.resources.models.TexturedModel;
import net.guerra24.infinity.client.world.physics.CollisionType;
import net.guerra24.infinity.universal.util.vector.Vector3f;

public class Player extends Entity implements IEntity {
	private final float JUMP_POWER = 4;
	private boolean isInWater = false;
	private float upwardsSpeed = 0;

	private boolean isInAir = false;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

	@Override
	public void update(float delta, GameResources gm) {
		super.increasePosition(0, upwardsSpeed * delta, 0);
		if (isCollision(0) == CollisionType.FRONT) {
			super.increasePosition(0.1f, 0, 0);
		}
		CollisionType collision = isCollision(0);
		if (collision == CollisionType.TOP) {
			upwardsSpeed = 0;
			isInAir = false;
			isInWater = false;
		} else if (collision == CollisionType.WATER) {
			upwardsSpeed = -30f * delta;
			isInWater = true;
			isInAir = false;
		} else {
			upwardsSpeed += InfinityVariables.GRAVITY * delta;
			isInAir = true;
			isInWater = false;
		}
	}

	public void jump() {
		if (!isInAir && !isInWater) {
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		} else if (isInWater) {
			this.upwardsSpeed = 4;
		}
	}

	@Override
	public Entity getEntity() {
		return this;
	}

	private CollisionType isCollision(int direction) {
		return CollisionType.TOP;
	}
}
