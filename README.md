# Build the project
1. From the root of the project `gradlew build`
1. RESULT: it will produce a jar inside of: ./cnswapweb/build/libs/
1. To build the docker container 
      1. FOR MAC PLATFORM: `docker build --build-arg JAR_FILE=build/libs/cnswapweb-0.0.1-SNAPSHOT.jar -t avera/cnswap .`
      1. FOR x64: `docker build --platform linux/amd64 --build-arg JAR_FILE=build/libs/cnswapweb-0.0.1-SNAPSHOT.jar -t avera/cnswap:0.0.1_linux .`
1. RESULT: it will produce a new container into the local docker registry: avera/swapy:latest
1. To run the docker: `docker run -p 8080:8080 avera/swapy`

If you get an invalid username or password, is likely that the DB has not been initialized. Modify the application.properties to be: spring.sql.init.mode=always




# Things to remember
@Controller classes serve as C from MVC. Note that the real controller in Spring MVC is DispatcherServlet that will use the specific @Controller class to handle the URL request.

@Service classes should serve for your service layer. Here you should put your business logic.

@Repository classes should serve for your data access layer. Here you should put CRUD logic: insert, update, delete, select.

@Service, @Repository and your entity classes will be M from MVC. JSP and other view technologies(e.g. JSP, Thymeleaf etc.) will conform V from MVC.

@Controller classes should only have access to @Service classes through interfaces. Similar, @Service classes should only have access to other @Service classes and for a specific set of @Repository classes through interfaces.

   * (Source) https://stackoverflow.com/questions/25355385/where-to-put-business-logic-in-spring-mvc-framework
   
   
# Wireframes
https://www.figma.com/file/BL4vBmvGUHMzRbmud9KcmJ/Figma-basics?type=design&node-id=1669-162202&mode=design&t=mFnSZBqeSXLrXO1m-0
   

# Source in continued use for reference:
   * Bootstrap reference documents: Used to render components in HTML
      * https://getbootstrap.com/docs/4.0/components/card/

# Sources used:
   * Should had started with this one, as it explains the whole Database USER, ROLE relation for the Database login part
      * https://www.javaguides.net/2021/10/spring-boot-login-rest-api.html
   * I kept going back to this guy as it has a over all explanation of the processs too.
      * https://www.marcobehler.com/guides/spring-security#_authentication_with_spring_security
   * This one explains the lambda anotation, I kept refering to it at times
      * https://docs.spring.io/spring-security/reference/migration-7/configuration.html#_goals_of_the_lambda_dsl
   * This one has a very good overall process too and it usesrs thymeleaf
      * https://www.javaguides.net/2023/04/spring-security-custom-login-page.html
   * Explains the difference when using an MVC controler vs a RestControler
      * https://www.baeldung.com/spring-controllers
      * https://www.javaguides.net/2021/10/spring-boot-login-rest-api.html
      * https://docs.spring.io/spring-security/site/docs/4.1.3.RELEASE/guides/html5/form-javaconfig.html#configuring-a-login-view-controller
   * a Complete end to end of what I did
      * https://www.javaguides.net/2018/10/user-registration-module-using-springboot-springmvc-springsecurity-hibernate5-thymeleaf-mysql.html
   * OH MY GOSH!! Why didn't I think of starting here, react and spring boots, but this one doesn't have the DB and authentication part
     * https://spring.io/guides/tutorials/react-and-spring-data-rest/  
   * A course in 28 minutes: I didn't try it but it seems to cover the basics
      * https://www.springboottutorial.com/spring-boot-with-static-content-css-and-javascript-js
    
      