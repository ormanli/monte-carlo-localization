/*******************************************************************************
 * Copyright (c) 2013 Serdar Ormanlı.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Serdar Ormanlı - initial API and implementation
 ******************************************************************************/
package com.serdarormanli;

import processing.core.PApplet;

/**
 * Particle class for probability
 * 
 * @author Serdar Ormanlı
 * 
 */
public class Particle implements Cloneable {
	PApplet parent;
	private float orientation, X, Y;
	private double weight;

	public Particle(PApplet parent, float x, float y, float orientation, double weight) {
		this.parent = parent;
		this.orientation = orientation;
		X = x;
		Y = y;
		this.weight = weight;
	}

	public void display() {
		parent.stroke(255f, 0f, 0f);
		parent.fill(255f, 0f, 0f);
		parent.ellipse(X, Y, 3, 3);
	}

	public float getX() {
		return X;
	}

	public void setX(float x) {
		X = x;
	}

	public float getY() {
		return Y;
	}

	public void setY(float y) {
		Y = y;
	}

	public float getOrientation() {
		return orientation;
	}

	public void setOrientation(float orientation) {
		this.orientation = orientation;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}

}
