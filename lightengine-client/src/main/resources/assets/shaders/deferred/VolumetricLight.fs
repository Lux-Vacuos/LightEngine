//
// This file is part of Light Engine
//
// Copyright (C) 2016-2018 Lux Vacuos
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

in vec2 textureCoords;

out vec4 out_Color;

uniform vec3 cameraPosition;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform mat4 projectionLightMatrix[4];
uniform mat4 viewLightMatrix;
uniform mat4 biasMatrix;
uniform vec3 lightPosition;
uniform float time;
uniform sampler2DShadow shadowMap[4];

uniform int useShadows;
uniform int useVolumetricLight;

#include function computeShadow

#include function random

#include variable GLOBAL

#define VOLUMETRIC_MULT 0.08
#define VOLUMETRIC_SUN 1.0

void main() {
	if (useVolumetricLight == 1 && useShadows == 1) {
		vec4 position = texture(gPosition, textureCoords);
		vec3 normal = texture(gNormal, textureCoords).rgb;

		vec3 cameraToWorld = position.xyz - cameraPosition;
		float cameraToWorldDist = length(cameraToWorld);
		vec3 cameraToWorldNorm = normalize(cameraToWorld);
		vec3 L = normalize(lightPosition);
		vec3 N = normalize(normal);

		vec3 rayTrace = cameraPosition;
		float rayDist, incr = 0.2;
		float rays;
		float bias = max(0.1 * (1.0 - dot(N, L)), 0.005);
		int itr;
		vec3 randSample, finalTrace;
		do {
			rayTrace += cameraToWorldNorm * incr;
			incr *= 1.05;

			randSample =
				vec3(random(rayTrace.x), random(rayTrace.y), random(rayTrace.z)) * 0.25 - 0.125;
			finalTrace = rayTrace + randSample;
			rayDist = length(finalTrace - cameraPosition);
			if (rayDist > cameraToWorldDist - bias)
				break;
			itr++;

			rays += computeShadow(finalTrace);
			if (rayDist > MAX_DISTANCE_VOLUME)
				break;
		} while (rayDist < cameraToWorldDist);
		rays /= itr;
		rays = max(rays * VOLUMETRIC_MULT, 0.0) *
			   (1.0 + smoothstep(0, 0.5, dot(cameraToWorldNorm, L) - 0.5) * VOLUMETRIC_SUN);
		out_Color = vec4(rays, 0, 0, 0);
	} else {
		out_Color = vec4(0.0);
	}
}