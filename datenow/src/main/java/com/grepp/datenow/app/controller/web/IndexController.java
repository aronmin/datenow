package com.grepp.datenow.app.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    // 메인 페이지
    @GetMapping("/main")
    public String main() {
        return "main_page";
    }

    @GetMapping("/")
    public String main2() {
        return "main_page";
    }

}
