# springdocker


Dockerfile.old simply puts the jar in the container, works. When we rename it to just Dockerfile, we can do

gradle bootJar
docker build -t pikachu .
docker run -p 8080:8080
this runs the springboot app inside the container




https://spring.io/blog/2018/11/08/spring-boot-in-a-container#a-better-dockerfile

Dockerfile (the new approach) is the layered approach to only rebuild stuff that is new. For that we temp copy the extracted jar into /build/dependencies or whereever we will find it again.

tar -zxf /build/lib/publictransport.jar

then we manually move /BOOT-INF, /META-INF and /org to /build/dependencies

and then, well... nothing. Don't know how to write the Dockerfile so that it finds the JarLauncher from Spring Boot that WAS in the jar file but according to the tutorial has not to be included in the file anymore. Or if we include it, they don't say how
