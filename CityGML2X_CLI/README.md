# CityGML2X

A command line interface for the CityGML2X library

```
usage: java -jar c2x -f <format> -i <input> -l <lod> [-o <output>] [-s] -t
       <geomtype> [-v]
 -f,--format <format>       Format of the output file. Possible values are
                            X3D and COLLADA.
 -i,--input <input>         CityGML file to be converted.
 -l,--lod <lod>             Level of detail to be converted. Possible
                            values are LOD1 and LOD2.
 -o,--output <output>       File path of the output file.
 -s,--split                 Generate one scene node for each building (X3D
                            only).
 -t,--geomtype <geomtype>   Geometry type to be converted. Possible values
                            are SOLID and MULTI_SURFACE.
 -v,--validate              Validate the CityGML file.
```
