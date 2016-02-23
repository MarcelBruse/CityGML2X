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
package de.bruse.c2x.converter.collada;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.bruse.c2x.Color;
import de.bruse.c2x.ConversionException;
import de.bruse.c2x.Geometry;
import de.bruse.c2x.Polygon;
import de.bruse.c2x.Vertex;
import de.bruse.c2x.Writer;

public class ColladaWriter implements Writer {
	
	public static final String OUTPUT_FILE_PATH = "output.dae";
	
	private Path outputFilePath;
	
	private int vertexCounter = 0;
	
	private int geometryCounter = 0;
	
	private int normalCounter = 0;
	
	private int materialCounter = 0;
	
	private Document doc;
	
	private Element root;
	
	private Element libraryGeometries;
	
	private Element visualScene;
	
	private Element libraryEffects;
	
	private Element libraryMaterials;
	
	private LinkedList<String> polygonList = new LinkedList<>();
	
	private HashMap<Vertex, Integer> indexMap = new HashMap<>();
	
	private HashMap<Polygon, Integer> normalIndexMap = new HashMap<>();
	
	private HashMap<Color, String> effectMap = new HashMap<>();
	
	private HashMap<Color, LinkedList<Polygon>> sortedPolygons = new HashMap<>();

	public ColladaWriter() {
		this(Paths.get(OUTPUT_FILE_PATH));
	}
	
	public ColladaWriter(Path outputFilePath) {
		this.outputFilePath = outputFilePath;
	}
	
	@Override
	public void write(List<Geometry> geometryList) throws ConversionException {
		if (geometryList.isEmpty()) {
			throw new ConversionException("Found no geometries to write.");
		}
		for (Geometry g : geometryList) {		
			double minX = Double.MAX_VALUE;
			double maxX = Double.MIN_VALUE;
			double minY = Double.MAX_VALUE;
			double maxY = Double.MIN_VALUE;
			double minZ = Double.MAX_VALUE;
			double maxZ = Double.MIN_VALUE;
			
			for (Vertex v : g.getVertexList()) {
				if (v.x < minX) {
					minX = v.x;
				}
				if (v.x > maxX) {
					maxX = v.x;
				}
				if (v.y < minY) {
					minY = v.y;
				}
				if (v.y > maxY) {
					maxY = v.y;
				}
				if (v.z < minZ) {
					minZ = v.z;
				}
				if (v.z > maxZ) {
					maxZ = v.z;
				}
			}
			
			double offsetX = (maxX - minX) / 2;
			double offsetY = (maxY - minY) / 2;
			for (Vertex v : g.getVertexList()) {
				v.x = v.x - minX - offsetX;
				v.y = v.y - minY - offsetY;
				v.z = v.z - minZ;
			}
			
			for (Polygon p : g.getPolygonList()) {
				if (p.getVertexList().size() > 2) {
					Vertex a = p.getVertexList().get(0);
					Vertex b = p.getVertexList().get(1);
					Vertex c = p.getVertexList().get(2);
					Vertex s = new Vertex(
						b.x - a.x,
						b.y - a.y,
						b.z - a.z
					);
					Vertex t = new Vertex(
						c.x - a.x,
						c.y - a.y,
						c.z - a.z
					);
					double vx = s.y * t.z - s.z * t.y;
					double vy = s.z * t.x - s.x * t.z;
					double vz = s.x * t.y - s.y * t.x;
					Vertex v = new Vertex(vx, vy, vz);
					double l = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
					v.x = v.x / l;
					v.y = v.y / l;
					v.z = v.z / l;
					p.setNormal(v);
				}
			}
		}
		
		for (Geometry geometry : geometryList) {
			polygonList = new LinkedList<>();
			indexMap = new HashMap<>();
			effectMap = new HashMap<>();
			normalIndexMap = new HashMap<>();
			sortedPolygons = new HashMap<>();
			vertexCounter = 0;
			geometryCounter = 0;
			materialCounter = 0;
			normalCounter = 0;
			writeGeometry(geometry);			
		}
	}

	private void writeGeometry(Geometry geometry) throws ConversionException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new ConversionException("Unable to instantiate document builder.");
		}
		doc = builder.newDocument();
		root = doc.createElement("COLLADA");
		root.setAttribute("xmlns", "http://www.collada.org/2005/11/COLLADASchema");
		root.setAttribute("version", "1.4.1");
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.setAttribute("xsi:schemaLocation", "http://www.collada.org/2005/11/COLLADASchema http://www.khronos.org/files/collada_schema_1_4");
		doc.appendChild(root);
		
		Element asset = doc.createElement("asset");
		root.appendChild(asset);
		
		Element contributor = doc.createElement("contributor");
		asset.appendChild(contributor);
		
		Element author = doc.createElement("author"); 
		author.appendChild(doc.createTextNode("Marcel Bruse"));
		contributor.appendChild(author);
		
		Element created = doc.createElement("created");
		created.appendChild(doc.createTextNode("2015-10-12T09:30:40.0Z"));
		asset.appendChild(created);
		
		Element modified = doc.createElement("modified");
		modified.appendChild(doc.createTextNode("2015-10-12T09:30:40.0Z"));
		asset.appendChild(modified);
		
		Element unit = doc.createElement("unit");
		unit.setAttribute("name", "meter");
		unit.setAttribute("meter", "1.0");
		asset.appendChild(unit);
		
		Element upAxis = doc.createElement("up_axis");
		upAxis.appendChild(doc.createTextNode("Z_UP"));
		asset.appendChild(upAxis);
		
		Element libraryVisualScenes = doc.createElement("library_visual_scenes");
		root.appendChild(libraryVisualScenes);
		
		libraryEffects = doc.createElement("library_effects");
		root.appendChild(libraryEffects);
		
		libraryMaterials = doc.createElement("library_materials");
		root.appendChild(libraryMaterials);
		
		visualScene = doc.createElement("visual_scene");
		visualScene.setAttribute("id", "main_scene");
		visualScene.setAttribute("name", "main_scene");
		libraryVisualScenes.appendChild(visualScene);
		
		libraryGeometries = doc.createElement("library_geometries");
		createGeometryElement(geometry);
		root.appendChild(libraryGeometries);
		
		Element scene = doc.createElement("scene");
		root.appendChild(scene);
		
		Element instanceVisualScene = doc.createElement("instance_visual_scene");
		instanceVisualScene.setAttribute("url", "#main_scene");
		scene.appendChild(instanceVisualScene);
		
		createEffectLibrary();
		
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			StringWriter writer = new StringWriter();
			StreamResult streamResult = new StreamResult(writer);
			transformer.transform(new DOMSource(doc), streamResult);
			PrintWriter printWriter = new PrintWriter(outputFilePath.toFile());
			printWriter.print(writer.toString());
			printWriter.close();
		} catch (FileNotFoundException | TransformerException e) {
			throw new ConversionException("Unable to write the output file.");
		}
	}
	
	private void createGeometryElement(Geometry geometry) {
		String geometryId = "FaceSet_" + geometryCounter;
		
		Element geo = doc.createElement("geometry");
		geo.setAttribute("id", geometryId);
		libraryGeometries.appendChild(geo);
		
		Element mesh = doc.createElement("mesh");
		geo.appendChild(mesh);
		
		String positionId = geometryId + "_positions";
		Element source = doc.createElement("source");
		source.setAttribute("id", positionId);
		mesh.appendChild(source);
		
		String arrayId = positionId + "_array";
		Element floatArray = doc.createElement("float_array");
		floatArray.setAttribute("id", arrayId);
		floatArray.setAttribute("count", String.valueOf(geometry.getVertexList().size() * 3));
		String positions = geometry.getVertexList().stream()
				.map(vertex -> {
					indexMap.put(vertex, vertexCounter++);
					return String.join(" ", String.valueOf(vertex.x), String.valueOf(vertex.y),
							String.valueOf(vertex.z));
				})
				.collect(Collectors.joining(" "));
		floatArray.appendChild(doc.createTextNode(positions));
		source.appendChild(floatArray);
		
		Element technique = doc.createElement("technique_common");
		source.appendChild(technique);
		
		Element accessor = doc.createElement("accessor");
		accessor.setAttribute("count", String.valueOf(geometry.getVertexList().size()));
		accessor.setAttribute("stride", "3");
		accessor.setAttribute("source", "#" + arrayId);
		technique.appendChild(accessor);
		
		Element paramX = doc.createElement("param");
		paramX.setAttribute("name", "X");
		paramX.setAttribute("type", "float");
		accessor.appendChild(paramX);

		Element paramY = doc.createElement("param");
		paramY.setAttribute("name", "Y");
		paramY.setAttribute("type", "float");
		accessor.appendChild(paramY);

		Element paramZ = doc.createElement("param");
		paramZ.setAttribute("name", "Z");
		paramZ.setAttribute("type", "float");
		accessor.appendChild(paramZ);
		
		String normalsId = geometryId + "_normals";
		source = doc.createElement("source");
		source.setAttribute("id", normalsId);
		mesh.appendChild(source);
		
		arrayId = normalsId + "_array";
		Element normalArray = doc.createElement("float_array");
		normalArray.setAttribute("id", arrayId);
		normalArray.setAttribute("count", String.valueOf(geometry.getPolygonList().size() * 3));
		String normals = geometry.getPolygonList().stream()
				.map(polygon -> {
					if (Objects.nonNull(polygon.getNormal())) {
						normalIndexMap.put(polygon, normalCounter++);
						Vertex normal = polygon.getNormal();
						return String.join(" ", String.valueOf(normal.x), String.valueOf(normal.y),
								String.valueOf(normal.z));						
					}
					return "";
				})
				.collect(Collectors.joining(" "));
		normalArray.appendChild(doc.createTextNode(normals));
		source.appendChild(normalArray);
		
		technique = doc.createElement("technique_common");
		source.appendChild(technique);
		
		accessor = doc.createElement("accessor");
		accessor.setAttribute("count", String.valueOf(geometry.getPolygonList().size()));
		accessor.setAttribute("source", "#" + arrayId);
		accessor.setAttribute("stride", "3");
		technique.appendChild(accessor);
		
		paramX = doc.createElement("param");
		paramX.setAttribute("name", "X");
		paramX.setAttribute("type", "float");
		accessor.appendChild(paramX);

		paramY = doc.createElement("param");
		paramY.setAttribute("name", "Y");
		paramY.setAttribute("type", "float");
		accessor.appendChild(paramY);

		paramZ = doc.createElement("param");
		paramZ.setAttribute("name", "Z");
		paramZ.setAttribute("type", "float");
		accessor.appendChild(paramZ);
				
		String verticesId = geometryId + "_vertices";
		Element vertices = doc.createElement("vertices");
		vertices.setAttribute("id", verticesId);
		mesh.appendChild(vertices);
		
		Element inputPosition = doc.createElement("input");
		inputPosition.setAttribute("semantic", "POSITION");
		inputPosition.setAttribute("source", "#" + positionId);
		vertices.appendChild(inputPosition);
		
		Element node = doc.createElement("node");
		node.setAttribute("id", geometryId + "_node");
		node.setAttribute("name", geometryId + "_node");
		node.setAttribute("type", "NODE");
		visualScene.appendChild(node);
		
		Element instanceGeometry = doc.createElement("instance_geometry");
		instanceGeometry.setAttribute("url", "#" + geometryId);
		node.appendChild(instanceGeometry);
		
		Element bindMaterial = doc.createElement("bind_material");
		instanceGeometry.appendChild(bindMaterial);
		
		Element techniqueCommon = doc.createElement("technique_common");
		bindMaterial.appendChild(techniqueCommon);
		
		geometry.getPolygonList().forEach(polygon -> {
			LinkedList<Polygon> polys = new LinkedList<>();
			if (sortedPolygons.containsKey(polygon.getColor())) {
				polys = sortedPolygons.get(polygon.getColor());
			}
			if (!effectMap.containsKey(polygon.getColor())) {
				effectMap.put(polygon.getColor(), "Material_" + materialCounter++);			
			}
			polys.add(polygon);
			sortedPolygons.put(polygon.getColor(), polys);
		});
		
		sortedPolygons.forEach((color, polys) -> {
			String materialId = effectMap.get(color);
			
			Element polylist = doc.createElement("polylist");
			polylist.setAttribute("material", materialId);
			polylist.setAttribute("count", String.valueOf(polys.size()));
			mesh.appendChild(polylist);
			
			Element inputVertices = doc.createElement("input");
			inputVertices.setAttribute("semantic", "VERTEX");
			inputVertices.setAttribute("source", "#" + verticesId);
			inputVertices.setAttribute("offset", "0");
			polylist.appendChild(inputVertices);
			
			Element inputNormals = doc.createElement("input");
			inputNormals.setAttribute("semantic", "NORMAL");
			inputNormals.setAttribute("source", "#" + normalsId);
			inputNormals.setAttribute("offset", "1");
			polylist.appendChild(inputNormals);
			
			polygonList.clear();
			ArrayList<String> vertexCounts = new ArrayList<>();
			polys.forEach(polygon -> {
				vertexCounts.add(String.valueOf(visitPolygon(polygon)));
			});
			
			Element vcount = doc.createElement("vcount");
			vcount.appendChild(doc.createTextNode(String.join(" ", vertexCounts)));
			polylist.appendChild(vcount);
			
			Element p = doc.createElement("p");
			p.appendChild(doc.createTextNode(String.join(" ", polygonList)));
			polylist.appendChild(p);
			
			Element instanceMaterial = doc.createElement("instance_material");
			instanceMaterial.setAttribute("target", "#" + materialId);
			instanceMaterial.setAttribute("symbol", materialId);
			techniqueCommon.appendChild(instanceMaterial);
		});
	}
	
	private int visitPolygon(Polygon polygon) {
		polygon.getVertexList().forEach(vertex -> {
			polygonList.add(indexMap.get(vertex).toString());
			// TEST
			//polygonList.add(normalIndexMap.get(polygon).toString());
			// TEST
		});
		return polygon.getVertexList().size();
	}
	
	private void createEffectLibrary() {
		effectMap.forEach((color, materialId) -> {
			Element effect = doc.createElement("effect");
			effect.setAttribute("id", materialId + "_fx");
			libraryEffects.appendChild(effect);
			
			Element profileCommon = doc.createElement("profile_COMMON");
			effect.appendChild(profileCommon);
			
			Element technique = doc.createElement("technique");
			technique.setAttribute("sid", "common");
			profileCommon.appendChild(technique);
			
			Element blinn = doc.createElement("blinn");
			technique.appendChild(blinn);
			
			Element ambient = doc.createElement("ambient");
			blinn.appendChild(ambient);
			
			Element ambientColor = doc.createElement("color");
			ambientColor.appendChild(doc.createTextNode(color.toString()));
			ambient.appendChild(ambientColor);
			
			Element diffuse = doc.createElement("diffuse");
			blinn.appendChild(diffuse);
			
			Element diffuseColor = doc.createElement("color");
			diffuseColor.appendChild(doc.createTextNode(color.toString()));
			diffuse.appendChild(diffuseColor);
			
			Element shininess = doc.createElement("shininess");
			blinn.appendChild(shininess);
			
			Element shininessFloat = doc.createElement("float");
			shininessFloat.appendChild(doc.createTextNode("0.1"));
			shininess.appendChild(shininessFloat);
			
			Element material = doc.createElement("material");
			material.setAttribute("id", materialId);
			libraryMaterials.appendChild(material);
			
			Element instanceEffect = doc.createElement("instance_effect");
			instanceEffect.setAttribute("url", "#" + materialId + "_fx");
			material.appendChild(instanceEffect);
		});
		
	}

}