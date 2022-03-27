package com.alex.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //включает @PreAuthorize аннотацию в контроллере для доступа по ролям
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private final LoginSuccessHandler loginSuccessHandler;
    private final UserDetailsService userDetailsService;
    @Autowired
    public SecurityConfig(LoginSuccessHandler loginSuccessHandler, @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.loginSuccessHandler = loginSuccessHandler;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/","/user-create").permitAll()
                .antMatchers("/admin/**").hasAuthority("ADMIN")
                .antMatchers("/moderator/**").hasAuthority("MODERATOR")
                .antMatchers("/user/**").hasAnyAuthority("USER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .successHandler(loginSuccessHandler)
                .failureUrl("/login?error=true")
                .and()
                .logout()
                .logoutSuccessUrl("/login");
    }

    @Bean
    protected BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    protected DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(encoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
}
