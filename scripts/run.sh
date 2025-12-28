cd src/main/angular && ng serve --open ; cd ../../..
OPENAI_API_KEY=$EMBABEL_OPEN_AI ./mvnw -U -Dmaven.test.skip=true spring-boot:run
