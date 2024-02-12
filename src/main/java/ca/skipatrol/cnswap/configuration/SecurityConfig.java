package ca.skipatrol.cnswap.configuration;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//	
//	@Value("${cnswap.db.script.path}")
//	private String cnswapDbScriptPath;
//	@Value("${cnswap.db.name}")
//	private String cnswapDbName;
//	@Value("${spring.datasource.username}")
//	private String springDatasourceUsername;
//	@Value("${spring.datasource.password}")
//	private String springDatasourcePassword;
	
	
	
	
//	@Bean
//	protected DataSource dataSource() {
//	//	return new SQLiteDataSource();		
//		DriverManagerDataSource dm = new DriverManagerDataSource(cnswapDbName,springDatasourceUsername,springDatasourcePassword);
//		Properties properties = new Properties();
//		properties.setProperty("create", "true");
//
//		dm.setConnectionProperties(properties);
//		dm.setSchema("APP");
//		dm.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
//		
//		return dm;
//		I DOES NOT WORK
//		return DataSourceBuilder
//				.create()
//				.driverClassName("org.apache.derby.jdbc.EmbeddedDriver")
//				.url(cnswapDbName)
//				.username(springDatasourceUsername)
//				.password(springDatasourcePassword)
//				.build();				
		
// IT WORKS BUT I AM NOT ABLE TO CHANGE THE PATH
//		return new EmbeddedDatabaseBuilder()
//			.setType(EmbeddedDatabaseType.DERBY)
//			.setName(cnswapDbName)
////			.addScript(cnswapDbScriptPath)
//			.build();
//	}
	
	
	@Bean
	public UserDetailsManager users(DataSource dataSource) {
		return new JdbcUserDetailsManager(dataSource);
		//return new CnSwapJdbcUserDetailsManager(dataSource); // (1)
	}
	
	@Bean
	protected PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

	
	@Bean
	protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {		
						
		http.formLogin( (form) -> form
        		.loginPage("/login").permitAll()
        		.loginProcessingUrl("/login")
        		.defaultSuccessUrl("/?success").permitAll()
        		.failureUrl("/login?error").permitAll()
        		)
			.authorizeHttpRequests( (request) -> request
			    .requestMatchers(AntPathRequestMatcher.antMatcher("/debug")).permitAll()
			    //.requestMatchers(AntPathRequestMatcher.antMatcher("/**")).permitAll() // to disable security
			    .requestMatchers(AntPathRequestMatcher.antMatcher("/")).permitAll()
			    .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST,"/logout")).permitAll()
			    .requestMatchers(AntPathRequestMatcher.antMatcher("/home")).permitAll()
			    .requestMatchers(AntPathRequestMatcher.antMatcher("/error")).permitAll()
			    .requestMatchers(AntPathRequestMatcher.antMatcher("/custom-logout")).permitAll()
			    .requestMatchers(AntPathRequestMatcher.antMatcher("/resources/**")).permitAll()
			    .requestMatchers(AntPathRequestMatcher.antMatcher("/api/**")).permitAll()
			    .requestMatchers(AntPathRequestMatcher.antMatcher("/register/**")).hasAnyAuthority("ROLE_ADMIN","ROLE_TELLER")
			    .requestMatchers(AntPathRequestMatcher.antMatcher("/manage/**")).hasAuthority("ROLE_ADMIN")
			    .requestMatchers(AntPathRequestMatcher.antMatcher("/admin/**")).hasAuthority("ROLE_ADMIN")
			    )
			.logout((logout) -> logout
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .logoutUrl("/custom-logout")
                .logoutSuccessUrl("/logout-success"))
		//	.logout(Customizer.withDefaults());
		//	.logout(logout -> logout.logoutSuccessUrl("/home"))
				//.httpBasic(Customizer.withDefaults())
				;
		
		
        return http.build();
		
	}
	
	
	
	@Bean
	protected AuthenticationManager authenticationManager(UserDetailsService userDetailsService,PasswordEncoder passwordEncoder) {
//		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//		authenticationProvider.setUserDetailsService(userDetailsService);
//		authenticationProvider.setPasswordEncoder(passwordEncoder);

		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);

		
		return new ProviderManager(authenticationProvider);
	}
	
	
	
	
//	@Bean
//	protected AuthenticationManager authenticationManager(List<AuthenticationProvider> authenticationProviders) {
//	    return new ProviderManager(authenticationProviders);
//	}

    @Bean
    protected WebSecurityCustomizer webSecurityCustomizer() {
	    return (web) -> web.ignoring().requestMatchers("/resources/**","/logout");
	  }
	
}
