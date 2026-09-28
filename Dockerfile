# build: compila e gera o jar
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build

# copia só o pom primeiro: as dependências ficam em cache enquanto o pom não mudar
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
# testes ficam de fora: o contextLoads precisa do MySQL, que não existe no build
RUN mvn -q -B package -DskipTests \
    && java -Djarmode=tools -jar target/*.jar extract --destination /build/app

# jre: monta um JRE só com os módulos que a aplicação usa
FROM eclipse-temurin:17-jdk-alpine AS jre
WORKDIR /jre
COPY --from=build /build/app ./app

# jdeps descobre os módulos necessários
RUN MODULOS=$(jdeps --ignore-missing-deps -q --recursive --multi-release 17 \
        --print-module-deps --class-path './app/lib/*' ./app/*.jar) \
    && jlink --add-modules "$MODULOS,jdk.crypto.ec,jdk.unsupported" \
        --strip-debug --no-man-pages --no-header-files --compress=2 \
        --output /jre/runtime

# final
FROM alpine:3.20
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app

COPY --from=jre /jre/runtime /opt/jre
COPY --from=build /build/app/lib ./lib
COPY --from=build /build/app/*.jar ./app.jar

ENV PATH="/opt/jre/bin:${PATH}"
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseSerialGC", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
