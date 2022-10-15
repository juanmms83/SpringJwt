package es.sofftek.jwt.config;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import es.sofftek.jwt.utilidates.Constantes;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * Prioceso de autorizacion
 * @author Laura
 *
 */
@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter{	
	
	
	//@Value("${llave.secreta}")
	private String secreta="mySecretKey";
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			if(existeJWTToken(request)) {
				Claims claims = validateToken(request);
				if(claims.get("authorities") != null) {
					setUpSpringAuthetication(claims);
				}else {
					SecurityContextHolder.clearContext();
				}
			}else {
				SecurityContextHolder.clearContext();
			}
			filterChain.doFilter(request, response);
		}catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}

	private Claims validateToken(HttpServletRequest request) {
		String jwtToken = request.getHeader(Constantes.HEADER).replace(Constantes.PREFIX, "");
		return Jwts.parser().setSigningKey(secreta.getBytes()).parseClaimsJws(jwtToken).getBody();
	}
	
	/**
	 * Metodo para autenticarnos dentro del flujo de spring
	 * @param claims
	 */
	private void setUpSpringAuthetication(Claims claims) {
		List<String> authorities = (List)claims.get("authorities");
		
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
				authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
		
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	}
	
	/**
	 * Valida si existe un token
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean existeJWTToken(HttpServletRequest request) {
		String authenticationHeader = request.getHeader(Constantes.HEADER);
		
		if(authenticationHeader == null || !authenticationHeader.startsWith(Constantes.PREFIX)) {
			return false;
		}
		
		return true;
	}
	
}
