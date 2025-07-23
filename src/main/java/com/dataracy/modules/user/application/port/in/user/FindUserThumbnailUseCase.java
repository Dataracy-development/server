package com.dataracy.modules.user.application.port.in.user;

import java.util.List;
import java.util.Map;

public interface FindUserThumbnailUseCase {
Map<Long, String> findUserThumbnailsByIds(List<Long> userIds);
}
