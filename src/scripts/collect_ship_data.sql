CALL Hierarchy(4,1);

SELECT concat('"',typeID, '":"', typeName, '",') FROM eve_static.invTypes
WHERE marketGroupID IN (SELECT marketGroupID FROM out_temp)
UNION
SELECT concat('"',typeID,'":"',typeName,'",') FROM eve_static.invTypes
WHERE typeName LIKE '%Capsule%';