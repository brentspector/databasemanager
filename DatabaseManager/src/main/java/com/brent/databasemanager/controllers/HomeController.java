package com.brent.databasemanager.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@RequestMapping("/")
	public String homeGreeting( ) {
		return "Hey there!";
	}
}
