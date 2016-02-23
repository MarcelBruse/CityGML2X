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

import java.util.HashMap;
import java.util.LinkedList;

public class Geometry {
	
	private HashMap<String, Vertex> vertexMap = new HashMap<>();
	private LinkedList<Polygon> polygonList = new LinkedList<>();
	
	public void addVertex(Vertex vertex) {
		vertexMap.put(vertex.toString(), vertex);
	}
	
	public void addPolygon(Polygon polygon) {
		polygonList.add(polygon);
	}
	
	public LinkedList<Polygon> getPolygonList() {
		return polygonList;
	}
	
	public LinkedList<Vertex> getVertexList() {
		return new LinkedList<>(vertexMap.values());
	}
	
	public Vertex registerVertex(double x, double y, double z) { 
		Vertex vertex = new Vertex(x, y, z);
		String signature = vertex.toString();
		if (vertexMap.containsKey(signature)) {
			vertex = vertexMap.get(signature);
		} else {
			vertexMap.put(signature, vertex);			
		}
		return vertex;
	}
	
	public boolean isEmpty() {
		return polygonList.isEmpty();
	}
	
}
