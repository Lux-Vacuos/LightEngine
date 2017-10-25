/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2017 Lux Vacuos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.luxvacuos.lightengine.client.resources.animation;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;

public class Joint {

	public final int index;
	public final String name;
	public final List<Joint> children = new ArrayList<>();

	private Matrix4f animatedTransform = new Matrix4f();

	private final Matrix4f localBindTransform;
	private Matrix4f inverseBindTransform = new Matrix4f();

	public Joint(int index, String name, Matrix4f localBindTransform) {
		this.index = index;
		this.name = name;
		this.localBindTransform = localBindTransform;
	}

	public void calcInverseBindTransform(Matrix4f parentBindTransform) {
		Matrix4f bindTransform = parentBindTransform.mul(localBindTransform);
		bindTransform.invert(inverseBindTransform);
		for (Joint joint : children) {
			joint.calcInverseBindTransform(bindTransform);
		}
	}

	public void addChild(Joint child) {
		this.children.add(child);
	}

	public Matrix4f getAnimatedTransform() {
		return animatedTransform;
	}

	public void setAnimationTransform(Matrix4f animationTransform) {
		this.animatedTransform = animationTransform;
	}

	public Matrix4f getInverseBindTransform() {
		return inverseBindTransform;
	}

}
