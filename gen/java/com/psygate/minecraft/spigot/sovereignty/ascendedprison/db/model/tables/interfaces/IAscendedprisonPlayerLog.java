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
public interface IAscendedprisonPlayerLog extends Serializable {

	/**
	 * Getter for <code>nucleus.ascendedprison_player_log.puuid</code>.
	 */
	public UUID getPuuid();

	/**
	 * Getter for <code>nucleus.ascendedprison_player_log.day</code>.
	 */
	public Long getDay();

	/**
	 * Getter for <code>nucleus.ascendedprison_player_log.playtime</code>.
	 */
	public Long getPlaytime();
}
