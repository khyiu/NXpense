package nxpense.security;

import nxpense.helper.SecurityPrincipalHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Kro on 23/09/2015.
 */
public class AttachmentAccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private SecurityPrincipalHelper securityPrincipalHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // todo restrict access to attachment to its actual owner
        return super.preHandle(request, response, handler);
    }
}
