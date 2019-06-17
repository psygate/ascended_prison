/**
 * This class is generated by jOOQ
 */
package com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.daos;


import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.AscendedprisonPrisonHistory;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.records.AscendedprisonPrisonHistoryRecord;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


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
public class AscendedprisonPrisonHistoryDao extends DAOImpl<AscendedprisonPrisonHistoryRecord, com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.pojos.AscendedprisonPrisonHistory, UUID> {

	/**
	 * Create a new AscendedprisonPrisonHistoryDao without any configuration
	 */
	public AscendedprisonPrisonHistoryDao() {
		super(AscendedprisonPrisonHistory.ASCENDEDPRISON_PRISON_HISTORY, com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.pojos.AscendedprisonPrisonHistory.class);
	}

	/**
	 * Create a new AscendedprisonPrisonHistoryDao with an attached configuration
	 */
	public AscendedprisonPrisonHistoryDao(Configuration configuration) {
		super(AscendedprisonPrisonHistory.ASCENDEDPRISON_PRISON_HISTORY, com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.pojos.AscendedprisonPrisonHistory.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UUID getId(com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.pojos.AscendedprisonPrisonHistory object) {
		return object.getPrisonerUuid();
	}

	/**
	 * Fetch records that have <code>prisoner_uuid IN (values)</code>
	 */
	public List<com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.pojos.AscendedprisonPrisonHistory> fetchByPrisonerUuid(UUID... values) {
		return fetch(AscendedprisonPrisonHistory.ASCENDEDPRISON_PRISON_HISTORY.PRISONER_UUID, values);
	}

	/**
	 * Fetch a unique record that has <code>prisoner_uuid = value</code>
	 */
	public com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.pojos.AscendedprisonPrisonHistory fetchOneByPrisonerUuid(UUID value) {
		return fetchOne(AscendedprisonPrisonHistory.ASCENDEDPRISON_PRISON_HISTORY.PRISONER_UUID, value);
	}

	/**
	 * Fetch records that have <code>imprisoner_uuid IN (values)</code>
	 */
	public List<com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.pojos.AscendedprisonPrisonHistory> fetchByImprisonerUuid(UUID... values) {
		return fetch(AscendedprisonPrisonHistory.ASCENDEDPRISON_PRISON_HISTORY.IMPRISONER_UUID, values);
	}

	/**
	 * Fetch records that have <code>imprisonTime IN (values)</code>
	 */
	public List<com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.pojos.AscendedprisonPrisonHistory> fetchByImprisontime(Timestamp... values) {
		return fetch(AscendedprisonPrisonHistory.ASCENDEDPRISON_PRISON_HISTORY.IMPRISONTIME, values);
	}

	/**
	 * Fetch records that have <code>cost IN (values)</code>
	 */
	public List<com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.pojos.AscendedprisonPrisonHistory> fetchByCost(Integer... values) {
		return fetch(AscendedprisonPrisonHistory.ASCENDEDPRISON_PRISON_HISTORY.COST, values);
	}

	/**
	 * Fetch records that have <code>recorded IN (values)</code>
	 */
	public List<com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.pojos.AscendedprisonPrisonHistory> fetchByRecorded(Timestamp... values) {
		return fetch(AscendedprisonPrisonHistory.ASCENDEDPRISON_PRISON_HISTORY.RECORDED, values);
	}
}
