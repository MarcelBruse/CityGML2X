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
package de.bruse.c2x.converter.citygml;

import java.util.List;

import org.citygml4j.model.gml.base.AbstractGML;
import org.citygml4j.model.gml.geometry.AbstractGeometry;
import org.citygml4j.model.gml.geometry.GeometryProperty;
import org.citygml4j.model.gml.geometry.complexes.CompositeSolid;
import org.citygml4j.model.gml.geometry.complexes.CompositeSurface;
import org.citygml4j.model.gml.geometry.primitives.DirectPositionList;
import org.citygml4j.model.gml.geometry.primitives.LinearRing;
import org.citygml4j.model.gml.geometry.primitives.Polygon;
import org.citygml4j.model.gml.geometry.primitives.Surface;
import org.citygml4j.util.walker.GeometryWalker;
import org.citygml4j.util.xlink.XLinkResolver;

import de.bruse.c2x.Color;
import de.bruse.c2x.Geometry;

public class PolygonWalker extends GeometryWalker {
	
	private AbstractGML abstractGML;
	
	private Geometry geometry;
	
	private Color color;
	
	public PolygonWalker(AbstractGML abstractGML, Geometry geometry, Color color) {
		this.abstractGML = abstractGML;
		this.geometry = geometry;
		this.color = color;
	}
	
	@Override
	public void visit(LinearRing linearRing) {
		super.visit(linearRing);
		de.bruse.c2x.Polygon polygon = new de.bruse.c2x.Polygon(color);
		if (linearRing.isSetPosList()) {
			DirectPositionList positionList = linearRing.getPosList();
			List<Double> coords = positionList.getValue();
			int vertexCount = (coords.size() / 3) - 1;
			for (int i = 0; i < vertexCount; i++) {
				int offset = i * 3;
				double x = coords.get(offset);
				double y = coords.get(offset + 1);
				double z = coords.get(offset + 2);
				polygon.addVertex(geometry.registerVertex(x, y, z));
			}
		}
		geometry.addPolygon(polygon);
	}
	
	@Override
	public <T extends AbstractGeometry> void visit(GeometryProperty<T> property) {
		super.visit(property);
		if (property.isSetHref()) {
			XLinkResolver resolver = new XLinkResolver();
			AbstractGeometry geometry = 
					(AbstractGeometry) resolver.getGeometry(property.getHref(), abstractGML);
			if (geometry instanceof Polygon) {
				visit((Polygon) geometry);
			} else if (geometry instanceof CompositeSurface) {
				visit((CompositeSurface) geometry);
			} else if (geometry instanceof Surface) {
				visit((Surface) geometry);
			} else if (geometry instanceof CompositeSolid) {
				visit((CompositeSolid) geometry);
			}
		}
	}
	
	public Geometry getGeometry() {
		return geometry;
	}
	
}