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

#function DistributionGGX
float DistributionGGX(vec3 N, vec3 H, float roughness) {
	float a = roughness * roughness;
	float a2 = a * a;
	float NdotH = max(dot(N, H), 0.0);
	float NdotH2 = NdotH * NdotH;

	float nom = a2;
	float denom = (NdotH2 * (a2 - 1.0) + 1.0);
	denom = PI * denom * denom;

	return nom / denom;
}
#end

#function GeometrySchlickGGX
float GeometrySchlickGGX(float NdotV, float roughness) {
	float r = (roughness + 1.0);
	float k = (r * r) / 8.0;

	float nom = NdotV;
	float denom = NdotV * (1.0 - k) + k;

	return nom / denom;
}
#end

#function GeometrySmith
float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
	float NdotV = max(dot(N, V), 0.0);
	float NdotL = max(dot(N, L), 0.0);
	float ggx2 = GeometrySchlickGGX(NdotV, roughness);
	float ggx1 = GeometrySchlickGGX(NdotL, roughness);

	return ggx1 * ggx2;
}
#end

#function fresnelSchlickRoughness
vec3 fresnelSchlickRoughness(float cosTheta, vec3 F0, float roughness) {
	return F0 + (max(vec3(1.0 - roughness), F0) - F0) * pow(1.0 - cosTheta, 5.0);
}
#end

#function computeAmbientOcclusion
float computeAmbientOcclusion(vec3 position, vec3 normal) {
	if (useAmbientOcclusion == 1) {
		float ambientOcclusion = 0;
		vec2 filterRadius = vec2(10 / resolution.x, 10 / resolution.y);
		for (int i = 0; i < sample_count; ++i) {
			vec2 sampleTexCoord = textureCoords + (poisson16[i] * (filterRadius));
			float sampleDepth = texture(gDepth, sampleTexCoord).r;
			vec3 samplePos = texture(gPosition, sampleTexCoord).rgb;
			vec3 sampleDir = normalize(samplePos - position);
			float NdotS = max(dot(normal, sampleDir), 0);
			float VPdistSP = distance(position, samplePos);
			float a = 1.0 - smoothstep(distanceThreshold, distanceThreshold * 2, VPdistSP);
			float b = NdotS;
			ambientOcclusion += (a * b) * 1.3;
		}
		return -(ambientOcclusion / sample_count) + 1;
	} else
		return 1.0;
}
#end

#function computeShadow

vec4 ShadowCoord[4];

vec2 multTex[4];

float lookup(vec2 offset) {
	if (ShadowCoord[3].x > 0 && ShadowCoord[3].x < 1 && ShadowCoord[3].y > 0 &&
		ShadowCoord[3].y < 1) {
		if (ShadowCoord[2].x > 0 && ShadowCoord[2].x < 1 && ShadowCoord[2].y > 0 &&
			ShadowCoord[2].y < 1) {
			if (ShadowCoord[1].x > 0 && ShadowCoord[1].x < 1 && ShadowCoord[1].y > 0 &&
				ShadowCoord[1].y < 1) {
				if (ShadowCoord[0].x > 0 && ShadowCoord[0].x < 1 && ShadowCoord[0].y > 0 &&
					ShadowCoord[0].y < 1) {
					offset *= multTex[0];
					return texture(shadowMap[0], ShadowCoord[0].xyz + vec3(offset.x, offset.y, 0));
				}
				offset *= multTex[1];
				return texture(shadowMap[1], ShadowCoord[1].xyz + vec3(offset.x, offset.y, 0));
			}
			offset *= multTex[2];
			return texture(shadowMap[2], ShadowCoord[2].xyz + vec3(offset.x, offset.y, 0));
		}
		offset *= multTex[3];
		return texture(shadowMap[3], ShadowCoord[3].xyz + vec3(offset.x, offset.y, 0));
	}
	return 1.0;
}

float computeShadow(vec3 position) {
	if (useShadows == 1) {
		float shadow = 0.0;
		vec4 posLight = viewLightMatrix * vec4(position, 1.0);
		ShadowCoord[0] = biasMatrix * (projectionLightMatrix[0] * posLight);
		ShadowCoord[1] = biasMatrix * (projectionLightMatrix[1] * posLight);
		ShadowCoord[2] = biasMatrix * (projectionLightMatrix[2] * posLight);
		ShadowCoord[3] = biasMatrix * (projectionLightMatrix[3] * posLight);
		multTex[0] = 1.0 / textureSize(shadowMap[0], 0);
		multTex[1] = 1.0 / textureSize(shadowMap[1], 0);
		multTex[2] = 1.0 / textureSize(shadowMap[2], 0);
		multTex[3] = 1.0 / textureSize(shadowMap[3], 0);
		for (int x = -1; x <= 1; ++x) {
			for (int y = -1; y <= 1; ++y) {
				shadow += lookup(vec2(x, y));
			}
		}
		return shadow / 9.0;
	} else
		return 1.0;
}

#end

#struct Light
struct Light {
	vec3 position;
	vec3 color;
	vec3 direction;
	float radius;
	float inRadius;
	int type;
	int shadowEnabled;
	sampler2DShadow shadowMap;
	mat4 shadowViewMatrix;
	mat4 shadowProjectionMatrix;
};
#end