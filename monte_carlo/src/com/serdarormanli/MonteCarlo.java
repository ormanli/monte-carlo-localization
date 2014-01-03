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
import java.util.List;

import math.geom2d.Point2D;
import math.geom2d.line.Line2D;
import processing.core.PApplet;
import processing.core.PConstants;

/**
 * Class for all monte carlo localization calculations
 * 
 * @author Serdar Ormanlı
 * 
 */
public class MonteCarlo extends PApplet {

	private static final long serialVersionUID = 6550177041668681591L;

	Robot d;
	Room room;
	Particle[] particleList;
	double bestProb = 0;
	double bestDist = 0;

	public void setup() {
		frameRate(30);
		size(Util.width, Util.height,PConstants.P2D);
		room = new Room(this, Util.getCorners());
		room.display();
		particleList = generateParticles(Util.getParticleNum());
		d = new Robot(this, 320, 240, room);
		monteCarlo();
	}

	public void draw() {
		background(255);
		room.display();
		d.display();
		monteCarlo();
		fill(0);
		text(new StringBuilder().append("P: ").append(Util.df.format(bestProb)).append(" D: ").append(Util.df.format(bestDist)).toString(), Util.width - 180, Util.height - 20);
	}

	/**
	 * Takes movement model and applies to particles, get sensor reading and
	 * compares with particle, resamples and shows on screen
	 */
	private void monteCarlo() {

		if (d.getM().getDistance() > 0.1) {

			double[] probs = new double[Util.getParticleNum()];

			for (int i = 0; i < particleList.length; i++) {
				Particle currParticle = particleList[i];

				double prob = currParticle.getWeight();

				float orientationChange = d.getM().getAngle() - d.getM().getPrevAngle();

				if (Float.isInfinite(orientationChange) || Float.isNaN(orientationChange)) {
					orientationChange = 0;
				}

				float orientation = (float) Util.radianAdd(orientationChange, Util.getNoise(Util.getOrientNoise()));

				currParticle.setOrientation((float) Util.radianAdd(currParticle.getOrientation(), orientation));
				currParticle.setX((float) (currParticle.getX() + Math.cos(currParticle.getOrientation()) * (d.getM().getDistance() + Util.getNoise(Util.getMoveNoise()))));
				currParticle.setY((float) (currParticle.getY() + Math.sin(currParticle.getOrientation()) * (d.getM().getDistance() + Util.getNoise(Util.getMoveNoise()))));

				if (!room.isPointIn(currParticle.getX(), currParticle.getY())) {
					do {
						currParticle = new Particle(this, (int) Math.round(Math.random() * Util.width), (int) Math.round(Math.random() * Util.height), 0, (1.0 / Util.getParticleNum()));
					} while (!room.isPointIn(currParticle.getX(), currParticle.getY()));
				}

				double[] pdist2 = measure(currParticle.getOrientation(), currParticle.getX(), currParticle.getY());

				for (int j = 0; j < pdist2.length; j++) {
					prob *= Util.gaussian(pdist2[j], 1.0, d.getPdist()[j]);
				}

				probs[i] = prob;
			}

			Util.normalize(probs);

			for (int i = 0; i < particleList.length; i++) {
				Particle currParticle = particleList[i];
				currParticle.setWeight((float) probs[i]);
			}

			particleList = generateNewParticles(particleList);
		}

		for (int i = 0; i < particleList.length; i++) {
			particleList[i].display();
		}

	}

	/**
	 * Generates partilces at random positions with 1/number probability
	 * 
	 * @param number
	 *            of particles
	 * @return new particles based on number of particles
	 */
	private Particle[] generateParticles(int number) {
		Particle[] particles = new Particle[number];
		Particle particle = null;

		for (int j = 0; j < number; j++) {
			do {
				particle = new Particle(this, (int) Math.round(Math.random() * Util.width), (int) Math.round(Math.random() * Util.height), (float) (Math.random() * 2 * Math.PI - Math.PI), (1.0 / number));
			} while (!room.isPointIn(particle.getX(), particle.getY()));

			particle.display();
			particles[j] = (Particle) particle.clone();
		}

		return particles;
	}

	/**
	 * Resamples particles based on weight. Based on resampling wheel
	 * 
	 * @param old_particles
	 * @return
	 */
	private Particle[] generateNewParticles(Particle[] old_particles) {
		int N = old_particles.length;
		List<Particle> new_particles = new ArrayList<Particle>();
		double incr = 0;
		bestProb = 0;

		int index = 0;

		for (int i = 0; i < N; i++) {
			incr += old_particles[i].getWeight();
		}

		incr = incr / 2.0 / N;

		double beta = incr;

		for (int i = 0; i < N; i++) {
			while (beta > old_particles[index].getWeight()) {
				beta -= old_particles[index].getWeight();
				index = (index + 1) % N;
			}

			beta += incr;
			new_particles.add((Particle) old_particles[index].clone());
			if (old_particles[index].getWeight() > bestProb) {
				bestProb = old_particles[index].getWeight();
				bestDist = dist(old_particles[index].getX(), old_particles[index].getY(), d.getM().getX(), d.getM().getY());
			}
		}
		return new_particles.toArray(new Particle[N]);
	}

	/**
	 * Measure particle
	 * 
	 * @param orientation
	 * @param x
	 * @param y
	 * @return measurements of a particle
	 */
	public double[] measure(double orientation, double x, double y) {
		double distance = Util.getSensorRange() + 1.5;

		double[] reading = new double[Util.sensorRadians.length];

		for (int i = 0; i < reading.length; i++) {
			double sensor = 0;

			double sensorx = distance * Math.cos(Util.radianAdd(orientation, Util.sensorRadians[i]));
			double sensory = distance * Math.sin(Util.radianAdd(orientation, Util.sensorRadians[i]));

			double konumx = x + sensorx;
			double konumy = y + sensory;

			double CX = x + 1.5 * Math.cos(orientation);
			double CY = y + 1.5 * Math.sin(orientation);

			Point2D value = room.isIntersects(new Line2D(CX, CY, konumx, konumy));
			if (value != null) {
				double EX = value.x();
				double EY = value.y();

				sensor = dist((float) CX, (float) CY, (float) EX, (float) EY);

				sensor = sensor + Util.getNoise(Util.getSensorNoise());

			} else {
				sensor = Util.getSensorRange() + Util.getNoise(Util.getSensorNoise());

			}
			reading[i] = sensor;
		}
		return reading;
	}

	/**
	 * @see processing.core.PApplet#keyPressed()
	 */
	public void keyPressed() {
		if (key == 'K' || key == 'k') {
			do {
				d.getM().setX(Math.round(Math.random() * Util.width));
				d.getM().setY(Math.round(Math.random() * Util.height));
			} while (!room.isPointIn(d.getM().getX(), d.getM().getY()));
		}
	}

	static public void main(String[] passedArgs) {
		String[] appletArgs = new String[] { "com.serdarormanli.MonteCarlo" };

		// try {
		// OutputStream output = new FileOutputStream("system.out.txt");
		// PrintStream printOut = new PrintStream(output);
		// System.setOut(printOut);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// }

		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} else {
			PApplet.main(appletArgs);
		}
	}
}
