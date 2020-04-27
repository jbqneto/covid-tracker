package io.jbqneto.covidtracker.model;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class CoronaVirus {

	private static final String DATA_CONFIRMED_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	private static final String DATA_RECOVERED_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
	private static final String DATA_DEATH_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
	
	private static List<CovidData> covidList = new ArrayList<CovidData>();
	
	public void fetchVirusData() throws Exception {
	
		HttpClient client = HttpClients.createDefault();
		HttpGet request = new HttpGet(DATA_CONFIRMED_URL);
		
		try {
			List<CovidData> updatedDataList = new ArrayList<CovidData>();
			
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			
			if (entity != null) {
				String result = EntityUtils.toString(entity);
				Reader in = new StringReader(result);
				
				Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
				
				for (CSVRecord record : records) {
				    String state = record.get("Province/State");
				    String country = record.get("Country/Region");
				    String total = record.get(record.size() - 1);				    
				    
				    CovidData covid = new CovidData();
				    covid.setState(state);
				    covid.setCountry(country);
				    covid.setLatestTotalCases(Integer.parseInt(total));
				    
				    updatedDataList.add(covid);
				}
			
				synchronized (covidList) {
					covidList = updatedDataList;
					System.out.println(covidList.toString());
				}
			
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception("Error GETTing data on github: " + e.getMessage());
		}
		
		
	}
	
}
