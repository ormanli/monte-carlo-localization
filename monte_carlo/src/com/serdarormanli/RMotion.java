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

import seltar.motion.Motion;

/**
 * Extends Motion library's {@link seltar.motion.Motion} class for angle calculation of robot
 * 
 * @author Serdar Ormanlı
 *
 */
public class RMotion extends Motion {
	public float prevAngle;

	/**
	 * @param X
	 * @param Y
	 */
	public RMotion(float X, float Y) {
		super(X, Y);
	}

	/**
	 * @return
	 */
	public float getPrevAngle() {
		return prevAngle;
	}

	/**
	 * @param prevAngle
	 */
	public void setPrevAngle(float prevAngle) {
		this.prevAngle = prevAngle;
	}

	/**
	 * @see seltar.motion.Motion#followTo(float, float)
	 */
	public void followTo(float X, float Y) {
		setPrevAngle(getAngle());
		super.setVX(((X - super.getX()) / super.Const));
		super.setVY(((Y - super.getY()) / super.Const));
	}
}
