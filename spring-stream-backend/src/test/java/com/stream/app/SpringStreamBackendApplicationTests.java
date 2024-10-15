package com.stream.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.stream.app.services.VideoService;

@SpringBootTest
class SpringStreamBackendApplicationTests {

	@Autowired
	VideoService service;
	@Test
	void contextLoads() {
	}

}
