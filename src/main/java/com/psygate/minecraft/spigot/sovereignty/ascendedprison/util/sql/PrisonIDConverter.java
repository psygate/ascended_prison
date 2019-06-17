/*
 *     Copyright (C) 2016 psygate (https://github.com/psygate)
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 */

package com.psygate.minecraft.spigot.sovereignty.ascendedprison.util.sql;

import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.PrisonID;
import com.psygate.minecraft.spigot.sovereignty.nucleus.sql.util.UUIDByteConverter;
import org.jooq.Converter;

/**
 * Created by psygate on 30.06.2016.
 */
public class PrisonIDConverter implements Converter<byte[], PrisonID> {
    private final UUIDByteConverter conv = new UUIDByteConverter();

    @Override
    public PrisonID from(byte[] databaseObject) {
        return new PrisonID(conv.from(databaseObject));
    }

    @Override
    public byte[] to(PrisonID userObject) {
        return conv.to(userObject.getId());
    }

    @Override
    public Class<byte[]> fromType() {
        return byte[].class;
    }

    @Override
    public Class<PrisonID> toType() {
        return PrisonID.class;
    }
}
