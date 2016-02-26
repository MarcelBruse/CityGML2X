/*
 * CityGML2X_CLI - The command line interface for the CityGML2X library. 
 * https://github.com/900k/CityGML2X_CLI
 * 
 * Copyright (c) 2016, Marcel Bruse <marcel.bruse@posteo.de>
 *
 * CityGML2X_CLI is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * CityGML2X_CLI is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * CityGML2X_CLI. If not, see <http://www.gnu.org/licenses/>.
 */
package de.bruse.c2x.cli;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.bruse.c2x.ConversionException;
import de.bruse.c2x.Converter;
import de.bruse.c2x.ValidationException;
import de.bruse.c2x.converter.citygml.CityGMLSource;
import de.bruse.c2x.converter.citygml.GeometryType;
import de.bruse.c2x.converter.citygml.LevelOfDetail;
import de.bruse.c2x.converter.collada.ColladaWriter;
import de.bruse.c2x.converter.x3d.X3DWriter;

public class Main {
	
	public static final String INPUT = "input";
	
	public static final String OUTPUT = "output";
	
	public static final String FORMAT = "format";
	
	public static final String LEVEL_OF_DETAIL = "lod";
	
	public static final String GEOMETRY_TYPE = "geomtype";
	
	public static final String SPLIT = "split";
	
	public static final String VALIDATE = "validate";
	
	public static final String X3D = "X3D";
	
	public static final String COLLADA = "COLLADA";
	
	public static final String X3D_FILE_EXT = ".x3d";
	
	public static final String COLLADA_FILE_EXT = ".dae";
	
	public static final boolean HAS_ARGS = true;
	
	public static final boolean NO_ARGS = false;
	
	public static final int ONE = 1;

	public static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar c2x.jar", options, true);
	}

	public static void main(String[] args) {
		CommandLineParser parser = new GnuParser();
		Options options = new Options();
		
		Option input = new Option("i", INPUT, HAS_ARGS, "CityGML file to be converted.");
		input.setArgs(ONE);
		input.setArgName(INPUT);
		input.setRequired(Boolean.TRUE);
		options.addOption(input);
		
		Option levelOfDetail = new Option("l", LEVEL_OF_DETAIL, HAS_ARGS, "Level of detail to be converted. Possible values are LOD1 and LOD2.");
		levelOfDetail.setArgs(ONE);
		levelOfDetail.setArgName(LEVEL_OF_DETAIL);
		levelOfDetail.setRequired(Boolean.TRUE);
		options.addOption(levelOfDetail);
		
		Option geometryType = new Option("t", GEOMETRY_TYPE, HAS_ARGS, "Geometry type to be converted. Possible values are SOLID and MULTI_SURFACE.");
		geometryType.setArgs(ONE);
		geometryType.setArgName(GEOMETRY_TYPE);
		geometryType.setRequired(Boolean.TRUE);
		options.addOption(geometryType);
		
		Option output = new Option("o", OUTPUT, HAS_ARGS, "File path of the output file.");
		output.setArgs(ONE);
		output.setArgName(OUTPUT);
		output.setRequired(Boolean.FALSE);
		options.addOption(output);

		Option targetFormat = new Option("f", FORMAT, HAS_ARGS, "Format of the output file. Possible values are X3D and COLLADA.");
		targetFormat.setArgs(ONE);
		targetFormat.setArgName(FORMAT);
		targetFormat.setRequired(Boolean.TRUE);
		options.addOption(targetFormat);
		
		Option split = new Option("s", SPLIT, NO_ARGS, "Generate one scene node for each building (X3D only).");
		split.setArgName(SPLIT);
		split.setRequired(Boolean.FALSE);
		options.addOption(split);
		
		Option validate = new Option("v", VALIDATE, NO_ARGS, "Validate the CityGML file.");
		validate.setArgName(VALIDATE);
		validate.setRequired(Boolean.FALSE);
		options.addOption(validate);
		
		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption(INPUT)
					&& line.hasOption(LEVEL_OF_DETAIL)
					&& line.hasOption(GEOMETRY_TYPE)
					&& line.hasOption(FORMAT)) {
				String inputValue = line.getOptionValue(INPUT);
				String levelOfDetailValue = line.getOptionValue(LEVEL_OF_DETAIL);
				String geometryTypeValue = line.getOptionValue(GEOMETRY_TYPE);
				String targetFormatValue = line.getOptionValue(FORMAT);
				String outputValue = line.getOptionValue(OUTPUT);
				LevelOfDetail lod = LevelOfDetail.valueOf(levelOfDetailValue);
				GeometryType type = GeometryType.valueOf(geometryTypeValue);
				if (Objects.isNull(lod)
						|| Objects.isNull(type) 
						|| (!targetFormatValue.equals(X3D) 
								&& !targetFormatValue.equals(COLLADA))) {
					printHelp(options);
				} else {
					if (line.hasOption(VALIDATE)) {
						triggerValidation(inputValue);
					}
					if (targetFormatValue.equals(X3D)) {
						boolean splitValue = false;
						if (line.hasOption(SPLIT)) {
							splitValue = true;
						}
						if (Objects.isNull(outputValue) || outputValue.isEmpty()) {
							outputValue = inputValue + X3D_FILE_EXT;
						}
						triggerX3DConversion(inputValue, lod, type, outputValue, splitValue);
					} else if (targetFormatValue.equals(COLLADA)) {
						if (Objects.isNull(outputValue) || outputValue.isEmpty()) {
							outputValue = inputValue + COLLADA_FILE_EXT;
						}
						triggerColladaConversion(inputValue, lod, type, outputValue);
					}
					System.out.println("Conversion succeeded.");
				}
			} else {
				printHelp(options);
			}
		} catch (ParseException | IllegalArgumentException e) {
			printHelp(options);
		} catch (ValidationException e) {
			System.out.println("Input file is invalid. Operation canceled.\n" + e.getMessage());
		} catch (ConversionException e) {
			String message = "Failed to convert CityGML.";
			if (Objects.nonNull(e.getMessage())) {
				message += " " + e.getMessage();				
			}
			System.out.println(message);
		} catch (IOException e) {
			System.out.println("Failed to read from file '" + input + "'.");
		}
	}
	
	private static void triggerValidation(String input) throws ValidationException, IOException {
		CityGMLSource citygml = CityGMLSource.fromFile(input);
		citygml.validate();
		System.out.println("Input file is valid.");
	}
	
	private static void triggerX3DConversion(String input, LevelOfDetail lod, GeometryType geometryType, String output,
			boolean split) throws ConversionException, IOException {
		CityGMLSource citygml = CityGMLSource.fromFile(input);
		citygml.addGeometryType(geometryType);
		citygml.addLevelOfDetail(lod);
		Path out = Paths.get(output);
		X3DWriter writer = new X3DWriter(out, !split);
		Converter converter = new Converter(writer);
		converter.addSource(citygml);
		converter.convert();
		printSummary(citygml);
	}
	
	private static void triggerColladaConversion(String input, LevelOfDetail lod, GeometryType geometryType, String output)
			throws ConversionException, IOException {
		CityGMLSource citygml = CityGMLSource.fromFile(input);
		citygml.addGeometryType(geometryType);
		citygml.addLevelOfDetail(lod);
		Path out = Paths.get(output);
		ColladaWriter writer = new ColladaWriter(out);
		Converter converter = new Converter(writer);
		converter.addSource(citygml);
		converter.convert();
		printSummary(citygml);
	}
	
	private static void printSummary(CityGMLSource citygml) {
		System.out.println(citygml.getNumberOfFoundBuildings() + " building(s) found.");
		System.out.println(citygml.getNumberOfFoundBuildingParts() + " building part(s) found.");
		System.out.println(citygml.getNumberOfProcessedGeometries() + " geometry(s) processed.");		
	}

}