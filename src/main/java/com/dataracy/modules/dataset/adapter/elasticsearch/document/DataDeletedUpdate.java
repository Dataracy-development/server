package com.dataracy.modules.dataset.adapter.elasticsearch.document;

public record DataDeletedUpdate(Boolean isDeleted) {
    public static DataDeletedUpdate deleted() {
        return new DataDeletedUpdate(true);
    }

    public static DataDeletedUpdate restored() {
        return new DataDeletedUpdate(false);
    }
}
