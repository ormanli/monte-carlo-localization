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

import java.util.ArrayList;

import math.geom2d.Point2D;
import math.geom2d.line.Line2D;
import math.geom2d.polygon.SimplePolygon2D;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * @author Serdar Ormanlı
 * 
 */
public class Room {

	PApplet parent;
	private ArrayList<PVector> corners;
	private SimplePolygon2D walls;

	/**
	 * @param parent
	 * @param corners
	 */
	public Room(PApplet parent, ArrayList<PVector> corners) {
		this.corners = corners;
		this.walls = cornersToPolygon(corners);
		this.parent = parent;
	}

	/**
	 * Converts corner list to polygon
	 * 
	 * @param list
	 *            of corners
	 * @return Rooms polygon constructed using corners
	 */
	private SimplePolygon2D cornersToPolygon(ArrayList<PVector> list) {
		SimplePolygon2D result = new SimplePolygon2D();
		for (int i = 0; i < list.size(); i++) {
			result.addVertex(new Point2D(list.get(i).x, list.get(i).y));
		}
		return result;
	}

	/**
	 * Draws room
	 */
	public void display() {
		for (int i = 0; i < corners.size(); i++) {
			parent.stroke(0);
			parent.line(corners.get(i).x, corners.get(i).y, corners.get((i + 1) % corners.size()).x, corners.get((i + 1) % corners.size()).y);
		}

	}

	/**
	 * Checks a point is in room
	 * 
	 * @param x
	 * @param y
	 * @return true if point is in room, else false
	 */
	public boolean isPointIn(double x, double y) {
		return walls.contains(x, y);
	}

	/**
	 * Checks if line intersect boundries of room
	 * 
	 * @param line
	 * @return nearest intersection point, if there is no intersection point
	 *         returns null
	 */
	public Point2D isIntersects(Line2D line) {
		Point2D[] intersectionPoints = walls.boundary().intersections(line).toArray(new Point2D[walls.boundary().intersections(line).size()]);

		if (intersectionPoints.length > 0) {
			double distance = Util.distance;
			int k = 0;
			for (int i = 0; i < intersectionPoints.length; i++) {
				double distance2 = intersectionPoints[i].distance(line.p1);
				if (distance2 < distance) {
					distance = distance2;
					k = i;
				}
			}
			return intersectionPoints[k];
		}

		return null;
	}
}
