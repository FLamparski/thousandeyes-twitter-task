package com.thousandeyes;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.thousandeyes.models.Person;
import com.thousandeyes.repository.PersonRepository;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	private PersonRepository persons;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String idString = authentication.getName();
		String password = authentication.getCredentials().toString();
		if (!password.equals("thousandeyes")) throw new BadCredentialsException("Incorrect password");
		long id = Long.parseLong(idString, 10);
		Person p = persons.byId(id);
		if (p == null) throw new BadCredentialsException("Incorrect user ID");
		return new UsernamePasswordAuthenticationToken(p, password, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
