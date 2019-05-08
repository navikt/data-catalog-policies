package no.nav.data.catalog.policies.app.common.security;

import com.microsoft.azure.spring.autoconfigure.aad.AADAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AADAuthenticationFilter aadAuthFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests().antMatchers("/").permitAll();
        http.authorizeRequests().antMatchers("/policy/internal**").permitAll();
        http.authorizeRequests().antMatchers("/policy/policy**").authenticated();

        //http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        //    .logoutSuccessUrl("/").deleteCookies("JSESSIONID").invalidateHttpSession(true);
        //http.authorizeRequests().anyRequest().permitAll();
        //http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

        http.addFilterBefore(aadAuthFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
