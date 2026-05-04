FROM gradle:latest

WORKDIR /app

COPY / .

RUN ./gradlew installDist

CMD ./build/install/app/bin/app