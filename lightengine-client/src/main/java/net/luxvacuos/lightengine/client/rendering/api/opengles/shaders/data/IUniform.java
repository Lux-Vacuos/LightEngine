package net.luxvacuos.lightengine.client.rendering.api.opengles.shaders.data;

import net.luxvacuos.lightengine.universal.resources.IDisposable;

public interface IUniform extends IDisposable {

	public void storeUniformLocation(int programID);
	
}
