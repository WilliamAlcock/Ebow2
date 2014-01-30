package com.example.androidebow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import GameEngine.GameObj;
import GameEngine.InPlayObj;
import GameEngine.Particle;
import OpenGLTools.GraphicsLibrary;
import OpenGLTools.ShaderProgram;
import VMQ.Light;
import VMQ.Material;
import VMQ.Mat4x4;
import VMQ.MeshObject;
import VMQ.Vec3;
import VMQ.Vec4;
import VMQ.WindowDimensions;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

public class Display {
	
	private GraphicsLibrary graphicsLibrary;
	private WindowDimensions windowDimensions; 
	private HashMap<String,ShaderProgram> shaders = new HashMap<String,ShaderProgram>();
	private HashMap<String,Light> lights = new HashMap<String,Light>();

	private List<GameObj> backgroundObjects;
	private List<InPlayObj> enemys;
	private List<InPlayObj> players;
	private List<Particle> particles;
	private List<GameObj> HUDObjects;
	
	public void setBackgroundObjects(List<GameObj> backgroundObjects) {
		this.backgroundObjects = backgroundObjects;
	}

	public void setEnemyObjects(List<InPlayObj> enemys) {
		this.enemys = enemys;
	}

	public void setPlayerObjects(List<InPlayObj> players) {
		this.players = players;
	}
	
	public void setParticleObjects(List<Particle> particles) {
		this.particles = particles;
	}
	
	public void setHUDObjects(List<GameObj> HUDObjects) {
		this.HUDObjects = HUDObjects;
	}
	
	public HashMap<String,Vec3> getObjectDimensions() {
		return graphicsLibrary.getDimensions();
	}

	public Display(GLSurfaceView mGLSurfaceView,WindowDimensions windowDimensions) {
		// Set the background clear color to black.
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		// Use culling to remove back faces.
//		GLES20.glEnable(GLES20.GL_CULL_FACE);
		// Enable depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		this.windowDimensions = windowDimensions;
		// Load the texture and vbos
		graphicsLibrary = new GraphicsLibrary(mGLSurfaceView.getContext(),"EbowGraphics.egf");
		// Set the active texture unit to texture unit 0
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		lights.put("NaiveLight", new Light (new Vec3(1.0f,1.0f,1.0f),new Vec3 (1.0f,1.0f,1.0f),new Vec3 (0.0f,-40.0f,0.0f),1.0f,new Vec3 (0.0f,-1.0f,0.0f)));
		
		// Naive shader
		shaders.put("NaiveShader", new ShaderProgram(mGLSurfaceView.getContext(),"MDLVertexShader.glsl","MDLFragmentShader.glsl",new String[] {"position",  "normal", "textureCoords"}));
		shaders.get("NaiveShader").use();
		// Setup shader
		shaderSetup(shaders.get("NaiveShader")); 
		shaders.get("NaiveShader").putLocation("material_Alpha");

		// Transparent shader
		shaders.put("TransparentShader", new ShaderProgram(mGLSurfaceView.getContext(),"MDLVertexShader.glsl","TRANSFragmentShader.glsl",new String[] {"position",  "normal", "textureCoords"}));
		shaders.get("TransparentShader").use();
		// Setup shader 
		shaderSetup(shaders.get("TransparentShader"));
		shaders.get("TransparentShader").putLocation("object_Color");
	}
	
	private void shaderSetup(ShaderProgram shader) {
		GLES20.glUniform3fv(shaders.get("NaiveShader").putLocation("ambient_Light_Color"), 1, lights.get("NaiveLight").getAmbientColor().getAsArray(), 0);
		GLES20.glUniform3fv(shaders.get("NaiveShader").putLocation("diffuse_Light_Color"),1, lights.get("NaiveLight").getDiffuseColor().getAsArray(),0);
		GLES20.glUniform1f(shaders.get("NaiveShader").putLocation("diffuse_Light_Strength"), lights.get("NaiveLight").getDiffuseStrength());
		GLES20.glUniform3fv(shaders.get("NaiveShader").putLocation("eye_Direction"), 1, lights.get("NaiveLight").getEyeDirection().getAsArray(), 0);
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
			
	public void tick() {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//		renderObjects(backgroundObjects);
		renderObjects(enemys);
		renderObjects(players);
		renderParticles(particles);
//		renderObjects(HUDObjects);
	}

	private void passMatrices(ShaderProgram shader, Mat4x4 modelMatrix) {
		// This multiplies the view matrix by the model matrix, and stores
		// the result in the MVP matrix
		Mat4x4 mMVPMatrix =  windowDimensions.getViewMatrix().multiply(modelMatrix);

		// Pass in the modelview matrix.
		GLES20.glUniformMatrix4fv(shaders.get("TransparentShader").putLocation("u_MVMatrix"), 1, false, mMVPMatrix.getAsArray(), 0);

		// This multiplies the modelview matrix by the projection matrix,
		Mat4x4 mTemporaryMatrix = windowDimensions.getProjectionMatrix().multiply(mMVPMatrix);

		// Pass in the combined matrix.
		GLES20.glUniformMatrix4fv(shaders.get("TransparentShader").putLocation("u_MVPMatrix"), 1, false, mTemporaryMatrix.getAsArray(), 0);
	}
	
	private void passLight(ShaderProgram shader, Light light) {
		// Pass in the light position in eye space.
		float[] mLightPosInEyeSpace = windowDimensions.getViewMatrix().multiply(new Vec4(windowDimensions.getCenter().sub(light.getDiffusePosition()),1.0f)).getAsArray();
		GLES20.glUniform3f(shader.getLocation("diffuse_Light_Position"), mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);
	}
	
	private Mat4x4 getModelMatrix(GameObj object) {
		return Mat4x4.getTranslationMatrix(object.getPosition()).multiply(object.getRotation().rotationMatrix().multiply(Mat4x4.getScalingMatrix(object.getScale())));
	}
	
	/*
	 * Renders explosions (Transparent billboards) 
	 */
	private void renderParticles(List<Particle> particles) {
		ShaderProgram currentShader = shaders.get("TransparentShader");
		currentShader.use();
	    GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glDepthMask(false);
		
		passLight(shaders.get("TransparetnShader"),lights.get("NaiveLight"));
		
		// Sort particles by z
		Collections.sort(particles);
		
		String currentTexture = "";
		
		for (Particle curParticle: particles) {
			
			MeshObject curObj = graphicsLibrary.getMeshObj(curParticle.getType())[0];
			
			// if the texture is different to the current texture bind the texture
			if (!(curObj.getTextureName().equals(currentTexture))) {
				currentTexture = curObj.getTextureName();
				graphicsLibrary.bindTexture(currentTexture);
			}
			
			passMatrices(shaders.get("TransparentShader"), getModelMatrix(curParticle));
			
			// pass the material information to the shader
			Material curMaterial = graphicsLibrary.getMaterialObject(curObj.getMaterialIndex());
			
			GLES20.glUniform3fv(currentShader.getLocation("material_Emission"),3,curMaterial.getEmission().getAsArray(),0);
			GLES20.glUniform3fv(currentShader.getLocation("material_Ambient"),3,curMaterial.getAmbient().getAsArray(),0);
			GLES20.glUniform3fv(currentShader.getLocation("material_Diffuse"),3,curMaterial.getDiffuse().getAsArray(),0);
			GLES20.glUniform3fv(currentShader.getLocation("material_Specular"),3,curMaterial.getSpecular().getAsArray(),0);
			GLES20.glUniform1f(currentShader.getLocation("material_Shininess"),curMaterial.GetSpecularWeight());
			GLES20.glUniform4fv(currentShader.getLocation("object_Color"),3,new Vec4(curParticle.getColor(),curParticle.getAlpha()).getAsArray(),0);
			
			// render the mesh (vertices/normals/texturecoords)
			graphicsLibrary.getVBOObject(curObj.getVBOIndex()).renderVBO(
					currentShader.getAttribute("position"),
					currentShader.getAttribute("normal"),
					currentShader.getAttribute("textureCoords"));
		}
		GLES20.glDepthMask(true);
		GLES20.glDisable(GLES20.GL_BLEND);
	}
	
	/*
	 * Returns all the meshes to be drawn and computes their Model Matrices  
	 */
	private ArrayList<MeshObjectToDraw> getMeshObjectToDraw(List<? extends GameObj> gameObjects) {		
		ArrayList<MeshObjectToDraw> retMeshObjectsToDraw = new ArrayList<MeshObjectToDraw>();
		for (GameObj curObj: gameObjects) {
			MeshObject[] meshObject = graphicsLibrary.getMeshObj(curObj.getType());	    	
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
	public void renderObjects(List<? extends GameObj> gameObjects) {
		ShaderProgram currentShader = shaders.get("NaiveShader");
		currentShader.use();                      
        
		// get the meshs to draw
		ArrayList<MeshObjectToDraw> meshObjects = getMeshObjectToDraw(gameObjects);
		// sort them into order dependent on the textures being used
		Collections.sort(meshObjects);
		
		passLight(shaders.get("NaiveShader"),lights.get("NaiveLight"));
		
		String currentTexture = "";
		GameObj.DISPLAYTYPE currentDisplayType = null;
		
		for (MeshObjectToDraw curMeshObject: meshObjects) {
			// if the display type has changed change the opengl state
			if (curMeshObject.getDisplayType()!=currentDisplayType) {
				currentDisplayType = curMeshObject.getDisplayType();
				switch (currentDisplayType) {
					case TRANSPARENT_ONE:	GLES20.glEnable(GLES20.GL_BLEND);
											GLES20.glDepthMask(false);
											GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);
											break;
					case SOLID:				GLES20.glDepthMask(true);
											GLES20.glDisable(GLES20.GL_BLEND);
					case TEXT:
					case TRANSPARENT:
											break;
				}
			}
			
			// if the texture is different to the current texture bind the texture
			if (!(curMeshObject.getMeshObject().getTextureName().equals(currentTexture))) {
				currentTexture = curMeshObject.getMeshObject().getTextureName();
				graphicsLibrary.bindTexture(currentTexture);
			}
			
			passMatrices(shaders.get("NaiveShader"), curMeshObject.getModelMatrix());

			// pass the material information to the shader
			Material curMaterial = graphicsLibrary.getMaterialObject(curMeshObject.getMeshObject().getMaterialIndex());
			
			GLES20.glUniform3fv(currentShader.getLocation("material_Emission"),3,curMaterial.getEmission().getAsArray(),0);
			if (curMeshObject.useColorObject()) {
				GLES20.glUniform3fv(currentShader.getLocation("material_Ambient"),3,curMeshObject.getObjectColor().getVec3().getAsArray(),0);
				GLES20.glUniform3fv(currentShader.getLocation("material_Diffuse"),3,curMeshObject.getObjectColor().getVec3().getAsArray(),0);
				GLES20.glUniform1f(currentShader.getLocation("material_Alpha"), curMeshObject.getObjectColor().getW());
			} else {
				GLES20.glUniform3fv(currentShader.getLocation("material_Ambient"),3,curMaterial.getAmbient().getAsArray(),0);
				GLES20.glUniform3fv(currentShader.getLocation("material_Diffuse"),3,curMaterial.getDiffuse().getAsArray(),0);
			}
			GLES20.glUniform3fv(currentShader.getLocation("material_Specular"),3,curMaterial.getSpecular().getAsArray(),0);
			GLES20.glUniform1f(currentShader.getLocation("material_Shininess"),curMaterial.GetSpecularWeight());
			
			// render the mesh (vertices/normals/texturecoords)
			graphicsLibrary.getVBOObject(curMeshObject.getMeshObject().getVBOIndex()).renderVBO(
												currentShader.getAttribute("position"),
												currentShader.getAttribute("normal"),
												currentShader.getAttribute("textureCoords"));
		}
		GLES20.glDepthMask(true);
		GLES20.glDisable(GLES20.GL_BLEND);
	}
	
	public void setProjection(int width, int height) {
		if ((width!=windowDimensions.getWidth()) | (height!=windowDimensions.getHeight())) {
			// Set the OpenGL viewport to the same size as the surface.
			GLES20.glViewport(0, 0, width, height);
			windowDimensions.setWidth(width);
			windowDimensions.setHeight(height);
			windowDimensions.setProjectionMatrix();
		}
	}
}
