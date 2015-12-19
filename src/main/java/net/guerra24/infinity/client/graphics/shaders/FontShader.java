package net.guerra24.infinity.client.graphics.shaders;

import net.guerra24.infinity.client.core.InfinityVariables;
import net.guerra24.infinity.universal.util.vector.Vector2f;
import net.guerra24.infinity.universal.util.vector.Vector3f;

public class FontShader extends ShaderProgram {

	private int loc_color;
	private int loc_translation;

	public FontShader() {
		super(InfinityVariables.VERTEX_FILE_FONT, InfinityVariables.FRAGMENT_FILE_FONT);
	}

	@Override
	protected void getAllUniformLocations() {
		loc_color = super.getUniformLocation("color");
		loc_translation = super.getUniformLocation("translation");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}

	public void loadColor(Vector3f color) {
		super.loadVector(loc_color, color);
	}

	public void loadTranslation(Vector2f translation) {
		super.load2DVector(loc_translation, translation);
	}

}
