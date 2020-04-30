package io.jbqneto.covidtracker.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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
import io.jbqneto.covidtracker.model.TimeSeries;

@Service
public class CovidService {
	
	private static final String DATA_CONFIRMED_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	private static final String DATA_RECOVERED_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
	private static final String DATA_DEATH_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
	
	private static final String CSV_HEADERS_COUNTRY = "Country/Region";
	private static final String CSV_HEADERS_STATE = "Province/State";
	
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
		HashMap<String, CovidData> mapCountryData = new HashMap<String, CovidData>();
		
		try {			
			List<TimeSeries> confirmedList = getGithubData(DATA_CONFIRMED_URL, true);
			List<TimeSeries> deathList = getGithubData(DATA_DEATH_URL, true);
			List<TimeSeries> recoverList = getGithubData(DATA_RECOVERED_URL, true);
			
			if (confirmedList == null || deathList == null || recoverList == null)
				return;
			
			String[] dates = null;
			String lastDate = null;
			
			for (TimeSeries ts: confirmedList) {
				String country = ts.getCountry();
				CovidData data = null;
				
				if (mapCountryData.containsKey(country)) {
					data = mapCountryData.get(country);
				} else {
					data = new CovidData();
					data.setCountry(country);
				}

				if (dates == null) {
					dates = ts.getDates();
					lastDate = dates[dates.length - 1];
				}
				
				int[] values = ts.getValues();
				
				data.setDate(lastDate);
				data.setTotalConfirmed(data.getTotalConfirmed() + values[values.length -1]);
			
				mapCountryData.put(country, data);
			}
			
			for (TimeSeries ts: deathList) {
				String country = ts.getCountry();
				CovidData data = null;
				
				if (mapCountryData.containsKey(country)) {
					data = mapCountryData.get(country);
				} else {
					data = new CovidData();
					data.setCountry(country);
				}
				
				int[] values = ts.getValues();
				
				data.setDate(lastDate);
				data.setTotalDeaths(data.getTotalConfirmed() + values[values.length -1]);
				mapCountryData.put(country, data);
			}
			
			for (TimeSeries ts: recoverList) {
				String country = ts.getCountry();
				CovidData data = null;
				
				if (mapCountryData.containsKey(country)) {
					data = mapCountryData.get(country);
				} else {
					data = new CovidData();
					data.setCountry(country);
				}
				
				int[] values = ts.getValues();
				
				data.setDate(lastDate);
				data.setTotalRecovered(data.getTotalConfirmed() + values[values.length -1]);
				
				mapCountryData.put(country, data);
			}
			
			SortedSet<String> keys = new TreeSet<String>(mapCountryData.keySet());
			List<CovidData> covidDataList = new ArrayList<CovidData>();
			
			for (String country: keys) {
				covidDataList.add(mapCountryData.get(country));
			}
			
			synchronized (covidList) {
				queryDate = lastDate;
				lastUpdated = LocalDateTime.now();			
				covidList = covidDataList;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	private static List<TimeSeries> getGithubData(String url, boolean retry) throws Exception {
		HttpClient client = HttpClients.createDefault();
		
		try {
			HttpGet request = new HttpGet(url);	
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			
			if (entity == null)
				return null; 

			List<TimeSeries> updatedDataList = new ArrayList<TimeSeries>();
			Reader in = new StringReader(EntityUtils.toString(entity));
			
			String[] headers = null;
			String[] dates = null;
			String[] fixedHeaders = new String[] {CSV_HEADERS_STATE, CSV_HEADERS_COUNTRY, "Lat", "Long"};
			int count = 0;
			int totalHeaders = 0;
			int FixedHeadersSize = fixedHeaders.length;
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().withSkipHeaderRecord(false).parse(in);

			SimpleDateFormat format = new SimpleDateFormat("M/dd/yy");
			
			for (CSVRecord record : records) {
				TimeSeries ts = new TimeSeries();
				
				if (count == 0) {
					headers = record.toMap().keySet().toArray(new String[0]);
					totalHeaders = headers.length;
					dates = new String[(totalHeaders - FixedHeadersSize)];
					
					for (int i = FixedHeadersSize; i < totalHeaders; i++) {
						Date date = format.parse(headers[i]);
						dates[i - FixedHeadersSize] = new SimpleDateFormat("dd/MM/yyyy").format(date);
					}
				}
				
			    String state = record.get(CSV_HEADERS_STATE);
			    String country = record.get(CSV_HEADERS_COUNTRY);
			    
			    int[] values = new int[totalHeaders - FixedHeadersSize];
			    
			    for (int i = FixedHeadersSize; i < totalHeaders; i++) {
			    	String total = record.get(i);
			    	if (total.trim() != "") {
			    		values[i - FixedHeadersSize] = Integer.parseInt(total);
			    	} else {
			    		values[i - FixedHeadersSize] = 0;
			    	}
			    }
			    
			    ts.setState(state);
			    ts.setCountry(country);
			    ts.setService(TimeSeries.SERVICE_CONFIRMED_GLOBAL);
			    ts.setDates(dates);
			    ts.setValues(values);
			    
			    updatedDataList.add(ts);
				count++;
			}
			
			return updatedDataList;
			
		} catch (UnknownHostException u) {
			if (retry) {
				Thread.sleep((15 * 1000)); // 15 SEGUNDOS
				return getGithubData(url, false);
			}
		}
		
		return null;
	}
	
}
