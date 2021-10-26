package com.shiyi.login.controller;

import com.shiyi.login.pojo.User;
import com.shiyi.login.utils.LoginCacheUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;
import sun.rmi.runtime.Log;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

/**
 * 页面跳转逻辑
 */
@Controller
@RequestMapping("/view")
public class ViewController {
    /**
     * 跳转到登陆页面
     * @return
     */
    @GetMapping("/login")
    public String toLogin(@RequestParam(required = false,defaultValue = "") String target,
                          HttpSession session,@CookieValue(required = false,value = "Token") Cookie cookie){
        if (StringUtils.isEmpty(target)){
            target="http://www.codeshop.com:9001";
        }
        if (cookie!=null){
            //如果是已经登录的用户再次访问登录系统时，就要重定向
            String value = cookie.getValue();
            User user = LoginCacheUtil.loginUser.get(value);
            if (user!=null){
                return "redirect:"+target;
            }
        }
        //TODO：要做target地址是否合法的校验
        //重定向地址
        session.setAttribute("target",target);
        return "login";
    }
}
