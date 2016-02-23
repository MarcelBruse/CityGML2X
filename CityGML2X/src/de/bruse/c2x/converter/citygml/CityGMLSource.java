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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.CityGMLBuilder;
import org.citygml4j.model.citygml.CityGML;
import org.citygml4j.model.citygml.CityGMLClass;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLReadException;
import org.citygml4j.xml.io.reader.CityGMLReader;
import org.citygml4j.xml.schema.SchemaHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.bruse.c2x.ConversionException;
import de.bruse.c2x.Geometry;
import de.bruse.c2x.Source;
import de.bruse.c2x.ValidationException;

public class CityGMLSource implements Source {
	
	private String name;
	
	private InputStream citygml;
	
	private HashSet<LevelOfDetail> lodSet = new HashSet<>();
	
	private HashSet<GeometryType> geometryTypeSet = new HashSet<>();
	
	private int numberOfFoundBuildings;
	
	private int numberOfFoundBuildingParts;
	
	private int numberOfProcessedGeometries;
	
	public CityGMLSource() {}
	
	public CityGMLSource(String name, InputStream citygml) {
		this.citygml = citygml;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setCityGML(InputStream citygml) {
		this.citygml = citygml;
	}
	
	public InputStream getInputStream() {
		return citygml;
	}
	
	public void addLevelOfDetail(LevelOfDetail lod) {
		lodSet.add(lod);
	}
	
	public HashSet<LevelOfDetail> getLODSet() {
		return lodSet;
	}
	
	public void addGeometryType(GeometryType geometryType) {
		geometryTypeSet.add(geometryType);
	}
	
	public HashSet<GeometryType> getGeometryTypeSet() {
		return geometryTypeSet;
	}

	public int getNumberOfFoundBuildings() {
		return numberOfFoundBuildings;
	}

	public int getNumberOfFoundBuildingParts() {
		return numberOfFoundBuildingParts;
	}
	
	public int getNumberOfProcessedGeometries() {
		return numberOfProcessedGeometries;
	}

	@Override
	public void validate() throws ValidationException {
		try {
			SchemaHandler schemaHandler = SchemaHandler.newInstance();
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(schemaHandler.getSchemaSources());
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(citygml));
		} catch (SAXParseException e) {
			String message = "XML parser exception during CityGML validation.\n"
					+ "[" + e.getLineNumber() + ", " + e.getColumnNumber() + "] "
					+ e.getMessage() + "\n";
			throw new ValidationException(message, e);
		} catch (SAXException e) {
			throw new ValidationException("XML parser exception during CityGML validation.\n" + e.getMessage(), e);
		} catch (IOException e) {
			throw new ValidationException("IO exception during CityGML validation.", e);
		}
	}

	@Override
	public LinkedList<Geometry> extractGeometries() throws ConversionException {
		if (lodSet.isEmpty()) {
			throw new ConversionException("No level of detail defined.");
		}
		if (geometryTypeSet.isEmpty()) {
			throw new ConversionException("No geometry type defined.");
		}
		BuildingWalker walker = new BuildingWalker(this);
		try {
			CityGMLContext context = new CityGMLContext();
			CityGMLBuilder builder = context.createCityGMLBuilder();
			CityGMLInputFactory in = builder.createCityGMLInputFactory();
			CityGMLReader reader = in.createCityGMLReader(name, citygml);
			while (reader.hasNext()) {
				CityGML _citygml = reader.nextFeature();
				if (_citygml.getCityGMLClass() == CityGMLClass.CITY_MODEL) {
					CityModel cityModel = (CityModel) _citygml;
					cityModel.accept(walker);
				}
			}
			reader.close();
		} catch (JAXBException | CityGMLReadException e) {
			throw new ConversionException("Failed to read CityGML stream.", e);
		}
		numberOfFoundBuildings = walker.getBuildingsTotal();
		numberOfFoundBuildingParts = walker.getBuildingParts();
		numberOfProcessedGeometries = walker.getProcessedGeometries();
		return walker.getGeometryList();
	}
	
	public static CityGMLSource fromFile(String input) throws IOException {
		Path in = Paths.get(input);
		CityGMLSource citygml = new CityGMLSource(input, Files.newInputStream(in));
		return citygml;
	}

}