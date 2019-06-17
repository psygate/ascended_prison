-- SCRIPT INFORMATION --
-- Types: mysql mariadb
-- Version: 1
-- Upgrades: 0
-- SCRIPT INFORMATION --

START TRANSACTION;
SET foreign_key_checks = 0;

DROP TABLE IF EXISTS ascendedprison_delayed_actions CASCADE;
DROP TABLE IF EXISTS ascendedprison_delayed_actions_location CASCADE;
DROP TABLE IF EXISTS ascendedprison_prison CASCADE;
DROP TABLE IF EXISTS ascendedprison_feeding_cycles CASCADE;
DROP TABLE IF EXISTS ascendedprison_player_log CASCADE;
DROP TABLE IF EXISTS ascendedprison_inventory CASCADE;
DROP TABLE IF EXISTS ascendedprison_inventory_stack CASCADE;
DROP TABLE IF EXISTS ascendedprison_inventory_stack_enchantments CASCADE;
DROP TABLE IF EXISTS ascendedprison_inventory_stack_lore CASCADE;
DROP TABLE IF EXISTS ascendedprison_prison_history CASCADE;

CREATE TABLE ascendedprison_delayed_actions (
  action_id   BIGINT     NOT NULL            AUTO_INCREMENT,
  action_type INTEGER    NOT NULL,
  puuid       BINARY(16) NOT NULL,
  reason      VARCHAR(255),

  PRIMARY KEY (action_id)
);

CREATE TABLE ascendedprison_delayed_actions_location (
  action_id  BIGINT     NOT NULL            AUTO_INCREMENT,
  x          DOUBLE     NOT NULL,
  y          DOUBLE     NOT NULL,
  z          DOUBLE     NOT NULL,
  world_uuid BINARY(16) NOT NULL,


  PRIMARY KEY (action_id),
  FOREIGN KEY (action_id) REFERENCES ascendedprison_delayed_actions (action_id)
);

CREATE TABLE ascendedprison_prison (
  prisonid             BINARY(16) NOT NULL,
  prisoner_uuid        BINARY(16) NOT NULL,
  imprisoner_uuid      BINARY(16) NOT NULL,
  imprisonTime         TIMESTAMP  NOT NULL,
  summoned_state       BOOLEAN    NOT NULL,
  prison_location_type VARCHAR(255),
  leash_range          DOUBLE     NOT NULL,
  x                    DOUBLE     NOT NULL,
  y                    DOUBLE     NOT NULL,
  z                    DOUBLE     NOT NULL,
  world_uuid           BINARY(16) NOT NULL,
  location_uuid        BINARY(16), -- This is an additional UUID for entities that hold prisons.

  PRIMARY KEY (prisoner_uuid),
  UNIQUE (prisonid)
);

CREATE TABLE ascendedprison_inventory_stack (
  puuid       BINARY(16)   NOT NULL,
  world_uuid  BINARY(16)   NOT NULL,
  type        VARCHAR(255) NOT NULL,
  data        TINYINT      NOT NULL,
  damage      SMALLINT     NOT NULL,
  slot_id     INTEGER      NOT NULL,
  amount      INTEGER      NOT NULL,
  custom_name VARCHAR(255),
  PRIMARY KEY (puuid, world_uuid, slot_id)
);

CREATE TABLE ascendedprison_inventory_stack_enchantments (
  puuid       BINARY(16)   NOT NULL,
  world_uuid  BINARY(16)   NOT NULL,
  slot_id     INTEGER      NOT NULL,
  enchantment VARCHAR(255) NOT NULL,
  elevel      INTEGER      NOT NULL,
  PRIMARY KEY (puuid, world_uuid, slot_id, enchantment),
  FOREIGN KEY (puuid, world_uuid, slot_id) REFERENCES ascendedprison_inventory_stack (puuid, world_uuid, slot_id)
);

CREATE TABLE ascendedprison_inventory_stack_lore (
  puuid      BINARY(16) NOT NULL,
  world_uuid BINARY(16) NOT NULL,
  slot_id    INTEGER    NOT NULL,
  line       INTEGER    NOT NULL,
  lore       TEXT       NOT NULL,
  PRIMARY KEY (puuid, world_uuid, slot_id, line),
  FOREIGN KEY (puuid, world_uuid, slot_id) REFERENCES ascendedprison_inventory_stack (puuid, world_uuid, slot_id)
);

CREATE TABLE ascendedprison_feeding_cycles (
  cycle_time TIMESTAMP  NOT NULL,
  prisonid   BINARY(16) NOT NULL,
  cost       INTEGER    NOT NULL,
  PRIMARY KEY (cycle_time),
  FOREIGN KEY (prisonid) REFERENCES ascendedprison_prison (prisonid)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE ascendedprison_prison_history (
  prisoner_uuid   BINARY(16) NOT NULL,
  imprisoner_uuid BINARY(16) NOT NULL,
  imprisonTime    TIMESTAMP  NOT NULL,
  cost            INTEGER    NOT NULL,
  recorded        TIMESTAMP  NOT NULL,
  PRIMARY KEY (prisoner_uuid)
);

CREATE TABLE ascendedprison_player_log (
  puuid    BINARY(16) NOT NULL,
  day      BIGINT     NOT NULL,
  playtime BIGINT     NOT NULL,
  INDEX (puuid, day)
);


SET foreign_key_checks = 1;
COMMIT;