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

import java.util.Objects;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class ColladaHandler implements ContentHandler {
	
	public String positionsArray = "";
	
	public String normalsArray = "";
	
	private boolean readPositionsArray = false;
	
	private boolean readNormalsArray = false;

	@Override
	public void setDocumentLocator(Locator locator) {}

	@Override
	public void startDocument() throws SAXException {}

	@Override
	public void endDocument() throws SAXException {}

	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if ("float_array".equals(qName)) {
			String id = atts.getValue("id");
			if (Objects.nonNull(id)) {
				switch (id) {
				case "FaceSet_0_positions_array":
					readPositionsArray = true;
					break;
				case "FaceSet_0_normals_array":
					readNormalsArray = true;
				}
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("float_array".equals(qName)) {
			readPositionsArray = false;
			readNormalsArray = false;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (readPositionsArray) {
			positionsArray = String.copyValueOf(ch, start, length);
		} else if (readNormalsArray) {
			normalsArray = String.copyValueOf(ch, start, length);
		}
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}

	@Override
	public void processingInstruction(String target, String data) throws SAXException {}

	@Override
	public void skippedEntity(String name) throws SAXException {}

}