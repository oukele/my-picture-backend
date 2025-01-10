package com.oukele.picturebackend.aop;

import cn.hutool.core.util.ObjUtil;
import com.oukele.picturebackend.annotation.LoginCheck;
import com.oukele.picturebackend.exception.ErrorCode;
import com.oukele.picturebackend.exception.ThrowUtils;
import com.oukele.picturebackend.model.entity.User;
import com.oukele.picturebackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author oukele
 */
@Aspect
@Component
public class LoginInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint  切入点
     * @param loginCheck 登录校验注解
     */
    @Around("@annotation(loginCheck)")
    public Object loginCheck(ProceedingJoinPoint joinPoint, LoginCheck loginCheck) throws Throwable {
        boolean value = loginCheck.value();
        if (value) {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            // 当前登录用户
            User loginUser = userService.getLoginUser(request);
            ThrowUtils.throwIf(ObjUtil.isNull(loginUser), ErrorCode.NOT_LOGIN_ERROR);
        }
        return joinPoint.proceed();
    }

}
