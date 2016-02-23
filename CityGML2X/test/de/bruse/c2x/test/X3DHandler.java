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

import java.util.HashSet;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class X3DHandler implements ContentHandler {
	
	public HashSet<String> coordIndexSet = new HashSet<>();
	
	public HashSet<String> pointSet = new HashSet<>();
	
	public HashSet<String> colorIndexSet = new HashSet<>();
	
	public HashSet<String> colorSet = new HashSet<>();
	
	public int numberOfShapeNodes;

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
		if ("IndexedFaceSet".equals(qName)) {
			coordIndexSet.add(atts.getValue("coordIndex"));
			colorIndexSet.add(atts.getValue("colorIndex"));
		} else if ("Coordinate".equals(qName)) {
			pointSet.add(atts.getValue("point"));
		} else if ("ColorRGBA".equals(qName)) {
			colorSet.add(atts.getValue("color"));
		} else if ("Shape".equals(qName)) {
			numberOfShapeNodes++;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}

	@Override
	public void processingInstruction(String target, String data) throws SAXException {}

	@Override
	public void skippedEntity(String name) throws SAXException {}

}
