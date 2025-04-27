FROM openjdk:21

LABEL authors="yjl"

COPY ./build/libs/user-service.jar /usr/h-nas/user-service.jar

WORKDIR /usr/h-nas

EXPOSE 12001

CMD ["java", "-jar" , "user-service.jar"]
