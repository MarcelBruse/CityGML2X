/*
 * CityGML2X - A Java library for converting CityGML to X3D. 
 * https://github.com/900k/CityGML2X
 * 
 * Copyright (c) 2016, Marcel Bruse <marcel.bruse@posteo.de>
 *
 * CityGML2X is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * CityGML2X is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CityGML2X. If not, see <http://www.gnu.org/licenses/>.
 */
package de.bruse.c2x;

public class Color {
	
	public static final String SPACE = " ";
	
	public double red;
	
	public double green;
	
	public double blue;
	
	public double alpha;
	
	public static final Color WHITE = new Color(1, 1, 1, 1);
	
	public static final Color BLACK = new Color(0, 0, 0, 1);
	
	public static final Color RED = new Color(1, 0, 0, 1);
	
	public static final Color GRAY = new Color(0.5, 0.5, 0.5, 1);
	
	public static final Color BROWN = new Color(0.65, 0.16, 0.16, 1);
	
	public Color(double red, double green, double blue, double alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(red)
				.append(SPACE)
				.append(green)
				.append(SPACE)
				.append(blue)
				.append(SPACE)
				.append(alpha);
		return builder.toString();
	}
	
}