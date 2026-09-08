FROM registry.cn-beijing.aliyuncs.com/lianwuxian-prod/java-8u202:v1
ENV JAR_PATH /usr/local/common-export-service/common-export-service.jar
ENV CUSTOM_JAVA_OPTS -XX:+UseContainerSupport -XX:-UseAdaptiveSizePolicy -XX:MaxDirectMemorySize=512M -Xss512K -XX:MetaspaceSize=256m  -XX:MaxMetaspaceSize=512m -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/logs/jvm/oom/statistics-service.hprof -XX:ErrorFile=/data/logs/jvm/statistics-service_hs_err_pid.log
ENV NACOS_NAMESPACE 887b2633-1948-4f8b-a1be-ce073626b4a1
ENV NACOS_SERVERADDR 192.168.0.15:8848
ENV profile staging
ADD ./common-export-service.jar /usr/local/common-export-service/
ENTRYPOINT ["sh","-c","java $CUSTOM_JAVA_OPTS -jar $JAR_PATH --spring.profiles.active=$profile --spring.cloud.nacos.config.server-addr=$NACOS_SERVERADDR --spring.cloud.nacos.config.namespace=$NACOS_NAMESPACE"]
