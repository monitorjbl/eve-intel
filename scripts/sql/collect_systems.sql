SELECT concat('"',s.solarSystemID,'":{"name":"',s.solarSystemName,'","regionId":"',s.regionID,'","regionName":"',r.regionName,'"},') 
FROM mapSolarSystems s
join mapRegions r on r.regionID = s.regionID
limit 100000000;