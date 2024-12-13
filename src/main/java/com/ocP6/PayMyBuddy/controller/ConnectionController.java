package com.ocP6.PayMyBuddy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ConnectionController {



    @GetMapping("/connection")
    public String connection(Principal principal, Model model) {

        return "connection";
    }

}
