FROM openjdk:21

LABEL authors="yjl"

COPY ./build/libs/ai-service.jar /usr/h-nas/ai-service.jar

WORKDIR /usr/h-nas

EXPOSE 12999

CMD ["java", "-jar" , "ai-service.jar"]
