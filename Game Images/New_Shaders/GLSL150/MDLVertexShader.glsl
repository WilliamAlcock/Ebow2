#version 150 core

uniform mat4 u_MVPMatrix;		// A constant representing the combined model/view/projection matrix.      		       
uniform mat4 u_MVMatrix;		// A constant representing the combined model/view matrix.       	

in vec3 position;
in vec3 normal;
in vec2 textureCoords;

out vec3 fPosition;
out vec3 fNormal;
out vec2 fTextureCoords;

void main(void) {
	fPosition = vec3(u_MVMatrix * position);
	fTextureCoords = textureCoords;
	fNormal = vec3(u_MVMatrix * vec4(normal,0.0));
	gl_Position = u_MVPMatrix * position;   
}