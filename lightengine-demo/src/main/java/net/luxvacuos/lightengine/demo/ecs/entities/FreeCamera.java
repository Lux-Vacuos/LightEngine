package net.luxvacuos.lightengine.demo.ecs.entities;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.opengl.Renderer;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.ecs.components.Player;
import net.luxvacuos.lightengine.universal.ecs.components.Rotation;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class FreeCamera extends CameraEntity {

	private int mouseSpeed = 8;
	private final int maxLookUp = 90;
	private final int maxLookDown = -90;

	public FreeCamera(String name, String uuid) {
		super(name, uuid);
		int width = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width"));
		int height = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height"));

		setProjectionMatrix(Renderer.createProjectionMatrix(width, height, (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/fov")), ClientVariables.NEAR_PLANE,
				ClientVariables.FAR_PLANE));
		setViewMatrix(Maths.createViewMatrix(this));
		this.remove(Player.class);
	}

	@Override
	public void update(float delta) {
		Window window = GraphicalSubsystem.getMainWindow();
		KeyboardHandler kbh = window.getKeyboardHandler();
		MouseHandler mh = window.getMouseHandler();
		Rotation rotation = Components.ROTATION.get(this);

		float mouseDX = mh.getDX() * mouseSpeed * delta;
		float mouseDY = mh.getDY() * mouseSpeed * delta;
		if (rotation.getY() + mouseDX >= 360)
			rotation.setY(rotation.getY() + mouseDX - 360);
		else if (rotation.getY() + mouseDX < 0)
			rotation.setY(360 - rotation.getY() + mouseDX);
		else
			rotation.setY(rotation.getY() + mouseDX);

		if (rotation.getX() - mouseDY >= maxLookDown && rotation.getX() - mouseDY <= maxLookUp)
			rotation.setX(rotation.getX() - mouseDY);
		else if (rotation.getX() - mouseDY < maxLookDown)
			rotation.setX(maxLookDown);
		else if (rotation.getX() - mouseDY > maxLookUp)
			rotation.setX(maxLookUp);

		Vector3f walkDirection = new Vector3f(0.0f, 0.0f, 0.0f);
		float walkVelocity = 1.1f * 2.0f;
		if (kbh.isCtrlPressed())
			walkVelocity *= 10f;
		float walkSpeed = walkVelocity * delta;

		if (kbh.isKeyPressed(GLFW.GLFW_KEY_W)) {
			walkDirection.z += (float) -Math.cos(Math.toRadians(rotation.getY()));
			walkDirection.x += (float) Math.sin(Math.toRadians(rotation.getY()));
		} else if (kbh.isKeyPressed(GLFW.GLFW_KEY_S)) {
			walkDirection.z += (float) Math.cos(Math.toRadians(rotation.getY()));
			walkDirection.x += (float) -Math.sin(Math.toRadians(rotation.getY()));
		}

		if (kbh.isKeyPressed(GLFW.GLFW_KEY_D)) {
			walkDirection.z += (float) Math.sin(Math.toRadians(rotation.getY()));
			walkDirection.x += (float) Math.cos(Math.toRadians(rotation.getY()));
		} else if (kbh.isKeyPressed(GLFW.GLFW_KEY_A)) {
			walkDirection.z += (float) -Math.sin(Math.toRadians(rotation.getY()));
			walkDirection.x += (float) -Math.cos(Math.toRadians(rotation.getY()));
		}

		if (kbh.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
			walkDirection.y = 1;
		}
		if(kbh.isShiftPressed()) {
			walkDirection.y = -1;
		}
		walkDirection.mul(walkSpeed);
		getPosition().add(walkDirection);
		super.update(delta);
	}

}
