select concat('"',typeID, '":"', replace(typeName, '"', '\\"'), '",') from invTypes limit 100000;