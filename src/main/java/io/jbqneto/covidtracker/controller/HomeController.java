package io.jbqneto.covidtracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.jbqneto.covidtracker.model.CovidData;
import io.jbqneto.covidtracker.service.CovidService;

@Controller
public class HomeController {
	
	@Autowired
	CovidService covidService;
	
	@GetMapping("/")
	public String home(Model model) {
		List<CovidData> covidCases = covidService.getData();
		
		try {
			if (covidCases.size() == 0) {
				covidService.getVirusData();
				covidCases = covidService.getData();
			}
			
			int totalConfirmed = covidCases.stream().mapToInt(covid -> covid.getTotalConfirmed()).sum();
			int totalDeaths = covidCases.stream().mapToInt(covid -> covid.getTotalDeaths()).sum();
			int totalRecovered = covidCases.stream().mapToInt(covid -> covid.getTotalRecovered()).sum();
			
			model.addAttribute("cases", covidCases);
			model.addAttribute("totalConfirmed", totalConfirmed);
			model.addAttribute("totalDeaths", totalDeaths);
			model.addAttribute("totalRecovered", totalRecovered);
			model.addAttribute("updated", covidService.getLastUpdated());
			model.addAttribute("queryDate", covidService.getQueryDate());
			
		} catch (Exception e) {
			System.out.println("Error getting Virus Data: " + e.getMessage());
		}
		
		return "home";
	}

}
