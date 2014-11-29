DELIMITER $$
DROP PROCEDURE IF EXISTS `Hierarchy` $$
CREATE PROCEDURE `Hierarchy` (IN GivenID INT, IN initial INT)
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE next_id INT;

    -- CURSOR TO LOOP THROUGH RESULTS --
    DECLARE cur1 CURSOR FOR SELECT marketGroupID FROM eve_static.invMarketGroups WHERE parentGroupID = GivenID;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    -- CREATE A TEMPORARY TABLE TO HOLD RESULTS --
    IF initial=1 THEN
        -- MAKE SURE TABLE DOESN'T CONTAIN OUTDATED INFO IF IT EXISTS (USUALLY ON ERROR) --
        DROP TABLE IF EXISTS OUT_TEMP;
        CREATE TEMPORARY TABLE OUT_TEMP (marketGroupID int, marketGroupName varchar(512));
    END IF;

    -- ADD OURSELF TO THE TEMPORARY TABLE --
    INSERT INTO OUT_TEMP SELECT marketGroupID, marketGroupName FROM eve_static.invMarketGroups WHERE marketGroupID = GivenID;

    -- AND LOOP THROUGH THE CURSOR --
    OPEN cur1;
    read_loop: LOOP
        FETCH cur1 INTO next_id;

        -- NO ROWS FOUND, LEAVE LOOP --
        IF done THEN
        LEAVE read_loop;
        END IF;

        -- NEXT ROUND --
        CALL Hierarchy(next_id, 0);
    END LOOP;

    CLOSE cur1;

    -- THIS IS THE INITIAL CALL, LET'S GET THE RESULTS --
    IF initial=1 THEN
    SELECT * FROM OUT_TEMP;
        -- CLEAN UP AFTER OURSELVES --
        -- DROP TABLE OUT_TEMP;
    END IF;
END $$
DELIMITER ;

CALL Hierarchy(4,1);

SELECT concat('"',typeID, '":"', typeName, '",') FROM eve_static.invTypes
where marketGroupID in (select marketGroupID from out_temp);