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

#variable pi
#define PI 3.14159265359
#end

#function random
/*
	by Spatial
	05 July 2013
*/

// A single iteration of Bob Jenkins' One-At-A-Time hashing algorithm.
uint hash(uint x) {
	x += (x << 10u);
	x ^= (x >> 6u);
	x += (x << 3u);
	x ^= (x >> 11u);
	x += (x << 15u);
	return x;
}

// Compound versions of the hashing algorithm I whipped together.
uint hash(uvec2 v) {
	return hash(v.x ^ hash(v.y));
}
uint hash(uvec3 v) {
	return hash(v.x ^ hash(v.y) ^ hash(v.z));
}
uint hash(uvec4 v) {
	return hash(v.x ^ hash(v.y) ^ hash(v.z) ^ hash(v.w));
}

// Construct a float with half-open range [0:1] using low 23 bits.
// All zeroes yields 0.0, all ones yields the next smallest representable value below 1.0.
float floatConstruct(uint m) {
	const uint ieeeMantissa = 0x007FFFFFu; // binary32 mantissa bitmask
	const uint ieeeOne = 0x3F800000u;	  // 1.0 in IEEE binary32

	m &= ieeeMantissa; // Keep only mantissa bits (fractional part)
	m |= ieeeOne;	  // Add fractional part to 1.0

	float f = uintBitsToFloat(m); // Range [1:2]
	return f - 1.0;				  // Range [0:1]
}

// Pseudo-random value in half-open range [0:1].
float random(float x) {
	return floatConstruct(hash(floatBitsToUint(x)));
}
float random(vec2 v) {
	return floatConstruct(hash(floatBitsToUint(v)));
}
float random(vec3 v) {
	return floatConstruct(hash(floatBitsToUint(v)));
}
float random(vec4 v) {
	return floatConstruct(hash(floatBitsToUint(v)));
}
#end

#function goldNoise
// Gold Noise ©2017-2018 dcerisano@standard3d.com

const float PHI = 1.61803398874989484820459 * 00000.1; // Golden Ratio
const float PI  = 3.14159265358979323846264 * 00000.1; // PI
const float SQ2 = 1.41421356237309504880169 * 10000.0; // Square Root of Two

float gold_noise(in vec2 coordinate, in float seed){
    return fract(sin(dot(coordinate*(seed+PHI), vec2(PHI, PI)))*SQ2);
}
#end