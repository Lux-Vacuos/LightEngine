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
in vec2 blurTexCoords[17];

out vec4 outColor;

uniform sampler2D accumulator;
uniform int blurBehind;

void main() {
	if (blurBehind != 1) {
		outColor = vec4(0.0);
		return;
	}
	vec4 result = vec4(0.0);
	result.rgb += texture(accumulator, blurTexCoords[0]).rgb * 0.024418;
	result.rgb += texture(accumulator, blurTexCoords[1]).rgb * 0.032928;
	result.rgb += texture(accumulator, blurTexCoords[2]).rgb * 0.042669;
	result.rgb += texture(accumulator, blurTexCoords[3]).rgb * 0.05313;
	result.rgb += texture(accumulator, blurTexCoords[4]).rgb * 0.06357;
	result.rgb += texture(accumulator, blurTexCoords[5]).rgb * 0.073088;
	result.rgb += texture(accumulator, blurTexCoords[6]).rgb * 0.080748;
	result.rgb += texture(accumulator, blurTexCoords[7]).rgb * 0.085724;
	result.rgb += texture(accumulator, blurTexCoords[8]).rgb * 0.08745;
	result.rgb += texture(accumulator, blurTexCoords[9]).rgb * 0.085724;
	result.rgb += texture(accumulator, blurTexCoords[10]).rgb * 0.080748;
	result.rgb += texture(accumulator, blurTexCoords[11]).rgb * 0.073088;
	result.rgb += texture(accumulator, blurTexCoords[12]).rgb * 0.06357;
	result.rgb += texture(accumulator, blurTexCoords[13]).rgb * 0.05313;
	result.rgb += texture(accumulator, blurTexCoords[14]).rgb * 0.042669;
	result.rgb += texture(accumulator, blurTexCoords[15]).rgb * 0.032928;
	result.rgb += texture(accumulator, blurTexCoords[16]).rgb * 0.024418;
	outColor.rgb = result.rgb;
	outColor.a = 1.0;
}