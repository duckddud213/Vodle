package com.tes.server.domain.vodle.entity;

import com.tes.server.domain.vodle.entity.type.ContentType;
import com.tes.server.domain.vodle.entity.type.Location;
import com.tes.server.domain.vodle.entity.type.RecordType;
import com.tes.server.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "vodle")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "recordType", "fileOriginName", "fileOriginPath", "fileConversionPath", "contentType", "location"})
public class VodleEntity {

    // 기본 키 생성을 DB에 위임하는 전략
    @Column(name = "vodle_id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 음성을 남긴 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // 작성자
    @Column(name = "writer", nullable = false)
    private String writer;

    // 기록 종류
    @Column(name = "record_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecordType recordType;

    // 음성 원본 파일명
    @Column(name = "file_origin_name", nullable = false)
    private String fileOriginName;

    // 음성 원본 파일 경로 (HLS 변환 이전 파일)
    @Column(name = "file_origin_path", nullable = false, unique = true)
    private String fileOriginPath;

    // 음성 변환 파일 경로 (HLS 변환 이후 파일)
    @Column(name = "file_conversion_path", nullable = false, unique = true)
    private String fileConversionPath;

    // 음성 카테고리
    @Column(name = "content_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    // 위치 (위도, 경도)
    @Embedded
    private Location location;

    // 생성날짜
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    // 음성 <-> 유저 연관관계 메서드
    public void addSoundRecord(UserEntity user) {
        this.user = user;
        user.getSoundRecords().add(this);
    }

    @Builder
    public VodleEntity(UserEntity user, String writer, RecordType recordType, ContentType contentType,
                       String fileOriginName, String fileOriginPath, String fileConversionPath,
                       Location location) {
        this.user = user;
        this.writer = writer;
        this.recordType = recordType;
        this.contentType = contentType;
        this.fileOriginName = fileOriginName;
        this.fileOriginPath = fileOriginPath;
        this.fileConversionPath = fileConversionPath;
        this.location = location;
        this.createdDate = LocalDateTime.now();
    }
}
