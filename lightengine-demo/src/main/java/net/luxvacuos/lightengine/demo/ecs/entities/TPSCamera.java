package net.luxvacuos.lightengine.demo.ecs.entities;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ecs.ClientComponents;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.api.opengl.Renderer;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class TPSCamera extends CameraEntity {

	private int boundary = 100;
	private int speed = 400;

	public TPSCamera(String name, String uuid) {
		super(name, uuid);
		int width = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width"));
		int height = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height"));

		ClientComponents.PROJECTION_MATRIX.get(this)
				.setProjectionMatrix(Renderer.createProjectionMatrix(width, height,
						(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/fov")),
						ClientVariables.NEAR_PLANE, ClientVariables.FAR_PLANE));
	}

	@Override
	public void update(float delta) {
		MouseHandler mh = GraphicalSubsystem.getMainWindow().getMouseHandler();
		Vector3f velocity = new Vector3f();
		int width = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width"));
		int height = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height"));
		if (mh.getX() > width - boundary) {
			velocity.x += speed * delta;
			velocity.z += speed * delta;
		}
		if (mh.getX() < 0 + boundary) {
			velocity.x -= speed * delta;
			velocity.z -= speed * delta;
		}
		if (mh.getY() > height - boundary) {
			velocity.z -= speed * delta;
			velocity.x += speed * delta;
		}
		if (mh.getY() < 0 + boundary) {
			velocity.z += speed * delta;
			velocity.x -= speed * delta;
		}
		super.update(delta);
	}

}
