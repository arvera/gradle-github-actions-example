package ca.skipatrol.cnswap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.WebApplicationInitializer;

import ca.skipatrol.cnswap.configuration.StorageConfig;
import ca.skipatrol.cnswap.util.SwapyAppConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

@SpringBootApplication( exclude = { SecurityAutoConfiguration.class } )
@EnableConfigurationProperties(StorageConfig.class)
//@ EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class)
public class CnswapwebApplication implements WebApplicationInitializer  {

	public static void main(String[] args) {
		SpringApplication.run(CnswapwebApplication.class, args);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		servletContext.setAttribute(SwapyAppConfig.SWAPY_CONFIG, SwapyAppConfig.getInstance());
		
	}

	
	//https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html failure Analyzer
}
