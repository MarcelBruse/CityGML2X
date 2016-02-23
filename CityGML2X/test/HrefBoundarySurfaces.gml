<?xml version="1.0" encoding="UTF-8"?>
<CityModel
		xmlns="http://www.opengis.net/citygml/2.0"
		xmlns:xAL="urn:oasis:names:tc:ciq:xsdschema:xAL:2.0" 
		xmlns:gen="http://www.opengis.net/citygml/generics/2.0" 
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
		xmlns:xlink="http://www.w3.org/1999/xlink"
		xmlns:bldg="http://www.opengis.net/citygml/building/2.0" 
		xmlns:gml="http://www.opengis.net/gml">
	<cityObjectMember>
		<bldg:Building>
			<bldg:lod2Solid>
				<gml:Solid>
					<gml:exterior>
						<gml:CompositeSurface>
							<gml:surfaceMember>
								<gml:Polygon gml:id="_1">
									<gml:exterior>
										<gml:LinearRing>
											<gml:posList srsDimension="3">
												7.155 -7.675 -3.5175 
												-7.155 -7.675 -3.5175 
												-7.155 -7.675 3.5175 
												7.155 -7.675 3.5175 
												7.155 -7.675 -3.5175
											</gml:posList>
										</gml:LinearRing>
									</gml:exterior>
								</gml:Polygon>
							</gml:surfaceMember>
							<gml:surfaceMember>
								<gml:Polygon gml:id="_2">
									<gml:exterior>
										<gml:LinearRing>
											<gml:posList srsDimension="3">
												-7.155 -7.675 3.5175 
												-7.155 3.115 3.5175 
												7.155 3.115 3.5175 
												7.155 -7.675 3.5175 
												-7.155 -7.675 3.5175
											</gml:posList>
										</gml:LinearRing>
									</gml:exterior>
								</gml:Polygon>
							</gml:surfaceMember>
							<gml:surfaceMember>
								<gml:Polygon gml:id="_3">
									<gml:exterior>
										<gml:LinearRing>
											<gml:posList srsDimension="3">
												7.155 -7.675 -3.5175 
												7.155 3.115 -3.5175 
												-7.155 3.115 -3.5175 
												-7.155 -7.675 -3.5175 
												7.155 -7.675 -3.5175
											</gml:posList>
										</gml:LinearRing>
									</gml:exterior>
								</gml:Polygon>
							</gml:surfaceMember>
							<gml:surfaceMember>
								<gml:Polygon gml:id="_4">
									<gml:exterior>
										<gml:LinearRing>
											<gml:posList srsDimension="3">
												7.155 -7.675 -3.5175 
												7.155 -7.675 3.5175 
												7.155 3.115 3.5175 
												7.155 7.675 0.0 
												7.155 3.115 -3.5175 
												7.155 -7.675 -3.5175
											</gml:posList>
										</gml:LinearRing>
									</gml:exterior>
								</gml:Polygon>
							</gml:surfaceMember>
							<gml:surfaceMember>
								<gml:Polygon gml:id="_5">
									<gml:exterior>
										<gml:LinearRing>
											<gml:posList srsDimension="3">
												-7.155 3.115 -3.5175 
												-7.155 7.675 0.0 
												-7.155 3.115 3.5175 
												-7.155 -7.675 3.5175 
												-7.155 -7.675 -3.5175 
												-7.155 3.115 -3.5175
											</gml:posList>
										</gml:LinearRing>
									</gml:exterior>
								</gml:Polygon>
							</gml:surfaceMember>
							<gml:surfaceMember>
								<gml:Polygon gml:id="_6">
									<gml:exterior>
										<gml:LinearRing>
											<gml:posList srsDimension="3">
												7.155 3.115 -3.5175 
												7.155 7.675 0.0 
												-7.155 7.675 0.0 
												-7.155 3.115 -3.5175 
												7.155 3.115 -3.5175
											</gml:posList>
										</gml:LinearRing>
									</gml:exterior>
								</gml:Polygon>
							</gml:surfaceMember>
							<gml:surfaceMember>
								<gml:Polygon gml:id="_7">
									<gml:exterior>
										<gml:LinearRing>
											<gml:posList srsDimension="3">
												-7.155 7.675 0.0 
												7.155 7.675 0.0 
												7.155 3.115 3.5175 
												-7.155 3.115 3.5175 
												-7.155 7.675 0.0
											</gml:posList>
										</gml:LinearRing>
									</gml:exterior>
								</gml:Polygon>
							</gml:surfaceMember>
						</gml:CompositeSurface>
					</gml:exterior>
				</gml:Solid>
			</bldg:lod2Solid>
			<bldg:boundedBy>
				<bldg:GroundSurface>
					<bldg:lod2MultiSurface>
						<gml:MultiSurface srsDimension="3">
							<gml:surfaceMember xlink:href="#_1"/>
						</gml:MultiSurface>
					</bldg:lod2MultiSurface>
				</bldg:GroundSurface>
			</bldg:boundedBy>
			<bldg:boundedBy>
				<bldg:WallSurface>
					<bldg:lod2MultiSurface>
						<gml:MultiSurface srsDimension="3">
							<gml:surfaceMember xlink:href="#_2"/>
							<gml:surfaceMember xlink:href="#_3"/>
							<gml:surfaceMember xlink:href="#_4"/>
							<gml:surfaceMember xlink:href="#_5"/>
						</gml:MultiSurface>
					</bldg:lod2MultiSurface>
				</bldg:WallSurface>
			</bldg:boundedBy>
			<bldg:boundedBy>
				<bldg:RoofSurface>
					<bldg:lod2MultiSurface>
						<gml:MultiSurface srsDimension="3">
							<gml:surfaceMember xlink:href="#_6"/>
							<gml:surfaceMember xlink:href="#_7"/>
						</gml:MultiSurface>
					</bldg:lod2MultiSurface>
				</bldg:RoofSurface>
			</bldg:boundedBy>
		</bldg:Building>
	</cityObjectMember>
</CityModel>
