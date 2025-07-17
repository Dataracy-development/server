package com.dataracy.modules.project.application.port.out;

import com.dataracy.modules.project.adapter.index.document.ProjectSearchDocument;

public interface ProjectIndexingPort {
    void index(ProjectSearchDocument doc);
}
