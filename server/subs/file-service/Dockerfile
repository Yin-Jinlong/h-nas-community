FROM nvidia/cuda:12.8.1-runtime-ubuntu24.04
LABEL authors="yjl"

RUN apt update
RUN apt install -y locales
RUN sed -i 's/# en_US.UTF-8 UTF-8/en_US.UTF-8 UTF-8/' /etc/locale.gen && locale-gen
ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US:en
ENV LC_ALL=en_US.UTF-8

# ffprobe 依赖
RUN apt install -y libpulse0
RUN apt install -y openjdk-21-jre

COPY ./build/libs/file-service.jar /usr/h-nas/file-service.jar

WORKDIR /usr/h-nas

EXPOSE 12002

CMD ["java","-jar", "file-service.jar"]
