package com.kentarsivi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.kentarsivi.service.CustomUserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

	//xuqruR-tuxre8-jihdym
	private String SECRET_KEY = "xuqruR-tuxre8-jihdym";
	private int DAY_FOR_EXPIRATION_TIME = 1;
	private int EXPIRATION_TIME = 86400000 * DAY_FOR_EXPIRATION_TIME;

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public String generateToken(CustomUserDetails customUserDetails) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, customUserDetails.getUsername(),customUserDetails.getUser().getFirstName(),customUserDetails.getUser().getLastName());
	}

	private String createToken(Map<String, Object> claims, String subject,String firstName,String lastName) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.claim("firstName", firstName)
				.claim("lastName", lastName)
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

}
