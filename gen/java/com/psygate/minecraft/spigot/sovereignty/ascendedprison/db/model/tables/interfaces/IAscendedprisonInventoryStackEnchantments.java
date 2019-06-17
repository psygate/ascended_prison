/**
 * This class is generated by jOOQ
 */
package com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.interfaces;


import java.io.Serializable;
import java.util.UUID;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.7.2"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public interface IAscendedprisonInventoryStackEnchantments extends Serializable {

	/**
	 * Getter for <code>nucleus.ascendedprison_inventory_stack_enchantments.puuid</code>.
	 */
	public UUID getPuuid();

	/**
	 * Getter for <code>nucleus.ascendedprison_inventory_stack_enchantments.world_uuid</code>.
	 */
	public UUID getWorldUuid();

	/**
	 * Getter for <code>nucleus.ascendedprison_inventory_stack_enchantments.slot_id</code>.
	 */
	public Integer getSlotId();

	/**
	 * Getter for <code>nucleus.ascendedprison_inventory_stack_enchantments.enchantment</code>.
	 */
	public String getEnchantment();

	/**
	 * Getter for <code>nucleus.ascendedprison_inventory_stack_enchantments.elevel</code>.
	 */
	public Integer getElevel();
}