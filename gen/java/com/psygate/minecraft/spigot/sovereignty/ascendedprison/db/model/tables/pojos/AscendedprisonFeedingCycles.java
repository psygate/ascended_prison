/**
 * This class is generated by jOOQ
 */
package com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.pojos;


import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.interfaces.IAscendedprisonFeedingCycles;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.PrisonID;

import java.sql.Timestamp;

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
public class AscendedprisonFeedingCycles implements IAscendedprisonFeedingCycles {

	private static final long serialVersionUID = 766585626;

	private final Timestamp cycleTime;
	private final PrisonID  prisonid;
	private final Integer   cost;

	public AscendedprisonFeedingCycles(AscendedprisonFeedingCycles value) {
		this.cycleTime = value.cycleTime;
		this.prisonid = value.prisonid;
		this.cost = value.cost;
	}

	public AscendedprisonFeedingCycles(
		Timestamp cycleTime,
		PrisonID  prisonid,
		Integer   cost
	) {
		this.cycleTime = cycleTime;
		this.prisonid = prisonid;
		this.cost = cost;
	}

	@Override
	public Timestamp getCycleTime() {
		return this.cycleTime;
	}

	@Override
	public PrisonID getPrisonid() {
		return this.prisonid;
	}

	@Override
	public Integer getCost() {
		return this.cost;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AscendedprisonFeedingCycles (");

		sb.append(cycleTime);
		sb.append(", ").append(prisonid);
		sb.append(", ").append(cost);

		sb.append(")");
		return sb.toString();
	}
}
