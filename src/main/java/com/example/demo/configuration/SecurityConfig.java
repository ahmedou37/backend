package com.example.demo.configuration;


import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.filters.JwtFilter;




@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

   @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    JwtFilter jwtFilter;

    // public SecurityConfig(UserDetailsService userDetailsService){
    //     this.userDetailsService=userDetailsService;
    // }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{//Extracts username/password.Wraps them into an Authentication object (specifically UsernamePasswordAuthenticationToken with credentials, not yet authenticated).
        http.csrf(customizer->customizer.disable());//u don't neet it because the session is stateless(don't remember u)
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.authorizeHttpRequests(request->request
        .requestMatchers("/users/login","/test","/ws/**", "/topic/**", "/app/**","users/verify/**").permitAll()
        .requestMatchers(HttpMethod.POST,"/users","/users/images").permitAll()
        .anyRequest().authenticated());
        //http.formLogin(Customizer.withDefaults());
        http.httpBasic(Customizer.withDefaults());
        http.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    // @Bean
    // public UserDetailsService userDetailsService(){
    //     UserDetails user1 = User.withDefaultPasswordEncoder().username("ahmed").password("1111").build();
    //     UserDetails user2 = User.withDefaultPasswordEncoder().username("sidi").password("2222").build();

    //     return new  InMemoryUserDetailsManager(user1,user2);

    // }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(10));
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
    


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));//Headers the browser can access from the response
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();//patern matching
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
//the HttpSecurity configures  Spring Security filters
//the SecurityFilterChain Represents the actual sequence of security filters

//spring automaticly create an HttpSecurity instance , and then it passe it to the methode

//A Servlet is a Java class that can handle HTTP requests and send HTTP responses.

//Authentication A simple object that represents the identity of a user ,it holds the username, password, roles etc..
//AuthenticationManger main role is deciding which AuthenticationProvider should handle the request.
//AuthenticationProvider actually performs the verification of the credentials inside an Authentication object.

//SecurityContext is like a thread-local storage: it holds the user’s identity for the current request.


// Putting it all together:
//1. A login request comes in with credentials → username/password.
//2.Spring Security creates an Authentication object (e.g., UsernamePasswordAuthenticationToken).
//3.This object is passed to the AuthenticationManager.
//4.The AuthenticationManager delegates to one of its AuthenticationProviders (e.g., DaoAuthenticationProvider).
//5.The provider verifies credentials (checks user in DB, compares password, etc.).Provider uses UserDetailsService to load user
//6.If valid → it returns a new authenticated Authentication object (with roles, no raw password,isAthenticated=true).
//7.Spring Security stores this in the SecurityContext, and the user is considered authenticated.


//A token is basically a piece of data (string) used for identifying and authenticating a user.



//Spring beans are singletons, meaning the same instance is reused everywhere. However, you can configure beans to create new instances by @scope("prototype")


