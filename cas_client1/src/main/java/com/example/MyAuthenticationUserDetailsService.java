package com.example;

import org.jasig.cas.client.validation.Assertion;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;


@Component
public class MyAuthenticationUserDetailsService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {
    @Override
    public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
        Assertion assertion = token.getAssertion();
        System.out.println(String.format("validFromDate:{ }", assertion.getValidFromDate()));
        System.out.println(String.format("validUntilDate:{ }", assertion.getValidUntilDate()));
        System.out.println(String.format("principal:{ }", assertion.getPrincipal()));
        System.out.println("attributes:{");

        Map<String, Object> map = assertion.getAttributes();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.println(String.format("{ }:{ },", entry.getKey(), entry.getValue()));
        }
        System.out.println("attributes:}");

        System.out.println(String.format("principal:{ }", token.getPrincipal()));
        System.out.println(String.format("credentials:{ }", token.getCredentials()));
        System.out.println(String.format("name:{ }", token.getName()));
        System.out.println("getAuthorities");
        Collection<GrantedAuthority> authorities = token.getAuthorities();

        return new User((String) token.getPrincipal(), (String) token.getCredentials(), true, true, true, true, token.getAuthorities());

    }
}
