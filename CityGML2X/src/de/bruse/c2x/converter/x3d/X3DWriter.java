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
package de.bruse.c2x.converter.x3d;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.bruse.c2x.Color;
import de.bruse.c2x.ConversionException;
import de.bruse.c2x.Geometry;
import de.bruse.c2x.Polygon;
import de.bruse.c2x.Vertex;
import de.bruse.c2x.Writer;
import eu.semanticity.converter.x3d.Appearance;
import eu.semanticity.converter.x3d.ColorRGBA;
import eu.semanticity.converter.x3d.Coordinate;
import eu.semanticity.converter.x3d.Group;
import eu.semanticity.converter.x3d.IndexedFaceSet;
import eu.semanticity.converter.x3d.Material;
import eu.semanticity.converter.x3d.ObjectFactory;
import eu.semanticity.converter.x3d.ProfileNames;
import eu.semanticity.converter.x3d.Scene;
import eu.semanticity.converter.x3d.Shape;
import eu.semanticity.converter.x3d.X3D;

public class X3DWriter implements Writer {

	public static final String X3D_VERSION = "3.3";
	
	public static final String EMPTY = "";
	
	public static final String SPACE = " ";
	
	public static final String POLYGON_DELIMITER = "-1";
	
	public static final String OUTPUT_FILE_PATH = "output.x3d";
	
	public static final boolean WRAP_GEOMETRIES = true;
	
	public static final boolean SPLIT_GEOMETRIES = false;
	
	public final ObjectFactory objectFactory = new ObjectFactory();
	
	private Path outputFilePath;
	
	private boolean wrapGeometries;
	
	private X3D x3d;
	
	private Group group;
	
	private Shape shape;
	
	private IndexedFaceSet indexedFaceSet;
	
	private Coordinate coordinate;
		
	private ColorRGBA colorRGBA;
	
	private HashMap<Vertex, Integer> vertexMap = new HashMap<>();
	
	private HashMap<String, Integer> colorMap = new HashMap<>();
	
	private int vertexCounter;
	
	private int colorCounter;
	
	private StringBuilder coord = new StringBuilder();
	
	private StringBuilder coordIndex = new StringBuilder();
	
	private StringBuilder color = new StringBuilder();
	
	private StringBuilder colorIndex = new StringBuilder();
	
	private String coordDelimiter = EMPTY;
	
	private String colorDelimiter = EMPTY;
	
	public X3DWriter() {
		this(Paths.get(OUTPUT_FILE_PATH), WRAP_GEOMETRIES);
	}
	
	public X3DWriter(Path outputFilePath, boolean wrapGeometies) {
		this.outputFilePath = outputFilePath;
		this.wrapGeometries = wrapGeometies;
	}
	
	public void write(List<Geometry> geometryList) throws ConversionException {
		if (geometryList.isEmpty()) {
			throw new ConversionException("Found no geometries to write.");
		}
		initX3DDocument();
		visitGeometries(geometryList);
		try {
			JAXBContext context = JAXBContext.newInstance(x3d.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			FileOutputStream fos = new FileOutputStream(outputFilePath.toString());
			marshaller.marshal(x3d, fos);
			fos.close();
		} catch (JAXBException e) {
			throw new ConversionException("Object tree creation or marshalling failed.", e);
		} catch (IOException e) {
			throw new ConversionException("Creation of output file + '" + outputFilePath + "' failed.", e);
		}
	}
	
	private void initX3DDocument() {
		x3d = objectFactory.createX3D();
		x3d.setVersion(X3D_VERSION);
		x3d.setProfile(ProfileNames.INTERCHANGE);
		Scene scene = objectFactory.createScene();
		x3d.setScene(scene);
		group = objectFactory.createGroup();
		scene.getMetadataBooleanOrMetadataDoubleOrMetadataFloat().add(group);				
	}
	
	private void visitGeometries(List<Geometry> geometryList) {
		initShape();
		for (Geometry geometry : geometryList) {
			visitPolygons(geometry.getPolygonList());
			if (!wrapGeometries) {
				flushShape();			
				initShape();	
			}
		}
		if (wrapGeometries) {
			flushShape();
		}
	}
	
	private void initShape() {
		shape = objectFactory.createShape();
		Appearance appearance = objectFactory.createAppearance();
		shape.getRest().add(appearance);
		Material material = objectFactory.createMaterial();
		material.setDiffuseColor(Color.WHITE.toString());
		appearance.getAppearanceChildContentModel().add(material);
		indexedFaceSet = objectFactory.createIndexedFaceSet();
		indexedFaceSet.setSolid(Boolean.FALSE);
		indexedFaceSet.setColorPerVertex(Boolean.FALSE);
		indexedFaceSet.setConvex(Boolean.FALSE);
		shape.getRest().add(indexedFaceSet);
		coordinate = objectFactory.createCoordinate();
		indexedFaceSet.getComposedGeometryContentModel().add(coordinate);
		colorRGBA = objectFactory.createColorRGBA();
		indexedFaceSet.getComposedGeometryContentModel().add(colorRGBA);
		vertexMap = new HashMap<>();
		vertexCounter = 0;
		colorMap = new HashMap<>();
		colorCounter = 0;
		coord = new StringBuilder();
		coordIndex = new StringBuilder();
		coordDelimiter = EMPTY;
		color = new StringBuilder();
		colorIndex = new StringBuilder();
		colorDelimiter = EMPTY;
	}
	
	private void flushShape() {
		group.getBackgroundOrColorInterpolatorOrCoordinateInterpolator().add(shape);
		coordinate.setPoint(coord.toString());
		indexedFaceSet.setCoordIndex(coordIndex.toString());
		colorRGBA.setColor(color.toString());
		indexedFaceSet.setColorIndex(colorIndex.toString());
	}
	
	private void visitPolygons(List<Polygon> polygonList) {
		for (Polygon polygon : polygonList) {
			visitVertices(polygon.getVertexList());
			setPolygonColor(polygon);
		}
	}
	
	private void visitVertices(List<Vertex> vertexList) {
		for (Vertex vertex : vertexList) {
			int k = vertexCounter;
			if (vertexMap.containsKey(vertex)) {
				k = vertexMap.get(vertex);
			} else {
				coord.append(coordDelimiter)
				.append(vertex.x).append(SPACE)
				.append(vertex.y).append(SPACE)
				.append(vertex.z);
				vertexMap.put(vertex, vertexCounter);
				vertexCounter++;
			}
			coordIndex.append(coordDelimiter).append(k);
			coordDelimiter = SPACE;
		}
		coordIndex.append(SPACE).append(POLYGON_DELIMITER);
	}
	
	private void setPolygonColor(Polygon polygon) {
		String colorKey = polygon.getColor().toString();
		int l = colorCounter;
		if (colorMap.containsKey(colorKey)) {
			l = colorMap.get(colorKey);
		} else {
			color.append(colorDelimiter).append(colorKey);
			colorMap.put(colorKey, colorCounter);
			colorCounter++;
		}
		colorIndex.append(colorDelimiter).append(l);
		colorDelimiter = SPACE;
	}

}