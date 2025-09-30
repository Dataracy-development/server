package com.dataracy.modules.dataset.application.service.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.response.read.*;
import com.dataracy.modules.dataset.application.dto.response.support.DataLabelMapResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import com.dataracy.modules.dataset.application.mapper.read.DataReadDtoMapper;
import com.dataracy.modules.dataset.application.port.in.query.read.*;
import com.dataracy.modules.dataset.application.port.out.query.read.*;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.model.vo.DataUser;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.GetDataTypeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.GetOccupationLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.GetUserInfoUseCase;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import com.dataracy.modules.dataset.application.port.out.storage.PopularDataSetsStoragePort;
import com.dataracy.modules.dataset.application.port.in.storage.UpdatePopularDataSetsStorageUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DataReadService implements
        GetPopularDataSetsUseCase,
        GetDataDetailUseCase,
        GetRecentMinimalDataSetsUseCase,
        GetDataGroupCountUseCase,
        FindConnectedDataSetsUseCase
{
    private final DataReadDtoMapper dataReadDtoMapper;

    private final GetPopularDataSetsPort getPopularDataSetsPort;
    private final GetRecentDataSetsPort getRecentDataSetsPort;
    private final FindDataWithMetadataPort findDataWithMetadataPort;
    private final GetDataGroupCountPort getDataGroupCountPort;
    private final FindConnectedDataSetsPort findConnectedDataSetsPort;

    // Use Case 상수 정의
    private static final String GET_POPULAR_DATA_SETS_USE_CASE = "GetPopularDataSetsUseCase";
    private static final String GET_DATA_DETAIL_USE_CASE = "GetDataDetailUseCase";
    private static final String GET_RECENT_MINIMAL_DATA_SETS_USE_CASE = "GetRecentMinimalDataSetsUseCase";
    private static final String GET_DATA_GROUP_COUNT_USE_CASE = "GetDataGroupCountUseCase";
    private static final String FIND_CONNECTED_DATA_SETS_USE_CASE = "FindConnectedDataSetsUseCase";
    
    // 메시지 상수 정의
    private static final String DATA_NOT_FOUND_MESSAGE = "해당 데이터셋이 존재하지 않습니다. dataId=";

    private final GetUserInfoUseCase getUserInfoUseCase;

    private final FindUsernameUseCase findUsernameUseCase;
    private final FindUserThumbnailUseCase findUserThumbnailUseCase;
    private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    private final GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
    private final GetDataTypeLabelFromIdUseCase getDataTypeLabelFromIdUseCase;
    private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;
    private final GetOccupationLabelFromIdUseCase getOccupationLabelFromIdUseCase;
    private final FindDataLabelMapUseCase findDataLabelMapUseCase;
    
    // 저장소 관련 의존성
    private final PopularDataSetsStoragePort popularDataSetsStoragePort;
    private final UpdatePopularDataSetsStorageUseCase updatePopularDataSetsStorageUseCase;

    /**
         * 지정된 개수만큼 인기 데이터셋을 조회하여 사용자 닉네임, 프로필 썸네일 URL, 주제/데이터 소스/데이터 타입 라벨,
         * 및 각 데이터셋의 연결된 프로젝트 수를 포함한 응답 리스트를 반환합니다.
         * 
         * 캐시 우선 조회 후, 캐시가 없으면 DB에서 조회하고 캐시를 워밍업합니다.
         *
         * @param size 조회할 인기 데이터셋의 최대 개수
         * @return 인기 데이터셋 정보(닉네임, 프로필 썸네일 URL, 라벨들 및 연결된 프로젝트 수)를 담은 응답 리스트
         */
    @Override
    @Transactional(readOnly = true)
    public List<PopularDataResponse> getPopularDataSets(int size) {
        Instant startTime = LoggerFactory.service().logStart(GET_POPULAR_DATA_SETS_USE_CASE, "인기 데이터셋 목록 조회 서비스 시작 size=" + size);

        // 저장소에서 먼저 조회
        LoggerFactory.service().logInfo(GET_POPULAR_DATA_SETS_USE_CASE, "저장소에서 캐시 조회 시작");
        var cachedResult = popularDataSetsStoragePort.getPopularDataSets();
        LoggerFactory.service().logInfo(GET_POPULAR_DATA_SETS_USE_CASE, "저장소 캐시 조회 결과: " + (cachedResult.isPresent() ? "데이터 존재" : "데이터 없음"));
        if (cachedResult.isPresent()) {
            List<PopularDataResponse> cachedData = cachedResult.get();
            List<PopularDataResponse> result = cachedData.stream()
                    .limit(size)
                    .toList();
            
            LoggerFactory.service().logSuccess(GET_POPULAR_DATA_SETS_USE_CASE, 
                "인기 데이터셋 저장소 조회 성공 size=" + size + " cachedCount=" + cachedData.size(), startTime);
            return result;
        }

        // 저장소에 데이터가 없으면 DB에서 조회 (기존 로직)
        LoggerFactory.service().logInfo(GET_POPULAR_DATA_SETS_USE_CASE, "저장소에 데이터가 없어 DB에서 조회합니다. size=" + size);
        
        List<DataWithProjectCountDto> savedDataSets = getPopularDataSetsPort.getPopularDataSets(size);
        DataLabelMapResponse labelResponse = findDataLabelMapUseCase.labelMapping(savedDataSets);

        List<PopularDataResponse> popularDataResponses = savedDataSets.stream()
                .map(wrapper -> {
                    Data data = wrapper.data();
                    return dataReadDtoMapper.toResponseDto(
                            data,
                            labelResponse.usernameMap().get(data.getUserId()),
                            labelResponse.userProfileUrlMap().get(data.getUserId()),
                            labelResponse.topicLabelMap().get(data.getTopicId()),
                            labelResponse.dataSourceLabelMap().get(data.getDataSourceId()),
                            labelResponse.dataTypeLabelMap().get(data.getDataTypeId()),
                            wrapper.countConnectedProjects()
                    );
                })
                .toList();

        // 캐시 워밍업 (비동기)
        updatePopularDataSetsStorageUseCase.warmUpCacheIfNeeded(Math.max(size, 20));

        LoggerFactory.service().logSuccess(GET_POPULAR_DATA_SETS_USE_CASE, "인기 데이터셋 DB 조회 서비스 종료 size=" + size, startTime);
        return popularDataResponses;
    }

    /**
     * 주어진 데이터 ID에 해당하는 데이터셋의 상세 정보를 반환합니다.
     * 데이터셋의 기본 정보, 작성자 닉네임, 작성자 등급 및 직업 라벨, 주제/데이터 소스/데이터 타입 라벨, 기간, 설명, 분석 가이드, 썸네일 URL, 다운로드 수, 메타데이터(행/열 개수, 미리보기 JSON), 생성일시를 포함한 상세 정보를 제공합니다.
     *
     * @param dataId 조회할 데이터셋의 ID
     * @return 데이터셋의 상세 정보를 담은 DataDetailResponse 객체
     * @throws DataException 데이터셋이 존재하지 않을 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public DataDetailResponse getDataDetail(Long dataId) {
        Instant startTime = LoggerFactory.service().logStart(GET_DATA_DETAIL_USE_CASE, "데이터셋 상세 정보 조회 서비스 시작 dataId=" + dataId);

        Data data = findDataWithMetadataPort.findDataWithMetadataById(dataId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning(GET_DATA_DETAIL_USE_CASE, DATA_NOT_FOUND_MESSAGE + dataId);
                    return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });
        UserInfo userInfo = getUserInfoUseCase.extractUserInfo(data.getUserId());
        DataUser dataUser = DataUser.fromUserInfo(userInfo);

        String authorLabel = dataUser.authorLevelId() == null ? null : getAuthorLevelLabelFromIdUseCase.getLabelById(dataUser.authorLevelId());
        String occupationLabel = dataUser.occupationId() == null ? null : getOccupationLabelFromIdUseCase.getLabelById(dataUser.occupationId());

        DataDetailResponse dataDetailResponse = dataReadDtoMapper.toResponseDto(
                data,
                dataUser.nickname(),
                dataUser.profileImageUrl(),
                dataUser.introductionText(),
                authorLabel,
                occupationLabel,
                getTopicLabelFromIdUseCase.getLabelById(data.getTopicId()),
                getDataSourceLabelFromIdUseCase.getLabelById(data.getDataSourceId()),
                getDataTypeLabelFromIdUseCase.getLabelById(data.getDataTypeId())
        );

        LoggerFactory.service().logSuccess(GET_DATA_DETAIL_USE_CASE, "데이터셋 상세 정보 조회 서비스 종료 dataId=" + dataId, startTime);
        return dataDetailResponse;
    }

    /**
         * 최신 데이터셋을 지정된 개수만큼 조회하여 사용자 닉네임과 프로필 썸네일을 포함한 최소 정보 응답 리스트를 반환합니다.
         *
         * 조회된 각 데이터는 사용자 ID로부터 닉네임과 썸네일 URL을 조회하여 DTO로 매핑됩니다.
         *
         * @param size 조회할 데이터셋의 최대 개수
         * @return 최신 데이터셋의 최소 정보 응답 리스트
         */
    @Override
    @Transactional(readOnly = true)
    public List<RecentMinimalDataResponse> getRecentDataSets(int size) {
        Instant startTime = LoggerFactory.service().logStart(GET_RECENT_MINIMAL_DATA_SETS_USE_CASE, "최신 미니 데이터셋 목록 조회 서비스 시작 size=" + size);

        List<Data> recentDataSets = getRecentDataSetsPort.getRecentDataSets(size);
        List<Long> userIds = recentDataSets.stream()
                .map(Data::getUserId)
                .toList();

        Map<Long, String> usernameMap = findUsernameUseCase.findUsernamesByIds(userIds);
        Map<Long, String> userProfileUrlMap = findUserThumbnailUseCase.findUserThumbnailsByIds(userIds);
        List<RecentMinimalDataResponse> recentMinimalDataResponses = recentDataSets.stream()
                .map(data -> dataReadDtoMapper.toResponseDto(
                            data,
                            usernameMap.get(data.getUserId()),
                            userProfileUrlMap.get(data.getUserId())
                ))
                .toList();

        LoggerFactory.service().logSuccess(GET_RECENT_MINIMAL_DATA_SETS_USE_CASE, "최신 미니 데이터셋 목록 조회 서비스 종료 size=" + size, startTime);
        return recentMinimalDataResponses;
    }

    /**
     * 데이터셋을 주제별로 그룹화하여 각 주제에 속한 데이터셋의 개수를 조회합니다.
     *
     * @return 각 주제별 데이터셋 개수를 담은 DataGroupCountResponse 객체의 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<DataGroupCountResponse> getDataGroupCountByTopicLabel() {
        Instant startTime = LoggerFactory.service().logStart(GET_DATA_GROUP_COUNT_USE_CASE, "데이터셋을 주제별로 그룹화하여 각 주제에 속한 데이터셋의 개수를 반환 서비스 시작");
        List<DataGroupCountResponse> dataGroupCountResponses = getDataGroupCountPort.getDataGroupCount();
        LoggerFactory.service().logSuccess(GET_DATA_GROUP_COUNT_USE_CASE, "데이터셋을 주제별로 그룹화하여 각 주제에 속한 데이터셋의 개수를 반환 서비스 종료", startTime);
        return dataGroupCountResponses;
    }

    /**
     * 지정된 프로젝트와 연결된 데이터셋들을 페이지 단위로 조회하여 라벨(작성자 닉네임, 프로필 URL, 주제, 데이터 타입)과
     * 각 데이터셋의 연결된 프로젝트 수를 포함한 ConnectedDataResponse 페이지로 반환합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @param pageable  페이지 번호와 크기, 정렬 정보를 담은 Pageable 객체
     * @return 해당 프로젝트와 연결된 데이터셋들의 라벨 및 연결 프로젝트 수를 포함한 Page<ConnectedDataResponse>
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ConnectedDataResponse> findConnectedDataSetsAssociatedWithProject(Long projectId, Pageable pageable) {
        Instant startTime = LoggerFactory.service().logStart(FIND_CONNECTED_DATA_SETS_USE_CASE, "프로젝트와 연결된 데이터셋 목록 조회 서비스 시작 projectId=" + projectId);

        Page<DataWithProjectCountDto> savedDataSets = findConnectedDataSetsPort.findConnectedDataSetsAssociatedWithProject(projectId, pageable);
        DataLabelMapResponse labelResponse = findDataLabelMapUseCase.labelMapping(savedDataSets.getContent());
        Page<ConnectedDataResponse> connectedDataResponses = savedDataSets.map(wrapper -> {
            Data data = wrapper.data();
            return dataReadDtoMapper.toResponseDto(
                    data,
                    labelResponse.usernameMap().get(data.getUserId()),
                    labelResponse.userProfileUrlMap().get(data.getUserId()),
                    labelResponse.topicLabelMap().get(data.getTopicId()),
                    labelResponse.dataTypeLabelMap().get(data.getDataTypeId()),
                    wrapper.countConnectedProjects()
            );
        });

        LoggerFactory.service().logSuccess(FIND_CONNECTED_DATA_SETS_USE_CASE, "프로젝트와 연결된 데이터셋 목록 조회 서비스 종료 projectId=" + projectId, startTime);
        return connectedDataResponses;
    }

    /**
     * 주어진 데이터셋 ID 목록으로 해당하는 연결된 데이터셋들을 조회하여 DTO 리스트로 반환합니다.
     *
     * 주어진 ID 목록이 null이거나 비어 있으면 빈 리스트를 반환하며, 내부적으로 중복 ID는 제거됩니다.
     * 반환되는 각 항목은 데이터 엔티티와 함께 작성자명/프로필 URL, 토픽 라벨, 데이터 타입 라벨 및 연결된 프로젝트 수를 포함한 ConnectedDataResponse로 매핑됩니다.
     *
     * @param dataIds 조회할 데이터셋 ID 목록 (null 또는 빈 리스트일 수 있음)
     * @return 조회된 연결된 데이터셋 정보를 담은 ConnectedDataResponse 리스트 (ID 목록이 없으면 빈 리스트)
     */
    @Override
    @Transactional(readOnly = true)
    public List<ConnectedDataResponse> findDataSetsByIds(List<Long> dataIds) {
        Instant startTime = LoggerFactory.service().logStart(FIND_CONNECTED_DATA_SETS_USE_CASE, "아이디 목록을 통한 데이터셋 목록 조회 서비스 시작 dataIds=" + dataIds);

        if (dataIds == null || dataIds.isEmpty()) {
            LoggerFactory.service().logSuccess(FIND_CONNECTED_DATA_SETS_USE_CASE, "빈 ID 목록으로 빈 결과 반환", startTime);
            return List.of();
        }
        List<Long> distinctIds = dataIds.stream().distinct().toList();
        List<DataWithProjectCountDto> savedDataSets = findConnectedDataSetsPort.findConnectedDataSetsAssociatedWithProjectByIds(distinctIds);

        DataLabelMapResponse labelResponse = findDataLabelMapUseCase.labelMapping(savedDataSets);
        List<ConnectedDataResponse> connectedDataResponses = savedDataSets.stream()
                .map(wrapper -> {
                    Data data = wrapper.data();
                    return dataReadDtoMapper.toResponseDto(
                            data,
                            labelResponse.usernameMap().get(data.getUserId()),
                            labelResponse.userProfileUrlMap().get(data.getUserId()),
                            labelResponse.topicLabelMap().get(data.getTopicId()),
                            labelResponse.dataTypeLabelMap().get(data.getDataTypeId()),
                            wrapper.countConnectedProjects()
                    );
                })
                .toList();

        LoggerFactory.service().logSuccess(FIND_CONNECTED_DATA_SETS_USE_CASE, "아이디 목록을 통한 데이터셋 목록 조회 서비스 종료 dataIds=" + dataIds, startTime);
        return connectedDataResponses;
    }
}
