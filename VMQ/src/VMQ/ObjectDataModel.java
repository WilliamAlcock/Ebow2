package VMQ;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class ObjectDataModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public HashMap<String,Integer> textureNames = new HashMap<String,Integer>();
	public HashMap<String,MeshObject[]> meshObjs = new HashMap<String,MeshObject[]>();
	public HashMap<String,Vec3> dimensions = new HashMap<String,Vec3>();
	public HashMap<String,String> fileNames = new HashMap<String,String>();
	
	public ArrayList<Material> materials = new ArrayList<Material>();
	public ArrayList<LinkedList<Reference>> materialRefs = new ArrayList<LinkedList<Reference>>();
	
	public ArrayList<Mesh> Meshs = new ArrayList<Mesh>();
	public ArrayList<LinkedList<Reference>> vboObjectRefs = new ArrayList<LinkedList<Reference>>();
}

