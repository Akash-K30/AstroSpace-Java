package com.astrospace.util;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;

public final class Ids {

    private Ids() {
    }

    public static ObjectId parseOrNull(String value) {
        return value != null && ObjectId.isValid(value) ? new ObjectId(value) : null;
    }

    /** An id that is not even a valid ObjectId can never exist, so it is reported as 404 (Express returned a 500 CastError). */
    public static ObjectId require(String value, String notFoundMessage) {
        ObjectId id = parseOrNull(value);
        if (id == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, notFoundMessage);
        }
        return id;
    }

    public static List<String> hex(Collection<ObjectId> ids) {
        return ids.stream().map(ObjectId::toHexString).toList();
    }
}
