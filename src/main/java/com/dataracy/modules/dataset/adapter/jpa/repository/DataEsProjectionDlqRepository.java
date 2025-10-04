package com.dataracy.modules.dataset.adapter.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionDlqEntity;

public interface DataEsProjectionDlqRepository
    extends JpaRepository<DataEsProjectionDlqEntity, Long> {}
