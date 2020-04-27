package io.jbqneto.covidtracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.jbqneto.covidtracker.service.CovidService;

@Controller
public class HomeController {
	
	@Autowired
	CovidService covidService;
	
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("covidData", covidService.getData());
		return "home";
	}

}
