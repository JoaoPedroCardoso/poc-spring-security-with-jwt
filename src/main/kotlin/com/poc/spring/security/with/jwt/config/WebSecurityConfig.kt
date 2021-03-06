package com.poc.spring.security.with.jwt.config

import com.poc.spring.security.with.jwt.security.CustomAuthenticationProvider
import com.poc.spring.security.with.jwt.security.JWTAuthenticationFilter
import com.poc.spring.security.with.jwt.security.JWTAuthorizationFilter
import com.poc.spring.security.with.jwt.security.JWTUtil
import com.poc.spring.security.with.jwt.service.UserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


/**
 * Created by JoaoPedroCardoso on 30/08/18
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private val userDetailsService: UserDetailsService? = null

    @Autowired
    private val jwtUtil: JWTUtil? = null

    @Autowired
    private val authProvider: CustomAuthenticationProvider? = null

    @Autowired
    lateinit var messageSource: MessageSource

    companion object {

        private val PUBLIC_MATCHERS = arrayOf("/**")

        private val PUBLIC_MATCHERS_GET = arrayOf("/api/user/{id}", "/api/user/userName/{userName}")

        private val PRIVATE_MATCHERS_GET = arrayOf("/api/user")

        private val PUBLIC_SWAGGER_MATCHERS = arrayOf("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**", "/swagger-resources/configuration/ui", "/swagge‌​r-ui.html", "/swagger-resources/configuration/security")

        private val PUBLIC_MATCHERS_POST = arrayOf("/**")

        private val PUBLIC_MATCHERS_DELETE = arrayOf("/**")

        private val PUBLIC_MATCHERS_PUT = arrayOf("/**")
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {

        http.csrf().disable()
        http.cors().configurationSource(corsConfigurationSource())
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http.authorizeRequests()
            .antMatchers(*PUBLIC_SWAGGER_MATCHERS).permitAll()
            .antMatchers(HttpMethod.GET, *PUBLIC_MATCHERS_GET).hasRole("CLIENT")
            .antMatchers(HttpMethod.GET, *PRIVATE_MATCHERS_GET).hasRole("ADMIN")
            .antMatchers(HttpMethod.PUT, *PUBLIC_MATCHERS_PUT).hasRole("ADMIN")
            .antMatchers(HttpMethod.POST, *PUBLIC_MATCHERS_POST).permitAll()
            .antMatchers(HttpMethod.DELETE, *PUBLIC_MATCHERS_DELETE).hasRole("ADMIN")
            .anyRequest().authenticated().and()
            .addFilterBefore(JWTAuthenticationFilter(authenticationManager(), jwtUtil!!, messageSource),
                UsernamePasswordAuthenticationFilter::class.java)
            .addFilter(JWTAuthorizationFilter(authenticationManager(), jwtUtil, userDetailsService!!, messageSource))

    }

    @Throws(Exception::class)
    public override fun configure(auth: AuthenticationManagerBuilder?) {
        auth!!.authenticationProvider(authProvider)
        auth.userDetailsService<org.springframework.security.core.userdetails.UserDetailsService>(userDetailsService)
    }

    @Bean
    internal fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues())
        return source
    }

    @Bean
    internal fun corsConfiguration(): CorsConfiguration {
        val cors = CorsConfiguration()
        cors.addAllowedOrigin("*")
        cors.addAllowedMethod("*")
        cors.addAllowedHeader("*")
        cors.setAllowCredentials(true)
        cors.setMaxAge(1800L)
        return cors
    }

}
