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

import static org.junit.Assert.assertTrue;

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
import de.bruse.c2x.converter.collada.ColladaWriter;

public class ColladaWriterTest {

	@Test
	public void convertSimpleBuilding() throws IOException, ConversionException, SAXException {
		Path in = Paths.get("test/BoundarySurfaces.gml");
		Path out = Paths.get("test/BoundarySurfaces.gml.dae");
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD2);
		source.addGeometryType(GeometryType.MULTI_SURFACE);
		Converter converter = new Converter(new ColladaWriter(out));
		converter.addSource(source);
		converter.convert();
		assertTrue(Files.exists(out));
		ColladaHandler handler = new ColladaHandler();
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(handler);
		reader.parse(out.toString());
		assertTrue(handler.positionsArray.contains("-7.155 7.675 3.5175 "
				+ "7.155 7.675 3.5175 "
				+ "7.155 3.1149999999999993 0.0 "
				+ "-7.155 -7.675 0.0 "
				+ "7.155 -7.675 0.0 "
				+ "7.155 -7.675 7.035 "
				+ "-7.155 -7.675 7.035 "
				+ "-7.155 3.1149999999999993 7.035 "
				+ "-7.155 3.1149999999999993 0.0 "
				+ "7.155 3.1149999999999993 7.035"));
		assertTrue(handler.normalsArray.contains("0.0 1.0 0.0 "
				+ "0.0 0.0 -1.0 "
				+ "0.0 -0.0 1.0 "
				+ "-1.0 0.0 0.0 "
				+ "1.0 0.0 0.0 "
				+ "0.0 -0.6107801272066281 0.7918002501953729 "
				+ "0.0 -0.6107801272066281 -0.7918002501953729"));
		Files.delete(out);
	}
	
	@Test
	public void processDirectPositions() throws IOException, ConversionException, SAXException {
		Path in = Paths.get("test/DirectPosition.gml");
		Path out = Paths.get("test/DirectPosition.gml.dae");
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD2);
		source.addGeometryType(GeometryType.MULTI_SURFACE);
		Converter converter = new Converter(new ColladaWriter(out));
		converter.addSource(source);
		converter.convert();
		assertTrue(Files.exists(out));
		ColladaHandler handler = new ColladaHandler();
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(handler);
		reader.parse(out.toString());
		assertTrue(handler.positionsArray.contains("4.82449999999 4.15049999952 0.0 "
				+ "-0.2285000000270001 7.32149999961 17.834 "
				+ "5.16249999998 -5.79449999984 0.0 "
				+ "-0.2285000000270001 7.32149999961 0.0 "
				+ "3.61550000001 -7.21250000037 0.0 "
				+ "4.714499999999999 7.45349999983 12.204 "
				+ "5.17249999999 -7.16750000045 0.0 "
				+ "-4.65749999997 -7.45349999983 0.0 "
				+ "3.61550000001 -7.21250000037 13.988 "
				+ "-5.17249999999 7.18850000016 0.0 "
				+ "0.257500000007 -7.3105000006 0.0 "
				+ "0.257500000007 -7.3105000006 17.834 "
				+ "5.16249999998 -5.79449999984 12.167 "
				+ "4.714499999999999 7.45349999983 0.0 "
				+ "-5.17249999999 7.18850000016 12.204 "
				+ "4.82449999999 4.15049999952 12.197 "
				+ "-4.65749999997 -7.45349999983 12.204 "
				+ "5.17249999999 -7.16750000045 12.204"));
		assertTrue(handler.normalsArray.contains("0.029171617978988634 -0.9995744177921362 0.0 "
				+ "0.029082301759734818 -0.9995770204063096 0.0 "
				+ "-0.9993820107390602 -0.0351510541968963 0.0 "
				+ "-0.02689156570783933 0.9996383564538633 0.0 "
				+ "-0.026694913821792423 0.9996436272872684 0.0 "
				+ "0.9994459140272715 0.03328460506287844 0.0 "
				+ "0.9994229442335921 0.033967315752910926 0.0 "
				+ "0.9999734776713478 0.007283128027874133 0.0 "
				+ "0.028889670652944427 -0.9995826063560551 0.0 "
				+ "-0.524495001342934 0.7292918687908247 -0.43936131336505047 "
				+ "-0.7508300197034383 -0.026408787062051096 0.6599673154619444 "
				+ "0.0 0.0 -1.0"));
		Files.delete(out);
	}
	
	@Test
	public void convertFourSimpleBlocks() throws IOException, ConversionException, SAXException {
		Path in = Paths.get("test/FourSimpleBlocks.gml");
		CityGMLSource source = new CityGMLSource("", Files.newInputStream(in));
		source.addLevelOfDetail(LevelOfDetail.LOD1);
		source.addGeometryType(GeometryType.SOLID);
		Path out = Paths.get("test/FourSimpleBlocks.gml.dae");
		Converter converter = new Converter();
		converter.setWriter(new ColladaWriter(out));
		converter.addSource(source);
		converter.convert();
		assertTrue(Files.exists(out));
		ColladaHandler handler = new ColladaHandler();
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(handler);
		reader.parse(out.toString());
		assertTrue(handler.positionsArray.contains(
				"-2.50068321818644 -3.06511742015757 7.21911419532262E-16 "
				+ "-4.180683218186445 -3.06511742015757 1.7300000000000006 "
				+ "-2.50068321818644 -3.06511742015757 1.7300000000000006 "
				+ "-2.50068321818644 -0.8351174201575802 1.7300000000000006 "
				+ "-4.180683218186445 -0.8351174201575802 7.21911419532262E-16 "
				+ "-2.50068321818644 -0.8351174201575802 7.21911419532262E-16 "
				+ "-4.180683218186445 -0.8351174201575802 1.7300000000000006 "
				+ "-4.180683218186445 -3.06511742015757 7.21911419532262E-16"));
		assertTrue(handler.positionsArray.contains(
				"0.4361120119038704 -1.1396239269057202 7.21911419532262E-16 "
				+ "0.4361120119038704 0.24037607309427944 1.6300000000000006 "
				+ "-1.7967748925709799 0.24037607309427944 7.21911419532262E-16 "
				+ "0.4361120119038704 -1.1396239269057202 1.6300000000000006 "
				+ "0.4361120119038704 0.24037607309427944 7.21911419532262E-16 "
				+ "-1.7967748925709799 -1.1396239269057202 1.6300000000000006 "
				+ "-1.7967748925709799 0.24037607309427944 1.6300000000000006 "
				+ "-1.7967748925709799 -1.1396239269057202 7.21911419532262E-16"));
		assertTrue(handler.positionsArray.contains(
				"-4.394683349050099 3.06511742015757 1.4100000000000006 "
				+ "-4.394683349050099 1.5051174201575694 7.21911419532262E-16 "
				+ "-4.394683349050099 3.06511742015757 7.21911419532262E-16 "
				+ "-4.394683349050099 1.5051174201575694 1.4100000000000006 "
				+ "-2.3446833490501 1.5051174201575694 1.4100000000000006 "
				+ "-2.3446833490501 1.5051174201575694 7.21911419532262E-16 "
				+ "-2.3446833490501 3.06511742015757 1.4100000000000006 "
				+ "-2.3446833490501 3.06511742015757 7.21911419532262E-16"));
		assertTrue(handler.positionsArray.contains(
				"4.394683349050099 -0.23604931709393018 0.0 "
				+ "2.9646833490501 -0.23604931709393018 1.9000000000000006 "
				+ "2.9646833490501 -0.23604931709393018 0.0 "
				+ "4.394683349050099 -2.69604931709393 1.9000000000000006 "
				+ "4.394683349050099 -0.23604931709393018 1.9000000000000006 "
				+ "4.394683349050099 -2.69604931709393 0.0 "
				+ "2.9646833490501 -2.69604931709393 1.9000000000000006 "
				+ "2.9646833490501 -2.69604931709393 0.0"));
		Files.delete(out);
	}

}