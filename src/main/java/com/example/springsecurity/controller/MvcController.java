package com.example.springsecurity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Naomi
 * @date 2022/1/11 17:19
 */
@Controller
public class MvcController {

    // 执行方法前校验权限
    @PreAuthorize("hasAnyRole('ADMIN') and hasAnyAuthority('root','user')")
    @RequestMapping("/toMain")
    public String toMain() {
        return "redirect:main.html";
    }

    @RequestMapping("/toError")
    public String toError() {
        return "redirect:error.html";
    }
}
