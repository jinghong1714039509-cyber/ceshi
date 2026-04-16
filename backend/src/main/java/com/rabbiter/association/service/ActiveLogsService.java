package com.rabbiter.association.service;

import com.rabbiter.association.entity.ActiveLogs;

import java.util.List;
import java.util.Map;

public interface ActiveLogsService extends BaseService<ActiveLogs, String> {

    Boolean isActive(String activeId, String userId);

    List<Map<String, Object>> getListByActiveId(String activeId);

    void cancelSignup(String activeId, String userId);
}
