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

import java.util.List;

public interface Writer {
	
	public void write(List<Geometry> geometryList) throws ConversionException;
	
}
