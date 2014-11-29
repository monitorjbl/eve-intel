SELECT concat('"',s.solarSystemID,'":{"name":"',s.solarSystemName,'","regionId":"',s.regionID,'","regionName":"',r.regionName,'"},') 
FROM eve_static.mapSolarSystems s
join eve_static.mapRegions r on r.regionID = s.regionID
limit 100000;