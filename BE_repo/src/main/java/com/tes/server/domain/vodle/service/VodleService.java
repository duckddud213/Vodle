package com.tes.server.domain.vodle.service;

import com.tes.server.domain.vodle.dto.request.VodleCreateReqDto;
import com.tes.server.domain.vodle.dto.request.VodleLocationReqDto;
import com.tes.server.domain.vodle.dto.response.*;
import com.tes.server.domain.vodle.dto.request.VodleTtsReqDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VodleService {

    // 음성 낙서 등록 서비스
    public void createVodle(String userCode, VodleCreateReqDto vodleCreateReqDto, MultipartFile soundFile);

    // 음성 낙서 다운로드 서비스
    public VodleDownloadResDto downloadVodle(Long contentId);

    // 음성 낙서 스트리밍 서비스
    public String getStreamingURLVodle(Long contentId);

    // 모든 음성 낙서 리스트 조회 서비스
    public List<VodleGetResDto> getAllListVodle();

    // 단일 변조 요청
    public VodleConversioinResDto callConversion(String selectedVoice, String gender, MultipartFile soundFile);

    // 단일 tts 요청
    public VodleTtsResDto callTts(VodleTtsReqDto vodleTtsReqDto);

    // 범위 내 음성 낙서 리스트 조회 서비스
    public List<VodleGetResDto> getListVodle(VodleLocationReqDto vodleLocationReqDto);

    // 비동기로 여러번 변조요청
    public List<VodleConversioinResDto> callConversionAsync(String gender, MultipartFile soundFile);

    // 비동기로 여러번 tts 요청
    public List<VodleTtsResDto> callTtsAsync(VodleTtsReqDto vodleTtsReqDto);

}
