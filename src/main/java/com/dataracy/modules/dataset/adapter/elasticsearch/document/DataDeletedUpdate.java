package com.dataracy.modules.dataset.adapter.elasticsearch.document;

public record DataDeletedUpdate(Boolean isDeleted) {
    /**
     * 데이터가 삭제된 상태를 나타내는 DataDeletedUpdate 인스턴스를 반환합니다.
     *
     * @return isDeleted 값이 true인 DataDeletedUpdate 인스턴스
     */
    public static DataDeletedUpdate deleted() {
        return new DataDeletedUpdate(true);
    }

    /**
     * 데이터가 삭제되지 않은 상태를 나타내는 DataDeletedUpdate 인스턴스를 반환합니다.
     *
     * @return 삭제되지 않은 상태의 DataDeletedUpdate 객체
     */
    public static DataDeletedUpdate restored() {
        return new DataDeletedUpdate(false);
    }
}
