package OpenGLTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;

import VMQ.Material;
import VMQ.Mesh;
import VMQ.MeshObject;
import VMQ.ObjectDataModel;
import VMQ.Vec3;

public class GraphicsLibrary {
	private TextureLibrary textureLibrary;
	private HashMap<String,Vec3> dimensions;
	private ArrayList<Material> materials;
	private ArrayList<VBOObject> vboObjects = new ArrayList<VBOObject>();
	private HashMap<String,MeshObject[]> meshObjs; 
	
	public GraphicsLibrary(GL3 gl,String filename) {
		ObjectDataModel dataModel = load(new File(filename));
		// load textures
		textureLibrary = new TextureLibrary(gl,dataModel.textureNames.keySet());
		dimensions = dataModel.dimensions;
		materials = dataModel.materials;
		// build VBOs 
		for (Mesh curMesh: dataModel.Meshs) {
			VBOObject curVBOObject = new VBOObject(curMesh);
			curVBOObject.buildVBO(gl);
			vboObjects.add(curVBOObject);
		}
		meshObjs = dataModel.meshObjs;
	}
	
	private ObjectDataModel load(File file) {
		ObjectDataModel newDataModel = null;
		String newLogs = null;
		try {																						// Loads the file
	        FileInputStream fileIn = new FileInputStream(file);
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        newDataModel = (ObjectDataModel) in.readObject();
	        newLogs = (String) in.readObject();
	        in.close();
	        fileIn.close();
	    } catch(IOException i) {
	    	System.out.println("IO Exception "+i);
	    } catch(ClassNotFoundException c) {
	    	System.out.println("Class not found "+c);
	    }
		
		if ((newDataModel!=null) && (newLogs!=null)) return newDataModel;
		System.err.println("ERROR LOADING GRAPHICS FILES");
		return null;
	}
	
	public Texture getTexture(String textureName) {
		return textureLibrary.getTexture(textureName);
	}
	
	public HashMap<String,Vec3> getDimensions() {
		return dimensions;
	}
	
	public Material getMaterialObject(int index) {
		return materials.get(index);
	}
	
	public int getMaterialSize() {
		return materials.size();
	}
	
	public VBOObject getVBOObject(int index) {
		return vboObjects.get(index);
	}
	
	public MeshObject[] getMeshObj(String type) {
		return meshObjs.get(type);
	}
}