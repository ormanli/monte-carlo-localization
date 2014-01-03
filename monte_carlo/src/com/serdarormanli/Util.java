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

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import processing.core.PVector;

import com.serdarormanli.Settings.Map;
import com.serdarormanli.Settings.Map.Corner;

/**
 * Utilities used by this project
 * 
 * @author Serdar Ormanlı
 * 
 */
public class Util {

	public static double[] sensorRadians = { -1.57079633, 0, 1.57079633 };

	public final static int height = 480;
	public final static int width = 640;

	private static Double sensorRange;
	private static Integer particleNum;

	public final static int distance = 10000;

	public final static DecimalFormat df = new DecimalFormat("#.#####");

	private static Settings settings;

	private static Double orientNoise;
	private static Double moveNoise;
	private static Double sensorNoise;

	private static final String settingsPath = "settings.xml";

	/**
	 * Adding to radians for Motion library. Plus operator wont give true result
	 * 
	 * @param a
	 * @param b
	 * @return a+b radians according to rule
	 */
	public final static double radianAdd(double a, double b) {
		double result = 0;

		result = a + b;

		if (result < -1 * Math.PI) {
			result %= Math.PI;
			result += Math.PI;
		} else if (result > Math.PI) {
			result %= Math.PI;
			result += (-1 * Math.PI);
		}

		return result;
	}

	/**
	 * Box-muller noise generator
	 * 
	 * @param value
	 *            variance of noise
	 * @return generated noise
	 */
	public final static double getNoise(double value) {
		double r, x, y;

		do {
			x = 2.0 * Math.random() - 1.0;
			y = 2.0 * Math.random() - 1.0;
			r = x * x + y * y;
		} while (r > 1 || r == 0);

		return x * Math.sqrt(-2.0 * Math.log(r) / r) * value;
	}

	/**
	 * Gaussian probabilty density function
	 * 
	 * @param mu
	 * @param sigma
	 * @param x
	 * @return probability
	 */
	public final static double gaussian(double mu, double sigma, double x) {
		return (1 / (sigma * Math.sqrt(2.0 * Math.PI))) * Math.exp(-0.5 * Math.pow(((x - mu) / sigma), 2));
	}

	/**
	 * Normalizes the doubles in the array by their sum.(from Weka)
	 * 
	 * @param doubles
	 *            the array of double
	 * @exception IllegalArgumentException
	 *                if sum is Zero or NaN
	 */
	public final static void normalize(double[] doubles) {

		double sum = 0;
		for (int i = 0; i < doubles.length; i++) {
			sum += doubles[i];
		}
		normalize(doubles, sum);
	}

	/**
	 * Normalizes the doubles in the array using the given value.(from Weka)
	 * 
	 * @param doubles
	 *            the array of double
	 * @param sum
	 *            the value by which the doubles are to be normalized
	 * @exception IllegalArgumentException
	 *                if sum is zero or NaN
	 */
	public final static void normalize(double[] doubles, double sum) {

		if (Double.isNaN(sum)) {
			throw new IllegalArgumentException("Can't normalize array. Sum is NaN.");
		}
		if (sum != 0) {
			for (int i = 0; i < doubles.length; i++) {
				doubles[i] /= sum;
			}
		}

	}

	/**
	 * Imports settings from settings.xml
	 * 
	 * @param path
	 * @return settings
	 */
	public final static Settings importSettings(String path) {
		if (settings == null) {
			try {
				File file = new File(path);
				JAXBContext jaxbContext = JAXBContext.newInstance(Settings.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				settings = (Settings) jaxbUnmarshaller.unmarshal(file);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}

		return settings;
	}

	/**
	 * Get corners from settings.xml
	 * 
	 * @return corners
	 */
	public final static ArrayList<PVector> getCorners() {
		Settings settingsList = importSettings(settingsPath);

		ArrayList<PVector> walls = new ArrayList<PVector>();

		for (int i = 0; i < settingsList.getMap().size(); i++) {
			Map map = settingsList.getMap().get(i);
			if (settingsList.getDefaultmap().equals(map.getName())) {
				for (int j = 0; j < map.getCorner().size(); j++) {
					walls.add(cornerToPVector(map.getCorner().get(j)));
				}
			}
		}
		return walls;
	}

	/**
	 * Converts settings.xml's {@link Settings.Map.Corner} to {@link PVector}
	 * 
	 * @param corner
	 * @return pvector
	 */
	public final static PVector cornerToPVector(Corner corner) {
		return new PVector(corner.x, corner.y);
	}

	/**
	 * @return sensor range
	 */
	public final static double getSensorRange() {
		if (sensorRange == null) {
			sensorRange = new Double(importSettings(settingsPath).sensorrange);
		}
		return sensorRange.doubleValue();
	}

	/**
	 * @return number of particles
	 */
	public final static int getParticleNum() {
		if (particleNum == null) {
			particleNum = new Integer(importSettings(settingsPath).numofparticles);
		}
		return particleNum.intValue();
	}

	/**
	 * @return orientationNoise
	 */
	public static double getOrientNoise() {
		if (orientNoise == null) {
			orientNoise = new Double(importSettings(settingsPath).orientationnoise);
		}
		return orientNoise.doubleValue();
	}

	/**
	 * @return moveNoise
	 */
	public static double getMoveNoise() {
		if (moveNoise == null) {
			moveNoise = new Double(importSettings(settingsPath).movenoise);
		}
		return moveNoise.doubleValue();
	}

	/**
	 * @return sensorNoise
	 */
	public static double getSensorNoise() {
		if (sensorNoise == null) {
			sensorNoise = new Double(importSettings(settingsPath).sensornoise);
		}
		return sensorNoise.doubleValue();
	}
}
