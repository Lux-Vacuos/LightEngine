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

layout(location = 0) in vec2 position;
layout(location = 1) in mat4 modelViewMatrix;
layout(location = 5) in vec4 texOffsets;
layout(location = 6) in float blendFactor;

out vec2 textureCoords0;
out vec2 textureCoords1;
out float blend;

uniform mat4 projectionMatrix;
uniform float numberOfRows;

void main() {
	vec2 textureCoords = position + vec2(0.5, 0.5);
	textureCoords.y = 1.0 - textureCoords.y;
	textureCoords /= numberOfRows;
	textureCoords0 = textureCoords + texOffsets.xy;
	textureCoords1 = textureCoords + texOffsets.zw;
	blend = blendFactor;

	vec4 worldPosition = modelViewMatrix * vec4(position, 0.0, 1.0);
	gl_Position = projectionMatrix * worldPosition;
}