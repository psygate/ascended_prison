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
public interface IAscendedprisonDelayedActions extends Serializable {

	/**
	 * Getter for <code>nucleus.ascendedprison_delayed_actions.action_id</code>.
	 */
	public Long getActionId();

	/**
	 * Getter for <code>nucleus.ascendedprison_delayed_actions.action_type</code>.
	 */
	public Integer getActionType();

	/**
	 * Getter for <code>nucleus.ascendedprison_delayed_actions.puuid</code>.
	 */
	public UUID getPuuid();

	/**
	 * Getter for <code>nucleus.ascendedprison_delayed_actions.reason</code>.
	 */
	public String getReason();
}
