package net.luxvacuos.lightengine.client.rendering.opengl.pipeline;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import net.luxvacuos.lightengine.client.rendering.opengl.DeferredPass;
import net.luxvacuos.lightengine.client.rendering.opengl.FBO;
import net.luxvacuos.lightengine.client.rendering.opengl.IDeferredPipeline;
import net.luxvacuos.lightengine.client.rendering.opengl.ShadowFBO;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.CubeMapTexture;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Texture;

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
