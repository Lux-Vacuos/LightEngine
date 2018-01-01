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

#version 330 core

in vec2 textureCoords;

out vec4 out_Color;

uniform vec3 cameraPosition;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gMask;
uniform sampler2D gPBR;
uniform sampler2D gDiffuse;
uniform sampler2D composite0;
uniform samplerCube composite2;
uniform sampler2D composite3;
uniform sampler2D gDepth;

uniform int useReflections;

#include variable GLOBAL

#include function fresnelSchlickRoughness

void main(void) {
	vec2 texcoord = textureCoords;
	vec4 image = texture(composite0, texcoord);
	vec4 mask = texture(gMask, texcoord);
	if (mask.a != 1) {
		if (useReflections == 1) {
			vec4 diffuse = texture(gDiffuse, textureCoords);
			vec2 pbr = texture(gPBR, textureCoords).rg;
			vec3 position = texture(gPosition, textureCoords).rgb;
			vec3 normal = texture(gNormal, textureCoords).rgb;

			vec3 N = normalize(normal);
			vec3 V = normalize(cameraPosition - position);
			vec3 R = reflect(-V, N);

			float roughness = pbr.r;
			float metallic = pbr.g;

			vec3 F0 = vec3(0.04);
			F0 = mix(F0, diffuse.rgb, metallic);
			vec3 F = fresnelSchlickRoughness(max(dot(N, V), 0.0), F0, roughness);

			vec3 prefilteredColor = textureLod(composite2, R, roughness * MAX_REFLECTION_LOD).rgb;
			vec2 envBRDF = texture(composite3, vec2(max(dot(N, V), 0.0), roughness)).rg;
			vec3 specular = prefilteredColor * (F * envBRDF.x + envBRDF.y);

			float depth = texture(gDepth, texcoord).r;
			vec3 cameraToWorld = position - cameraPosition.xyz;
			float cameraToWorldDist = length(cameraToWorld);
			vec3 cameraToWorldNorm = normalize(cameraToWorld);
			vec3 refl = normalize(reflect(cameraToWorldNorm, N));
			vec3 newPos;
			vec4 newScreen;
			vec3 rayTrace = position;
			float currentWorldDist, rayDist;
			float incr = 0.4;
			do {
				rayTrace += refl * incr;
				incr *= 1.2;
				newScreen = viewMatrix * vec4(rayTrace, 1);
				newScreen = projectionMatrix * newScreen;
				newScreen /= newScreen.w;
				newPos = texture(gPosition, newScreen.xy / 2.0 + 0.5).xyz;
				currentWorldDist = length(newPos.xyz - cameraPosition);
				rayDist = length(rayTrace - cameraPosition);
				if (newScreen.x > 1 || newScreen.x < -1 || newScreen.y > 1 || newScreen.y < -1 ||
					newScreen.z > 1 || newScreen.z < -1 || cameraToWorldDist > currentWorldDist ||
					dot(refl, cameraToWorldNorm) < 0 || rayDist > MAX_DISTANCE_REFLECTION)
					break;
			} while (rayDist < currentWorldDist);

			vec4 newColor = texture(composite0, newScreen.xy / 2.0 + 0.5);
			for (int i = -4; i < 4; i++) {
				for (int j = -4; j < 4; j++) {
					newColor += texture(composite0,
										newScreen.xy / 2.0 + 0.5 + vec2(j, i) * roughness * 0.05);
				}
			}
			newColor /= 65.0;
			float fact = 1.0;
			if (dot(refl, cameraToWorldNorm) < 0)
				fact = 0.0;
			else if (newScreen.x > 1 || newScreen.x < -1 || newScreen.y > 1 || newScreen.y < -1)
				fact = 0.0;
			else if (cameraToWorldDist > currentWorldDist)
				fact = 0.0;
			else if (newScreen.z < -1)
				fact = 0.0;
			else if (rayDist > MAX_DISTANCE_REFLECTION)
				fact = 0.0;
			image.rgb -= max(specular, 0.0);
			image.rgb +=
				mix(max(specular, 0.0), max(newColor.rgb * (F * envBRDF.x + envBRDF.y), 0.0), fact);
		}
	}
	out_Color = image;
}