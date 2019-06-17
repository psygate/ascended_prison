/**
 * This class is generated by jOOQ
 */
package com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables;


import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.Keys;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.Nucleus;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.records.AscendedprisonFeedingCyclesRecord;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.PrisonID;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.util.sql.PrisonIDConverter;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
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
public class AscendedprisonFeedingCycles extends TableImpl<AscendedprisonFeedingCyclesRecord> {

	private static final long serialVersionUID = -1999739587;

	/**
	 * The reference instance of <code>nucleus.ascendedprison_feeding_cycles</code>
	 */
	public static final AscendedprisonFeedingCycles ASCENDEDPRISON_FEEDING_CYCLES = new AscendedprisonFeedingCycles();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<AscendedprisonFeedingCyclesRecord> getRecordType() {
		return AscendedprisonFeedingCyclesRecord.class;
	}

	/**
	 * The column <code>nucleus.ascendedprison_feeding_cycles.cycle_time</code>.
	 */
	public final TableField<AscendedprisonFeedingCyclesRecord, Timestamp> CYCLE_TIME = createField("cycle_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>nucleus.ascendedprison_feeding_cycles.prisonid</code>.
	 */
	public final TableField<AscendedprisonFeedingCyclesRecord, PrisonID> PRISONID = createField("prisonid", org.jooq.impl.SQLDataType.BINARY.length(16).nullable(false), this, "", new PrisonIDConverter());

	/**
	 * The column <code>nucleus.ascendedprison_feeding_cycles.cost</code>.
	 */
	public final TableField<AscendedprisonFeedingCyclesRecord, Integer> COST = createField("cost", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * Create a <code>nucleus.ascendedprison_feeding_cycles</code> table reference
	 */
	public AscendedprisonFeedingCycles() {
		this("ascendedprison_feeding_cycles", null);
	}

	/**
	 * Create an aliased <code>nucleus.ascendedprison_feeding_cycles</code> table reference
	 */
	public AscendedprisonFeedingCycles(String alias) {
		this(alias, ASCENDEDPRISON_FEEDING_CYCLES);
	}

	private AscendedprisonFeedingCycles(String alias, Table<AscendedprisonFeedingCyclesRecord> aliased) {
		this(alias, aliased, null);
	}

	private AscendedprisonFeedingCycles(String alias, Table<AscendedprisonFeedingCyclesRecord> aliased, Field<?>[] parameters) {
		super(alias, Nucleus.NUCLEUS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<AscendedprisonFeedingCyclesRecord> getPrimaryKey() {
		return Keys.KEY_ASCENDEDPRISON_FEEDING_CYCLES_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<AscendedprisonFeedingCyclesRecord>> getKeys() {
		return Arrays.<UniqueKey<AscendedprisonFeedingCyclesRecord>>asList(Keys.KEY_ASCENDEDPRISON_FEEDING_CYCLES_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<AscendedprisonFeedingCyclesRecord, ?>> getReferences() {
		return Arrays.<ForeignKey<AscendedprisonFeedingCyclesRecord, ?>>asList(Keys.ASCENDEDPRISON_FEEDING_CYCLES_IBFK_1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonFeedingCycles as(String alias) {
		return new AscendedprisonFeedingCycles(alias, this);
	}

	/**
	 * Rename this table
	 */
	public AscendedprisonFeedingCycles rename(String name) {
		return new AscendedprisonFeedingCycles(name, null);
	}
}
