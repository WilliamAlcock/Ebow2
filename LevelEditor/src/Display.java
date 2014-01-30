import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.media.opengl.GL3;

import GameEngine.Enemy;
import GameEngine.GameObj;
import GameEngine.Particle;
import OpenGLTools.GraphicsLibrary;

import OpenGLTools.ShaderProgram;
import VMQ.Mat4x4;
import VMQ.Material;
import VMQ.MeshObject;
import VMQ.Light;
import VMQ.Vec3;
import VMQ.Vec4;
import VMQ.WindowDimensions;


public class Display {

	private GL3 gl;
	private WindowDimensions windowDimensions;
	private GraphicsLibrary gameGraphicsLibrary;
	private GraphicsLibrary editorGraphicsLibrary;
	private HashMap<String,ShaderProgram> shaders = new HashMap<String,ShaderProgram>();
	private HashMap<String,Light> lights = new HashMap<String,Light>();
	
	public Display (GL3 gl,WindowDimensions windowDimensions) {
		this.gl = gl;
		this.windowDimensions = windowDimensions;
		System.out.println("Loading graphics librarys");
		gameGraphicsLibrary = new GraphicsLibrary(gl,"c:/Users/Jack/My Documents/Game/"+"EbowGraphics5.egf");
		editorGraphicsLibrary = new GraphicsLibrary(gl,"c:/Users/Jack/My Documents/Game/"+"EditorGraphics.egf");
		
		gl.glActiveTexture(GL3.GL_TEXTURE0);
		
		lights.put("NaiveLight", new Light (new Vec3(1.0f,1.0f,1.0f),new Vec3 (1.0f,1.0f,1.0f),new Vec3 (0.0f,0.0f,1.0f),0.2f,new Vec3 (0.0f,0.0f,1.0f)));
		
		// Naive shader
		shaders.put("NaiveShader", new ShaderProgram(gl,"MDLVertexShader.glsl","MDLFragmentShader.glsl", new String[]{"position","normal","textureCoords"}));
		shaders.get("NaiveShader").use();
		// Setup shader
		shaderSetup(shaders.get("NaiveShader")); 
		shaders.get("NaiveShader").putLocation("material_Alpha");
	
		// Transparent shader
		shaders.put("TransparentShader", new ShaderProgram(gl,"MDLVertexShader.glsl","TRANSFragmentShader.glsl",new String[]{"position","normal","textureCoords"}));
		shaders.get("TransparentShader").use();
		// Setup shader 
		shaderSetup(shaders.get("TransparentShader"));
		shaders.get("TransparentShader").putLocation("object_Color");
	}
	
	private void shaderSetup(ShaderProgram shader) {
		gl.glUniform3fv(shader.putLocation("ambient_Light_Color"), 3, lights.get("NaiveLight").getAmbientColor().getAsArray(), 0);
		gl.glUniform3fv(shader.putLocation("diffuse_Light_Color"),3, lights.get("NaiveLight").getDiffuseColor().getAsArray(),0);
		gl.glUniform1f(shader.putLocation("diffuse_Light_Strength"), lights.get("NaiveLight").getDiffuseStrength());
		gl.glUniform3fv(shader.putLocation("eye_Direction"), 3, lights.get("NaiveLight").getEyeDirection().getAsArray(), 0);
		// Setup locations
		shader.putLocation("diffuse_Light_Position");
		shader.putLocation("u_MVPMatrix");
		shader.putLocation("u_MVMatrix"); 
		
		shader.putLocation("material_Emission");
		shader.putLocation("material_Ambient");
		shader.putLocation("material_Diffuse");
		shader.putLocation("material_Specular");
		shader.putLocation("material_Shininess");
	}
	
	public void tick(ArrayList<Enemy> enemyObjects,ArrayList<LevelEditorObjects> editorObjects) {
		renderObjects(enemyObjects);
		gl.glEnable(GL3.GL_BLEND);
		gl.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDepthMask(false);
		renderHUD(editorObjects);
		gl.glDepthMask(true);
		gl.glDisable(GL3.GL_BLEND);
	}
	
	private void passMatrices(ShaderProgram shader, Mat4x4 modelMatrix) {
		// This multiplies the view matrix by the model matrix, and stores
		// the result in the MVP matrix
		Mat4x4 mMVPMatrix =  windowDimensions.getViewMatrix().multiply(modelMatrix);

		// Pass in the modelview matrix.
		gl.glUniformMatrix4fv(shader.putLocation("u_MVMatrix"), 1, false, mMVPMatrix.getAsArray(), 0);

		// This multiplies the modelview matrix by the projection matrix,
		Mat4x4 mTemporaryMatrix = windowDimensions.getProjectionMatrix().multiply(mMVPMatrix);

		// Pass in the combined matrix.
		gl.glUniformMatrix4fv(shader.putLocation("u_MVPMatrix"), 1, false, mTemporaryMatrix.getAsArray(), 0);
	}
	
	private void passLight(ShaderProgram shader, Light light) {
		// Pass in the light position in eye space.
		float[] mLightPosInEyeSpace = windowDimensions.getViewMatrix().multiply(new Vec4(windowDimensions.getCenter().sub(light.getDiffusePosition()),1.0f)).getAsArray();
		gl.glUniform3f(shader.getLocation("diffuse_Light_Position"), mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);
	}
	
	private Mat4x4 getModelMatrix(GameObj object) {
		return Mat4x4.getTranslationMatrix(object.getPosition()).multiply(object.getRotation().rotationMatrix().multiply(Mat4x4.getScalingMatrix(object.getScale())));
	}
	
	/*
	 * Renders explosions (Transparent billboards) 
	 */
	private void renderHUD(List<LevelEditorObjects> objects) {
		ShaderProgram currentShader = shaders.get("TransparentShader");
		currentShader.use();
		
		passLight(currentShader,lights.get("NaiveLight"));
			
		String currentTexture = "";
		
		for (LevelEditorObjects curObject: objects) {
			
			MeshObject curObj = editorGraphicsLibrary.getMeshObj(curObject.getType())[0];
			
			// if the texture is different to the current texture bind the texture
			if (!(curObj.getTextureName().equals(currentTexture))) {
				currentTexture = curObj.getTextureName();
				editorGraphicsLibrary.getTexture(currentTexture).bind(gl);
			}
			
			passMatrices(currentShader, getModelMatrix(curObject));
			
			// pass the material information to the shader
			Material curMaterial = editorGraphicsLibrary.getMaterialObject(curObj.getMaterialIndex());
			
			gl.glUniform3fv(currentShader.getLocation("material_Emission"),3,curMaterial.getEmission().getAsArray(),0);
			gl.glUniform3fv(currentShader.getLocation("material_Ambient"),3,curMaterial.getAmbient().getAsArray(),0);
			gl.glUniform3fv(currentShader.getLocation("material_Diffuse"),3,curMaterial.getDiffuse().getAsArray(),0);
			gl.glUniform3fv(currentShader.getLocation("material_Specular"),3,curMaterial.getSpecular().getAsArray(),0);
			gl.glUniform1f(currentShader.getLocation("material_Shininess"),curMaterial.GetSpecularWeight());
			gl.glUniform4fv(currentShader.getLocation("object_Color"),3,new Vec4(curObject.getColor(),0.5f).getAsArray(),0);
			
			// render the mesh (vertices/normals/texturecoords)
			editorGraphicsLibrary.getVBOObject(curObj.getVBOIndex()).renderVBO(gl,
					currentShader.getAttribute("position"),
					currentShader.getAttribute("normal"),
					currentShader.getAttribute("textureCoords"));
		}
	}
	
	/*
	 * Returns all the meshes to be drawn and computes their Model Matrices  
	 */
	private ArrayList<MeshObjectToDraw> getMeshObjectToDraw(List<? extends GameObj> gameObjects) {
		ArrayList<MeshObjectToDraw> retMeshObjectsToDraw = new ArrayList<MeshObjectToDraw>();
		for (GameObj curObj: gameObjects) {
			MeshObject[] meshObject = gameGraphicsLibrary.getMeshObj(curObj.getType());
			Mat4x4 modelMatrix = getModelMatrix(curObj);
			for (MeshObject curMeshObject: meshObject) {
				retMeshObjectsToDraw.add(new MeshObjectToDraw(curMeshObject,modelMatrix,
										new Vec4(curObj.getColor(),curObj.getLife()),
										curObj.getDisplayType(),curObj.useObjectColor()));
			}
		}
		return retMeshObjectsToDraw;
	}
	
	/*
	 * Renders a list of gameObjects
	 */
	private void renderObjects(List<? extends GameObj> gameObjects) { 
		ShaderProgram currentShader = shaders.get("NaiveShader");
		currentShader.use();
				
		// get the meshs to draw
		ArrayList<MeshObjectToDraw> meshObjects = getMeshObjectToDraw(gameObjects);
		// sort them into order dependent on the textures being used
		Collections.sort(meshObjects);
		
		passLight(currentShader,lights.get("NaiveLight"));
		
		String currentTexture = "";
		GameObj.DISPLAYTYPE currentDisplayType = null;
				
		for (MeshObjectToDraw curMeshObject: meshObjects) {
			// if the display type has changed change the opengl state
			if (curMeshObject.getDisplayType()!=currentDisplayType) {
				currentDisplayType = curMeshObject.getDisplayType();
				switch (currentDisplayType) {
					case TRANSPARENT_ONE:	gl.glEnable(GL3.GL_BLEND);
											gl.glDepthMask(false);
											gl.glBlendFunc(GL3.GL_SRC_ALPHA,GL3.GL_ONE_MINUS_SRC_ALPHA);
											break;
					case SOLID:				gl.glDepthMask(true);
											gl.glDisable(GL3.GL_BLEND);
					case TEXT:
					case TRANSPARENT:
											break;
				}
			}
			
			// if the texture is different to the current texture bind the texture
			if (!(curMeshObject.getMeshObject().getTextureName().equals(currentTexture))) {
				currentTexture = curMeshObject.getMeshObject().getTextureName();
				gameGraphicsLibrary.getTexture(currentTexture).bind(gl);
			}
			
			passMatrices(currentShader, curMeshObject.getModelMatrix());

			// pass the material information to the shader
			Material curMaterial = gameGraphicsLibrary.getMaterialObject(curMeshObject.getMeshObject().getMaterialIndex());
			
			gl.glUniform3fv(currentShader.getLocation("material_Emission"),3,curMaterial.getEmission().getAsArray(),0);
			if (curMeshObject.useColorObject()) {
				gl.glUniform3fv(currentShader.getLocation("material_Ambient"),3,curMeshObject.getObjectColor().getVec3().getAsArray(),0);
				gl.glUniform3fv(currentShader.getLocation("material_Diffuse"),3,curMeshObject.getObjectColor().getVec3().getAsArray(),0);
				gl.glUniform1f(currentShader.getLocation("material_Alpha"), curMeshObject.getObjectColor().getW());
			} else {
				gl.glUniform3fv(currentShader.getLocation("material_Ambient"),3,curMaterial.getAmbient().getAsArray(),0);
				gl.glUniform3fv(currentShader.getLocation("material_Diffuse"),3,curMaterial.getDiffuse().getAsArray(),0);
			}
			gl.glUniform3fv(currentShader.getLocation("material_Specular"),3,curMaterial.getSpecular().getAsArray(),0);
			gl.glUniform1f(currentShader.getLocation("material_Shininess"),curMaterial.GetSpecularWeight());

			// render the mesh (vertices/normals/texturecoords)
			gameGraphicsLibrary.getVBOObject(curMeshObject.getMeshObject().getVBOIndex()).renderVBO(gl,
					currentShader.getAttribute("position"),
					currentShader.getAttribute("normal"),
					currentShader.getAttribute("textureCoords"));
		}
	}
	
}
