package com.dataracy.modules.data.application.elasticsearch;

import com.dataracy.modules.data.adapter.elasticsearch.document.DataSearchDocument;

public interface DataIndexingPort {
void index(DataSearchDocument doc);
}
