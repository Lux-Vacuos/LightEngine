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
uniform mat4 projectionLightMatrix[4];
uniform mat4 viewLightMatrix;
uniform mat4 biasMatrix;
uniform vec3 lightPosition;
uniform float time;
uniform sampler2DShadow shadowMap[4];

uniform int useShadows;
uniform int useVolumetricLight;

#include function computeShadow

#include variable GLOBAL

vec3 permute(vec3 x) {
	return mod(((x * 34.0) + 1.0) * x, 289.0);
}

float snoise(vec2 v) {
	const vec4 C =
		vec4(0.211324865405187, 0.366025403784439, -0.577350269189626, 0.024390243902439);
	vec2 i = floor(v + dot(v, C.yy));
	vec2 x0 = v - i + dot(i, C.xx);
	vec2 i1;
	i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
	vec4 x12 = x0.xyxy + C.xxzz;
	x12.xy -= i1;
	i = mod(i, 289.0);
	vec3 p = permute(permute(i.y + vec3(0.0, i1.y, 1.0)) + i.x + vec3(0.0, i1.x, 1.0));
	vec3 m = max(0.5 - vec3(dot(x0, x0), dot(x12.xy, x12.xy), dot(x12.zw, x12.zw)), 0.0);
	m = m * m;
	m = m * m;
	vec3 x = 2.0 * fract(p * C.www) - 1.0;
	vec3 h = abs(x) - 0.5;
	vec3 ox = floor(x + 0.5);
	vec3 a0 = x - ox;
	m *= 1.79284291400159 - 0.85373472095314 * (a0 * a0 + h * h);
	vec3 g;
	g.x = a0.x * x0.x + h.x * x0.y;
	g.yz = a0.yz * x12.xz + h.yz * x12.yw;
	return 130.0 * dot(m, g);
}

#define VOLUMETRIC_MULT 0.025
#define VOLUMETRIC_SUN 1.0

#define CLOUD_BOTTOM 300
#define CLOUD_TOP 400
#define CLOUD_MULT 16
#define CLOUD_DIFF 80

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
		float rayDist, perl, incr = 0.2;
		float rays, clouds;
		float bias = max(0.1 * (1.0 - dot(N, L)), 0.005);
		int itr;
		do {
			rayTrace += cameraToWorldNorm * incr;
			incr *= 1.05;
			rayDist = length(rayTrace - cameraPosition);
			if (rayDist > cameraToWorldDist - bias)
				break;
			itr++;
			rays += computeShadow(rayTrace);
			perl = max(snoise(rayTrace.xz * 0.001 + vec2(time * 0.5, time * 0.1) * 0.005), 0.0) *
				   CLOUD_MULT;
			clouds += perl * smoothstep(CLOUD_BOTTOM, CLOUD_TOP - CLOUD_DIFF, rayTrace.y) *
					  (1 - smoothstep(CLOUD_BOTTOM + CLOUD_DIFF, CLOUD_TOP, rayTrace.y));
			if (rayDist > MAX_DISTANCE_VOLUME)
				break;
		} while (rayDist < cameraToWorldDist);
		rays /= itr;
		clouds /= itr;
		rays = max(rays * VOLUMETRIC_MULT, 0.0) *
			   (1.0 + smoothstep(0, 0.5, dot(cameraToWorldNorm, L) - 0.5) * VOLUMETRIC_SUN);
		out_Color = vec4(rays, max(clouds, 0.0), 0, 0);
	} else {
		out_Color = vec4(0.0);
	}
}