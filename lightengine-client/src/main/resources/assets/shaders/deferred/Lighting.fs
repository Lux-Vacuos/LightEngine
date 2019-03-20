//
// This file is part of Light Engine
//
// Copyright (C) 2016-2019 Lux Vacuos
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
uniform vec3 lightPosition;
uniform sampler2D gDiffuse;
uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gPBR; // R = roughness, G = metallic
uniform sampler2D gMask;
uniform sampler2D gDepth;
uniform sampler2D composite0;
uniform samplerCube composite1;
uniform samplerCube composite2;
uniform sampler2D composite3;
uniform int useAmbientOcclusion;
uniform int useShadows;
uniform int useReflections;
uniform vec2 resolution;
uniform mat4 projectionLightMatrix[4];
uniform mat4 viewLightMatrix;
uniform mat4 biasMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform sampler2DShadow shadowMap[4];

#include variable GLOBAL

#include variable pi

#include function DistributionGGX

#include function GeometrySchlickGGX

#include function GeometrySmith

#include function fresnelSchlick

#include function fresnelSchlickRoughness

#include function computeAmbientOcclusion

#include function computeShadow

// Linear depth
// float zndc = texture(gDepth, textureCoords).r;
// float A = projectionMatrix[2][2];
// float B = projectionMatrix[3][2];
// float zeye = B / (A + zndc);

float computeContactShadows(vec3 pos, vec3 N, vec3 L) {
	float rayDist, posDist, incr = 0.005, rayDistToObject, shadow = 1;
	vec3 newPos, rayTrace, finalRayTrace, newPosRel, dirTraceToNew;
	vec4 newScreen;
	bool wasNegative = false;
	int itr;
	if (dot(N, L) >= 0)
		do {

			rayTrace += L * incr;
			finalRayTrace = rayTrace + pos;

			newScreen = viewMatrix * vec4(finalRayTrace, 1);
			newScreen = projectionMatrix * newScreen;
			newScreen /= newScreen.w;

			newPos = texture(gPosition, newScreen.xy / 2.0 + 0.5).xyz;
			rayDist = length(rayTrace);
			newPosRel = newPos - pos;
			posDist = length(newPosRel);

			if (newScreen.x > 1 || newScreen.x < -1 || newScreen.y > 1 || newScreen.y < -1 ||
				newScreen.z > 1 || newScreen.z < -1 || rayDist > 0.5)
				break;

			dirTraceToNew = normalize(newPosRel - rayTrace);

			if (rayDist <= posDist && dot(dirTraceToNew, L) > 0)
				wasNegative = true;

			rayDistToObject = length(newPos - finalRayTrace);
			if (rayDist > posDist && wasNegative && rayDistToObject < 0.020) {
				shadow = 0;
				break;
			}
		} while (true);
	return shadow;
}

void main() {
	vec4 mask = texture(gMask, textureCoords);
	vec4 image = texture(gDiffuse, textureCoords);
	if (mask.a != 1) {
		vec2 pbr = texture(gPBR, textureCoords).rg;
		vec3 position = texture(gPosition, textureCoords).rgb;
		vec3 normal = texture(gNormal, textureCoords).rgb;
		float roughness = pbr.r;
		float metallic = pbr.g;

		vec3 N = normalize(normal);
		vec3 V = normalize(cameraPosition - position);
		vec3 R = reflect(-V, N);
		vec3 L = normalize(lightPosition);
		vec3 H = normalize(V + L);

		vec3 F0 = vec3(0.04);
		F0 = mix(F0, image.rgb, metallic);

		vec3 Lo = vec3(0.0);
		vec3 radiance = vec3(1.0);

		float NDF = DistributionGGX(N, H, roughness);
		float G = GeometrySmith(N, V, L, roughness);
		vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);

		vec3 nominator = NDF * G * F;
		float denominator = max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.001;
		vec3 brdf = nominator / denominator;

		vec3 kS = F;
		vec3 kD = 1.0 - kS;
		kD *= 1.0 - metallic;

		float NdotL =
			max(dot(N, L), 0.0) * computeShadow(position) * computeContactShadows(position, N, L);
		Lo += (kD * image.rgb / PI + brdf) * radiance * NdotL;

		F = fresnelSchlickRoughness(max(dot(N, V), 0.0), F0, roughness);

		kS = F;
		kD = 1.0 - kS;
		kD *= 1.0 - metallic;

		vec3 irradiance = texture(composite1, N).rgb;
		vec3 diffuse = irradiance * image.rgb;

		vec3 prefilteredColor = textureLod(composite2, R, roughness * MAX_REFLECTION_LOD).rgb;
		vec2 envBRDF = texture(composite3, vec2(max(dot(N, V), 0.0), roughness)).rg;
		vec3 specular = prefilteredColor * (F * envBRDF.x + envBRDF.y);

		vec3 ambient = kD * diffuse + specular;
		float ao = computeAmbientOcclusion(position, N);

		if (useReflections != 1) { // Using SSR
			ambient *= ao;
		}

		vec3 emissive = texture(gMask, textureCoords).rgb;
		vec3 color = ambient + emissive + Lo;
		image.rgb = color;
	}
	vec4 vol = texture(composite0, textureCoords);
	image.rgb += vol.rgb;
	out_Color = image;
}