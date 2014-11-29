CALL Hierarchy(477,1);

SELECT concat('"',typeID,'":"',typeName,'",')
FROM eve_static.invTypes
WHERE marketGroupID in (select marketGroupID from out_temp)
UNION
SELECT concat('"',t.typeID,'":"',t.typeName,'",')
FROM eve_static.invGroups g
JOIN eve_Static.invTypes t ON t.groupID=g.groupID
WHERE anchorable=1;