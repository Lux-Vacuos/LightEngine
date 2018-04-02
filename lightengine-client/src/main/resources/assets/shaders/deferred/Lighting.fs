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
uniform vec2 resolution;
uniform mat4 projectionLightMatrix[4];
uniform mat4 viewLightMatrix;
uniform mat4 biasMatrix;
uniform sampler2DShadow shadowMap[4];

const float distanceThreshold = 1;
const int sample_count = 16;
const vec2 poisson16[] = vec2[](
	vec2(-0.94201624, -0.39906216), vec2(0.94558609, -0.76890725), vec2(-0.094184101, -0.92938870),
	vec2(0.34495938, 0.29387760), vec2(-0.91588581, 0.45771432), vec2(-0.81544232, -0.87912464),
	vec2(-0.38277543, 0.27676845), vec2(0.97484398, 0.75648379), vec2(0.44323325, -0.97511554),
	vec2(0.53742981, -0.47373420), vec2(-0.26496911, -0.41893023), vec2(0.79197514, 0.19090188),
	vec2(-0.24188840, 0.99706507), vec2(-0.81409955, 0.91437590), vec2(0.19984126, 0.78641367),
	vec2(0.14383161, -0.14100790));

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
		vec3 kD = vec3(1.0) - kS;
		kD *= 1.0 - metallic;

		float NdotL = max(dot(N, L), 0.0) * computeShadow(position);
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

		vec3 emissive = texture(gMask, textureCoords).rgb;

		vec3 ambient = (kD * diffuse + specular) * computeAmbientOcclusion(position, N);
		vec3 color = ambient + emissive + Lo;
		image.rgb = color;
	}
	vec4 vol = texture(composite0, textureCoords);
	image += vec4(vol.r);
	out_Color = image;
}