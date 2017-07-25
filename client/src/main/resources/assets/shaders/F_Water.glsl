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

in vec3 passPositionOut;
in vec4 clipSpaceOut;
in vec3 normal;
in vec2 textureCoordsOut;

out vec4 outColor;

uniform vec3 cameraPosition;
uniform samplerCube reflection;
uniform sampler2D refraction;
uniform sampler2D dudv;
uniform sampler2D depth;
uniform float time;

const float near = 0.1;
const float far = 1000.0;

float fresnelSchlickRoughness(float cosTheta, float F0, float roughness) {
    return F0 + (max(1.0 - roughness, F0) - F0) * pow(1.0 - cosTheta, 5.0);
}

void main(void) {

	vec2 ndc = (clipSpaceOut.xy / clipSpaceOut.w) / 2.0 + 0.5;

	float dist = texture(depth, ndc).r;
	float floorDistance = 2.0 * near * far / (far + near - (2.0 * dist - 1.0) * (far - near));

	dist = gl_FragCoord.z;
	float waterDistance = 2.0 * near * far / (far + near - (2.0 * dist - 1.0) * (far - near));
	float waterDepth = floorDistance - waterDistance;

	vec2 distortion1 = (texture(dudv, vec2(textureCoordsOut.x + time / 40, textureCoordsOut.y + time / 20)).rg * 2.0 - 1.0) * 0.02;
	vec2 distortion2 = (texture(dudv, vec2(textureCoordsOut.x + time / 20, textureCoordsOut.y + time / 30)).rg * 2.0 - 1.0) * 0.02;
	vec2 distortion = distortion1 + distortion2;

	vec3 N = normalize(normal);
	vec3 V = normalize(cameraPosition - passPositionOut);
	vec3 R = reflect(-V, N);

	float F0 = 0.04;
	float F = fresnelSchlickRoughness(max(dot(N, V), 0.0), F0, 0);

	vec3 reflectionColor = texture(reflection, R + (distortion.x, 0, distortion.y)).rgb;

	vec4 refractV = clipSpaceOut;
	refractV.xyz /= refractV.w;
	refractV.x = 0.5f*refractV.x + 0.5f;
	refractV.y = 0.5f*refractV.y + 0.5f;
	refractV.z = .1f / refractV.z;
	vec3 refractionColor = texture(refraction, refractV.xy - refractV.z * N.xz + distortion).rgb;
	outColor.rgb = mix(reflectionColor, refractionColor, 1.0 - F);
	outColor.a = 1;
	if(waterDepth < 1) {
		//outColor.rgb = mix(vec3(1.0), outColor.rgb, 1 - smoothstep(0, 1, waterDepth));
		outColor.a = smoothstep(0, 1, waterDepth);
	}
}