package net.luxvacuos.lightengine.client.ecs.entities;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class Sun {

	private Vector3f rotation = new Vector3f(5, 0, -40);
	private Vector3f sunPosition = new Vector3f(0, 0, 0);
	private Vector3f invertedSunPosition = new Vector3f(0, 0, 0);
	private SunCamera camera;

	public Sun() {
		Matrix4f[] shadowProjectionMatrix = new Matrix4f[4];

		int shadowDrawDistance = (int) REGISTRY
				.getRegistryItem(new Key("/Light Engine/Settings/Graphics/shadowsDrawDistance"));
		shadowDrawDistance *= 2;
		shadowProjectionMatrix[0] = Maths.orthoSymmetric(-shadowDrawDistance / 25, shadowDrawDistance / 25,
				-shadowDrawDistance, shadowDrawDistance, false);
		shadowProjectionMatrix[1] = Maths.orthoSymmetric(-shadowDrawDistance / 10, shadowDrawDistance / 10,
				-shadowDrawDistance, shadowDrawDistance, false);
		shadowProjectionMatrix[2] = Maths.orthoSymmetric(-shadowDrawDistance / 4, shadowDrawDistance / 4,
				-shadowDrawDistance, shadowDrawDistance, false);
		shadowProjectionMatrix[3] = Maths.orthoSymmetric(-shadowDrawDistance, shadowDrawDistance, -shadowDrawDistance,
				shadowDrawDistance, false);
		camera = new SunCamera(shadowProjectionMatrix);
	}

	public Sun(Vector3f rotation, Matrix4f[] shadowProjectionMatrix) {
		this.rotation = rotation;
		camera = new SunCamera(shadowProjectionMatrix);
	}

	public void update(Vector3f cameraPosition, float rot, float delta) {
		camera.setPosition(cameraPosition);
		rotation.y = rot;
		camera.setRotation(new Vector3f(rotation.y, rotation.x, rotation.z));
		camera.updateShadowRay(true);
		sunPosition.set(camera.getDRay().getRay().dX * 10, camera.getDRay().getRay().dY * 10,
				camera.getDRay().getRay().dZ * 10);

		camera.updateShadowRay(false);
		invertedSunPosition.set(camera.getDRay().getRay().dX * 10, camera.getDRay().getRay().dY * 10,
				camera.getDRay().getRay().dZ * 10);
	}

	public CameraEntity getCamera() {
		return camera;
	}

	public Vector3f getInvertedSunPosition() {
		return invertedSunPosition;
	}

	public Vector3f getSunPosition() {
		return sunPosition;
	}

}
