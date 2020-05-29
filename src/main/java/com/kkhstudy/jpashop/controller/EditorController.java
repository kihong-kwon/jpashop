package com.kkhstudy.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class EditorController {
    @GetMapping("/editor")
    public String editor() {
        return "editor/editor";
    }
}
