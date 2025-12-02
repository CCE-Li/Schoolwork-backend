package cn.uptra.schoolwork.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);
        
        if (StringUtils.hasText(jwt)) {
            try {
                // 先提取用户名
                String username = tokenProvider.extractUsername(jwt);
                logger.debug("Extracted username from token: " + username);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    try {
                        // 加载用户详情
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        logger.debug("Loaded user details for: " + username);
                        
                        // 验证token
                        if (userDetails != null) {
                            logger.debug("Validating token for user: " + username);
                            if (tokenProvider.validateToken(jwt, userDetails)) {
                                // 创建认证对象
                                UsernamePasswordAuthenticationToken authentication = 
                                    new UsernamePasswordAuthenticationToken(
                                        userDetails, 
                                        null, 
                                        userDetails.getAuthorities()
                                    );
                                
                                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                                logger.debug("Authenticated user: " + username);
                            } else {
                                logger.warn("Invalid JWT token for user: " + username);
                            }
                        } else {
                            logger.warn("User details not found for: " + username);
                        }
                    } catch (UsernameNotFoundException ex) {
                        logger.error("User not found: " + username, ex);
                    } catch (Exception ex) {
                        logger.error("Error loading user details for: " + username, ex);
                    }
                } else if (username == null) {
                    logger.warn("Username is null in JWT token");
                }
            } catch (Exception ex) {
                logger.error("Error processing JWT token: " + ex.getMessage(), ex);
            }
        } else {
            logger.debug("No JWT token found in request");
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // 去除 "Bearer " 前缀并去除首尾空格
            String token = bearerToken.substring(7).trim();
            // 确保 token 不为空且不包含空格
            if (StringUtils.hasText(token) && !token.contains(" ")) {
                return token;
            }
        }
        return null;
    }
}
