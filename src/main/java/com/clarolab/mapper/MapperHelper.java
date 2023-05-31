/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.mapper;

import com.clarolab.dto.BaseDTO;
import com.clarolab.model.Entry;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class MapperHelper {

    private MapperHelper() {}

    /**
     * Given a list of Entries (@link com.clarolab.Entry) it will convert all occurrences into DTOs using the conversion function received.
     * @param converter function from Entry to DTO
     * @param entries collection to convert
     * @param <T> any Entry
     * @param <R> any DTO
     * @return a list of converted Entries to DTO
     */
    public static <T extends Entry, R extends BaseDTO> List<R> getDTOList(Function<T, R> converter, Collection<T> entries) {
        if (entries == null) {
            return Collections.emptyList();
        } else {
            return entries.stream()
                    .map(converter)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Given a list of Entries (@link com.clarolab.Entry) it will convert all occurrences into Long IDs.
     * @param entries collection to convert
     * @param <T> any Entry
     * @return a list of converted Entries to IDs
     */
    public static <T extends Entry> List<Long> getIDList(Collection<T> entries) {
        if (entries == null) {
            return Collections.emptyList();
        } else {
            return entries.stream()
                    .map(Entry::getId)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Given a list of IDs it will convert all occurrences into it's Entries using the converter function.
     * @param converter function from ID to Entry
     * @param ids collection to convert
     * @param <T> any Entry
     * @return a list of converted IDs to Entries
     */
    public static <T extends Entry> List<T> getEntryListFromIDs(Function<Long, T> converter, Collection<Long> ids) {
        if (ids == null) {
            return Collections.emptyList();
        } else {
            return ids.stream()
                    .map(converter)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }

    /**
     * It will set all defaul Entry fields into it's child classes
     * @param entry to retrieve data from.
     * @param dto to put data in.
     */
    public static void setEntryFields(final Entry entry , BaseDTO dto) {
        if (entry == null || dto == null) {
            return;
        }
        dto.setId(entry.getId());
        dto.setEnabled(entry.isEnabled());
        dto.setTimestamp(entry.getTimestamp());
        dto.setUpdated(entry.getUpdated());
    }

    /**
     * @param tEnum to get name from.
     * @return Enum String representation if exists o null otherwise
     */
    @Nullable
    public static String getEnumName(Enum tEnum) {
        return tEnum == null ? null : tEnum.name();
    }

    /**
     * It will attempt to return an Entry of a given ID using the retriever function or null if ID is invalid or not existent.
     * @param id of the Entry to return
     * @param retriever function to map ID to Entry
     * @param <T> any Entry
     * @return an Entry or null if ID is invalid or not existent.
     */
    @Nullable
    public static <T extends Entry> T getNullableByID(Long id, Function<Long, T> retriever) {
        try {
            if (id == null || id < 1) {
                return null;
            } else {
                return retriever.apply(id);
            }
        } catch (NoSuchElementException e) {
            return null;
        }
    }

}
