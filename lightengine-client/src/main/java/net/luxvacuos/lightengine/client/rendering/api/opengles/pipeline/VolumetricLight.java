package net.luxvacuos.lightengine.client.rendering.api.opengles.pipeline;

import static org.lwjgl.opengles.GLES20.GL_TEXTURE1;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE6;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE_2D;
import static org.lwjgl.opengles.GLES20.glActiveTexture;
import static org.lwjgl.opengles.GLES20.glBindTexture;

import net.luxvacuos.lightengine.client.rendering.api.opengles.DeferredPass;
import net.luxvacuos.lightengine.client.rendering.api.opengles.FBO;
import net.luxvacuos.lightengine.client.rendering.api.opengles.IDeferredPipeline;
import net.luxvacuos.lightengine.client.rendering.api.opengles.ShadowFBO;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.CubeMapTexture;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.Texture;

public class VolumetricLight extends DeferredPass {

	public VolumetricLight(String name, int width, int height) {
		super(name, width, height);
	}

	@Override
	public void render(FBO[] auxs, IDeferredPipeline pipe, CubeMapTexture irradianceCapture,
			CubeMapTexture environmentMap, Texture brdfLUT, ShadowFBO shadow) {
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, pipe.getMainFBO().getPositionTex());
		glActiveTexture(GL_TEXTURE6);
		glBindTexture(GL_TEXTURE_2D, auxs[1].getTexture());
	}

}
