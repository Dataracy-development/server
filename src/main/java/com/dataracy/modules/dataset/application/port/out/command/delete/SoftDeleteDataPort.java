package com.dataracy.modules.dataset.application.port.out.command.delete;

public interface SoftDeleteDataPort {
  /**
   * 주어진 데이터 ID에 해당하는 문서를 소프트 삭제 상태로 변경합니다.
   *
   * @param dataId 소프트 삭제할 데이터의 고유 식별자
   */
  void deleteData(Long dataId);

  /**
   * 지정된 데이터 ID에 해당하는 문서를 삭제 취소(복구) 상태로 변경합니다.
   *
   * @param dataId 복구할 데이터의 고유 식별자
   */
  void restoreData(Long dataId);
}
