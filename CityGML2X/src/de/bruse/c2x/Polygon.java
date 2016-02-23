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

public class Polygon {
	
	private LinkedList<Vertex> vertexList = new LinkedList<>();
	
	private Vertex normal;
	
	private Color color;
	
	public Polygon() {
		this(Color.GRAY);
	}
	
	public Polygon(Color color) {
		this.color = color;
	}
	
	public void addVertex(Vertex vertex) {
		vertexList.add(vertex);
	}
	
	public LinkedList<Vertex> getVertexList() {
		return vertexList;
	}
	
	public Vertex getNormal() {
		return normal;
	}

	public void setNormal(Vertex normal) {
		this.normal = normal;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("(");
		boolean firstIteration = true;
		for (Vertex vertex : vertexList) {
			if (firstIteration) {
				result.append(vertex.toString());
				firstIteration = false;
			} else {
				result.append(";").append(vertex.toString());				
			}
		}
		result.append(")");
		return result.toString();
	}
	
}
