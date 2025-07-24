package com.dataracy.modules.project.adapter.elasticsearch.document;

public record ProjectDeletedUpdate(Boolean isDeleted) {
    public static ProjectDeletedUpdate deleted() {
        return new ProjectDeletedUpdate(true);
    }

    public static ProjectDeletedUpdate restored() {
        return new ProjectDeletedUpdate(false);
    }
}
