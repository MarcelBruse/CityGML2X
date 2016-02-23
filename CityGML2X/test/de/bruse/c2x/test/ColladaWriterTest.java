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

import static org.junit.Assert.assertEquals;
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
		assertEquals("-7.155 7.675 3.5175 "
				+ "7.155 7.675 3.5175 "
				+ "7.155 3.1149999999999993 0.0 "
				+ "-7.155 -7.675 0.0 "
				+ "7.155 -7.675 0.0 "
				+ "7.155 -7.675 7.035 "
				+ "-7.155 -7.675 7.035 "
				+ "-7.155 3.1149999999999993 7.035 "
				+ "-7.155 3.1149999999999993 0.0 "
				+ "7.155 3.1149999999999993 7.035",
					handler.positionsArray);
		assertEquals("0.0 1.0 0.0 "
				+ "0.0 0.0 -1.0 "
				+ "0.0 -0.0 1.0 "
				+ "-1.0 0.0 0.0 "
				+ "1.0 0.0 0.0 "
				+ "0.0 -0.6107801272066281 0.7918002501953729 "
				+ "0.0 -0.6107801272066281 -0.7918002501953729",
					handler.normalsArray);
		Files.delete(out);
	}

}