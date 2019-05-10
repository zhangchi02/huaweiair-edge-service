FROM swr.cn-north-1.myhuaweicloud.com/hwstaff_j00347529/openjdk:8-jre-alpine

RUN mkdir -p /huaweiair/lib

COPY target/huaweiair-edge-service-0.0.1-SNAPSHOT.jar  /huaweiair/

COPY target/lib /huaweiair/lib

ENTRYPOINT java -jar /huaweiair/huaweiair-edge-service-0.0.1-SNAPSHOT.jar