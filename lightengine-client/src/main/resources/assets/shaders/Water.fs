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
uniform sampler2D foamMask;
uniform float time;

const float near = 0.1;
const float far = 1000.0;

float fresnelSchlickRoughness(float cosTheta, float F0, float roughness) {
    return F0 + (max(1.0 - roughness, F0) - F0) * pow(1.0 - cosTheta, 5.0);
}

##include variable GLOBAL

void main() {

	vec2 ndc = (clipSpaceOut.xy / clipSpaceOut.w) / 2.0 + 0.5;

	vec4 refractV = clipSpaceOut;
	refractV.xyz /= refractV.w;
	refractV.x = 0.5f*refractV.x + 0.5f;
	refractV.y = 0.5f*refractV.y + 0.5f;
	refractV.z = .1f / refractV.z;

	vec3 N = normalize(normal);
	vec3 V = normalize(cameraPosition - passPositionOut);
	vec3 R = reflect(-V, N);

	vec2 distortion1 = (texture(dudv, vec2(textureCoordsOut.x + time / 40, textureCoordsOut.y + time / 20)).rg * 2.0 - 1.0) * 0.02;
	vec2 distortion2 = (texture(dudv, vec2(textureCoordsOut.x + time / 20, textureCoordsOut.y + time / 30)).rg * 2.0 - 1.0) * 0.02;
	vec2 distortion = distortion1 + distortion2;

	float channelA = texture(foamMask, textureCoordsOut - vec2(time / 40, cos(textureCoordsOut.x))).r; 
	float channelB = texture(foamMask, textureCoordsOut * 0.5 + vec2(sin(textureCoordsOut.y), time / 40)).b; 
		
	float mask = (channelA + channelB) * 0.8;
	mask = pow(mask, 2);
	mask = clamp(mask, 0, 1);
	
	float dist = texture(depth, ndc).r;
	float distDistort = texture(depth, refractV.xy - refractV.z * N.xz * 0.5 + distortion).r;
	float floorDistance = 2.0 * near * far / (far + near - (2.0 * dist - 1.0) * (far - near));
	float floorDistanceDistort = 2.0 * near * far / (far + near - (2.0 * distDistort - 1.0) * (far - near));

	dist = gl_FragCoord.z;
	float waterDistance = 2.0 * near * far / (far + near - (2.0 * dist - 1.0) * (far - near));
	float waterDepth = floorDistance - waterDistance;
	float waterDepthDistort = floorDistanceDistort - waterDistance;

	float F0 = 0.04;
	float F = fresnelSchlickRoughness(max(dot(N, V), 0.0), F0, 0);

	vec3 reflectionColor = texture(reflection, R + (distortion.x, 0, distortion.y)).rgb;
	
    reflectionColor = vec3(1.0) - exp(-reflectionColor * 1.0);
    reflectionColor = pow(reflectionColor, vec3(1.0 / GAMMA));

	vec3 refractionColor = texture(refraction, refractV.xy - refractV.z * N.xz * 0.5 + distortion).rgb;
	refractionColor = mix(refractionColor, refractionColor * vec3(0.0392156862745098, 0.3647058823529412, 0.6509803921568627), smoothstep(0, 20, waterDepthDistort));
	outColor.rgb = mix(reflectionColor, refractionColor, 1.0 - F);
	outColor.a = 1;
	if(waterDepth <= 2) {
		outColor.rgb = mix(vec3(0.8), outColor.rgb, smoothstep(0, 0.5, waterDepth));
		outColor.rgb = mix(outColor.rgb, vec3(0.8), clamp(mask - smoothstep(0, 2, waterDepth), 0, 1) * 0.95);
		outColor.a = smoothstep(0, 0.2, waterDepth);
	}
}