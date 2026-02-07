package com.skch.skch_api_server.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import com.skch.skch_api_server.common.AuthProps;
import com.skch.skch_api_server.dto.JwtDTO;
import com.skch.skch_api_server.dto.LoginRequest;
import com.skch.skch_api_server.dto.LoginResponse;
import com.skch.skch_api_server.dto.ReqSearch;
import com.skch.skch_api_server.dto.Result;
import com.skch.skch_api_server.dto.ValidateLinkDTO;
import com.skch.skch_api_server.exception.CustomException;
import com.skch.skch_api_server.service.LoginService;
import com.skch.skch_api_server.util.JwtUtil;
import com.skch.skch_api_server.util.PkceService;
import com.skch.skch_api_server.util.PkceUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/authenticate")
@Slf4j
@RequiredArgsConstructor
public class LoginController {

	private final LoginService loginService;
	private final JwtUtil jwtUtil;

	@Value("${app.token-expiry}")
	private long tokenExpiry;

	private final AuthProps authProps;
	private final PkceService pkceService;

	/**
	 * Login User and set HttpOnly cookies
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@PostMapping("/login")
	public ResponseEntity<Result> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {

		Result result = loginService.login(request);
		LoginResponse responce = (LoginResponse) result.getData();
		String accessToken = responce.getJwtDTO().getAccess_token();
		String refreshToken = responce.getJwtDTO().getRefresh_token();

		// ACCESS TOKEN (short-lived)
		ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", accessToken)
				.httpOnly(true).secure(true)
				.sameSite("None").path("/").maxAge(Duration.ofMinutes(tokenExpiry)).build();

		// REFRESH TOKEN (long-lived)
		ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
				.httpOnly(true).secure(true) // true
				.sameSite("Lax")
				.path("/authenticate").maxAge(Duration.ofHours(tokenExpiry)).build();

		response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

		return ResponseEntity.ok(result);
	}

	/**
	 * Initiate SSO Login
	 * 
	 * @param response
	 * @throws Exception
	 */
	@GetMapping("/sso-login")
	public void login(HttpServletResponse response) throws Exception {

		// 1. Generate PKCE
		String codeVerifier = PkceUtil.generateCodeVerifier();
		String codeChallenge = PkceUtil.generateCodeChallenge(codeVerifier);

		// 2. Generate OAuth state
		String state = UUID.randomUUID().toString();

		// 3. Store STATE in HttpOnly cookie
		ResponseCookie stateCookie = ResponseCookie.from("OAUTH2_STATE", state)
				.httpOnly(true).secure(false) // 🔒 true
				.sameSite("Lax") // 🔑 REQUIRED for redirects
				.path("/authenticate").maxAge(Duration.ofMinutes(5)).build();

		// 4. Store PKCE verifier in HttpOnly cookie
		ResponseCookie pkceCookie = ResponseCookie.from("PKCE_VERIFIER", codeVerifier)
				.httpOnly(true).secure(false) // 🔒
				.sameSite("Lax").path("/authenticate").maxAge(Duration.ofMinutes(5)).build();

		response.addHeader(HttpHeaders.SET_COOKIE, stateCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, pkceCookie.toString());

		// 5. Build Authorization Server redirect URL
		String redirectUrl = authProps.getServer().getAuthorizeUrl() + "?response_type=code" + "&client_id="
				+ authProps.getClientId() + "&redirect_uri="
				+ URLEncoder.encode(authProps.getRedirectUri(), StandardCharsets.UTF_8) + "&scope="
				+ URLEncoder.encode(authProps.getScope(), StandardCharsets.UTF_8) + "&state=" + state
				+ "&code_challenge=" + codeChallenge + "&code_challenge_method=S256";

		// 6. Redirect browser
		response.sendRedirect(redirectUrl);
	}

	@GetMapping("/callback")
	public void callback(@RequestParam("code") String code, @RequestParam("state") String returnedState,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		log.info("OAuth Callback received with code: {} and state: {}", code, returnedState);
		
		// 1. Read cookies
		Cookie stateCookie = WebUtils.getCookie(request, "OAUTH2_STATE");
		Cookie pkceCookie = WebUtils.getCookie(request, "PKCE_VERIFIER");

		if (stateCookie == null || pkceCookie == null) {
			throw new IllegalStateException("Missing OAuth cookies");
		}

		// 2. Validate STATE
		if (!returnedState.equals(stateCookie.getValue())) {
			throw new IllegalStateException("Invalid OAuth state");
		}

		String codeVerifier = pkceCookie.getValue();
		
		log.info("PKCE Code Verifier: {}", codeVerifier);

		// 3. Clear temporary cookies
		ResponseCookie clearState = ResponseCookie.from("OAUTH2_STATE", "")
				.path("/authenticate").maxAge(0).build();

		ResponseCookie clearPkce = ResponseCookie.from("PKCE_VERIFIER", "")
				.path("/authenticate").maxAge(0).build();

		response.addHeader(HttpHeaders.SET_COOKIE, clearState.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, clearPkce.toString());

		// 4. Exchange authorization code for tokens
		JwtDTO dto = jwtUtil.getAuthCodeTokens(code, codeVerifier);

		// 5. Store tokens in HttpOnly cookies
		ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", dto.getAccess_token())
				.httpOnly(true).secure(false) // 🔒 true in HTTPS
				.sameSite("Strict").path("/").maxAge(Duration.ofMinutes(15)).build();

		ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", dto.getRefresh_token())
				.httpOnly(true).secure(false) // 🔒 true in HTTPS
				.sameSite("Strict").path("/authenticate").maxAge(Duration.ofDays(7)).build();
//        ResponseCookie ssoInitCookie = ResponseCookie.from("SSO_INIT", "true")
//                .httpOnly(true)
//                .secure(false)      // true in prod
//                .sameSite("Lax")
//                .path("/")
//                .maxAge(Duration.ofMinutes(1)) // 🔑 very short
//                .build();
//
//        response.addHeader(HttpHeaders.SET_COOKIE, ssoInitCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

		// 6. Redirect back to React
		response.sendRedirect("http://localhost:5173/sso-page");
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOTP(@RequestBody LoginRequest request) {
		Result response = loginService.verifyOTP(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/generate-pdf")
	public ResponseEntity<?> generatePdf(@RequestBody ReqSearch search) throws Exception {
		try {
			ByteArrayOutputStream outputStream = loginService.getPdf();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("attachment", "Sample.pdf");

			InputStreamResource inputStreamResource = new InputStreamResource(
					new ByteArrayInputStream(outputStream.toByteArray()));

			outputStream.flush();// Flush the output stream

			return ResponseEntity.ok().headers(headers).body(inputStreamResource);
		} catch (Exception e) {
			log.error("Error in Get Pdf Controller....", e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/get-jwt-refresh-token")
	public ResponseEntity<?> getRefreshToken(@RequestBody JwtDTO dto) {
		JwtDTO result = jwtUtil.getRefreshToken(dto.getRefresh_token());
		return ResponseEntity.ok(result);
	}

	@PostMapping("/refresh")
	public ResponseEntity<Void> refresh(HttpServletResponse response, HttpServletRequest request) {

		String refreshToken = null;
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				log.info(cookie.getName() + " ::: " + cookie.getValue());
				if ("REFRESH_TOKEN".equals(cookie.getName())) {
					refreshToken = cookie.getValue();
					break;
				}
			}
		}

		if (refreshToken == null) {
			throw new CustomException("Refresh Token is missing", HttpStatus.BAD_REQUEST);
		}

		JwtDTO result = jwtUtil.getRefreshToken(refreshToken);

		ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", result.getAccess_token()).httpOnly(true)
				.secure(true).sameSite("None").path("/").maxAge(Duration.ofMinutes(tokenExpiry)).build();

		ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", result.getRefresh_token()).httpOnly(true)
				.secure(true).sameSite("None").path("/authenticate").maxAge(Duration.ofHours(tokenExpiry))
				.build();

		response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
		return ResponseEntity.ok().build();
	}

	/**
	 * Validate the Email Link
	 * 
	 * @param dto
	 * @return result
	 */
	@PostMapping("/validate-uuid")
	public ResponseEntity<?> validateUuid(@RequestBody ValidateLinkDTO dto) {
		Result result = loginService.validateUuid(dto);
		return ResponseEntity.ok(result);
	}

	/**
	 * Logout User by clearing cookies
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {

		ResponseCookie clearAccess = ResponseCookie.from("ACCESS_TOKEN", "").httpOnly(true).secure(true)
				.sameSite("None").path("/").maxAge(0).build();

		ResponseCookie clearRefresh = ResponseCookie.from("REFRESH_TOKEN", "").httpOnly(true).secure(true)
				.sameSite("None").path("/authenticate").maxAge(0).build();
		
		Cookie cookie = WebUtils.getCookie(request, "REFRESH_TOKEN");
		
		if (cookie == null) {
			throw new CustomException("Refresh Token is missing", HttpStatus.BAD_REQUEST);
		}
		
		log.info(">>>> Refresh Token Cookie :: {}",cookie.getValue());

		jwtUtil.revokeToken(cookie.getValue());

		response.addHeader(HttpHeaders.SET_COOKIE, clearAccess.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, clearRefresh.toString());

		return ResponseEntity.ok().build();
	}

}
