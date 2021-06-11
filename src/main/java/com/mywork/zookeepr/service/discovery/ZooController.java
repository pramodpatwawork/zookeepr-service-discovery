package com.mywork.zookeepr.service.discovery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZooController {

	@Autowired
	ServiceRegistry registry;
	
	@GetMapping(value = "/hello")
	public String hello() {
		registry.registerService("/test", "http://test.com");
		return "Welcome to Websparrow";
	}
}
