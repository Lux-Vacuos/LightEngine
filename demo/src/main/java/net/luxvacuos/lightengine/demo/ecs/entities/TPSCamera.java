package net.luxvacuos.lightengine.demo.ecs.entities;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import net.luxvacuos.igl.vector.Vector3d;
import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.ecs.ClientComponents;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.input.Mouse;
import net.luxvacuos.lightengine.client.rendering.api.opengl.Renderer;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.ecs.components.Velocity;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class TPSCamera extends CameraEntity {

	private int boundary = 100;
	private int speed = 400;

	public TPSCamera(String name, String uuid) {
		super(name, uuid);
		ClientComponents.AABB.get(this).setGravity(false);
		int width = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width"));
		int height = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height"));

		ClientComponents.PROJECTION_MATRIX.get(this)
				.setProjectionMatrix(Renderer.createProjectionMatrix(width, height,
						(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/fov")),
						ClientVariables.NEAR_PLANE, ClientVariables.FAR_PLANE));
	}

	@Override
	public void update(float delta) {
		Velocity vel = Components.VELOCITY.get(this);
		Vector3d velocity = new Vector3d();
		int width = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width"));
		int height = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height"));
		if (Mouse.getX() > width - boundary) {
			velocity.x += speed * delta;
			velocity.z += speed * delta;
		}
		if (Mouse.getX() < 0 + boundary) {
			velocity.x -= speed * delta;
			velocity.z -= speed * delta;
		}
		if (Mouse.getY() > height - boundary) {
			velocity.z -= speed * delta;
			velocity.x += speed * delta;
		}
		if (Mouse.getY() < 0 + boundary) {
			velocity.z += speed * delta;
			velocity.x -= speed * delta;
		}
		vel.set(velocity);
		super.update(delta);
	}

}
