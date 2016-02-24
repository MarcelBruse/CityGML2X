# CityGML2X

A Java library for converting CityGML to X3D and Collada

Features:
* Reads CityGML v2.0
* Writes X3D v3.3
* Validates CityGML input streams
* Converts solid geometries
* Converts multi-surface geometries
* Converts LOD1 and LOD2 geometries
* Converts composite surfaces
* Resolves XLINKs for composite surfaces
* Resolves XLINKs in multi surfaces (surface members)
* Colorizes roofs and wall surfaces during conversion 
* Resolves XLINKS to abstract surfaces and solids
* Converts composite solids
* Converts nested building parts
* Counts the number of processed building parts and geometries
* Eliminates douplicated vertices in the final X3D file which saves a significant amount of disk space
* Wraps all converted building geometries in a single X3D indexed face set (increases performance)
* Generates an individual indexed face sets for each converted building geometry
