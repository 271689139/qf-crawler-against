FROM registry.qiaofangyun.com/basetemplate/oraclejdk8_alpine:3.8
RUN mkdir /app
ADD qf-crawler-against-server.jar /app/crawleragainst.jar
ADD version.properties /app/
ADD runboot.sh /app/
RUN bash -c 'touch /app/crawleragainst.jar'
WORKDIR /app
RUN chmod a+x runboot.sh
CMD /app/runboot.sh