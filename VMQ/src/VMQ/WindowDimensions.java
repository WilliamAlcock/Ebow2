package VMQ;

import java.util.Observable;


public class WindowDimensions extends Observable{
	
	private float width;
	private float height;
	private float fovy;
	private float zFar;
	private float zNear;
	private Mat4x4 projectionMatrix;
	private Mat4x4 viewMatrix;
	
	private Vec3 eye;
	private Vec3 center;
	private Vec3 up;
	
	public WindowDimensions(float width,float height,float fovy,float zNear,float zFar,Vec3 eye,Vec3 center,Vec3 up) {
		this.width = width;
		this.height = height;
		this.fovy = fovy;
		this.zFar = zFar;
		this.zNear = zNear;
		setProjectionMatrix();
		
		this.eye = eye;
		this.center = center;
		this.up = up;
		setViewMatrix();
	}
	
	public Vec3 getEye() {
		return eye;
	}
	
	public void setEye(Vec3 eye) {
		this.eye = eye;
	}
	
	public Vec3 getCenter() {
		return center;
	}
	
	public void setCenter(Vec3 center) {
		this.center = center;
	}
	
	public Vec3 getUp() {
		return up;
	}
	
	public void setUp(Vec3 up) {
		this.up = up;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setFovy(float fovy) {
		this.fovy = fovy;
	}
	
	public void setZFar(float zFar) {
		this.zFar = zFar;
	}
	
	public void setZNear(float zNear) {
		this.zNear = zNear;
	}
	
	public float getZFar() {
		return zFar;
	}
	
	public float getZNear() {
		return zNear;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public float getFovyDeg() {
		return fovy;
	}
	
	public float getFovyRad() {
		return (float) Math.toRadians(fovy);
	}
	
	public float getAspect() {
		return (float) width/height;
	}
	
	public float getMaxXPos(float zCalc) {
//		System.out.println("Max X "+(getMaxYPos(zCalc)*(4.0f/3.0f)));
//		return (float) (getMaxYPos(zCalc)*(4.0f/3.0f));
		System.out.println("Max X "+(zCalc*Math.tan(getFovyRad()/2)*getAspect()));
		System.out.println("ASPECT "+getAspect());
		return (float) (zCalc*Math.tan(getFovyRad()/2)*getAspect());
	}
	
	public float getMaxYPos(float zCalc) {
		System.out.println("Max Y "+(float) (zCalc*Math.tan(getFovyRad()/2)));
		return (float) (zCalc*Math.tan(getFovyRad()/2));		
	}
	
	public void setViewMatrix() {
		//f
		Vec3 f = center.sub(eye);
		f = f.normalize();
		//s
		Vec3 s = f.crossProduct(up);
		s = s.normalize();
		//u
		Vec3 u = s.crossProduct(f);

		float[] viewMatrix = new float[16];
		
		viewMatrix[0] = s.getX();
		viewMatrix[1] = u.getX();
		viewMatrix[2] = -f.getX();
		viewMatrix[3] = 0.0f;
		
		viewMatrix[4] = s.getY();
		viewMatrix[5] = u.getY();
		viewMatrix[6] = -f.getY();
		viewMatrix[7] = 0.0f;
		
		viewMatrix[8] = s.getZ();
		viewMatrix[9] = u.getZ();
		viewMatrix[10] = -f.getZ();
		viewMatrix[11] = 0.0f;
		
		viewMatrix[12] = 0.0f;
		viewMatrix[13] = 0.0f;
		viewMatrix[14] = 0.0f;
		viewMatrix[15] = 1.0f;
		
		for (int i=0 ; i<4 ; i++) {	
			viewMatrix[12+i] += viewMatrix[i] * -eye.getX() + viewMatrix[4 + i] * -eye.getY() + viewMatrix[8 + i] * -eye.getZ();
		}
		this.viewMatrix = new Mat4x4(viewMatrix);
	}		
	
	public Mat4x4 getViewMatrix() {
		return viewMatrix;
	}
	
	public void setProjectionMatrix() {
		float f = (float) (1/Math.tan(getFovyRad()/2));
		float g = (zFar+zNear)/(zNear-zFar);
		float h = (2*zFar*zNear)/(zNear-zFar);
		// Column matrix
		this.projectionMatrix = new Mat4x4(new float[] {f/getAspect(),0,0,0,
														0,f,0,0,
														0,0,g,-1,
														0,0,h,0});
//		Matrix.frustumM(projectionMatrix, 0, -getAspect(), getAspect(), -1, 1, 1, 100);
		this.setChanged();
		this.notifyObservers();
	}
	
	public Mat4x4 getProjectionMatrix() {
		return projectionMatrix;
	}
}