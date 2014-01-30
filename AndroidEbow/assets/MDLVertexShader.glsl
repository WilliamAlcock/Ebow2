uniform mat4 u_MVPMatrix;		// A constant representing the combined model/view/projection matrix.      		       
uniform mat4 u_MVMatrix;		// A constant representing the combined model/view matrix.       	

attribute vec4 position;
attribute vec3 normal;
attribute vec2 textureCoords;

varying vec3 fPosition;
varying vec3 fNormal;
varying vec2 fTextureCoords;

void main() {
	fPosition = vec3(u_MVMatrix * position);
	fTextureCoords = textureCoords;
	fNormal = vec3(u_MVMatrix * vec4(normal,0.0));
	gl_Position = u_MVPMatrix * position;     
}