CREATE TABLE `carx`.`customer` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `uuid` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`, `uuid`));

CREATE TABLE `carx`.`activity` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `created` TIMESTAMP NOT NULL,
  `customer_id` INT NOT NULL,
  `value` INT NOT NULL,
  PRIMARY KEY (`id`));

ALTER TABLE `carx`.`activity`
ADD INDEX `fk_customer_idx` (`customer_id` ASC);
ALTER TABLE `carx`.`activity`
ADD CONSTRAINT `fk_customer`
  FOREIGN KEY (`customer_id`)
  REFERENCES `carx`.`customer` (`id`)
  ON DELETE RESTRICT
  ON UPDATE NO ACTION;