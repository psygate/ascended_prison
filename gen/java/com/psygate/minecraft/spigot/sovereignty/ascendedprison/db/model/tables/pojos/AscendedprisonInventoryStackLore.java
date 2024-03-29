/**
 * This class is generated by jOOQ
 */
package com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.pojos;


import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.interfaces.IAscendedprisonInventoryStackLore;

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
public class AscendedprisonInventoryStackLore implements IAscendedprisonInventoryStackLore {

	private static final long serialVersionUID = -445242047;

	private final UUID    puuid;
	private final UUID    worldUuid;
	private final Integer slotId;
	private final Integer line;
	private final String  lore;

	public AscendedprisonInventoryStackLore(AscendedprisonInventoryStackLore value) {
		this.puuid = value.puuid;
		this.worldUuid = value.worldUuid;
		this.slotId = value.slotId;
		this.line = value.line;
		this.lore = value.lore;
	}

	public AscendedprisonInventoryStackLore(
		UUID    puuid,
		UUID    worldUuid,
		Integer slotId,
		Integer line,
		String  lore
	) {
		this.puuid = puuid;
		this.worldUuid = worldUuid;
		this.slotId = slotId;
		this.line = line;
		this.lore = lore;
	}

	@Override
	public UUID getPuuid() {
		return this.puuid;
	}

	@Override
	public UUID getWorldUuid() {
		return this.worldUuid;
	}

	@Override
	public Integer getSlotId() {
		return this.slotId;
	}

	@Override
	public Integer getLine() {
		return this.line;
	}

	@Override
	public String getLore() {
		return this.lore;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AscendedprisonInventoryStackLore (");

		sb.append(puuid);
		sb.append(", ").append(worldUuid);
		sb.append(", ").append(slotId);
		sb.append(", ").append(line);
		sb.append(", ").append(lore);

		sb.append(")");
		return sb.toString();
	}
}
