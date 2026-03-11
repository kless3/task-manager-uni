package com.yafimchyk.labs.controller;

import com.yafimchyk.labs.service.impl.RaceConditionDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/demo/race-condition")
@RequiredArgsConstructor
public class RaceConditionController {

    private final RaceConditionDemoService demoService;

    @GetMapping("/run")
    public ResponseEntity<Map<String, String>> runRaceConditionDemo() throws InterruptedException {
        demoService.runAllDemos();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Демонстрация race condition запущена");
        response.put("status", "Проверьте логи для результатов");

        return ResponseEntity.ok(response);
    }
}