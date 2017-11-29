//
// This file is part of Light Engine
// 
// Copyright (C) 2016-2017 Lux Vacuos
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
// 
//

#version 330 core

in vec2 textureCoords;

out vec4 out_Color;

uniform vec3 cameraPosition;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform int useShadows;
uniform mat4 projectionLightMatrix[4];
uniform mat4 viewLightMatrix;
uniform mat4 biasMatrix;
uniform vec3 lightPosition;
uniform sampler2DShadow shadowMap[4];

uniform int useVolumetricLight;

##include function computeShadow

#define VOLUMETRIC_MULT 0.05
#define VOLUMETRIC_SUN 0.8

void main() {
	if(useVolumetricLight == 1) {
		vec4 position = texture(gPosition, textureCoords);
		vec3 normal = texture(gNormal, textureCoords).rgb;

		vec3 cameraToWorld = position.xyz - cameraPosition;
    	float cameraToWorldDist = length(cameraToWorld);
		vec3 cameraToWorldNorm = normalize(cameraToWorld);
		vec3 L = normalize(lightPosition);
		vec3 N = normalize(normal);

		vec4 image;
    	vec3 rayTrace = cameraPosition;
    	float rayDist;
    	float incr = 0.1;
		float bias = max(0.1 * (1.0 - dot(N, L)), 0.005);
		int itr;
		do {
			rayTrace += cameraToWorldNorm * incr;
			incr *= 1.1;
       		rayDist = length(rayTrace - cameraPosition);
			if(rayDist > cameraToWorldDist - bias)
				break;
			itr++;
			image += vec4(computeShadow(rayTrace));
   		} while(rayDist < cameraToWorldDist);
		image /= itr;
		out_Color = max(image * VOLUMETRIC_MULT, 0.0) * (1 + smoothstep(0, 0.5, dot(cameraToWorldNorm, L) - 0.5) * VOLUMETRIC_SUN);
	} else {
		out_Color = vec4(0.0);
	}
}