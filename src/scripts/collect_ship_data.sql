CALL Hierarchy(4,1);

SELECT concat('"',typeID, '":"', typeName, '",') FROM eve_static.invTypes
where marketGroupID in (select marketGroupID from out_temp);