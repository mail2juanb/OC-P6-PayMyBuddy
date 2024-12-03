package com.ocP6.PayMyBuddy.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class TransfertController {

    // TODO: Renommer en transfert et non transfertPage

    @GetMapping("/transfert")
    public String transfert() {

        log.error("transfert");
        return null;

    }

}
