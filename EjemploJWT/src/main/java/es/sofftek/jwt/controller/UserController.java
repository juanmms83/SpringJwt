package es.sofftek.jwt.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.sofftek.jwt.dto.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
public class UserController {

	@Value("${llave.secreta}")
	private String secreta;
	
	@PostMapping("user")
	public User login(@RequestParam("user") String userName,@RequestParam("pwd") String pwd) {
		
		String token = getJWTToken(userName);
		User user = new User();
		user.setToken(token);
		user.setUser(userName);
		return user;
	}
	
	private String getJWTToken(String userName) {
		
		List<GrantedAuthority> grantedAuthorities = 
				AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
		
		String token = Jwts
				.builder()
				.setId("sofftekJWT")
				.setSubject(userName)
				.claim("authorities", grantedAuthorities.stream()
						.map(GrantedAuthority::getAuthority)
						.collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+600000))
				.signWith(SignatureAlgorithm.HS512,
						secreta.getBytes()).compact();
		
		return "Bearer " + token;
	}
}
