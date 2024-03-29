/**
 * This class is generated by jOOQ
 */
package com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.records;


import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.AscendedprisonPlayerLog;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.interfaces.IAscendedprisonPlayerLog;

import java.util.UUID;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.TableRecordImpl;


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
public class AscendedprisonPlayerLogRecord extends TableRecordImpl<AscendedprisonPlayerLogRecord> implements Record3<UUID, Long, Long>, IAscendedprisonPlayerLog {

	private static final long serialVersionUID = 552924052;

	/**
	 * Setter for <code>nucleus.ascendedprison_player_log.puuid</code>.
	 */
	public void setPuuid(UUID value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>nucleus.ascendedprison_player_log.puuid</code>.
	 */
	@Override
	public UUID getPuuid() {
		return (UUID) getValue(0);
	}

	/**
	 * Setter for <code>nucleus.ascendedprison_player_log.day</code>.
	 */
	public void setDay(Long value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>nucleus.ascendedprison_player_log.day</code>.
	 */
	@Override
	public Long getDay() {
		return (Long) getValue(1);
	}

	/**
	 * Setter for <code>nucleus.ascendedprison_player_log.playtime</code>.
	 */
	public void setPlaytime(Long value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>nucleus.ascendedprison_player_log.playtime</code>.
	 */
	@Override
	public Long getPlaytime() {
		return (Long) getValue(2);
	}

	// -------------------------------------------------------------------------
	// Record3 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<UUID, Long, Long> fieldsRow() {
		return (Row3) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<UUID, Long, Long> valuesRow() {
		return (Row3) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UUID> field1() {
		return AscendedprisonPlayerLog.ASCENDEDPRISON_PLAYER_LOG.PUUID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Long> field2() {
		return AscendedprisonPlayerLog.ASCENDEDPRISON_PLAYER_LOG.DAY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Long> field3() {
		return AscendedprisonPlayerLog.ASCENDEDPRISON_PLAYER_LOG.PLAYTIME;
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
	public Long value2() {
		return getDay();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long value3() {
		return getPlaytime();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonPlayerLogRecord value1(UUID value) {
		setPuuid(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonPlayerLogRecord value2(Long value) {
		setDay(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonPlayerLogRecord value3(Long value) {
		setPlaytime(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonPlayerLogRecord values(UUID value1, Long value2, Long value3) {
		value1(value1);
		value2(value2);
		value3(value3);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached AscendedprisonPlayerLogRecord
	 */
	public AscendedprisonPlayerLogRecord() {
		super(AscendedprisonPlayerLog.ASCENDEDPRISON_PLAYER_LOG);
	}

	/**
	 * Create a detached, initialised AscendedprisonPlayerLogRecord
	 */
	public AscendedprisonPlayerLogRecord(UUID puuid, Long day, Long playtime) {
		super(AscendedprisonPlayerLog.ASCENDEDPRISON_PLAYER_LOG);

		setValue(0, puuid);
		setValue(1, day);
		setValue(2, playtime);
	}
}
