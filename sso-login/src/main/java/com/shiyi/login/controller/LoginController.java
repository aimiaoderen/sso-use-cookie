package com.shiyi.login.controller;

import com.shiyi.login.pojo.User;
import com.shiyi.login.utils.LoginCacheUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/login")
public class LoginController {
    private static Set<User> dbUSers;
    static {
        dbUSers = new HashSet<>();
        dbUSers.add(new User(0,"zhangsan","123"));
        dbUSers.add(new User(1,"lisi","1234"));
        dbUSers.add(new User(2,"zhangsan","12345"));
    }
    @PostMapping
    public String doLogin(User user, HttpSession session, HttpServletResponse response){
        String target = (String) session.getAttribute("target");
        //模拟从数据库中通过登录的用户名和密码去查找数据库中的用户
        Optional<User> first = dbUSers.stream().filter(dbUser -> dbUser.getUsername().equals(user.getUsername()) &&
                dbUser.getPassword().equals(user.getPassword()))
                .findFirst();
        //判断用户是否登录
        if(first.isPresent()){
          //保存用户登录信息
            String token = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("Token",token);
            cookie.setDomain("codeshop.com");
            response.addCookie(cookie);
            LoginCacheUtil.loginUser.put(token, first.get());

        }else{
            //登录失败，重定向到login，并返回错误信息
            session.setAttribute("msg","用户名或密码错误");
            return "login";
        }
        //重定向到target的地址
        return "redirect:"+target;
    }
    @GetMapping("/info")
    @ResponseBody
    public ResponseEntity<User> getUserInfo(String token){
        if (!StringUtils.isEmpty(token)){
            User user = LoginCacheUtil.loginUser.get(token);
            return ResponseEntity.ok(user);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
    @RequestMapping("/loginOut")
    public String loginOut(HttpServletResponse response, String target, @CookieValue(required = false,value = "Token")Cookie cookie){
        cookie.setMaxAge(0);
        LoginCacheUtil.loginUser.remove(cookie.getValue());
        return "redirect:"+target;
    }
}
