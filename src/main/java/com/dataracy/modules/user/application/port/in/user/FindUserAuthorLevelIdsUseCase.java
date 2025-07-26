package com.dataracy.modules.user.application.port.in.user;

import java.util.List;
import java.util.Map;

public interface FindUserAuthorLevelIdsUseCase {
Map<Long, String> findUserAuthorLevelIds(List<Long> userIds);
}
