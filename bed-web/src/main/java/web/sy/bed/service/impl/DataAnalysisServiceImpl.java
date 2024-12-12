package web.sy.bed.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import web.sy.base.mapper.DataAnalysisMapper;
import web.sy.base.pojo.entity.ImageInfo;
import web.sy.bed.service.DataAnalysisService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataAnalysisServiceImpl implements DataAnalysisService {
    
    private final DataAnalysisMapper dataAnalysisMapper;
    
    @Override
    public int getImageDownloadCount(String pathname, LocalDateTime startTime, LocalDateTime endTime) {
        return dataAnalysisMapper.getImageDownloadCount(pathname, startTime, endTime);
    }
    
    @Override
    public int getUserImagesDownloadCount(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return dataAnalysisMapper.getUserImagesDownloadCount(userId, startTime, endTime);
    }
    
    @Override
    public List<ImageInfo> getTopDownloadedImagesForToday() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();
        return dataAnalysisMapper.getTopDownloadedImages(startOfDay, endOfDay);
    }
    
    @Override
    public List<ImageInfo> getTopDownloadedImages(LocalDateTime startTime, LocalDateTime endTime) {
        return dataAnalysisMapper.getTopDownloadedImages(startTime, endTime);
    }
    
    @Override
    public Long getImageTrafficUsage(String pathname, LocalDateTime startTime, LocalDateTime endTime) {
        return dataAnalysisMapper.getImageTrafficUsage(pathname, startTime, endTime);
    }
    
    @Override
    public Long getTotalTrafficUsage(LocalDateTime startTime, LocalDateTime endTime) {
        return dataAnalysisMapper.getTotalTrafficUsage(startTime, endTime);
    }
    
    @Override
    public int getTotalDownloadCount(LocalDateTime startTime, LocalDateTime endTime) {
        return dataAnalysisMapper.getTotalDownloadCount(startTime, endTime);
    }
    
    @Override
    public List<Map<String, Object>> getDailyDownloadStatsLast30Days() {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(30);
        return dataAnalysisMapper.getDailyDownloadStats(startTime, endTime);
    }
    
    @Override
    public List<Map<String, Object>> getUserDailyDownloadStatsLast30Days(Long userId) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(30);
        return dataAnalysisMapper.getUserDailyDownloadStats(userId, startTime, endTime);
    }
    
    @Override
    public int getTodayUploadCount(Long userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();
        return dataAnalysisMapper.getTodayUploadCount(userId, startOfDay, endOfDay);
    }
    
    @Override
    public List<ImageInfo> getLatestUploadedImages(Long userId, int limit) {
        return dataAnalysisMapper.getLatestUploadedImages(userId, limit);
    }
} 