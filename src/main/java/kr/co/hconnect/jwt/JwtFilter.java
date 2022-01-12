package kr.co.hconnect.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.hconnect.common.ApiResponseCode;
import kr.co.hconnect.common.TokenStatus;
import kr.co.hconnect.domain.BaseResponse;
import kr.co.hconnect.domain.LoginSuccessInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class JwtFilter extends GenericFilterBean {

    private final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);

    private final String AUTHORIZATION_HEADER = "Authorization";

    private final TokenProvider tokenProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Set<String> passUrls = new HashSet<>();

    private final MessageSource messageSource;

    /**
     * token 유효성 검사 제외 URL 목록 Setter
     * @param passUrls token 유효성 검사 제외 URL 목록
     */
    public void setPassUrls(String... passUrls) {
        this.passUrls.clear();
        if (!ObjectUtils.isEmpty(passUrls)) {
            for (String passUrl : passUrls) {
                if (StringUtils.isEmpty(passUrl) || this.passUrls.contains(passUrl)) {
                    continue;
                }

                this.passUrls.add(passUrl);
            }
        }
    }

    /**
     * 생성자
     *
     * @param tokenProvider jwt Token 관리
     * @param messageSource MessageSource
     */
    public JwtFilter(TokenProvider tokenProvider, MessageSource messageSource) {
        this.tokenProvider = tokenProvider;
        this.messageSource = messageSource;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String requestURI = httpServletRequest.getRequestURI();

        // passUrl 확인
        if (!passUrls.contains(requestURI)) {
            String jwt = resolveToken(httpServletRequest);

            BaseResponse baseResponse = null;
            if (StringUtils.hasText(jwt)) {

                TokenStatus tokenStatus = tokenProvider.validateToken(jwt);

                if (tokenStatus == TokenStatus.EXPIRED) {
                    baseResponse = getBaseResponseByTokenInfo(ApiResponseCode.EXPIRED_TOKEN
                        //, "토큰이 만료되었습니다"
                        , messageSource.getMessage("message.jwtToken.expired", null, Locale.getDefault())
                        , true);
                } else if (tokenStatus == TokenStatus.ILLEGAL) {
                    baseResponse = getBaseResponseByTokenInfo(ApiResponseCode.ILLEGAL_TOKEN
                        // , "토큰정보가 유효하지 않습니다"
                        , messageSource.getMessage("message.jwtToken.illegal", null, Locale.getDefault())
                        , false);
                }

            } else {
                LOGGER.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);

                baseResponse = getBaseResponseByTokenInfo(ApiResponseCode.INVALID_TOKEN
                    // , "토큰정보가 존재하지 않습니다"
                    , messageSource.getMessage("message.jwtToken.invalid", null, Locale.getDefault())
                    , false);
            }

            if (baseResponse != null) {
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                httpServletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

                objectMapper.writeValue(httpServletResponse.getWriter(), baseResponse);
            }
        }

        chain.doFilter(httpServletRequest, httpServletResponse);
    }

    /**
     * Token 상태에 따른 반환정보 구성
     *
     * @param apiResponseCode API 결과코드 정보
     * @param message Message
     * @param isNewToken 신규 토큰 생성여부
     * @return Token
     */
    private LoginSuccessInfo getBaseResponseByTokenInfo(ApiResponseCode apiResponseCode, String message
        , boolean isNewToken) {
        LoginSuccessInfo token = new LoginSuccessInfo();
        token.setCode(apiResponseCode.getCode());
        token.setMessage(message);

        if (isNewToken) {
            token.setToken(tokenProvider.createToken());
        }

        return token;
    }

    /**
     * 토큰정보 추출
     *
     * @param request HttpServletRequest
     * @return 토큰정보
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
