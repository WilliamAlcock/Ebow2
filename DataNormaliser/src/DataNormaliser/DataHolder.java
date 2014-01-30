package DataNormaliser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Set;

import FileReader.ObjFileReader;
import VMQ.Mat4x4;
import VMQ.Material;
import VMQ.Mesh;
import VMQ.MeshObject;
import VMQ.ObjectDataModel;
import VMQ.Reference;
import VMQ.Triangle;
import VMQ.Vec3;
import VMQ.Vec4;

public class DataHolder extends Observable {
	private ObjFileReader objFileReader = new ObjFileReader();
	private ObjectDataModel dataModel = new ObjectDataModel(); 
	
	public DataHolder() {}
	
	public void add(String filename,String objectName) {
		addToDataHolder(filename,objectName,this.dataModel);
	}
	
	private boolean addToDataHolder(String filename,String objectName,ObjectDataModel dataModel) {
		if (dataModel.meshObjs.containsKey(objectName)) {
			report("Object \""+objectName+"\" already exists");
			return false;
		} else {
			try {
				objFileReader.readFile(filename);
			
				MeshObject[] meshObjects = new MeshObject[objFileReader.size()];
				
				for (int i=0;i<objFileReader.size();i++) {
					addTexture(objFileReader.getTextureNames()[i],dataModel);
					int materialIndex = addReference(dataModel.materials,dataModel.materialRefs,objFileReader.getMaterials()[i],objectName,i);
					int meshIndex = addReference(dataModel.Meshs,dataModel.vboObjectRefs,objFileReader.getMeshs()[i],objectName,i);
					
					meshObjects[i] = new MeshObject(materialIndex,meshIndex,objFileReader.getTextureNames()[i]);
				}
				dataModel.dimensions.put(objectName, objFileReader.getDimensions());
				dataModel.meshObjs.put(objectName, meshObjects);
				dataModel.fileNames.put(objectName, filename);
				report("Object \""+objectName+"\" successfully loaded from "+filename);
			} catch(FileNotFoundException e) {
				report("Unable to open "+filename);
				return false;
			} catch(IOException e) {
				report("Error loading file "+filename);
				return false;
			}
			return true;
		}
	}
	
	public void refreshFromFiles() {
		ObjectDataModel newDataModel = new ObjectDataModel();
		for (String curObj: this.dataModel.fileNames.keySet()) {
			if (!addToDataHolder(this.dataModel.fileNames.get(curObj),curObj,newDataModel)) {
				report("Error refreshing files, file "+this.dataModel.fileNames.get(curObj)+" not found");
				report("To enable refreshing you must replace this file");
				return;
			}
			report("Refreshed file "+this.dataModel.fileNames.get(curObj));
		}
		this.dataModel = newDataModel;
	}
	
	private void addTexture(String textureName,ObjectDataModel dataModel) {
		if (dataModel.textureNames.get(textureName)==null) {
			dataModel.textureNames.put(textureName,1);
		} else {
			dataModel.textureNames.put(textureName, dataModel.textureNames.get(textureName)+1);
		}
	}
	
	private void removeTexture(String textureName) {
		// this should always be the case
		if (dataModel.textureNames.get(textureName)!=null) {
			if (dataModel.textureNames.get(textureName)==1) {
				dataModel.textureNames.remove(textureName);
			} else if (dataModel.textureNames.get(textureName)>1) {
				dataModel.textureNames.put(textureName, dataModel.textureNames.get(textureName)-1);
			}
		}
	}
	
	private <T> int addReference(ArrayList<T> objects, ArrayList<LinkedList<Reference>> references,T object,String objectName,int objectIndex) {
		int referenceIndex = objects.indexOf(object);
		if (referenceIndex<0) {
			referenceIndex = objects.size();
			objects.add(object);
			references.add(new LinkedList<Reference>(Arrays.asList(new Reference(objectName,objectIndex))));
		} else {
			references.get(referenceIndex).add(new Reference(objectName,objectIndex));
		}
		return referenceIndex;
	}
		
	private <T> void removeReference(ArrayList<T> objects, ArrayList<LinkedList<Reference>> references,int index,String objectName,int objectIndex) {
		LinkedList<Reference> refs = references.get(index);
		if (refs.size()==1) {
			Object myObject = objects.remove(index);
			references.remove(index);
			// Update meshobject material index refs
			for (int i=index;i<references.size();i++) {
				for (Reference curReference: references.get(i)) {
					if (myObject instanceof Material) {
						dataModel.meshObjs.get(curReference.name)[curReference.index].setMaterialIndex(i);
					} else if (myObject instanceof Mesh) {
						dataModel.meshObjs.get(curReference.name)[curReference.index].setVBOIndex(i);
					}
				}
			}
		} else if (refs.size()>1) {
			refs.remove(new Reference(objectName,objectIndex));
		} else if (references.size()<1) {
			// this should never happen
			System.out.println("BAD ERROR WITH REFERENCES - remove ");
		}
	}
	
	private <T> void changeReference(ArrayList<LinkedList<Reference>> references,int index,int i,String oldObjectName,String newObjectName ) {
		int indexToChange = references.get(index).indexOf(new Reference(oldObjectName,i));
		if (indexToChange>=0) {
			references.get(index).get(indexToChange).name = newObjectName;
		} else {
			// this should not happen
			System.out.println("BAD ERROR WITH REFERENCES - rename");
		}
	}
	
	public void rename(String oldObjectName,String newObjectName) {
		if (dataModel.meshObjs.containsKey(newObjectName)) {
			report("Object \""+newObjectName+"\" already exists");
		} else {
			// Update the material references
			MeshObject[] curMeshObjs = dataModel.meshObjs.get(oldObjectName);
			for (int i=0;i<curMeshObjs.length;i++) {
				
				System.out.println("Material Index: "+curMeshObjs[i].getMaterialIndex());
				System.out.println("VBO Index: "+curMeshObjs[i].getVBOIndex());
				changeReference(dataModel.materialRefs,curMeshObjs[i].getMaterialIndex(),i,oldObjectName,newObjectName);
				changeReference(dataModel.vboObjectRefs,curMeshObjs[i].getVBOIndex(),i,oldObjectName,newObjectName);
			}
			// Update object name
			dataModel.meshObjs.put(newObjectName, dataModel.meshObjs.get(oldObjectName));
			dataModel.dimensions.put(newObjectName, dataModel.dimensions.get(oldObjectName));
			dataModel.meshObjs.remove(oldObjectName);
			dataModel.dimensions.remove(oldObjectName);
			report("Object \""+oldObjectName+"\" renamed as "+newObjectName);
		}
	}
	
	public void remove(String objectName) {
		if (dataModel.meshObjs.containsKey(objectName)) {
			MeshObject[] curMeshObj = dataModel.meshObjs.get(objectName);
			for (int i=0;i<curMeshObj.length;i++) {
				removeTexture(curMeshObj[i].getTextureName());
				removeReference(dataModel.materials,dataModel.materialRefs,curMeshObj[i].getMaterialIndex(),objectName,i);
				removeReference(dataModel.Meshs,dataModel.vboObjectRefs,curMeshObj[i].getVBOIndex(),objectName,i);
			}
			dataModel.meshObjs.remove(objectName);
			dataModel.dimensions.remove(objectName);
			report("Object \""+objectName+"\" removed");
		} else {
			// this should never happen
			report("Object \""+objectName+"\" does not exist cannot delete");
		}
	}
	
	public void save(File file,String logs) {
		try {																					// Saves the file
			FileOutputStream fileOut = new FileOutputStream(file);
	        ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(dataModel);
	        out.writeObject(logs);
	        out.close();
	        fileOut.close();
			report("Ebow Graphics File "+file+" saved");
	    } catch(IOException i) {
	    	report("Error Saving File "+file+"\n"+i);
	    }
	}
	
	public void load(File file) {
		ObjectDataModel newDataModel = null;
		String newLogs = null;
		String errorString = "";
		try {																						// Loads the file
	        FileInputStream fileIn = new FileInputStream(file);
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        newDataModel = (ObjectDataModel) in.readObject();
	        newLogs = (String) in.readObject();
	        in.close();
	        fileIn.close();
	    } catch(IOException i) {
	    	errorString += i;
	    } catch(ClassNotFoundException c) {
	        errorString += c;
	    }
		if ((newDataModel!=null) && (newLogs!=null)) {																	// if object has not been loaded assume there was an error
			this.dataModel = newDataModel;													
			report("Ebow Graphics File "+file+" loaded\n"+newLogs);
		} else {
			report("Error Loading File "+file+"\n"+errorString);
		}
	}
	
/*	public boolean scale(String objectName,float x,float y,float z) {
		if (dataModel.meshObjs.containsKey(objectName)) {
			MeshObject[] meshObj = dataModel.meshObjs.get(objectName);
			for (int i=0;i<meshObj.length;i++) {
				Mesh curMesh = dataModel.Meshs.get(meshObj[i].getVBOIndex());
				removeReference(dataModel.Meshs,dataModel.vboObjectRefs,meshObj[i].getVBOIndex(),objectName,i);
				return true;
			}
		} else {
			return false;
		}
	}	*/
	
	private void scaleMesh(Mesh mesh,float x,float y,float z) {
		Mat4x4 scale = Mat4x4.getScalingMatrix(new Vec3(x,y,z));
		// scale the vertices
		for (Triangle curTriangle: mesh.getMesh()) {
			for (int i=0;i<3;i++) {
				Vec3 scaledVertex = scale.multiply(new Vec4(curTriangle.getVert(i),1.0f)).getVec3();
				Vec3 scaledNormal = scale.multiply(new Vec4(curTriangle.getNorm(i),0.0f)).getVec3();
				
				curTriangle.getVert(i).setX(scaledVertex.getX());
				curTriangle.getVert(i).setY(scaledVertex.getY());
				curTriangle.getVert(i).setZ(scaledVertex.getZ());
				
				curTriangle.getNorm(i).setX(scaledNormal.getX());
				curTriangle.getNorm(i).setY(scaledNormal.getY());
				curTriangle.getNorm(i).setZ(scaledNormal.getZ());
			}
		}
	}
	
	public Set<String> getList() {
		return dataModel.meshObjs.keySet();
	}
	
	private void report(String str) {
		for (int i=0;i<dataModel.materialRefs.size();i++) {
			System.out.println("List "+i);
			for (Reference curObj: dataModel.materialRefs.get(i)) {
				System.out.println("name: "+curObj.name+" index: "+curObj.index+"\n");
			}
		}
		for (int i=0;i<dataModel.vboObjectRefs.size();i++) {
			System.out.println("List "+i);
			for (Reference curObj: dataModel.vboObjectRefs.get(i)) {
				System.out.println("name: "+curObj.name+" index: "+curObj.index+"\n");
			}
		}
		this.setChanged();
		this.notifyObservers(str);
	}
}
