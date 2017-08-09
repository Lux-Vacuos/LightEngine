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
uniform vec2 resolution;
uniform vec4 frame;

void main(void){
    vec4 result = vec4(0.0);
    vec4 mask = texture(image, textureCoords);
    if(mask.a == 0) {
        vec2 blurTex[17] = blurTexCoords;
        vec2 LR = vec2(frame.x, frame.x + frame.z) / resolution / 2;
        vec2 TB = vec2(frame.y - frame.w, frame.y) / resolution / 2;
        float aspect = resolution.x / resolution.y;
        for (int i = 0; i < 17; i++) {
            if(blurTex[i].x >= LR.y / aspect)
                blurTex[i].x = LR.y / aspect;
        }
        result.rgb += texture(image, blurTex[0]).rgb * 0.024418;
        result.rgb += texture(image, blurTex[1]).rgb * 0.032928;
        result.rgb += texture(image, blurTex[2]).rgb * 0.042669;
        result.rgb += texture(image, blurTex[3]).rgb * 0.05313;
        result.rgb += texture(image, blurTex[4]).rgb * 0.06357;
        result.rgb += texture(image, blurTex[5]).rgb * 0.073088;
        result.rgb += texture(image, blurTex[6]).rgb * 0.080748;
        result.rgb += texture(image, blurTex[7]).rgb * 0.085724;
        result.rgb += texture(image, blurTex[8]).rgb * 0.08745;
        result.rgb += texture(image, blurTex[9]).rgb * 0.085724;
        result.rgb += texture(image, blurTex[10]).rgb * 0.080748;
        result.rgb += texture(image, blurTex[11]).rgb * 0.073088;
        result.rgb += texture(image, blurTex[12]).rgb * 0.06357;
        result.rgb += texture(image, blurTex[13]).rgb * 0.05313;
        result.rgb += texture(image, blurTex[14]).rgb * 0.042669;
        result.rgb += texture(image, blurTex[15]).rgb * 0.032928;
        result.rgb += texture(image, blurTex[16]).rgb * 0.024418;
        out_Color.rgb = result.rgb;
        out_Color.a = 0;
    } else {
        out_Color = mask;
        out_Color.a = 1;
    }
}