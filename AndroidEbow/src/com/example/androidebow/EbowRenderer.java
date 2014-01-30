package com.example.androidebow;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import GameEngine.GameEngine;
import VMQ.WindowDimensions;
import VMQ.Vec3;
import android.opengl.GLSurfaceView;

public class EbowRenderer implements GLSurfaceView.Renderer {
	/** Used for debug logs. */
	private static final String TAG = "Ebow Renderer";
	
	private final EbowActivity ebowActivity;
	private final EbowSurfaceView mGLSurfaceView;
	private Display display;
	private GameEngine gameEngine;	
	private WindowDimensions windowDimensions;
	
	/** Android's OpenGL bindings are broken until Gingerbread, so we use LibGDX bindings here. */
//	private final AndroidGL20 mGlEs20;
	
	private float mDeltaX;
	private float mDeltaY;
	
	public EbowRenderer(final EbowActivity ebowActivity, final EbowSurfaceView glSurfaceView) {
		this.ebowActivity = ebowActivity;	
		this.mGLSurfaceView = glSurfaceView;
	}
	
	@Override
	public void onDrawFrame(GL10 arg0) {
		checkKeyBuffer();
		gameEngine.tick();
		display.tick();
	}

	@Override
	public void onSurfaceChanged(GL10 arg0, int width, int height) {
		display.setProjection(width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		windowDimensions = new WindowDimensions(800,1097,45.0f,1.0f,100.0f,new Vec3(0,80,0),new Vec3(0,0,0),new Vec3(0,0,1));
		this.display = new Display(mGLSurfaceView,windowDimensions);
		this.gameEngine = new GameEngine(display.getObjectDimensions(),windowDimensions);
		display.setBackgroundObjects(gameEngine.getBackgroundToDisplay());
		display.setEnemyObjects(gameEngine.getEnemysToDisplay());
		display.setPlayerObjects(gameEngine.getPlayersToDisplay());
		display.setParticleObjects(gameEngine.getParticlesToDisplay());
		display.setHUDObjects(gameEngine.getHUDObjects());
	}
	
    /**
     * Returns the rotation angle 
     *
     * @return - A float representing the rotation angle.
     */
    public float getXAngle() {
        return mDeltaX;
    }
    
    public float getYAngle() {
    	return mDeltaY;
    }
    
    public void checkKeyBuffer() {
		if (gameEngine!=null) { 
			for (int i=0;i<6;i++) {
				if (mGLSurfaceView.getMoveBuffer(i)) {
					gameEngine.setPlayerMove(i);
					mGLSurfaceView.resetMoveBuffer(i);
				}
			}
		} else {
			System.err.println("Game engine not set");
		}
    }
    
}


