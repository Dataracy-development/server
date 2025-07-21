package com.dataracy.modules.dataset.application.port.elasticsearch;

import com.dataracy.modules.dataset.adapter.elasticsearch.document.DataSearchDocument;

public interface DataIndexingPort {
void index(DataSearchDocument doc);
}
