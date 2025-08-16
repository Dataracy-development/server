package com.dataracy.modules.dataset.adapter.jpa.repository;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionDlqEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataEsProjectionDlqRepository extends JpaRepository<DataEsProjectionDlqEntity, Long> {
}
