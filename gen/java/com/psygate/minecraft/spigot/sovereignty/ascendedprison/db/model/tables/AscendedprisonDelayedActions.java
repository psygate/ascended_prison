/**
 * This class is generated by jOOQ
 */
package com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables;


import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.Keys;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.Nucleus;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.records.AscendedprisonDelayedActionsRecord;
import com.psygate.minecraft.spigot.sovereignty.nucleus.sql.util.UUIDByteConverter;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
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
public class AscendedprisonDelayedActions extends TableImpl<AscendedprisonDelayedActionsRecord> {

	private static final long serialVersionUID = 289156800;

	/**
	 * The reference instance of <code>nucleus.ascendedprison_delayed_actions</code>
	 */
	public static final AscendedprisonDelayedActions ASCENDEDPRISON_DELAYED_ACTIONS = new AscendedprisonDelayedActions();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<AscendedprisonDelayedActionsRecord> getRecordType() {
		return AscendedprisonDelayedActionsRecord.class;
	}

	/**
	 * The column <code>nucleus.ascendedprison_delayed_actions.action_id</code>.
	 */
	public final TableField<AscendedprisonDelayedActionsRecord, Long> ACTION_ID = createField("action_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>nucleus.ascendedprison_delayed_actions.action_type</code>.
	 */
	public final TableField<AscendedprisonDelayedActionsRecord, Integer> ACTION_TYPE = createField("action_type", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>nucleus.ascendedprison_delayed_actions.puuid</code>.
	 */
	public final TableField<AscendedprisonDelayedActionsRecord, UUID> PUUID = createField("puuid", org.jooq.impl.SQLDataType.BINARY.length(16).nullable(false), this, "", new UUIDByteConverter());

	/**
	 * The column <code>nucleus.ascendedprison_delayed_actions.reason</code>.
	 */
	public final TableField<AscendedprisonDelayedActionsRecord, String> REASON = createField("reason", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * Create a <code>nucleus.ascendedprison_delayed_actions</code> table reference
	 */
	public AscendedprisonDelayedActions() {
		this("ascendedprison_delayed_actions", null);
	}

	/**
	 * Create an aliased <code>nucleus.ascendedprison_delayed_actions</code> table reference
	 */
	public AscendedprisonDelayedActions(String alias) {
		this(alias, ASCENDEDPRISON_DELAYED_ACTIONS);
	}

	private AscendedprisonDelayedActions(String alias, Table<AscendedprisonDelayedActionsRecord> aliased) {
		this(alias, aliased, null);
	}

	private AscendedprisonDelayedActions(String alias, Table<AscendedprisonDelayedActionsRecord> aliased, Field<?>[] parameters) {
		super(alias, Nucleus.NUCLEUS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<AscendedprisonDelayedActionsRecord, Long> getIdentity() {
		return Keys.IDENTITY_ASCENDEDPRISON_DELAYED_ACTIONS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<AscendedprisonDelayedActionsRecord> getPrimaryKey() {
		return Keys.KEY_ASCENDEDPRISON_DELAYED_ACTIONS_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<AscendedprisonDelayedActionsRecord>> getKeys() {
		return Arrays.<UniqueKey<AscendedprisonDelayedActionsRecord>>asList(Keys.KEY_ASCENDEDPRISON_DELAYED_ACTIONS_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AscendedprisonDelayedActions as(String alias) {
		return new AscendedprisonDelayedActions(alias, this);
	}

	/**
	 * Rename this table
	 */
	public AscendedprisonDelayedActions rename(String name) {
		return new AscendedprisonDelayedActions(name, null);
	}
}
