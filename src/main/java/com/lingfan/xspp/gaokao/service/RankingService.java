package com.lingfan.xspp.gaokao.service;

import com.lingfan.xspp.gaokao.dto.RankResolution;

public interface RankingService {
    int resolveRank(Integer rank, Integer score, String province, String subjectType, Integer year);

    // 返回包含来源与区间信息的位次解析结果
    RankResolution resolveRankWithMeta(Integer rank, Integer score, String province, String subjectType, Integer year);
}
