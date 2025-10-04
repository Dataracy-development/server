package com.dataracy.modules.email.domain.model;

/** 이메일 제목 + 본문 묶음 */
public record EmailContent(String subject, String body) {}
