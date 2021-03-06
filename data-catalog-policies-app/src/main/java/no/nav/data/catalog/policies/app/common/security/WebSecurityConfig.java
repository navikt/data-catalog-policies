package no.nav.data.catalog.policies.app.common.security;

import no.nav.data.catalog.policies.app.common.web.CorrelationFilter;
import no.nav.data.catalog.policies.app.common.web.UserFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorrelationFilter correlationFilter = new CorrelationFilter();
    private final UserFilter userFilter = new UserFilter();

    @Autowired(required = false)
    private AADStatelessAuthenticationFilter aadAuthFilter;
    @Value("${security.enabled:true}")
    private boolean enable;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        addFilters(http);

        if (!enable) {
            return;
        }
        http.authorizeRequests().antMatchers("/internal/**").permitAll();

        // Swagger ui
        http.authorizeRequests().antMatchers("/swagger*/**").permitAll();
        http.authorizeRequests().antMatchers("/webjars/springfox-swagger-ui/**").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, "/policy/**").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/behandlingsgrunnlag/**").permitAll();
        http.authorizeRequests().anyRequest().authenticated();
    }

    private void addFilters(HttpSecurity http) {
        http.addFilterBefore(correlationFilter, SecurityContextPersistenceFilter.class);
        // In lightweight mvc tests where authfilter isnt initialized
        if (aadAuthFilter != null) {
            http.addFilterBefore(aadAuthFilter, UsernamePasswordAuthenticationFilter.class);
            http.addFilterAfter(userFilter, AADStatelessAuthenticationFilter.class);
        } else {
            http.addFilterAfter(userFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }

}
