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

layout(triangles) in;
layout(triangle_strip, max_vertices = 3) out;

in vec3 passPosition[];
in vec4 clipSpace[];
in vec2 passTextureCoords[];

out vec3 passPositionOut;
out vec4 clipSpaceOut;
out vec3 normal;
out vec2 textureCoordsOut;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform float time;

#define PI 3.14159265359

const float A = 0.1; // amplitude
const float L = 8;   // wavelength
const float w = 2 * PI / L;
const float Q = 1;

vec3 calculatePosition(vec3 basePos) {
	vec3 P0 = basePos;
	vec2 D = vec2(1, 0.5);
	float dotD = dot(P0.xz, D);
	float C = cos(w * dotD + time / 4);
	float S = sin(w * dotD + time / 4);
	return vec3(P0.x + Q * A * C * D.x, A * S + basePos.y, P0.z + Q * A * C * D.y);
}

vec3 calculateTriangleNormal0() {
	vec3 tangent = calculatePosition(passPosition[1]) - calculatePosition(passPosition[0]);
	vec3 bitangent = calculatePosition(passPosition[2]) - calculatePosition(passPosition[0]);
	vec3 normal = cross(tangent, bitangent);
	return normalize(normal);
}
vec3 calculateTriangleNormal1() {
	vec3 tangent = passPosition[1] - passPosition[0];
	vec3 bitangent = passPosition[2] - passPosition[0];

	tangent = calculatePosition(passPosition[1] + tangent) - calculatePosition(passPosition[1]);
	bitangent = calculatePosition(passPosition[1] + bitangent) - calculatePosition(passPosition[1]);

	vec3 normal = cross(tangent, bitangent);
	return normalize(normal);
}
vec3 calculateTriangleNormal2() {
	vec3 tangent = passPosition[1] - passPosition[0];
	vec3 bitangent = passPosition[2] - passPosition[0];

	tangent = calculatePosition(passPosition[2] + tangent) - calculatePosition(passPosition[2]);
	bitangent = calculatePosition(passPosition[2] + bitangent) - calculatePosition(passPosition[2]);

	vec3 normal = cross(tangent, bitangent);
	return normalize(normal);
}

void main() {
	vec4 positionRelativeToCam = viewMatrix * vec4(calculatePosition(passPosition[0]), 1.0);
	normal = calculateTriangleNormal0();
	passPositionOut = passPosition[0];
	clipSpaceOut = clipSpace[0];
	textureCoordsOut = passTextureCoords[0];
	gl_Position = projectionMatrix * positionRelativeToCam;
	EmitVertex();

	positionRelativeToCam = viewMatrix * vec4(calculatePosition(passPosition[1]), 1.0);
	normal = calculateTriangleNormal1();
	passPositionOut = passPosition[1];
	clipSpaceOut = clipSpace[1];
	textureCoordsOut = passTextureCoords[1];
	gl_Position = projectionMatrix * positionRelativeToCam;
	EmitVertex();

	positionRelativeToCam = viewMatrix * vec4(calculatePosition(passPosition[2]), 1.0);
	normal = calculateTriangleNormal2();
	passPositionOut = passPosition[2];
	clipSpaceOut = clipSpace[2];
	textureCoordsOut = passTextureCoords[2];
	gl_Position = projectionMatrix * positionRelativeToCam;
	EmitVertex();

	EndPrimitive();
}