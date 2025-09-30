package com.dataracy.modules.dataset.adapter.redis;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.response.read.PopularDataResponse;
import com.dataracy.modules.dataset.application.port.out.storage.PopularDataSetsStoragePort;
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
public class PopularDataSetsRedisAdapter implements PopularDataSetsStoragePort {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    
    // Adapter 및 키 상수 정의
    private static final String POPULAR_DATASETS_REDIS_ADAPTER = "PopularDataSetsRedisAdapter";
    private static final String POPULAR_DATASETS_KEY = "popular:datasets";
    private static final String POPULAR_DATASETS_METADATA_KEY = "popular:datasets:metadata";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10); // 10분 캐시
    
    /**
     * 인기 데이터셋 목록을 캐시에서 조회합니다.
     * 
     * @return 캐시된 인기 데이터셋 목록 (없으면 빈 Optional)
     */
    public Optional<List<PopularDataResponse>> getPopularDataSets() {
        Instant startTime = LoggerFactory.redis().logQueryStart(POPULAR_DATASETS_KEY, "인기 데이터셋 캐시 조회 시작");
        
        try {
            String cachedData = redisTemplate.opsForValue().get(POPULAR_DATASETS_KEY);
            if (cachedData == null) {
                LoggerFactory.redis().logWarning(POPULAR_DATASETS_KEY, "캐시에 인기 데이터셋 데이터가 없습니다.");
                return Optional.empty();
            }
            
            List<PopularDataResponse> popularDataSets = objectMapper.readValue(
                cachedData, 
                new TypeReference<List<PopularDataResponse>>() {}
            );
            
            LoggerFactory.redis().logQueryEnd(POPULAR_DATASETS_KEY, "인기 데이터셋 캐시 조회 성공", startTime);
            return Optional.of(popularDataSets);
            
        } catch (Exception e) {
            LoggerFactory.redis().logError(POPULAR_DATASETS_KEY, "인기 데이터셋 캐시 조회 실패", e);
            return Optional.empty();
        }
    }
    
    /**
     * 인기 데이터셋 목록을 캐시에 저장합니다.
     * 
     * @param popularDataSets 저장할 인기 데이터셋 목록
     */
    public void setPopularDataSets(List<PopularDataResponse> popularDataSets) {
        try {
            String jsonData = objectMapper.writeValueAsString(popularDataSets);
            redisTemplate.opsForValue().set(POPULAR_DATASETS_KEY, jsonData, CACHE_TTL);
            
            // 메타데이터도 함께 저장 (마지막 업데이트 시간)
            String metadata = String.valueOf(System.currentTimeMillis());
            redisTemplate.opsForValue().set(POPULAR_DATASETS_METADATA_KEY, metadata, CACHE_TTL);
            
            LoggerFactory.redis().logSaveOrUpdate(POPULAR_DATASETS_KEY, 
                "인기 데이터셋 캐시 저장 성공 count=" + popularDataSets.size());
                
        } catch (Exception e) {
            LoggerFactory.redis().logError(POPULAR_DATASETS_KEY, "인기 데이터셋 캐시 저장 실패", e);
        }
    }
    
    /**
     * 캐시된 데이터의 마지막 업데이트 시간을 조회합니다.
     * 
     * @return 마지막 업데이트 시간 (밀리초) 또는 빈 Optional
     */
    public Optional<Long> getLastUpdateTime() {
        try {
            String metadata = redisTemplate.opsForValue().get(POPULAR_DATASETS_METADATA_KEY);
            return metadata != null ? Optional.of(Long.parseLong(metadata)) : Optional.empty();
        } catch (Exception e) {
            LoggerFactory.redis().logError(POPULAR_DATASETS_REDIS_ADAPTER, "마지막 업데이트 시간 조회 실패", e);
            return Optional.empty();
        }
    }
    
    /**
     * 인기 데이터셋 캐시를 삭제합니다.
     */
    public void evictPopularDataSets() {
        try {
            redisTemplate.delete(POPULAR_DATASETS_KEY);
            redisTemplate.delete(POPULAR_DATASETS_METADATA_KEY);
            
            LoggerFactory.redis().logDelete(POPULAR_DATASETS_KEY, "인기 데이터셋 캐시 삭제 성공");
        } catch (Exception e) {
            LoggerFactory.redis().logError(POPULAR_DATASETS_KEY, "인기 데이터셋 캐시 삭제 실패", e);
        }
    }
    
    /**
     * 캐시가 존재하고 유효한지 확인합니다.
     * 
     * @return 캐시 존재 여부
     */
    public boolean hasValidData() {
        return redisTemplate.hasKey(POPULAR_DATASETS_KEY) && redisTemplate.hasKey(POPULAR_DATASETS_METADATA_KEY);
    }
}
