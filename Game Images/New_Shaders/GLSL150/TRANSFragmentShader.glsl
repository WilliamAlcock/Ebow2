#version 150 core

uniform sampler2D texture_color;

uniform vec3 ambient_Light_Color;
uniform vec3 diffuse_Light_Color;
uniform vec3 diffuse_Light_Position;
uniform float diffuse_Light_Strength;
uniform vec3 eye_Direction;

uniform vec3 material_Emission;
uniform vec3 material_Ambient;
uniform vec3 material_Diffuse;
uniform vec3 material_Specular;
uniform float material_Shininess;
uniform float material_Alpha;

varying vec3 fPosition;
varying vec3 fNormal;
varying vec2 fTextureCoords;

out vec4 FragColor;

void main(void) 
{
	// Get the color from the texture	
	vec4 color = texture(texture_color, fTextureCoords.xy);
	
	// Calculate the half vector from the light direction and eye direction
	vec3 halfVector = normalize(diffuse_Light_Direction + eye_Direction);
	
	// compute cosine of the directions, using dot products,
	// to see how much light would be reflected
	float diffuse = max(0.0, dot(fNormal, diffuse_Light_Direction));
	float specular = max(0.0, dot(fNormal, halfVector));
	
	// surfaces facing away from the light (negative dot products)
	// wont be lit by the directional light
	if (diffuse == 0.0)
		specular = 0.0;
	else
		specular = pow(specular, material_Shininess) * diffuse_Light_Strength;	// sharpen the highlight
		
	vec3 scatteredLight = ambient_Light_Color * object_Color.rgb + diffuse_Light_Color * object_Color.rgb * diffuse;
	vec3 reflectedLight = diffuse_Light_Color * material_Specular * specular;
	
	// dont modulate the underlying color with reflected light
	// only with scattered light
	
	vec3 rgb = min(material_Emission + color.rgb * scatteredLight , vec3(1.0));
	
	FragColor = vec4(rgb ,color.a * object_Color.a);
}