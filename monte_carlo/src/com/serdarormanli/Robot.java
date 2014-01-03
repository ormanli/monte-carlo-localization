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

import math.geom2d.Point2D;
import math.geom2d.line.Line2D;
import processing.core.PApplet;

/**
 * Class for robot
 * 
 * @author Serdar Ormanlı
 * 
 */
public class Robot {
	PApplet parent;
	RMotion m;
	private double[] pdist;
	private Room room;

	private double distance = Util.getSensorRange() + 5.5;
	private double[] reading = new double[Util.sensorRadians.length];

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            {@link PApplet} for acessing drawing area
	 * @param X
	 *            starting coordinate of robot
	 * @param Y
	 *            starting coordinate of robot
	 * @param room
	 *            {@link Room} for robot to traverse
	 */
	Robot(PApplet parent, float X, float Y, Room room) {
		this.parent = parent;
		this.room = room;
		m = new RMotion(X, Y);
	}

	/**
	 * Takes sensor readings according to its positions in {@link Room}
	 * 
	 * @return sensor measurements
	 */
	public double[] measure() {
		for (int i = 0; i < reading.length; i++) {
			double sensor = 0;

			double sensorx = distance * Math.cos(Util.radianAdd(m.getAngle(), Util.sensorRadians[i]));
			double sensory = distance * Math.sin(Util.radianAdd(m.getAngle(), Util.sensorRadians[i]));

			double konumx = m.getX() + sensorx;
			double konumy = m.getY() + sensory;

			double CX = m.getX() + 5.5 * Math.cos(m.getAngle());
			double CY = m.getY() + 5.5 * Math.sin(m.getAngle());

			Point2D value = room.isIntersects(new Line2D(CX, CY, konumx, konumy));
			if (value != null) {
				double EX = value.x();
				double EY = value.y();

				sensor = parent.dist((float) CX, (float) CY, (float) EX, (float) EY);
				parent.stroke(204, 102, 0);
				parent.fill(204, 102, 0);
				parent.line((float) CX, (float) CY, (float) EX, (float) EY);

				sensor = sensor + Util.getNoise(Util.getSensorNoise());

			} else {
				sensor = Util.getSensorRange() + Util.getNoise(Util.getSensorNoise());

			}
			reading[i] = sensor;
		}
		return reading;
	}

	/**
	 * Moves robot and updates sensor readings
	 */
	public void move() {
		m.followTo(parent.mouseX, parent.mouseY);
		m.wrap(0, 0, parent.width, parent.height);

		double CX = m.getX() + 5.5 * Math.cos(m.getAngle());
		double CY = m.getY() + 5.5 * Math.sin(m.getAngle());

		boolean stat = room.isIntersects(new Line2D(CX, CY, parent.mouseX, parent.mouseY)) != null || (room.isPointIn(m.getX(), m.getY() - 5.5) && room.isPointIn(m.getX(), m.getY() + 5.5) && room.isPointIn(m.getX() - 5.5, m.getY()) && room.isPointIn(m.getX() + 5.5, m.getY()));

		if (stat) {
			m.move();
			pdist = measure();
		}
	}

	/**
	 * Draws robot to {@link PApplet}
	 */
	public void display() {
		move();
		parent.stroke(128);
		parent.noFill();
		parent.strokeWeight(2);
		parent.ellipse(m.getX(), m.getY(), 11, 11);
		parent.strokeWeight(1);
		drawVector(20);
	}

	/**
	 * Draws movement vector to {@link PApplet}
	 * 
	 * @param scayl
	 */
	public void drawVector(float scayl) {
		parent.stroke(200);
		float arrowsize = 7;

		parent.pushMatrix();
		parent.translate(m.getX(), m.getY());
		parent.rotate(m.v.getDirection());
		float len = m.v.getVelocity() * scayl;

		parent.line(0, 0, len, 0);
		parent.line(len, 0, len - arrowsize, +arrowsize / 2);
		parent.line(len, 0, len - arrowsize, -arrowsize / 2);
		parent.popMatrix();
	}

	/**
	 * @return {@link RMotion} of robot
	 */
	public RMotion getM() {
		return m;
	}

	/**
	 * @return sensor measurements
	 */
	public double[] getPdist() {
		return pdist;
	}

}
