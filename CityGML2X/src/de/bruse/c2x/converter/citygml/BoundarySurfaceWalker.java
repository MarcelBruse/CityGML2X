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

import org.citygml4j.model.citygml.building.AbstractBoundarySurface;
import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.citygml.building.CeilingSurface;
import org.citygml4j.model.citygml.building.ClosureSurface;
import org.citygml4j.model.citygml.building.GroundSurface;
import org.citygml4j.model.citygml.building.OuterCeilingSurface;
import org.citygml4j.model.citygml.building.OuterFloorSurface;
import org.citygml4j.model.citygml.building.RoofSurface;
import org.citygml4j.model.citygml.building.WallSurface;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurface;
import org.citygml4j.util.walker.FeatureWalker;

import de.bruse.c2x.Color;
import de.bruse.c2x.Geometry;

public class BoundarySurfaceWalker extends FeatureWalker {

	private Geometry geometry;
	
	private AbstractBuilding abstractBuilding;
	
	public BoundarySurfaceWalker(AbstractBuilding abstractBuilding, Geometry geometry) {
		this.abstractBuilding = abstractBuilding;
		this.geometry = geometry;
	}
	
	@Override
	public void visit(RoofSurface surface) {
		super.visit(surface);
		traversePolygons(surface, Color.RED);
	}
	
	@Override
	public void visit(WallSurface surface) {
		super.visit(surface);
		traversePolygons(surface, Color.GRAY);
	}
	
	@Override
	public void visit(GroundSurface surface) {
		super.visit(surface);
		traversePolygons(surface, Color.BROWN);
	}
	
	@Override
	public void visit(ClosureSurface surface) {
		super.visit(surface);
		traversePolygons(surface, Color.GRAY);
	}
	
	@Override
	public void visit(CeilingSurface surface) {
		super.visit(surface);
		traversePolygons(surface, Color.GRAY);
	}
	
	@Override
	public void visit(OuterCeilingSurface surface) {
		super.visit(surface);
		traversePolygons(surface, Color.GRAY);
	}
	
	@Override
	public void visit(OuterFloorSurface surface) {
		super.visit(surface);
		traversePolygons(surface, Color.GRAY);
	}
	
	private void traversePolygons(AbstractBoundarySurface surface, Color color) {
		if (surface.isSetLod2MultiSurface() && surface.getLod2MultiSurface().isSetGeometry()) {
			MultiSurface multiSurface = surface.getLod2MultiSurface().getGeometry();
			PolygonWalker polygonWalker = new PolygonWalker(abstractBuilding, geometry, color);
			multiSurface.accept(polygonWalker);
		}
	}
	
	public Geometry getGeometry() {
		return geometry;
	}
	
}