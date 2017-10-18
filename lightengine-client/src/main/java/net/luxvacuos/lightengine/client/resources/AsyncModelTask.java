package net.luxvacuos.lightengine.client.resources;

import static org.lwjgl.assimp.Assimp.AI_SCENE_FLAGS_INCOMPLETE;
import static org.lwjgl.assimp.Assimp.aiGetErrorString;
import static org.lwjgl.assimp.Assimp.aiImportFileFromMemory;
import static org.lwjgl.assimp.Assimp.aiProcess_CalcTangentSpace;
import static org.lwjgl.assimp.Assimp.aiProcess_FindInvalidData;
import static org.lwjgl.assimp.Assimp.aiProcess_FlipUVs;
import static org.lwjgl.assimp.Assimp.aiProcess_ImproveCacheLocality;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_OptimizeMeshes;
import static org.lwjgl.assimp.Assimp.aiProcess_RemoveRedundantMaterials;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
import static org.lwjgl.assimp.Assimp.aiProcess_ValidateDataStructure;
import static org.lwjgl.assimp.Assimp.aiProcess_FindInstances;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.assimp.AIScene;

import com.badlogic.gdx.utils.async.AsyncTask;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Model;

public class AsyncModelTask implements AsyncTask<Model> {
	private String filePath;

	public AsyncModelTask(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public Model call() throws Exception {
		Logger.log("Loading Model: " + filePath);
		String fileName = "assets/" + filePath;
		String ext = fileName.split("\\.")[1];
		ByteBuffer bFile = null;
		try {
			bFile = ResourceLoader.ioResourceToByteBuffer(fileName, 2048 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
		AIScene scene = aiImportFileFromMemory(bFile,
				aiProcess_Triangulate | aiProcess_FlipUVs | aiProcess_FindInstances | aiProcess_RemoveRedundantMaterials
						| aiProcess_ValidateDataStructure | aiProcess_FindInvalidData | aiProcess_JoinIdenticalVertices
						| aiProcess_CalcTangentSpace | aiProcess_ImproveCacheLocality | aiProcess_OptimizeMeshes,
				ext);
		if (scene == null || scene.mFlags() == AI_SCENE_FLAGS_INCOMPLETE || scene.mRootNode() == null) {
			Logger.error(aiGetErrorString());
		}
		Model m = new Model(scene, filePath.substring(0, filePath.lastIndexOf("/")));
		while (true) {
			if (m.isDoneLoading())
				break;
			else
				Thread.sleep(100);
		}
		return m;
	}

}
