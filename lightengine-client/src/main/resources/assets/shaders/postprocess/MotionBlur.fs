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

out vec3 out_Color;

uniform vec2 resolution;
uniform vec3 cameraPosition;
uniform vec3 previousCameraPosition;
uniform mat4 projectionMatrix;
uniform mat4 inverseProjectionMatrix;
uniform mat4 inverseViewMatrix;
uniform mat4 previousViewMatrix;
uniform sampler2D composite0;
uniform sampler2D depth;

uniform int useMotionBlur;

void main() {
	vec3 textureColor = texture(composite0, textureCoords).rgb;
	if (useMotionBlur == 1) {
		vec3 sum = textureColor;
		vec4 tex = vec4(textureCoords, 0.0, 0.0);
		float depthSample = texture(depth, textureCoords).x;
		vec4 currentPosition = vec4(tex.x * 2.0 - 1.0, tex.y * 2.0 - 1.0, depthSample, 1.0);
		vec4 fragposition = inverseProjectionMatrix * currentPosition;
		fragposition = inverseViewMatrix * fragposition;
		fragposition /= fragposition.w;
		fragposition.xyz += cameraPosition;

		vec4 previousPosition = fragposition;
		previousPosition.xyz -= previousCameraPosition;
		previousPosition = previousViewMatrix * previousPosition;
		previousPosition = projectionMatrix * previousPosition;
		previousPosition /= previousPosition.w;
		vec2 velocity = (currentPosition - previousPosition).st * 0.015;
		int samples = 1;
		vec2 coord = tex.st + velocity;
		for (int i = 0; i < 12; ++i, coord += velocity) {
			sum += texture(composite0, coord).rgb;
			samples++;
		}
		sum = sum / samples;
		textureColor = sum;
	}
	out_Color = textureColor;
}