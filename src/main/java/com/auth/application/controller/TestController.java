package com.auth.application.controller;

import com.auth.application.model.TestModel;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Hello Word";
    }

    @GetMapping("/testGit")
    public String testGit() {
        return "Hello Git";
    }


    @PostMapping("/demo")
    public String demo(@RequestParam("name") String name) {
        TestModel testModel = new TestModel(name, "18");
        testModel.setName(name);
        return "Hello Word " + testModel.getName();
    }
}
