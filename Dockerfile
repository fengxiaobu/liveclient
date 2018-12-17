FROM java
COPY target/ROOT.war app.war
RUN bash -c 'touch ./app.war'
EXPOSE 8088
CMD ["java","-jar","app.war"]