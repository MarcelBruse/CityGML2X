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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.citygml.building.Building;
import org.citygml4j.model.citygml.building.BuildingPart;
import org.citygml4j.model.gml.geometry.AbstractGeometry;
import org.citygml4j.util.walker.FeatureWalker;

import de.bruse.c2x.Color;
import de.bruse.c2x.Geometry;

public class BuildingWalker extends FeatureWalker {

	private LinkedList<Geometry> geometryList = new LinkedList<>();
	
	HashSet<LevelOfDetail> lodSet;
	
	HashSet<GeometryType> geometryTypeSet;
	
	private int buildingsTotal = 0;
	
	private int buildingParts = 0;
	
	private int processedGeometries = 0;
	
	private AbstractBuilding abstractBuilding;
	
	public BuildingWalker(CityGMLSource source) {
		lodSet = source.getLODSet();
		geometryTypeSet = source.getGeometryTypeSet();
	}
	
	public int getBuildingsTotal() {
		return buildingsTotal;
	}
	
	public int getBuildingParts() {
		return buildingParts;
	}

	public int getProcessedGeometries() {
		return processedGeometries;
	}
	
	@Override
	public void visit(Building building) {
		super.visit(building);
		buildingsTotal++;
	}
	
	@Override
	public void visit(BuildingPart buildingPart) {
		super.visit(buildingPart);
		buildingParts++;
	}

	@Override
	public void visit(AbstractBuilding abstractBuilding) {
		super.visit(abstractBuilding);
		this.abstractBuilding = abstractBuilding;
		if (geometryTypeSet.contains(GeometryType.SOLID)) {
			if (lodSet.contains(LevelOfDetail.LOD2) && abstractBuilding.isSetLod2Solid()) {
				traversePolygons(abstractBuilding.getLod2Solid().getSolid());
			} else if (lodSet.contains(LevelOfDetail.LOD1) && abstractBuilding.isSetLod1Solid()) {
				traversePolygons(abstractBuilding.getLod1Solid().getSolid());
			} 			
		} else if (geometryTypeSet.contains(GeometryType.MULTI_SURFACE)) {
			if (abstractBuilding.isSetBoundedBySurface()) {
				traverseBoundarySurfaces(abstractBuilding.getBoundedBySurface());
			} else if (lodSet.contains(LevelOfDetail.LOD2) && abstractBuilding.isSetLod2MultiSurface()) {
				traversePolygons(abstractBuilding.getLod2MultiSurface().getMultiSurface());
			}			
		}
	}

	private void traverseBoundarySurfaces(List<BoundarySurfaceProperty> boundarySurfacePropertyList) {
		Geometry geometry = new Geometry();
		for (BoundarySurfaceProperty property : abstractBuilding.getBoundedBySurface()) {
			BoundarySurfaceWalker walker = new BoundarySurfaceWalker(abstractBuilding, geometry);
			if (property.isSetBoundarySurface()) {
				property.getBoundarySurface().accept(walker);
			}
		}	
		if (!geometry.isEmpty()) {
			processedGeometries++;
			geometryList.add(geometry);
		}
	}
	
	private void traversePolygons(AbstractGeometry abstractGeometry) {
		processedGeometries++;
		Geometry geometry = new Geometry();
		PolygonWalker polygonWalker = new PolygonWalker(abstractBuilding, geometry, Color.GRAY);
		abstractGeometry.accept(polygonWalker);
		geometryList.add(geometry);
	}
	
	public LinkedList<Geometry> getGeometryList() {
		return geometryList;
	}
	
}