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

in vec2 textureCoords;
in vec2 blurTexCoords[17];

out vec4 out_Color;

uniform sampler2D image;
uniform sampler2D window;

void main(){
    vec4 result = vec4(0.0);
    vec4 mask = texture(image, textureCoords);
    if(mask.a == 0) {
        result.rgb += texture(image, blurTexCoords[0]).rgb * 0.024418;
        result.rgb += texture(image, blurTexCoords[1]).rgb * 0.032928;
        result.rgb += texture(image, blurTexCoords[2]).rgb * 0.042669;
        result.rgb += texture(image, blurTexCoords[3]).rgb * 0.05313;
        result.rgb += texture(image, blurTexCoords[4]).rgb * 0.06357;
        result.rgb += texture(image, blurTexCoords[5]).rgb * 0.073088;
        result.rgb += texture(image, blurTexCoords[6]).rgb * 0.080748;
        result.rgb += texture(image, blurTexCoords[7]).rgb * 0.085724;
        result.rgb += texture(image, blurTexCoords[8]).rgb * 0.08745;
        result.rgb += texture(image, blurTexCoords[9]).rgb * 0.085724;
        result.rgb += texture(image, blurTexCoords[10]).rgb * 0.080748;
        result.rgb += texture(image, blurTexCoords[11]).rgb * 0.073088;
        result.rgb += texture(image, blurTexCoords[12]).rgb * 0.06357;
        result.rgb += texture(image, blurTexCoords[13]).rgb * 0.05313;
        result.rgb += texture(image, blurTexCoords[14]).rgb * 0.042669;
        result.rgb += texture(image, blurTexCoords[15]).rgb * 0.032928;
        result.rgb += texture(image, blurTexCoords[16]).rgb * 0.024418;
        out_Color.rgb = result.rgb;
        out_Color.a = 0;
    } else {
        out_Color = mask;
        out_Color.a = 1;
    }
}