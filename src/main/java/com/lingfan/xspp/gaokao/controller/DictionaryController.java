package com.lingfan.xspp.gaokao.controller;

import com.lingfan.xspp.common.ApiResponse;
import com.lingfan.xspp.gaokao.dto.DictionaryResponse;
import com.lingfan.xspp.gaokao.service.DictionaryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/gaokao")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping("/dictionaries")
    public ApiResponse<DictionaryResponse> getDictionaries() {
        String traceId = UUID.randomUUID().toString();
        DictionaryResponse data = dictionaryService.getDictionaries();
        return ApiResponse.ok(data, traceId);
    }
}
