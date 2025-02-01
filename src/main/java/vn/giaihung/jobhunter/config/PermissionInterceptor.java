package vn.giaihung.jobhunter.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.giaihung.jobhunter.domain.Permission;
import vn.giaihung.jobhunter.domain.Role;
import vn.giaihung.jobhunter.domain.User;
import vn.giaihung.jobhunter.service.UserService;
import vn.giaihung.jobhunter.utils.SecurityUtil;
import vn.giaihung.jobhunter.utils.error.PermissionException;

public class PermissionInterceptor implements HandlerInterceptor {
    private final UserService userService;
    private final Logger log = LoggerFactory.getLogger(PermissionInterceptor.class);

    public PermissionInterceptor(UserService userService) {
        this.userService = userService;
    }

    @SuppressWarnings("null")
    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        log.info(">>> RUN preHandle");
        log.info(">>> path= " + path);
        log.info(">>> httpMethod= " + httpMethod);
        log.info(">>> requestURI= " + requestURI);

        // Check permission
        // Email -> User -> Role -> Permission
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = userService.getUserByEmail(email);
        if (currentUser != null) {
            Role userRole = currentUser.getRole();
            if (userRole != null) {
                List<Permission> permissions = userRole.getPermissions();
                boolean isAllow = permissions.stream().anyMatch(permission -> permission.getApiPath().equals(path)
                        && permission.getMethod().equals(httpMethod));
                if (isAllow == false) {
                    throw new PermissionException("You aren't authorized to access this endpoint");
                }

            } else {
                throw new PermissionException("You aren't authorized to access this endpoint");
            }
        }

        return true;
    }
}
