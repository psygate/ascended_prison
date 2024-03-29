/**
 * This class is generated by jOOQ
 */
package com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.records;


import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.AscendedprisonInventoryStack;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.interfaces.IAscendedprisonInventoryStack;

import java.util.UUID;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;


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
public class AscendedprisonInventoryStackRecord extends UpdatableRecordImpl<AscendedprisonInventoryStackRecord> implements Record8<UUID, UUID, String, Byte, Short, Integer, Integer, String>, IAscendedprisonInventoryStack {

	private static final long serialVersionUID = 923794464;

	/**
	 * Setter for <code>nucleus.ascendedprison_inventory_stack.puuid</code>.
	 */
	public void setPuuid(UUID value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>nucleus.ascendedprison_inventory_stack.puuid</code>.
	 */
	@Override
	public UUID getPuuid() {
		return (UUID) getValue(0);
	}

	/**
	 * Setter for <code>nucleus.ascendedprison_inventory_stack.world_uuid</code>.
	 */
	public void setWorldUuid(UUID value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>nucleus.ascendedprison_inventory_stack.world_uuid</code>.
	 */
	@Override
	public UUID getWorldUuid() {
		return (UUID) getValue(1);
	}

	/**
	 * Setter for <code>nucleus.ascendedprison_inventory_stack.type</code>.
	 */
	public void setType(String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>nucleus.ascendedprison_inventory_stack.type</code>.
	 */
	@Override
	public String getType() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>nucleus.ascendedprison_inventory_stack.data</code>.
	 */
	public void setData(Byte value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>nucleus.ascendedprison_inventory_stack.data</code>.
	 */
	@Override
	public Byte getData() {
		return (Byte) getValue(3);
	}

	/**
	 * Setter for <code>nucleus.ascendedprison_inventory_stack.damage</code>.
	 */
	public void setDamage(Short value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>nucleus.ascendedprison_inventory_stack.damage</code>.
	 */
	@Override
	public Short getDamage() {
		return (Short) getValue(4);
	}

	/**
	 * Setter for <code>nucleus.ascendedprison_inventory_stack.slot_id</code>.
	 */
	public void setSlotId(Integer value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>nucleus.ascendedprison_inventory_stack.slot_id</code>.
	 */
	@Override
	public Integer getSlotId() {
		return (Integer) getValue(5);
	}

	/**
	 * Setter for <code>nucleus.ascendedprison_inventory_stack.amount</code>.
	 */
	public void setAmount(Integer value) {
		setValue(6, value);
	}

	/**
	 * Getter for <code>nucleus.ascendedprison_inventory_stack.amount</code>.
	 */
	@Override
	public Integer getAmount() {
		return (Integer) getValue(6);
	}

	/**
	 * Setter for <code>nucleus.ascendedprison_inventory_stack.custom_name</code>.
	 */
	public void setCustomName(String value) {
		setValue(7, value);
	}

	/**
	 * Getter for <code>nucleus.ascendedprison_inventory_stack.custom_name</code>.
	 */
	@Override
	public String getCustomName() {
		return (String) getValue(7);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record3<UUID, UUID, Integer> key() {
		return (Record3) super.key();
	}

	// -------------------------------------------------------------------------
	// Record8 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row8<UUID, UUID, String, Byte, Short, Integer, Integer, String> fieldsRow() {
		return (Row8) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row8<UUID, UUID, String, Byte, Short, Integer, Integer, String> valuesRow() {
		return (Row8) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UUID> field1() {
		return AscendedprisonInventoryStack.ASCENDEDPRISON_INVENTORY_STACK.PUUID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UUID> field2() {
		return AscendedprisonInventoryStack.ASCENDEDPRISON_INVENTORY_STACK.WORLD_UUID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return AscendedprisonInventoryStack.ASCENDEDPRISON_INVENTORY_STACK.TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Byte> field4() {
		return AscendedprisonInventoryStack.ASCENDEDPRISON_INVENTORY_STACK.DATA;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Short> field5() {
		return AscendedprisonInventoryStack.ASCENDEDPRISON_INVENTORY_STACK.DAMAGE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field6() {
		return AscendedprisonInventoryStack.ASCENDEDPRISON_INVENTORY_STACK.SLOT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field7() {
		return AscendedprisonInventoryStack.ASCENDEDPRISON_INVENTORY_STACK.AMOUNT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field8() {
		return AscendedprisonInventoryStack.ASCENDEDPRISON_INVENTORY_STACK.CUSTOM_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UUID value1() {
		return getPuuid();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UUID value2() {
		return getWorldUuid();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Byte value4() {
		return getData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Short value5() {
		return getDamage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value6() {
		return getSlotId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value7() {
		return getAmount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value8() {
		return getCustomName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonInventoryStackRecord value1(UUID value) {
		setPuuid(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonInventoryStackRecord value2(UUID value) {
		setWorldUuid(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonInventoryStackRecord value3(String value) {
		setType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonInventoryStackRecord value4(Byte value) {
		setData(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonInventoryStackRecord value5(Short value) {
		setDamage(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonInventoryStackRecord value6(Integer value) {
		setSlotId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonInventoryStackRecord value7(Integer value) {
		setAmount(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonInventoryStackRecord value8(String value) {
		setCustomName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonInventoryStackRecord values(UUID value1, UUID value2, String value3, Byte value4, Short value5, Integer value6, Integer value7, String value8) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		value8(value8);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached AscendedprisonInventoryStackRecord
	 */
	public AscendedprisonInventoryStackRecord() {
		super(AscendedprisonInventoryStack.ASCENDEDPRISON_INVENTORY_STACK);
	}

	/**
	 * Create a detached, initialised AscendedprisonInventoryStackRecord
	 */
	public AscendedprisonInventoryStackRecord(UUID puuid, UUID worldUuid, String type, Byte data, Short damage, Integer slotId, Integer amount, String customName) {
		super(AscendedprisonInventoryStack.ASCENDEDPRISON_INVENTORY_STACK);

		setValue(0, puuid);
		setValue(1, worldUuid);
		setValue(2, type);
		setValue(3, data);
		setValue(4, damage);
		setValue(5, slotId);
		setValue(6, amount);
		setValue(7, customName);
	}
}
