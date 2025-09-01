package com.example.iasminasteriskari.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/call")
class CallController {

    @GetMapping("/{phoneNumber}")
    fun makeCall(@PathVariable phoneNumber: String): String {
        return "Calling $phoneNumber..."
    }
}