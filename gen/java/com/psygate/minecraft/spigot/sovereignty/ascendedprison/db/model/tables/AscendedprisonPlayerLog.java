/**
 * This class is generated by jOOQ
 */
package com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables;


import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.Nucleus;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.records.AscendedprisonPlayerLogRecord;
import com.psygate.minecraft.spigot.sovereignty.nucleus.sql.util.UUIDByteConverter;

import java.util.UUID;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;


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
public class AscendedprisonPlayerLog extends TableImpl<AscendedprisonPlayerLogRecord> {

	private static final long serialVersionUID = -1118932516;

	/**
	 * The reference instance of <code>nucleus.ascendedprison_player_log</code>
	 */
	public static final AscendedprisonPlayerLog ASCENDEDPRISON_PLAYER_LOG = new AscendedprisonPlayerLog();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<AscendedprisonPlayerLogRecord> getRecordType() {
		return AscendedprisonPlayerLogRecord.class;
	}

	/**
	 * The column <code>nucleus.ascendedprison_player_log.puuid</code>.
	 */
	public final TableField<AscendedprisonPlayerLogRecord, UUID> PUUID = createField("puuid", org.jooq.impl.SQLDataType.BINARY.length(16).nullable(false), this, "", new UUIDByteConverter());

	/**
	 * The column <code>nucleus.ascendedprison_player_log.day</code>.
	 */
	public final TableField<AscendedprisonPlayerLogRecord, Long> DAY = createField("day", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>nucleus.ascendedprison_player_log.playtime</code>.
	 */
	public final TableField<AscendedprisonPlayerLogRecord, Long> PLAYTIME = createField("playtime", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * Create a <code>nucleus.ascendedprison_player_log</code> table reference
	 */
	public AscendedprisonPlayerLog() {
		this("ascendedprison_player_log", null);
	}

	/**
	 * Create an aliased <code>nucleus.ascendedprison_player_log</code> table reference
	 */
	public AscendedprisonPlayerLog(String alias) {
		this(alias, ASCENDEDPRISON_PLAYER_LOG);
	}

	private AscendedprisonPlayerLog(String alias, Table<AscendedprisonPlayerLogRecord> aliased) {
		this(alias, aliased, null);
	}

	private AscendedprisonPlayerLog(String alias, Table<AscendedprisonPlayerLogRecord> aliased, Field<?>[] parameters) {
		super(alias, Nucleus.NUCLEUS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonPlayerLog as(String alias) {
		return new AscendedprisonPlayerLog(alias, this);
	}

	/**
	 * Rename this table
	 */
	public AscendedprisonPlayerLog rename(String name) {
		return new AscendedprisonPlayerLog(name, null);
	}
}