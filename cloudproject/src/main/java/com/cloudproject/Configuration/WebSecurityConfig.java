package com.cloudproject.Configuration;

import com.cloudproject.auth.BasicAuthEntryPoint;
import com.cloudproject.auth.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableAutoConfiguration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private BasicAuthEntryPoint authEntryPoint;

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean(name = "dataSource")
//    public DriverManagerDataSource dataSource() {
//        Properties props = new Properties();
//        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
//        driverManagerDataSource.setDriverClassName("org.postgresql.Driver");
//        driverManagerDataSource.setUrl(props.getProperty("spring.datasource.url"));
//        driverManagerDataSource.setUsername(props.getProperty("spring.datasource.username"));
//        driverManagerDataSource.setPassword(props.getProperty("spring.datasource.password"));
//        return driverManagerDataSource;
//    }


    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("ABC");
        auth.authenticationProvider(customAuthenticationProvider);  //.dataSource(dataSource).usersByUsernameQuery("select username,password from logintb where username=?");

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().anyRequest().authenticated()
                .and().httpBasic()
                .and().sessionManagement().disable();

        http.csrf().disable().authorizeRequests().antMatchers("/time").authenticated();

        http.httpBasic().authenticationEntryPoint(authEntryPoint);

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/user/register");
    }

}