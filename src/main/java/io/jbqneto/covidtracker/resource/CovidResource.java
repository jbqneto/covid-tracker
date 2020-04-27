package io.jbqneto.covidtracker.resource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.jbqneto.covidtracker.model.CoronaVirus;

@Service("/api")
public class CovidResource {

	private CoronaVirus covid = new CoronaVirus();
	
	@PostConstruct
	@Scheduled(cron="30 2 * * * *")
	public void getVirusData() throws Exception {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");  
	    LocalDateTime now = LocalDateTime.now(); 
		covid.fetchVirusData();
		System.out.println("UPDATED AT: " + dtf.format(now));
	}
	
}
