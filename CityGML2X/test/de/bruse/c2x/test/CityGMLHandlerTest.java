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
package de.bruse.c2x.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

import org.citygml4j.xml.io.reader.CityGMLReadException;
import org.junit.Test;

import de.bruse.c2x.ConversionException;
import de.bruse.c2x.Geometry;
import de.bruse.c2x.Polygon;
import de.bruse.c2x.ValidationException;
import de.bruse.c2x.converter.citygml.CityGMLSource;
import de.bruse.c2x.converter.citygml.GeometryType;
import de.bruse.c2x.converter.citygml.LevelOfDetail;

public class CityGMLHandlerTest {

	@Test
	public void validateSimpleBuilding() throws IOException {
		Path path = Paths.get("test/SimpleBuilding.gml");
		CityGMLSource handler = new CityGMLSource("", Files.newInputStream(path));
		try {
			handler.validate();
			assertTrue(true);
		} catch (ValidationException e) {
			assertTrue(false);
		}
	}

	@Test
	public void unsuccessfullyValidateBrokenBuilding() throws IOException {
		Path path = Paths.get("test/BrokenBuilding.gml");
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(path));
		try {
			source.validate();
			assertTrue(false);
		} catch (ValidationException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void extractGeometriesFromSimpleBuilding() throws IOException, ConversionException {
		Path path = Paths.get("test/SimpleBuilding.gml");
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(path));
		source.addLevelOfDetail(LevelOfDetail.LOD2);
		source.addGeometryType(GeometryType.SOLID);
		LinkedList<Geometry> geometryList = source.extractGeometries();
		Geometry geometry = geometryList.get(0);
		Polygon polygon;
		polygon = geometry.getPolygonList().get(0);
		assertEquals("((7.155;-7.675;-3.5175);(-7.155;-7.675;-3.5175);(-7.155;-7.675;3.5175);(7.155;-7.675;3.5175))", polygon.toString());
		polygon = geometry.getPolygonList().get(1);
		assertEquals("((-7.155;-7.675;3.5175);(-7.155;3.115;3.5175);(7.155;3.115;3.5175);(7.155;-7.675;3.5175))", polygon.toString());
		polygon = geometry.getPolygonList().get(2);
		assertEquals("((7.155;3.115;-3.5175);(7.155;7.675;0.0);(-7.155;7.675;0.0);(-7.155;3.115;-3.5175))", polygon.toString());
		polygon = geometry.getPolygonList().get(3);
		assertEquals("((7.155;-7.675;-3.5175);(7.155;-7.675;3.5175);(7.155;3.115;3.5175);(7.155;7.675;0.0);(7.155;3.115;-3.5175))", polygon.toString());
		polygon = geometry.getPolygonList().get(4);
		assertEquals("((7.155;-7.675;-3.5175);(7.155;3.115;-3.5175);(-7.155;3.115;-3.5175);(-7.155;-7.675;-3.5175))", polygon.toString());
		polygon = geometry.getPolygonList().get(5);
		assertEquals("((-7.155;3.115;-3.5175);(-7.155;7.675;0.0);(-7.155;3.115;3.5175);(-7.155;-7.675;3.5175);(-7.155;-7.675;-3.5175))", polygon.toString());
		polygon = geometry.getPolygonList().get(6);
		assertEquals("((-7.155;7.675;0.0);(7.155;7.675;0.0);(7.155;3.115;3.5175);(-7.155;3.115;3.5175))", polygon.toString());
	}
	
	@Test
	public void unsuccessfullyExtractGeometriesFromBrokenBuilding() throws IOException {
		Path path = Paths.get("test/BrokenBuilding.gml");
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(path));
		source.addLevelOfDetail(LevelOfDetail.LOD2);
		source.addGeometryType(GeometryType.SOLID);
		try {
			source.extractGeometries();
		} catch (ConversionException e) {
			assertEquals(e.getCause().getClass(), CityGMLReadException.class);
		}
	}
	
}
