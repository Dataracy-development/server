package com.dataracy.modules.project.adapter.redis;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.response.read.PopularProjectResponse;
import com.dataracy.modules.project.application.port.out.storage.PopularProjectsStoragePort;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PopularProjectsRedisAdapter implements PopularProjectsStoragePort {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String POPULAR_PROJECTS_KEY = "popular:projects";
    private static final String POPULAR_PROJECTS_METADATA_KEY = "popular:projects:metadata";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10); // 10분 캐시
    
    /**
     * 인기 프로젝트 목록을 캐시에서 조회합니다.
     * 
     * @return 캐시된 인기 프로젝트 목록 (없으면 빈 Optional)
     */
    public Optional<List<PopularProjectResponse>> getPopularProjects() {
        LoggerFactory.redis().logInfo(POPULAR_PROJECTS_KEY, "PopularProjectsRedisAdapter.getPopularProjects() 호출됨");
        Instant startTime = LoggerFactory.redis().logQueryStart(POPULAR_PROJECTS_KEY, "인기 프로젝트 캐시 조회 시작");
        
        try {
            String cachedData = redisTemplate.opsForValue().get(POPULAR_PROJECTS_KEY);
            if (cachedData == null) {
                LoggerFactory.redis().logWarning(POPULAR_PROJECTS_KEY, "캐시에 인기 프로젝트 데이터가 없습니다.");
                return Optional.empty();
            }
            
            List<PopularProjectResponse> popularProjects = objectMapper.readValue(
                cachedData, 
                new TypeReference<List<PopularProjectResponse>>() {}
            );
            
            LoggerFactory.redis().logQueryEnd(POPULAR_PROJECTS_KEY, "인기 프로젝트 캐시 조회 성공", startTime);
            return Optional.of(popularProjects);
            
        } catch (Exception e) {
            LoggerFactory.redis().logError(POPULAR_PROJECTS_KEY, "인기 프로젝트 캐시 조회 실패", e);
            return Optional.empty();
        }
    }
    
    /**
     * 인기 프로젝트 목록을 캐시에 저장합니다.
     * 
     * @param popularProjects 저장할 인기 프로젝트 목록
     */
    public void setPopularProjects(List<PopularProjectResponse> popularProjects) {
        LoggerFactory.redis().logInfo(POPULAR_PROJECTS_KEY, "PopularProjectsRedisAdapter.setPopularProjects() 호출됨 - count=" + popularProjects.size());
        try {
            String jsonData = objectMapper.writeValueAsString(popularProjects);
            LoggerFactory.redis().logInfo(POPULAR_PROJECTS_KEY, "JSON 직렬화 완료 - size=" + jsonData.length());
            
            redisTemplate.opsForValue().set(POPULAR_PROJECTS_KEY, jsonData, CACHE_TTL);
            LoggerFactory.redis().logInfo(POPULAR_PROJECTS_KEY, "Redis 저장 완료 - key=" + POPULAR_PROJECTS_KEY);
            
            // 메타데이터도 함께 저장 (마지막 업데이트 시간)
            String metadata = String.valueOf(System.currentTimeMillis());
            redisTemplate.opsForValue().set(POPULAR_PROJECTS_METADATA_KEY, metadata, CACHE_TTL);
            LoggerFactory.redis().logInfo(POPULAR_PROJECTS_KEY, "메타데이터 저장 완료 - key=" + POPULAR_PROJECTS_METADATA_KEY);
            
            LoggerFactory.redis().logSaveOrUpdate(POPULAR_PROJECTS_KEY, 
                "인기 프로젝트 캐시 저장 성공 count=" + popularProjects.size());
                
        } catch (Exception e) {
            LoggerFactory.redis().logError(POPULAR_PROJECTS_KEY, "인기 프로젝트 캐시 저장 실패", e);
        }
    }
    
    /**
     * 캐시된 데이터의 마지막 업데이트 시간을 조회합니다.
     * 
     * @return 마지막 업데이트 시간 (밀리초) 또는 빈 Optional
     */
    public Optional<Long> getLastUpdateTime() {
        try {
            String metadata = redisTemplate.opsForValue().get(POPULAR_PROJECTS_METADATA_KEY);
            return metadata != null ? Optional.of(Long.parseLong(metadata)) : Optional.empty();
        } catch (Exception e) {
            LoggerFactory.redis().logError("PopularProjectsRedisAdapter", "마지막 업데이트 시간 조회 실패", e);
            return Optional.empty();
        }
    }
    
    /**
     * 인기 프로젝트 캐시를 삭제합니다.
     */
    public void evictPopularProjects() {
        try {
            redisTemplate.delete(POPULAR_PROJECTS_KEY);
            redisTemplate.delete(POPULAR_PROJECTS_METADATA_KEY);
            
            LoggerFactory.redis().logDelete(POPULAR_PROJECTS_KEY, "인기 프로젝트 캐시 삭제 성공");
        } catch (Exception e) {
            LoggerFactory.redis().logError(POPULAR_PROJECTS_KEY, "인기 프로젝트 캐시 삭제 실패", e);
        }
    }
    
    /**
     * 캐시가 존재하고 유효한지 확인합니다.
     * 
     * @return 캐시 존재 여부
     */
    public boolean hasValidData() {
        return redisTemplate.hasKey(POPULAR_PROJECTS_KEY) && redisTemplate.hasKey(POPULAR_PROJECTS_METADATA_KEY);
    }
}
