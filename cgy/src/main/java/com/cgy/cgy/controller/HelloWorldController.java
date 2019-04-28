package com.cgy.cgy.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

//	@Value("classpath:/static/index.html")
//	private Resource indexHtml;

	// http://localhost:8181/hello
	@RequestMapping("/hello")
	public String hello() {
		return "Hello World";
	}

	// 本地访问路径：http://localhost:8181/index
	@RequestMapping(value = "/index")
	public String jsPage() {
		return "index";
	}

//	@GetMapping
//	public Object index() {
//		return ResponseEntity.ok().body(indexHtml);
//	}
}
