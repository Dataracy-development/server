/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.application.port.out.command.update;

public interface UpdateDataDownloadPort {
  void increaseDownloadCount(Long dataId);
}
