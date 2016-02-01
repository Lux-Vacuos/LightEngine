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

package net.guerra24.infinity.client.world.entities;

import static net.guerra24.infinity.client.input.Keyboard.KEY_A;
import static net.guerra24.infinity.client.input.Keyboard.KEY_D;
import static net.guerra24.infinity.client.input.Keyboard.KEY_LSHIFT;
import static net.guerra24.infinity.client.input.Keyboard.KEY_S;
import static net.guerra24.infinity.client.input.Keyboard.KEY_SPACE;
import static net.guerra24.infinity.client.input.Keyboard.KEY_W;
import static net.guerra24.infinity.client.input.Keyboard.isKeyDown;
import static net.guerra24.infinity.client.input.Mouse.getDX;
import static net.guerra24.infinity.client.input.Mouse.getDY;
import static net.guerra24.infinity.client.input.Mouse.setCursorPosition;
import static net.guerra24.infinity.client.input.Mouse.setGrabbed;

import net.guerra24.infinity.client.graphics.opengl.Display;
import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.universal.util.vector.Matrix4f;
import net.guerra24.infinity.universal.util.vector.Vector2f;

public class PlayerCamera extends Camera {

	private float speed;
	private float multiplierMouse = 14;
	private boolean underWater = false;
	private int mouseSpeed = 2;
	private final int maxLookUp = 90;
	private final int maxLookDown = -90;
	private Vector2f center;

	public PlayerCamera(Matrix4f proj, Display display) {
		super(proj);
		center = new Vector2f(display.getDisplayWidth() / 2, display.getDisplayHeight() / 2);
		this.speed = 3f;
	}

	public void update(float delta, GameResources gm) {
		isMoved = false;
		float mouseDX = getDX() * delta * mouseSpeed * 0.16f * multiplierMouse;
		float mouseDY = getDY() * delta * mouseSpeed * 0.16f * multiplierMouse;
		if (yaw + mouseDX >= 360) {
			yaw = yaw + mouseDX - 360;
		} else if (yaw + mouseDX < 0) {
			yaw = 360 - yaw + mouseDX;
		} else {
			yaw += mouseDX;
		}
		if (pitch - mouseDY >= maxLookDown && pitch - mouseDY <= maxLookUp) {
			pitch += -mouseDY;
		} else if (pitch - mouseDY < maxLookDown) {
			pitch = maxLookDown;
		} else if (pitch - mouseDY > maxLookUp) {
			pitch = maxLookUp;
		}

		if (isKeyDown(KEY_W)) {
			velocityComponent.velocity.z += -Math.cos(Math.toRadians(yaw)) * speed;
			velocityComponent.velocity.x += Math.sin(Math.toRadians(yaw)) * speed;
			isMoved = true;

		} else if (isKeyDown(KEY_S)) {
			velocityComponent.velocity.z -= -Math.cos(Math.toRadians(yaw)) * speed;
			velocityComponent.velocity.x -= Math.sin(Math.toRadians(yaw)) * speed;
			isMoved = true;
		}

		if (isKeyDown(KEY_D)) {
			velocityComponent.velocity.z += Math.sin(Math.toRadians(yaw)) * speed;
			velocityComponent.velocity.x += Math.cos(Math.toRadians(yaw)) * speed;
			isMoved = true;
		} else if (isKeyDown(KEY_A)) {
			velocityComponent.velocity.z -= Math.sin(Math.toRadians(yaw)) * speed;
			velocityComponent.velocity.x -= Math.cos(Math.toRadians(yaw)) * speed;
			isMoved = true;
		}
		if (isKeyDown(KEY_SPACE) && !jump) {
			velocityComponent.velocity.y = 5;
			jump = true;
		}
		if (velocityComponent.velocity.y == 0)
			jump = false;
		if (isKeyDown(KEY_LSHIFT)) {
			speed = 0.5f;
		} else {
			speed = 3;
		}
		updateRay(gm.getRenderer().getProjectionMatrix(), gm.getDisplay().getDisplayWidth(),
				gm.getDisplay().getDisplayHeight(), center);
	}

	public void invertPitch() {
		pitch = -pitch;
	}

	public void setMouse(Display display) {
		setCursorPosition(display.getDisplayWidth() / 2, display.getDisplayHeight() / 2);
		setGrabbed(true);
	}

	public void unlockMouse() {
		setGrabbed(false);
	}

	public boolean isUnderWater() {
		return underWater;
	}

}
