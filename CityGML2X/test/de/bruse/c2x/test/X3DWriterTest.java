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

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.bruse.c2x.ConversionException;
import de.bruse.c2x.Converter;
import de.bruse.c2x.converter.citygml.CityGMLSource;
import de.bruse.c2x.converter.citygml.GeometryType;
import de.bruse.c2x.converter.citygml.LevelOfDetail;
import de.bruse.c2x.converter.x3d.X3DWriter;

public class X3DWriterTest {

	@Test
	public void noInputStream() throws IOException {
		CityGMLSource source = new CityGMLSource("", null);
		source.addLevelOfDetail(LevelOfDetail.LOD2);
		source.addGeometryType(GeometryType.SOLID);
		Converter converter = new Converter();
		converter.addSource(source);
		try {
			converter.convert();
			assertTrue(false);
		} catch (ConversionException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void noLevelOfDetailSet() throws IOException {
		Path in = Paths.get("test/SimpleBuilding.gml");
		CityGMLSource source = new CityGMLSource();
		source.setName("");
		source.setCityGML(Files.newInputStream(in));
		source.addGeometryType(GeometryType.SOLID);
		Converter converter = new Converter();
		converter.addSource(source);
		try {
			converter.convert();
			assertTrue(false);
		} catch (ConversionException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void noGeometryTypeSet() throws IOException {
		Path in = Paths.get("test/SimpleBuilding.gml");
		Path out = Paths.get("test/SimpleBuilding.gml.x3d");
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD2);
		Converter converter = new Converter();
		converter.setWriter(new X3DWriter(out, X3DWriter.SPLIT_GEOMETRIES));
		converter.addSource(source);
		try {
			converter.convert();
			assertTrue(false);
		} catch (ConversionException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void emptyDocument() throws IOException, ConversionException, SAXException {
		String filepath = "test/EmptyDocument.gml";
		Path in = Paths.get(filepath);
		Path out = Paths.get(filepath + ".x3d");
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD2);
		source.addGeometryType(GeometryType.SOLID);
		Converter converter = new Converter();
		converter.setWriter(new X3DWriter(out, X3DWriter.SPLIT_GEOMETRIES));
		converter.addSource(source);
		try {
			converter.convert();
			assertTrue(false);
		} catch (ConversionException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void emptyBuilding() throws IOException, SAXException {
		String filepath = "test/EmptyBuilding.gml";
		Path in = Paths.get(filepath);
		Path out = Paths.get(filepath + ".x3d");
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD2);
		source.addGeometryType(GeometryType.SOLID);
		Converter converter = new Converter();
		converter.setWriter(new X3DWriter(out, X3DWriter.SPLIT_GEOMETRIES));
		converter.addSource(source);
		try {
			converter.convert();
			assertTrue(false);
		} catch (ConversionException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void emptyBuildingGeometry() throws IOException, ConversionException, SAXException {
		Path in = Paths.get("test/SimpleBuilding.gml");
		Path out = Paths.get("test/SimpleBuilding.gml.x3d");
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD1);
		source.addGeometryType(GeometryType.MULTI_SURFACE);
		Converter converter = new Converter();
		converter.setWriter(new X3DWriter(out, X3DWriter.SPLIT_GEOMETRIES));
		converter.addSource(source);
		try {
			converter.convert();
			assertTrue(false);
		} catch (ConversionException e) {
			assertTrue(true);
		}
	}

	@Test
	public void convertSimpleBuilding() throws IOException, ConversionException, SAXException {
		Path in = Paths.get("test/SimpleBuilding.gml");
		Path out = Paths.get("test/SimpleBuilding.gml.x3d");
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD2);
		source.addGeometryType(GeometryType.SOLID);
		Converter converter = new Converter(new X3DWriter(out, X3DWriter.SPLIT_GEOMETRIES));
		converter.addSource(source);
		converter.convert();
		assertTrue(Files.exists(out));
		X3DHandler handler = new X3DHandler();
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(handler);
		reader.parse(out.toString());
		assertEquals(1, handler.numberOfShapeNodes);
		assertTrue(
			handler.coordIndexSet.contains(
				"0 1 2 3 -1 "
				+ "2 4 5 3 -1 "
				+ "6 7 8 9 -1 "
				+ "0 3 5 7 6 -1 "
				+ "0 6 9 1 -1 "
				+ "9 8 4 2 1 -1 "
				+ "8 7 5 4 -1"));
		assertTrue(
			handler.pointSet.contains(
				"7.155 -7.675 -3.5175 "
				+ "-7.155 -7.675 -3.5175 "
				+ "-7.155 -7.675 3.5175 "
				+ "7.155 -7.675 3.5175 "
				+ "-7.155 3.115 3.5175 "
				+ "7.155 3.115 3.5175 "
				+ "7.155 3.115 -3.5175 "
				+ "7.155 7.675 0.0 "
				+ "-7.155 7.675 0.0 "
				+ "-7.155 3.115 -3.5175"));
		assertTrue(handler.colorSet.contains("0.5 0.5 0.5 1.0"));
		assertTrue(handler.colorIndexSet.contains("0 0 0 0 0 0 0"));
		Files.delete(out);
	}

	@Test
	public void convertWinkelhaus() throws IOException, ConversionException, SAXException {
		Path in = Paths.get("test/Winkelhaus.gml");
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD2);
		source.addGeometryType(GeometryType.MULTI_SURFACE);
		Path out = Paths.get("test/Winkelhaus.gml.x3d");
		Converter converter = new Converter();
		converter.setWriter(new X3DWriter(out, X3DWriter.SPLIT_GEOMETRIES));
		converter.addSource(source);
		converter.convert();
		assertTrue(Files.exists(out));
		X3DHandler handler = new X3DHandler();
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(handler);
		reader.parse(out.toString());
		assertTrue(
			handler.coordIndexSet.contains(
				"0 1 2 3 4 5 -1 "
				+ "1 0 6 7 -1 "
				+ "3 2 8 9 -1 "
				+ "4 3 9 10 -1 "
				+ "0 5 11 6 -1 "
				+ "12 10 9 13 -1 "
				+ "13 9 8 14 -1 "
				+ "4 10 12 11 5 -1 "
				+ "11 12 13 6 -1 "
				+ "6 13 14 7 -1 "
				+ "2 1 7 14 8 -1"));
		assertTrue(
			handler.pointSet.contains(
				"6.83276519228401 22.0604762938076 0.0 "
				+ "23.682765192284 22.0604762938076 0.0 "
				+ "23.682765192284 15.0604762938076 0.0 "
				+ "14.092765192284 15.0604762938076 0.0 "
				+ "14.092765192284 6.8004762938076 0.0 "
				+ "6.83276519228401 6.8004762938076 0.0 "
				+ "6.83276519228401 22.0604762938076 11.57 "
				+ "23.682765192284 22.0604762938076 11.57 "
				+ "23.682765192284 15.0604762938076 11.57 "
				+ "14.092765192284 15.0604762938076 11.57 "
				+ "14.092765192284 6.8004762938076 11.57 "
				+ "6.83276519228401 6.8004762938076 11.57 "
				+ "10.462765192284 6.8004762938076 16.45 "
				+ "10.462765192284 18.5604762938076 16.45 "
				+ "23.682765192284 18.5604762938076 16.45"));
		Files.delete(out);
	}
	
	@Test
	public void convertBuildingWithBuildingPart() throws IOException, ConversionException, SAXException {
		String filepath = "test/TwoTowers.gml";
		Path in = Paths.get(filepath);
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD1);
		source.addGeometryType(GeometryType.SOLID);
		Path out = Paths.get(filepath + ".x3d");
		Converter converter = new Converter();
		converter.setWriter(new X3DWriter(out, X3DWriter.SPLIT_GEOMETRIES));
		converter.addSource(source);
		converter.convert();
		assertTrue(Files.exists(out));
		X3DHandler handler = new X3DHandler();
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(handler);
		reader.parse(out.toString());
		assertEquals(2, handler.numberOfShapeNodes);
		assertTrue(
			handler.coordIndexSet.contains(
				"0 1 2 3 -1 "
				+ "4 5 6 7 -1 "
				+ "1 0 5 4 -1 "
				+ "2 1 4 7 -1 "
				+ "3 2 7 6 -1 "
				+ "0 3 6 5 -1"));
		assertTrue(
			handler.pointSet.contains(
				"4.34246081692594 2.65195803631436 0.0 "
				+ "4.34246081692594 5.00195803631436 0.0 "
				+ "6.69246081692594 5.00195803631436 0.0 "
				+ "6.69246081692594 2.65195803631436 0.0 "
				+ "4.34246081692594 5.00195803631436 5.14 "
				+ "4.34246081692594 2.65195803631436 5.14 "
				+ "6.69246081692594 2.65195803631436 5.14 "
				+ "6.69246081692594 5.00195803631436 5.14"));
		assertTrue(handler.colorSet.contains("0.5 0.5 0.5 1.0"));
		assertTrue(handler.colorIndexSet.contains("0 0 0 0 0 0"));
		assertTrue(
			handler.pointSet.contains(
				"0.987714228261741 0.906720486947622 0.0 "
				+ "0.987714228261741 5.15672048694762 0.0 "
				+ "3.21771422826174 5.15672048694762 0.0 "
				+ "3.21771422826174 0.906720486947622 0.0 "
				+ "0.987714228261741 5.15672048694762 3.87999999999999 "
				+ "0.987714228261741 0.906720486947622 3.87999999999999 "
				+ "3.21771422826174 0.906720486947622 3.87999999999999 "
				+ "3.21771422826174 5.15672048694762 3.87999999999999"));
		Files.delete(out);
	}
	
	@Test
	public void convertNestedBuildingParts() throws IOException, ConversionException, SAXException {
		String filepath = "test/NestedBuildingParts.gml";
		Path in = Paths.get(filepath);
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD1);
		source.addGeometryType(GeometryType.SOLID);
		Path out = Paths.get(filepath + ".x3d");
		Converter converter = new Converter();
		converter.setWriter(new X3DWriter(out, X3DWriter.SPLIT_GEOMETRIES));
		converter.addSource(source);
		converter.convert();
		assertEquals(1, source.getNumberOfFoundBuildings());
		assertEquals(2, source.getNumberOfFoundBuildingParts());
		assertEquals(3, source.getNumberOfProcessedGeometries());
		assertTrue(Files.exists(out));
		X3DHandler handler = new X3DHandler();
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(handler);
		reader.parse(out.toString());
		assertEquals(3, handler.numberOfShapeNodes);
		Files.delete(out);
	}

	@Test
	public void convertFourSimpleBlocks() throws IOException, ConversionException, SAXException {
		Path in = Paths.get("test/FourSimpleBlocks.gml");
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD1);
		source.addGeometryType(GeometryType.SOLID);
		Path out = Paths.get("test/FourSimpleBlocks.gml.x3d");
		Converter converter = new Converter();
		converter.setWriter(new X3DWriter(out, X3DWriter.SPLIT_GEOMETRIES));
		converter.addSource(source);
		converter.convert();
		assertTrue(Files.exists(out));
		X3DHandler handler = new X3DHandler();
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(handler);
		reader.parse(out.toString());
		assertEquals(4, handler.numberOfShapeNodes);
		assertTrue(
			handler.coordIndexSet.contains(
				"0 1 2 3 -1 "
				+ "4 5 6 7 -1 "
				+ "1 0 5 4 -1 "
				+ "2 1 4 7 -1 "
				+ "3 2 7 6 -1 "
				+ "0 3 6 5 -1"));
		assertTrue(
			handler.pointSet.contains(
				"8.02943047402237 1.8700517818832 -7.21911419532262E-16 "
				+ "8.02943047402237 4.3300517818832 -7.21911419532262E-16 "
				+ "9.45943047402237 4.3300517818832 -7.21911419532262E-16 "
				+ "9.45943047402237 1.8700517818832 -7.21911419532262E-16 "
				+ "8.02943047402237 4.3300517818832 1.9 "
				+ "8.02943047402237 1.8700517818832 1.9 "
				+ "9.45943047402237 1.8700517818832 1.9 "
				+ "9.45943047402237 4.3300517818832 1.9"));
		Files.delete(out);
	}

	@Test
	public void convertHrefBoundarySurfaces() throws IOException, ConversionException, SAXException {
		String filepath = "test/HrefBoundarySurfaces.gml";
		Path in = Paths.get(filepath);
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD2);
		source.addGeometryType(GeometryType.MULTI_SURFACE);
		Path out = Paths.get(filepath + ".x3d");
		Converter converter = new Converter();
		converter.setWriter(new X3DWriter(out, X3DWriter.WRAP_GEOMETRIES));
		converter.addSource(source);
		converter.convert();
		assertTrue(Files.exists(out));
		X3DHandler handler = new X3DHandler();
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(handler);
		reader.parse(out.toString());
		assertEquals(1, handler.numberOfShapeNodes);
		assertTrue(
			handler.coordIndexSet.contains(
				"0 1 2 3 -1 "
				+ "2 4 5 3 -1 "
				+ "0 6 7 1 -1 "
				+ "0 3 5 8 6 -1 "
				+ "7 9 4 2 1 -1 "
				+ "6 8 9 7 -1 "
				+ "9 8 5 4 -1"));
		assertTrue(
			handler.pointSet.contains(
				"7.155 -7.675 -3.5175 "
				+ "-7.155 -7.675 -3.5175 "
				+ "-7.155 -7.675 3.5175 "
				+ "7.155 -7.675 3.5175 "
				+ "-7.155 3.115 3.5175 "
				+ "7.155 3.115 3.5175 "
				+ "7.155 3.115 -3.5175 "
				+ "-7.155 3.115 -3.5175 "
				+ "7.155 7.675 0.0 "
				+ "-7.155 7.675 0.0"));
		assertTrue(handler.colorSet.contains("0.65 0.16 0.16 1.0 0.5 0.5 0.5 1.0 1.0 0.0 0.0 1.0"));
		assertTrue(handler.colorIndexSet.contains("0 1 1 1 1 2 2"));
		Files.delete(out);
	}

	@Test
	public void convertHrefCompositeSurface() throws IOException, ConversionException, SAXException {
		Path in = Paths.get("test/HrefCompositeSurface.gml");
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD2);
		source.addGeometryType(GeometryType.SOLID);
		Path out = Paths.get("test/HrefCompositeSurface.gml.x3d");
		Converter converter = new Converter();
		converter.setWriter(new X3DWriter(out, X3DWriter.WRAP_GEOMETRIES));
		converter.addSource(source);
		converter.convert();
		assertTrue(Files.exists(out));
		X3DHandler handler = new X3DHandler();
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(handler);
		reader.parse(out.toString());
		assertEquals(1, handler.numberOfShapeNodes);
		assertTrue(
			handler.coordIndexSet.contains(
				"0 1 2 3 4 5 -1 "
				+ "6 7 2 1 -1 "
				+ "8 9 1 0 -1 "
				+ "6 1 9 10 -1 "
				+ "7 6 10 11 -1 "
				+ "2 7 11 12 -1 "
				+ "12 13 3 2 -1 "
				+ "13 14 4 3 -1 "
				+ "14 15 5 4 -1 "
				+ "15 8 0 5 -1 "
				+ "9 8 15 14 13 12 11 10 -1"));
		assertTrue(
			handler.pointSet.contains(
				"3509156.05 5431847.69 268.548 "
				+ "3509157.024 5431843.683 271.574 "
				+ "3509167.422 5431846.352 271.597 "
				+ "3509166.42 5431850.5 268.466 "
				+ "3509159.98 5431848.93 268.392 "
				+ "3509155.98 5431847.96 268.343 "
				+ "3509158.01 5431839.63 268.657 "
				+ "3509168.43 5431842.18 268.596 "
				+ "3509156.05 5431847.69 262.491 "
				+ "3509157.024 5431843.683 262.491 "
				+ "3509158.01 5431839.63 262.491 "
				+ "3509168.43 5431842.18 262.491 "
				+ "3509167.422 5431846.352 262.491 "
				+ "3509166.42 5431850.5 262.491 "
				+ "3509159.98 5431848.93 262.491 "
				+ "3509155.98 5431847.96 262.491"));
		Files.delete(out);
	}

	@Test
	public void convertInlineCompositeSurface() throws IOException, ConversionException, SAXException {
		Path in = Paths.get("test/InlineCompositeSurface.gml");
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD2);
		source.addGeometryType(GeometryType.SOLID);
		Path out = Paths.get("test/InlineCompositeSurface.gml.x3d");
		Converter converter = new Converter();
		converter.setWriter(new X3DWriter(out, X3DWriter.WRAP_GEOMETRIES));
		converter.addSource(source);
		converter.convert();
		assertTrue(Files.exists(out));
		X3DHandler handler = new X3DHandler();
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(handler);
		reader.parse(out.toString());
		assertEquals(1, handler.numberOfShapeNodes);
		assertTrue(
			handler.coordIndexSet.contains(
				"0 1 2 3 4 5 -1 "
				+ "6 7 2 1 -1 "
				+ "8 9 1 0 -1 "
				+ "6 1 9 10 -1 "
				+ "7 6 10 11 -1 "
				+ "2 7 11 12 -1 "
				+ "12 13 3 2 -1 "
				+ "13 14 4 3 -1 "
				+ "14 15 5 4 -1 "
				+ "15 8 0 5 -1 "
				+ "9 8 15 14 13 12 11 10 -1"));
		assertTrue(
			handler.pointSet.contains(
				"3509156.05 5431847.69 268.548 "
				+ "3509157.024 5431843.683 271.574 "
				+ "3509167.422 5431846.352 271.597 "
				+ "3509166.42 5431850.5 268.466 "
				+ "3509159.98 5431848.93 268.392 "
				+ "3509155.98 5431847.96 268.343 "
				+ "3509158.01 5431839.63 268.657 "
				+ "3509168.43 5431842.18 268.596 "
				+ "3509156.05 5431847.69 262.491 "
				+ "3509157.024 5431843.683 262.491 "
				+ "3509158.01 5431839.63 262.491 "
				+ "3509168.43 5431842.18 262.491 "
				+ "3509167.422 5431846.352 262.491 "
				+ "3509166.42 5431850.5 262.491 "
				+ "3509159.98 5431848.93 262.491 "
				+ "3509155.98 5431847.96 262.491"));
		Files.delete(out);
	}
	
	@Test
	public void convertInlineCompositeSolid() throws IOException, ConversionException, SAXException {
		String filepath = "test/InlineCompositeSurface.gml";
		Path in = Paths.get(filepath);
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD2);
		source.addGeometryType(GeometryType.SOLID);
		Path out = Paths.get(filepath + ".x3d");
		Converter converter = new Converter();
		converter.setWriter(new X3DWriter(out, X3DWriter.WRAP_GEOMETRIES));
		converter.addSource(source);
		converter.convert();
		assertTrue(Files.exists(out));
		X3DHandler handler = new X3DHandler();
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(handler);
		reader.parse(out.toString());
		assertEquals(1, handler.numberOfShapeNodes);
		assertTrue(
			handler.coordIndexSet.contains(
				"0 1 2 3 4 5 -1 "
				+ "6 7 2 1 -1 "
				+ "8 9 1 0 -1 "
				+ "6 1 9 10 -1 "
				+ "7 6 10 11 -1 "
				+ "2 7 11 12 -1 "
				+ "12 13 3 2 -1 "
				+ "13 14 4 3 -1 "
				+ "14 15 5 4 -1 "
				+ "15 8 0 5 -1 "
				+ "9 8 15 14 13 12 11 10 -1"));
		assertTrue(
			handler.pointSet.contains(
				"3509156.05 5431847.69 268.548 "
				+ "3509157.024 5431843.683 271.574 "
				+ "3509167.422 5431846.352 271.597 "
				+ "3509166.42 5431850.5 268.466 "
				+ "3509159.98 5431848.93 268.392 "
				+ "3509155.98 5431847.96 268.343 "
				+ "3509158.01 5431839.63 268.657 "
				+ "3509168.43 5431842.18 268.596 "
				+ "3509156.05 5431847.69 262.491 "
				+ "3509157.024 5431843.683 262.491 "
				+ "3509158.01 5431839.63 262.491 "
				+ "3509168.43 5431842.18 262.491 "
				+ "3509167.422 5431846.352 262.491 "
				+ "3509166.42 5431850.5 262.491 "
				+ "3509159.98 5431848.93 262.491 "
				+ "3509155.98 5431847.96 262.491"));
		Files.delete(out);
	}
	
	@Test
	public void convertEmptyBoundarySurfaces() throws IOException {
		String filepath = "test/EmptyBoundarySurfaces.gml";
		Path in = Paths.get(filepath);
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD2);
		source.addGeometryType(GeometryType.MULTI_SURFACE);
		Path out = Paths.get(filepath + ".x3d");
		Converter converter = new Converter();
		converter.setWriter(new X3DWriter(out, X3DWriter.SPLIT_GEOMETRIES));
		converter.addSource(source);
		try {
			converter.convert();
			assertTrue(false);
		} catch (ConversionException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void convertBoundarySurfaces() throws IOException, ConversionException, SAXException {
		String filepath = "test/BoundarySurfaces.gml";
		Path in = Paths.get(filepath);
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD2);
		source.addGeometryType(GeometryType.MULTI_SURFACE);
		Path out = Paths.get(filepath + ".x3d");
		Converter converter = new Converter();
		converter.setWriter(new X3DWriter(out, X3DWriter.SPLIT_GEOMETRIES));
		converter.addSource(source);
		converter.convert();
		assertTrue(Files.exists(out));
		X3DHandler handler = new X3DHandler();
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(handler);
		reader.parse(out.toString());
		assertEquals(1, handler.numberOfShapeNodes);
		assertTrue(
			handler.coordIndexSet.contains(
				"0 1 2 3 -1 "
				+ "2 4 5 3 -1 "
				+ "0 6 7 1 -1 "
				+ "0 3 5 8 6 -1 "
				+ "7 9 4 2 1 -1 "
				+ "6 8 9 7 -1 "
				+ "9 8 5 4 -1"));
		assertTrue(
			handler.pointSet.contains(
				"7.155 -7.675 -3.5175 "
				+ "-7.155 -7.675 -3.5175 "
				+ "-7.155 -7.675 3.5175 "
				+ "7.155 -7.675 3.5175 "
				+ "-7.155 3.115 3.5175 "
				+ "7.155 3.115 3.5175 "
				+ "7.155 3.115 -3.5175 "
				+ "-7.155 3.115 -3.5175 "
				+ "7.155 7.675 0.0 "
				+ "-7.155 7.675 0.0"));
		assertTrue(handler.colorSet.contains("0.65 0.16 0.16 1.0 0.5 0.5 0.5 1.0 1.0 0.0 0.0 1.0"));
		assertTrue(handler.colorIndexSet.contains("0 1 1 1 1 2 2"));
		Files.delete(out);
	}
	
	@Test
	public void splitFourSimpleBlocksInDifferentShapes() throws IOException, ConversionException, SAXException {
		Path in = Paths.get("test/FourSimpleBlocks.gml");
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD1);
		source.addGeometryType(GeometryType.SOLID);
		Path out = Paths.get("test/FourSimpleBlocks.gml.x3d");
		Converter converter = new Converter();
		converter.setWriter(new X3DWriter(out, X3DWriter.SPLIT_GEOMETRIES));
		converter.addSource(source);
		converter.convert();
		assertTrue(Files.exists(out));
		X3DHandler handler = new X3DHandler();
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(handler);
		reader.parse(out.toString());
		assertEquals(4, handler.numberOfShapeNodes);
		Files.delete(out);
	}

	@Test
	public void wrapFourSimpleBlocksInOneShape() throws IOException, ConversionException, SAXException {
		Path in = Paths.get("test/FourSimpleBlocks.gml");
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD1);
		source.addGeometryType(GeometryType.SOLID);
		Path out = Paths.get("test/FourSimpleBlocks.gml.x3d");
		Converter converter = new Converter();
		converter.setWriter(new X3DWriter(out, X3DWriter.WRAP_GEOMETRIES));
		converter.addSource(source);
		converter.convert();
		assertTrue(Files.exists(out));
		X3DHandler handler = new X3DHandler();
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(handler);
		reader.parse(out.toString());
		assertEquals(1, handler.numberOfShapeNodes);
		Files.delete(out);
	}

}
