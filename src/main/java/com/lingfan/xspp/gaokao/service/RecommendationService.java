package com.lingfan.xspp.gaokao.service;

import com.lingfan.xspp.gaokao.dto.RecommendRequest;
import com.lingfan.xspp.gaokao.dto.RecommendResponse;

public interface RecommendationService {
    RecommendResponse recommend(RecommendRequest req);
}
