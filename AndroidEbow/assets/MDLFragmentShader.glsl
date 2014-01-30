precision mediump float;

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

void main() 
{
	// Get the color from the texture	
	vec4 color = texture2D(texture_color, fTextureCoords.xy);

	// Will be used for attenuation.
    // float distance = length(diffuse_Light_Position - fPosition);                  
	
	// Get a lighting direction vector from the light to the vertex.
    vec3 lightVector = normalize(diffuse_Light_Position - fPosition);
    
    // Calculate the half vector from the light direction and eye direction
	vec3 halfVector = normalize(lightVector + eye_Direction);              	

	// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
	// pointing in the same direction then it will get max illumination.
	float diffuse = max(0.0, dot(fNormal, lightVector));
	float specular = max(0.0, dot(fNormal, halfVector));               	  		  													  

	// Add attenuation. 
    // diffuse = diffuse * (1.0 / distance);
    
    // surfaces facing away from the light (negative dot products)
	// wont be lit by the directional light
	if (diffuse == 0.0)
		specular = 0.0;
	else
		specular = pow(specular, material_Shininess) * diffuse_Light_Strength;	// sharpen the highlight
    
	vec3 scatteredLight = ambient_Light_Color * material_Ambient + diffuse_Light_Color * material_Diffuse * diffuse;
	vec3 reflectedLight = diffuse_Light_Color * material_Specular * specular;
	
	// dont modulate the underlying color with reflected light
	// only with scattered light + reflectedLight
	
	vec3 rgb = min(material_Emission + color.rgb * scatteredLight + reflectedLight, vec3(1.0));
	
	if (material_Alpha > 0.0)
		color.a = color.a * material_Alpha;
	
	gl_FragColor = vec4(rgb,color.a);
}