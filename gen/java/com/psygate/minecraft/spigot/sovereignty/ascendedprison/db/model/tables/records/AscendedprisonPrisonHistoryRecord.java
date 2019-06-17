/**
 * This class is generated by jOOQ
 */
package com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.records;


import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.AscendedprisonPrisonHistory;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.interfaces.IAscendedprisonPrisonHistory;

import java.sql.Timestamp;
import java.util.UUID;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
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
public class AscendedprisonPrisonHistoryRecord extends UpdatableRecordImpl<AscendedprisonPrisonHistoryRecord> implements Record5<UUID, UUID, Timestamp, Integer, Timestamp>, IAscendedprisonPrisonHistory {

	private static final long serialVersionUID = -2074273938;

	/**
	 * Setter for <code>nucleus.ascendedprison_prison_history.prisoner_uuid</code>.
	 */
	public void setPrisonerUuid(UUID value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>nucleus.ascendedprison_prison_history.prisoner_uuid</code>.
	 */
	@Override
	public UUID getPrisonerUuid() {
		return (UUID) getValue(0);
	}

	/**
	 * Setter for <code>nucleus.ascendedprison_prison_history.imprisoner_uuid</code>.
	 */
	public void setImprisonerUuid(UUID value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>nucleus.ascendedprison_prison_history.imprisoner_uuid</code>.
	 */
	@Override
	public UUID getImprisonerUuid() {
		return (UUID) getValue(1);
	}

	/**
	 * Setter for <code>nucleus.ascendedprison_prison_history.imprisonTime</code>.
	 */
	public void setImprisontime(Timestamp value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>nucleus.ascendedprison_prison_history.imprisonTime</code>.
	 */
	@Override
	public Timestamp getImprisontime() {
		return (Timestamp) getValue(2);
	}

	/**
	 * Setter for <code>nucleus.ascendedprison_prison_history.cost</code>.
	 */
	public void setCost(Integer value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>nucleus.ascendedprison_prison_history.cost</code>.
	 */
	@Override
	public Integer getCost() {
		return (Integer) getValue(3);
	}

	/**
	 * Setter for <code>nucleus.ascendedprison_prison_history.recorded</code>.
	 */
	public void setRecorded(Timestamp value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>nucleus.ascendedprison_prison_history.recorded</code>.
	 */
	@Override
	public Timestamp getRecorded() {
		return (Timestamp) getValue(4);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<UUID> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record5 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<UUID, UUID, Timestamp, Integer, Timestamp> fieldsRow() {
		return (Row5) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<UUID, UUID, Timestamp, Integer, Timestamp> valuesRow() {
		return (Row5) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UUID> field1() {
		return AscendedprisonPrisonHistory.ASCENDEDPRISON_PRISON_HISTORY.PRISONER_UUID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UUID> field2() {
		return AscendedprisonPrisonHistory.ASCENDEDPRISON_PRISON_HISTORY.IMPRISONER_UUID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field3() {
		return AscendedprisonPrisonHistory.ASCENDEDPRISON_PRISON_HISTORY.IMPRISONTIME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field4() {
		return AscendedprisonPrisonHistory.ASCENDEDPRISON_PRISON_HISTORY.COST;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field5() {
		return AscendedprisonPrisonHistory.ASCENDEDPRISON_PRISON_HISTORY.RECORDED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UUID value1() {
		return getPrisonerUuid();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UUID value2() {
		return getImprisonerUuid();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value3() {
		return getImprisontime();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value4() {
		return getCost();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value5() {
		return getRecorded();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonPrisonHistoryRecord value1(UUID value) {
		setPrisonerUuid(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonPrisonHistoryRecord value2(UUID value) {
		setImprisonerUuid(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonPrisonHistoryRecord value3(Timestamp value) {
		setImprisontime(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonPrisonHistoryRecord value4(Integer value) {
		setCost(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonPrisonHistoryRecord value5(Timestamp value) {
		setRecorded(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonPrisonHistoryRecord values(UUID value1, UUID value2, Timestamp value3, Integer value4, Timestamp value5) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached AscendedprisonPrisonHistoryRecord
	 */
	public AscendedprisonPrisonHistoryRecord() {
		super(AscendedprisonPrisonHistory.ASCENDEDPRISON_PRISON_HISTORY);
	}

	/**
	 * Create a detached, initialised AscendedprisonPrisonHistoryRecord
	 */
	public AscendedprisonPrisonHistoryRecord(UUID prisonerUuid, UUID imprisonerUuid, Timestamp imprisontime, Integer cost, Timestamp recorded) {
		super(AscendedprisonPrisonHistory.ASCENDEDPRISON_PRISON_HISTORY);

		setValue(0, prisonerUuid);
		setValue(1, imprisonerUuid);
		setValue(2, imprisontime);
		setValue(3, cost);
		setValue(4, recorded);
	}
}