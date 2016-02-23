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

import java.util.LinkedList;

import de.bruse.c2x.converter.x3d.X3DWriter;

public class Converter {

	public static final Writer DEFAULT_WRITER = new X3DWriter();
	
	private Writer writer;
	
	private LinkedList<Source> sourceList = new LinkedList<>();
	
	public Converter() {
		this(DEFAULT_WRITER);
	}
	
	public Converter(Writer writer) {
		this.writer = writer;
	}
	
	public void setWriter(Writer writer) {
		this.writer = writer;
	}
	
	public Writer getWriter() {
		return writer;
	}
	
	public void addSource(Source source) {
		sourceList.add(source);
	}
	
	public void convert() throws ConversionException {
		LinkedList<Geometry> geometryList = new LinkedList<>();
		for (Source source : sourceList) {
			geometryList.addAll(source.extractGeometries());
		}
		writer.write(geometryList);
	}
	
}
