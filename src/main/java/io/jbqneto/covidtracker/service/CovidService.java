package io.jbqneto.covidtracker.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.jbqneto.covidtracker.model.CovidData;

@Service
public class CovidService {
	
	private static final String DATA_CONFIRMED_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	private static final String DATA_RECOVERED_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
	private static final String DATA_DEATH_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
	
	private static List<CovidData> covidList = new ArrayList<CovidData>();
	private static LocalDateTime lastUpdated = null;
	private static String queryDate = null;
	
	public String getQueryDate() {
		return queryDate;
	}
	
	public List<CovidData> getData() {
		return covidList;
	}
	
	public String getLastUpdated() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); 
		return dtf.format(lastUpdated);
	}
	
	@PostConstruct
	@Scheduled(cron="* * 1 * * *")
	public void getVirusData() throws IOException, InterruptedException {
	    fetchConfirmedData();
	}
	
	private static String getGithubData(String url, boolean retry) throws Exception {
		HttpClient client = HttpClients.createDefault();
		try {
			HttpGet request = new HttpGet(url);	
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			
			if (entity != null)
				return EntityUtils.toString(entity);
			
		} catch (UnknownHostException u) {
			if (retry) {
				Thread.sleep((15 * 1000)); // 15 SEGUNDOS
				return getGithubData(url, false);
			}
		}
		
		return null;
	}

	private static void fetchConfirmedData() {
		List<CovidData> updatedDataList = new ArrayList<CovidData>();
		
		try {
			String result = getGithubData(DATA_CONFIRMED_URL, true);
			
			if (result != null) {
				Reader in = new StringReader(result);
				
				Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().withSkipHeaderRecord(false).parse(in);
				String[] headers = null;
				int count = 0;
				
				for (CSVRecord record : records) {
					
					if (count == 0) {
						headers = record.toMap().keySet().toArray(new String[0]);
						System.out.println(headers);
					}
					
				    String state = record.get("Province/State");
				    String country = record.get("Country/Region");
				    String total = record.get(record.size() - 1);	
				
				    CovidData covid = new CovidData();
				    covid.setState(state);
				    covid.setCountry(country);
				    covid.setLatestTotalCases(Integer.parseInt(total));
				    
				    updatedDataList.add(covid);
				    
					count++;
				}
			
				synchronized (covidList) {
					covidList = updatedDataList;
					queryDate = headers[headers.length-1];
					lastUpdated = LocalDateTime.now();
				}
			
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error GETTing data on github: " + e.getMessage());
		}
		
	}
	
}
