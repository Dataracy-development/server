package com.dataracy.modules.data.application.port.elasticsearch;

import com.dataracy.modules.data.adapter.elasticsearch.document.DataSearchDocument;

public interface DataIndexingPort {
void index(DataSearchDocument doc);
}
