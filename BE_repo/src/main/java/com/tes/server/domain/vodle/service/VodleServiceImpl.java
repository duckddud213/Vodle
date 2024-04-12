package com.tes.server.domain.vodle.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tes.server.domain.vodle.dto.request.VodleCreateReqDto;
import com.tes.server.domain.vodle.dto.request.VodleLocationReqDto;
import com.tes.server.domain.vodle.dto.response.*;
import com.tes.server.domain.vodle.entity.PitchEntity;
import com.tes.server.domain.vodle.entity.VodleEntity;
import com.tes.server.domain.vodle.entity.type.ContentType;
import com.tes.server.domain.vodle.entity.type.Location;
import com.tes.server.domain.vodle.repository.PitchRepository;
import com.tes.server.domain.vodle.repository.VodleRepository;
import com.tes.server.domain.user.entity.UserEntity;
import com.tes.server.domain.user.repository.UserRepository;
import com.tes.server.global.exception.ErrorCode;
import com.tes.server.global.exception.Exceptions;
import com.tes.server.global.openFeign.client.AiApiClient;
import com.tes.server.domain.vodle.dto.request.VodleTtsReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VodleServiceImpl implements VodleService {

    private final AmazonS3Client amazonS3Client;
    private final VodleRepository vodleRepository;
    private final UserRepository userRepository;
    private final PitchRepository pitchRepository;

    private final AiApiClient aiApiClient;
    private final VodleAIService vodleAIService;

    // S3 버킷
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    // CloudFront 배포 도메인
    @Value("${spring.cloud.aws.cloud-front}")
    private String deploymentDomain;

    @Value("${naver.clientId}")
    private String clientId;

    @Value("${naver.clientSecret}")
    private String clientSecret;

    // 음성 낙서 등록 서비스
    @Override
    @Transactional
    public void createVodle(String userCode, VodleCreateReqDto vodleCreateReqDto, MultipartFile soundFile) {

        // 현재 로그인 사용자 조회
        log.info("접속자 조회 시작 : {}", "START");
        UserEntity user = userRepository.findByUserCode(userCode);
        log.info("접속자 조회 완료 : {}", user.getId());

        log.info("음성 파일 저장 : {}", "... ING ...");

        try {

            log.info("STT 작업 시작 : {}", "START");
            // STT 작업을 비동기로 실행하고 결과를 기다립니다.
            CompletableFuture<String> sttFuture = vodleAIService.asyncStt(soundFile);
            String sttResult = sttFuture.join(); // STT 결과를 기다립니다.
            log.info("STT 작업 완료 : {}", "COMPLETE");
            
            log.info("Classification 작업 시작 : {}", "START");
            // STT 결과를 사용하여 Classification 작업을 비동기적으로 실행합니다.
            CompletableFuture<String> classificationFuture = CompletableFuture.supplyAsync(() -> {
                return aiApiClient.classifyText(sttResult);
            });

            // Classification 결과를 기다립니다.
            String classification = classificationFuture.join();
            log.info("Classification 작업 완료 : {}", "COMPLETE");

            // 파일 메타 데이터 설정
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentEncoding(soundFile.getContentType());
            objectMetadata.setContentLength(soundFile.getSize());
            
            // S3에 저장될 원본 파일명 생성
            String inputKey = vodleCreateReqDto.getStreamingURL()
                    .split(deploymentDomain)[1];
            inputKey = inputKey.substring(0, inputKey.length()-5)
                    + "_" + soundFile.getOriginalFilename();

            // ObjectRequest 생성 후 S3 저장
            amazonS3Client.putObject(bucket, inputKey, soundFile.getInputStream(), objectMetadata);

            // 음성 등록 엔티티 생성
            VodleEntity vodle = VodleEntity.builder()
                    .user(user)
                    .writer(vodleCreateReqDto.getWriter())
                    .recordType(vodleCreateReqDto.getRecordType())
                    .fileOriginName(soundFile.getOriginalFilename())
                    .fileOriginPath(inputKey)
                    .fileConversionPath(vodleCreateReqDto.getStreamingURL())
                    .contentType(ContentType.RESTAURANT.name().equals(classification)
                            ? ContentType.RESTAURANT:ContentType.SMALLTALK)
                    .location(new Location(
                            getLocation(vodleCreateReqDto.getLatitude(),vodleCreateReqDto.getLongitude()),
                            vodleCreateReqDto.getLatitude(),
                            vodleCreateReqDto.getLongitude()))
                    .build();

            // 유저의 음성 낙서 목록에 추가
            vodle.addSoundRecord(user);

            // 음성 등록 엔티티 생성
            vodleRepository.save(vodle);
        } catch (IOException e) {
            throw new Exceptions(ErrorCode.FILE_UPLOAD_FAIL);
        }

        log.info("음성 파일 저장 : {}", "... COMPLETE ...");
    }

    // 음성 낙서 다운로드 서비스
    @Override
    public VodleDownloadResDto downloadVodle(Long contentId) {

        log.info("음성 파일 엔티티 조회 시작 : {}", "... ING ...");

        // 음성 엔티티 조회
        if (!vodleRepository.findById(contentId).isPresent())
            throw new Exceptions(ErrorCode.VODLE_NOT_EXIST_DB);

        VodleEntity vodle = vodleRepository.findById(contentId).get();
        log.info("음성 파일 엔티티 조회 완료 : {}", "... COMPLETE ...");


        log.info("음성 파일 S3에서 찾기 : {}", "... ING ...");

        // KEY 생성
        String key = vodle.getFileOriginPath();

        // GetObjectRequest 생성
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);


        try {

            // AWS S3에서 해당되는 파일 다운로드
            S3Object s3Object = amazonS3Client.getObject(getObjectRequest);

            // S3 객체를 바이트 배열로 변환
            S3ObjectInputStream inputStream = s3Object.getObjectContent();

            // 파일 다운로드 Dto 생성
            VodleDownloadResDto vodleDownloadResDto = VodleDownloadResDto.builder()
                    .downloadSoundName(URLEncoder.encode(vodle.getFileOriginName(), "UTF-8").replaceAll("\\+", "%20"))
                    .downloadSoundBytes(IOUtils.toByteArray(inputStream))
                    .build();

            log.info("음성 파일 S3에서 가져오기 : {}", "... COMPLETE ...");
            return vodleDownloadResDto;
        } catch (IOException e) {
            throw new Exceptions(ErrorCode.FILE_DOWNLOAD_FAIL);
        }
    }

    // 음성 낙서 스트리밍 듣기 서비스
    @Override
    public String getStreamingURLVodle(Long contentId) {

        // 음성 낙서 조회
        VodleEntity vodle = vodleRepository.findById(contentId)
                .orElseThrow(() -> new Exceptions(ErrorCode.VODLE_NOT_EXIST_DB));

        return vodle.getFileConversionPath();
    }

    // 모든 음성 낙서 리스트 조회 서비스
    @Override
    public List<VodleGetResDto> getAllListVodle() {

        // 모든 음성 조회 결과를 담을 리스트 생성
        List<VodleGetResDto> vodleGetResDtoList = new ArrayList<>();

        // 음성 결과 담기
        for (VodleEntity vodle : vodleRepository.findAll()) {
            vodleGetResDtoList.add(
                    VodleGetResDto.builder()
                            .vodleId(vodle.getId())
                            .writer(vodle.getWriter())
                            .contentType(vodle.getContentType())
                            .fileOriginName(vodle.getFileOriginName()
                                    .substring(0, vodle.getFileOriginName().length() - 4))
                            .streamingURL(vodle.getFileConversionPath())
                            .address(vodle.getLocation().getAddress())
                            .latitude(vodle.getLocation().getLatitude())
                            .longitude(vodle.getLocation().getLongitude())
                            .createdDate(vodle.getCreatedDate())
                            .build());
        }

        return vodleGetResDtoList;
    }


    @Override
    public VodleConversioinResDto callConversion(String selectedVoice, String gender, MultipartFile soundFile) {
        if(!selectedVoice.equals("original")){
            Integer pitchChange = 0;
            PitchEntity pitchEntity = pitchRepository.findByVoiceType(selectedVoice);
            if(pitchEntity != null){
                if (gender.equals("male")){
                    pitchChange = pitchEntity.getMale();
                }else {
                    pitchChange = pitchEntity.getFemale();
                }
            }
            log.info("pitchChange : " + pitchChange);
            byte[] conversionData = aiApiClient.voiceConversion(selectedVoice, pitchChange, soundFile);

            // 파일 메타 데이터 설정
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentEncoding("audio/wav");
            objectMetadata.setContentLength(conversionData.length);

            // 식별 디렉터리 생성
            UUID directoryName = UUID.randomUUID();

            // 바이트 배열 파일명 생성 (파일명에 현재 시간 반영)
            String fileOriginPath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmSS"));

            // S3에 저장될 파일명
            String inputKey = "temp/" + directoryName + "/" + fileOriginPath;

            // ObjectRequest 생성 후 S3 저장
            amazonS3Client.putObject(bucket, inputKey, new ByteArrayInputStream(conversionData), objectMetadata);

            // HLS 변환 파일명 설정
            String convertedFileUrl = deploymentDomain + inputKey + ".m3u8";

            // ResponseDto 반환하고
            return new VodleConversioinResDto(convertedFileUrl, selectedVoice);
        }else {
            try{
                // 파일 메타 데이터 설정
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentEncoding(soundFile.getContentType());
                objectMetadata.setContentLength(soundFile.getSize());

                // 식별 디렉터리 생성
                UUID directoryName = UUID.randomUUID();

                // 바이트 배열 파일명 생성 (파일명에 현재 시간 반영)
                String fileOriginPath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmSS"));

                // S3에 저장될 파일명
                String inputKey = "temp/" + directoryName + "/" + fileOriginPath;

                // ObjectRequest 생성 후 S3 저장
                amazonS3Client.putObject(bucket, inputKey, soundFile.getInputStream(), objectMetadata);

                // HLS 변환 파일명 설정
                String convertedFileUrl = deploymentDomain + inputKey + ".m3u8";

                return new VodleConversioinResDto(convertedFileUrl, selectedVoice);
            }catch (IOException e){
                throw new Exceptions(ErrorCode.CONVERSION_PROCESSING_FAIL);
            }

        }
    }

    @Override
    public VodleTtsResDto callTts(VodleTtsReqDto vodleTtsReqDto) {

        byte[] tts = aiApiClient.tts(vodleTtsReqDto);

        // 파일 메타 데이터 설정
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentEncoding("audio/wav");
        objectMetadata.setContentLength(tts.length);

        // 식별 디렉터리 생성
        UUID directoryName = UUID.randomUUID();

        // 바이트 배열 파일명 생성 (파일명에 현재 시간 반영)
        String fileOriginPath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmSS"));

        // S3에 저장될 파일명
        String inputKey = "temp/" + directoryName + "/" + fileOriginPath;

        // ObjectRequest 생성 후 S3 저장
        amazonS3Client.putObject(bucket, inputKey, new ByteArrayInputStream(tts), objectMetadata);

        // HLS 변환 파일명 설정
        String convertedFileUrl = deploymentDomain + inputKey + ".m3u8";

        // ResponseDto 반환하고
        VodleTtsResDto vodleTtsResDto = VodleTtsResDto.builder()
                .selectedVoice(vodleTtsReqDto.getSelectedVoice())
                .convertedFileUrl(convertedFileUrl)
                .build();
        return vodleTtsResDto;
    }


    // 범위 내 음성 낙서 리스트 조회 서비스
    @Override
    public List<VodleGetResDto> getListVodle(VodleLocationReqDto vodleLocationReqDto) {

        // 현재 위치
        log.info("현재 위도 : {}", vodleLocationReqDto.getCenterLatitude());
        log.info("현재 경도 : {}", vodleLocationReqDto.getCenterLongitude());

        // 검색 결과를 담을 리스트 생성
        List<VodleGetResDto> vodleGetResDtoList = vodleRepository.findByLocation(vodleLocationReqDto);

        return vodleGetResDtoList;
    }


    // 비동기로 한번에 여러 요청 보내는 메서드
    @Override
    public List<VodleConversioinResDto> callConversionAsync(String gender, MultipartFile soundFile) {

        //목소리 타입리스트
        List<String> voiceTypes = Arrays.asList("ahri", "mundo", "optimusPrime", "trump", "elsa");

        // 메서드들을 비동기로 실행후 반환데이터들을 담을 리스트
        List<CompletableFuture<VodleConversioinResDto>> futures = new ArrayList<>();

        CompletableFuture<String> sttFuture = vodleAIService.asyncStt(soundFile);
        // STT 작업이 완료된 후, STT 결과를 사용하여 Classification 작업을 비동기적으로 실행
        CompletableFuture<String> classificationFuture = sttFuture.thenApplyAsync(stt -> aiApiClient.classifyText(stt));

        // 비동기로 목소리 변환과 stt를 실행
        for (String voiceType : voiceTypes){
            CompletableFuture<VodleConversioinResDto> future = classificationFuture.thenComposeAsync(classification -> {
                // 성별과 voiceType에 따라 pitchChange 조절
                Integer pitchChange = 0;
                PitchEntity pitchEntity = pitchRepository.findByVoiceType(voiceType);
                if(pitchEntity != null){
                    if (gender.equals("male")){
                        pitchChange = pitchEntity.getMale();
                    }else {
                        pitchChange = pitchEntity.getFemale();
                    }
                }
                log.info("ServiceImpl - selected_voice / pitchChange : " + voiceType + "/" +pitchChange);

                return vodleAIService.asyncVoiceConversion(voiceType, pitchChange, soundFile).thenApplyAsync(conversionData -> {

                    // 파일 메타 데이터 설정
                    ObjectMetadata objectMetadata = new ObjectMetadata();
                    objectMetadata.setContentEncoding("audio/wav");
                    objectMetadata.setContentLength(conversionData.length);

                    // 바이트 배열 파일명 생성 (파일명에 현재 시간 반영)
                    String fileOriginPath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmSS"))
                            + UUID.randomUUID();

                    // S3에 저장될 파일명
                    String inputKey = "temp/"+fileOriginPath;

                    // ObjectRequest 생성 후 S3 저장
                    amazonS3Client.putObject(bucket, inputKey, new ByteArrayInputStream(conversionData), objectMetadata);

                    // HLS 변환 파일명 설정
                    String convertedFileUrl = deploymentDomain+"temp/"+fileOriginPath+".m3u8";

                    // ResponseDto 반환하고
                    return new VodleConversioinResDto(convertedFileUrl, voiceType);
                });
            });
            futures.add(future);
        }

        // 모든 비동기처리가 완료될때까지 기다리고
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 결과를 조합
        List<VodleConversioinResDto> vodleConversioinResDtos = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return vodleConversioinResDtos;
    }


    // 비동기로 한번에 여러 요청 보내는 메서드
    @Override
    public List<VodleTtsResDto> callTtsAsync(VodleTtsReqDto vodleTtsReqDto) {
        //목소리 타입리스트
        List<String> voiceTypes = Arrays.asList("ahri", "mundo", "optimusPrime", "trump", "elsa");

        // 메서드들을 비동기로 실행후 반환데이터들을 담을 리스트
        List<CompletableFuture<VodleTtsResDto>> futures = new ArrayList<>();

        // 카테고리 추출 비동기로 실행
        CompletableFuture<String> classificationFuture = vodleAIService.asyncClassifyText(vodleTtsReqDto.getContent());

        // 목소리리스트를 반복문돌면서 tts 실행
        for (String voiceType : voiceTypes) {
            // 카테고리 추출이 완료가되면
            CompletableFuture<VodleTtsResDto> future = classificationFuture.thenComposeAsync(classification -> {
//                vodleTtsReqDto.setSelected_voice(voiceType);
                VodleTtsReqDto vodleTtsReqDto1 = VodleTtsReqDto.builder()
                        .selectedVoice(voiceType)
                        .content(vodleTtsReqDto.getContent())
                        .build();

                // tts를 비동기로 실행
                log.info("ttsRegDto.getSelected_voice : " + vodleTtsReqDto1.getSelectedVoice());

                return vodleAIService.asyncTts(vodleTtsReqDto1).thenApplyAsync(conversionData -> {

                    // 파일 메타 데이터 설정
                    ObjectMetadata objectMetadata = new ObjectMetadata();
                    objectMetadata.setContentEncoding("audio/wav");
                    objectMetadata.setContentLength(conversionData.length);

                    // 바이트 배열 파일명 생성 (파일명에 현재 시간 반영)
                    String fileOriginPath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmSS"))
                            + UUID.randomUUID();

                    // S3에 저장될 파일명
                    String inputKey = "temp/"+fileOriginPath;

                    // ObjectRequest 생성 후 S3 저장
                    amazonS3Client.putObject(bucket, inputKey, new ByteArrayInputStream(conversionData), objectMetadata);

                    // HLS 변환 파일명 설정
                    String convertedFileUrl = deploymentDomain+"temp/"+fileOriginPath+".m3u8";

                    // ResponseDto 반환하고
                    return new VodleTtsResDto(convertedFileUrl, voiceType);
                });
            });
            futures.add(future);
        }

        // 모든 비동기처리가 완료될때까지 기다리고
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 결과를 조합
        List<VodleTtsResDto> vodleTtsResDtos = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        return vodleTtsResDtos;
    }

    // 위도, 경도로 주소 조회
    public String getLocation(Double latitude, Double longitude) throws IOException {
        latitude = (double) Math.round(latitude*100000)/100000.0;
        longitude = (double) Math.round(longitude*100000)/100000.0;

        String coords = longitude + "," + latitude;

        String url = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?"+
                "coords=" + coords +
                "&sourcecrs=epsg:4326" +
                "&orders=addr,admcode,roadaddr" +
                "&output=json";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("X-NCP-APIGW-API-KEY-ID", clientId)
                .header("X-NCP-APIGW-API-KEY", clientSecret)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("response = " + response);

        Gson gson = new Gson();
        JsonObject obj = gson.fromJson(response.body().string(), JsonObject.class);

        JsonArray results = obj.getAsJsonArray("results");
        JsonObject result = results.get(0).getAsJsonObject();

        JsonObject region = result.getAsJsonObject("region");

        String area1 = region.getAsJsonObject("area1").get("name").getAsString();
        String area2 = region.getAsJsonObject("area2").get("name").getAsString();
        String area3 = region.getAsJsonObject("area3").get("name").getAsString();

        return area1 + " " + area2 + " " + area3;
    }
}