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

import static net.guerra24.infinity.client.input.Keyboard.KEY_A;
import static net.guerra24.infinity.client.input.Keyboard.KEY_D;
import static net.guerra24.infinity.client.input.Keyboard.KEY_LCONTROL;
import static net.guerra24.infinity.client.input.Keyboard.KEY_LSHIFT;
import static net.guerra24.infinity.client.input.Keyboard.KEY_S;
import static net.guerra24.infinity.client.input.Keyboard.KEY_SPACE;
import static net.guerra24.infinity.client.input.Keyboard.KEY_W;
import static net.guerra24.infinity.client.input.Keyboard.KEY_Y;
import static net.guerra24.infinity.client.input.Keyboard.isKeyDown;
import static net.guerra24.infinity.client.input.Mouse.getDX;
import static net.guerra24.infinity.client.input.Mouse.getDY;
import static net.guerra24.infinity.client.input.Mouse.setCursorPosition;
import static net.guerra24.infinity.client.input.Mouse.setGrabbed;

import net.guerra24.infinity.client.graphics.opengl.Display;
import net.guerra24.infinity.client.network.DedicatedClient;
import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.client.resources.Ray;
import net.guerra24.infinity.client.util.Maths;
import net.guerra24.infinity.universal.util.vector.Matrix4f;
import net.guerra24.infinity.universal.util.vector.Vector2f;
import net.guerra24.infinity.universal.util.vector.Vector3f;

/**
 * Camera
 * 
 * @author Guerra24 <pablo230699@hotmail.com>
 */
public class Camera {

	private Vector3f position = new Vector3f(0, 0, 1);
	private float pitch;
	private float yaw;
	private float roll;
	private float speed;
	private float multiplierMouse = 24;
	private float multiplierMovement = 24;
	private boolean underWater = false;
	private int mouseSpeed = 2;
	private final int maxLookUp = 90;
	private final int maxLookDown = -90;
	private Ray ray;
	private Vector2f center;

	public boolean isMoved = false;
	public float depth = 0;

	int id = 0;

	public Camera(Matrix4f proj) {
		this.speed = 0.2f;
		center = new Vector2f(Display.getWidth() / 2, Display.getHeight() / 2);
		ray = new Ray(proj, Maths.createViewMatrix(this), center, Display.getWidth(), Display.getHeight());
	}

	public void update(float delta, GameResources gm, DedicatedClient client) {
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
			position.z += -Math.cos(Math.toRadians(yaw)) * delta * speed * multiplierMovement;
			position.x += Math.sin(Math.toRadians(yaw)) * delta * speed * multiplierMovement;
			isMoved = true;
		} else if (isKeyDown(KEY_S)) {
			position.z -= -Math.cos(Math.toRadians(yaw)) * delta * speed * multiplierMovement;
			position.x -= Math.sin(Math.toRadians(yaw)) * delta * speed * multiplierMovement;
			isMoved = true;
		}

		if (isKeyDown(KEY_D)) {
			position.z += Math.sin(Math.toRadians(yaw)) * delta * speed * multiplierMovement;
			position.x += Math.cos(Math.toRadians(yaw)) * delta * speed * multiplierMovement;
			isMoved = true;
		} else if (isKeyDown(KEY_A)) {
			position.z -= Math.sin(Math.toRadians(yaw)) * delta * speed * multiplierMovement;
			position.x -= Math.cos(Math.toRadians(yaw)) * delta * speed * multiplierMovement;
			isMoved = true;
		}
		if (isKeyDown(KEY_SPACE)) {
			position.y += 0.2f;
		}

		// if (isKeyDown(KEY_SPACE)) {
		// gm.getPhysics().getMobManager().getPlayer().jump();
		//
		// }
		if (isKeyDown(KEY_LSHIFT)) {
			position.y -= 0.2f;

		}

		// if (isKeyDown(KEY_LSHIFT)) {
		// speed = 0.05f;
		// } else {
		// speed = 0.2f;
		// }
		if (isKeyDown(KEY_LCONTROL)) {
			speed = 1;
		} else {
			speed = 0.2f;
		}
		if (isKeyDown(KEY_Y)) {
			System.out.println(position);
		}

		updateRay(gm);
	}

	public void updateRay(GameResources gm) {
		ray = new Ray(gm.getRenderer().getProjectionMatrix(), Maths.createViewMatrix(this), center, Display.getWidth(),
				Display.getHeight());
	}

	public void updateRay(int width, int height, Matrix4f projectionMatrix, Vector2f pos) {
		ray = new Ray(projectionMatrix, Maths.createViewMatrix(this), pos, width, height);
	}

	public void moveToPosition(Vector3f pos) {
		if (pos.getX() > position.getX())
			increasePosition(0.01f, 0, 0);
		if (pos.getX() < position.getX())
			increasePosition(-0.01f, 0, 0);
		if (pos.getY() > position.getY())
			increasePosition(0, 0.01f, 0);
		if (pos.getY() < position.getY())
			increasePosition(0, -0.01f, 0);
		if (pos.getZ() > position.getZ())
			increasePosition(0, 0, 0.01f);
		if (pos.getZ() < position.getZ())
			increasePosition(0, 0, -0.01f);
	}

	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}

	public void setMouse() {
		setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
		setGrabbed(true);
	}

	public void unlockMouse() {
		setGrabbed(false);
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getRoll() {
		return roll;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}

	public boolean isUnderWater() {
		return underWater;
	}

	public Ray getRay() {
		return ray;
	}

}