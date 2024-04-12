package com.tes.server.domain.vodle.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tes.server.domain.vodle.dto.request.VodleLocationReqDto;
import com.tes.server.domain.vodle.dto.response.QVodleGetResDto;
import com.tes.server.domain.vodle.dto.response.VodleGetResDto;
import com.tes.server.domain.vodle.entity.type.ContentType;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.tes.server.domain.vodle.entity.QVodleEntity.vodleEntity;
import static java.lang.Math.*;

@Slf4j
public class VodleRepositoryImpl implements VodleRepositoryCustom {

    // JPAQueryFactory
    // Querydsl을 사용하여 JPA에서 타입-세이프한 쿼리를 생성하고 실행하기 위한 클래스
    private final JPAQueryFactory queryFactory;

    // EntityManager 인스턴스를 매개변수로 받아, 이를 사용하여 JPAQueryFactory 인스턴스를 초기화
    public VodleRepositoryImpl(EntityManager entityManager){
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    // 범위 내 음성 낙서 리스트 조회 메서드
    @Override
    public List<VodleGetResDto> findByLocation(VodleLocationReqDto vodleLocationReqDto) {

        // 반경 구하기
        double radius = calculateRadius(
                vodleLocationReqDto.getCenterLatitude(), vodleLocationReqDto.getCenterLongitude(),
                vodleLocationReqDto.getNorthEastLongitude(), vodleLocationReqDto.getSouthWestLongitude());

        log.info("현재 반경 : {}",radius);

        return queryFactory
                .select(new QVodleGetResDto(
                        vodleEntity.id, vodleEntity.writer ,vodleEntity.contentType,
                        vodleEntity.fileOriginName, vodleEntity.fileConversionPath,
                        vodleEntity.location.address, vodleEntity.location.latitude, vodleEntity.location.longitude,
                        vodleEntity.createdDate))
                .from(vodleEntity)
                .where(calculateDistance(vodleLocationReqDto.getCenterLatitude(), vodleLocationReqDto.getCenterLongitude(), vodleEntity.location.latitude, vodleEntity.location.longitude)
                        .loe(radius))
                .fetch();
    }

    // 음성 카테고리 판별 메서드
    private BooleanExpression contentTypeEq(String contentType) {
        return contentType != null ? vodleEntity.contentType.eq(ContentType.valueOf(contentType)) : null;
    }
    
    // 반경 계산 메서드
    private double calculateRadius(Double centerLatitude, Double centerLongitude, Double northEastLongitude, Double southWestLongitude) {

        // 지구의 반지름
        double earthRadius = 6371;

        // 중앙 좌표와 모바일 가장 왼쪽 중간 간의 경도 차이
        double deltaLongitude = toRadians(centerLongitude - southWestLongitude);

        // 중앙 좌표와 모바일 가장 왼쪽 중간 간의 각도 구하기 - 1
        double angle = cos(toRadians(centerLatitude)) * cos(toRadians(centerLatitude)) *
                sin(deltaLongitude/2) * sin(deltaLongitude/2);

        // 중앙 좌표와 모바일 가장 왼쪽 중간 간의 각도 구하기 - 2
        return earthRadius * 2 * atan2(sqrt(angle), sqrt(1-angle));
    }

    // 거리 계산 메서드
    private NumberExpression<Double> calculateDistance(Double currentLatitude, Double currentLongitude, NumberExpression<Double> latitude, NumberExpression<Double> longitude) {

        // 위도 radians 계산
        NumberExpression<Double> radiansLatitude = Expressions.numberTemplate(Double.class, "radians({0})", currentLatitude);

        // 위도 radians 코사인 계산
        NumberExpression<Double> cosRadiansLatitude = Expressions.numberTemplate(Double.class, "cos({0})", radiansLatitude);
        NumberExpression<Double> cosLatitude = Expressions.numberTemplate(Double.class, "cos(radians({0}))", latitude);

        // 위도 radians 사인 계산
        NumberExpression<Double> sinRadiansLatitude = Expressions.numberTemplate(Double.class, "sin({0})", radiansLatitude);
        NumberExpression<Double> sinLatitude = Expressions.numberTemplate(Double.class, "sin(radians({0}))", latitude);

        // 두 위치 사이 거리 계산
        NumberExpression<Double> cosLongitude = Expressions.numberTemplate(Double.class, "cos(radians({0}) - radians({1}))", longitude, currentLongitude);
        NumberExpression<Double> acosExpression = Expressions.numberTemplate(Double.class, "acos({0})", cosRadiansLatitude.multiply(cosLatitude).multiply(cosLongitude).add(sinRadiansLatitude.multiply(sinLatitude)));
        NumberExpression<Double> distanceExpression = Expressions.numberTemplate(Double.class, "6371 * {0}", acosExpression);

        return distanceExpression;
    }
}