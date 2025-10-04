package com.dataracy.modules.project.adapter.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEsProjectionDlqEntity;

public interface ProjectEsProjectionDlqRepository
    extends JpaRepository<ProjectEsProjectionDlqEntity, Long> {}
