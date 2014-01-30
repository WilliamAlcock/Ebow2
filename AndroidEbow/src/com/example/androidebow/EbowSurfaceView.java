package com.example.androidebow;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class EbowSurfaceView extends GLSurfaceView {
	
	private final EbowRenderer mRenderer;
	
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private float previousX;
    private float previousY;
    private boolean moveBuffer[] = new boolean[6];
	
	public EbowSurfaceView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		
		mRenderer = new EbowRenderer((EbowActivity)context,this);
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float currentX = e.getX();
        float currentY = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
            	
                float dx = currentX - previousX;
                float dy = currentY - previousY;
                
    			
    			if (dy<0) {
    				moveBuffer[0]=true;
    			} else if (dy>0) {
    				moveBuffer[1]=true;
    			}
    			if (dx>0) {
    				moveBuffer[2]=true;
    			} else if (dx<0) {
    				moveBuffer[3]=true;
    			} else {
    				moveBuffer[4]=true;
    			}
    			
//                mRenderer.setXAngle(mRenderer.getXAngle() + dx * TOUCH_SCALE_FACTOR);  // = 180.0f / 320
 //               mRenderer.setYAngle(mRenderer.getYAngle() + dy * TOUCH_SCALE_FACTOR);
  //              requestRender();
        }		
        
        previousX = currentX;
        previousY = currentY;
        		
        return true;
    }
	
	public boolean getMoveBuffer(int i) {
		return moveBuffer[i];
	}
	
	public void resetMoveBuffer(int i) {
		moveBuffer[i] = false;
	}
}
