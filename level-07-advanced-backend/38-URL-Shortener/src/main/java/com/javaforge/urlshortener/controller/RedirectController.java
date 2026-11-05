package com.javaforge.urlshortener.controller;

import com.javaforge.urlshortener.service.UrlShortenerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class RedirectController {

    private final UrlShortenerService service;

    public RedirectController(UrlShortenerService service) {
        this.service = service;
    }

    @GetMapping("/{code:[0-9a-zA-Z]{8,16}}")
    public RedirectView redirect(@PathVariable String code) {
        return new RedirectView(service.resolveAndRecordClick(code));
    }
}
