package telran.currency.controller;

import java.util.*;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.text.SimpleDateFormat;

public class CurrencyControllerAppl {
	private static final int CURRENCIES_PER_LINE = 16;
	static String url = "http://api.fixer.io/latest";
	static RestTemplate restTemplate = new RestTemplate();
	static Map<String, Float> euroRates = null;
	static Date date;

	public static void main(String[] args) {
		getRates();

		if (euroRates == null) {
			System.out.println("fixer service is unavailable");
			return;
		}

		try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
			while (true) {
				printPrompt(euroRates, date);
				String line = console.readLine();
				if (line.equals("exit"))
					break;
				if (line.equalsIgnoreCase("change date")) {
					System.out.println("input new date DD.MM.YYYY since 01.02.1999");
					String newDate = console.readLine();//dd.mm.yyyy -> YYYY-MM-DD 
					String archDate = getCorrectDate(newDate);
					getArchiveRates(archDate);
					continue;
				}
				printResult(line, euroRates);
			}
		} catch (IOException e) {
			System.out.println("console is unavailable");
		}
	}

	private static String getCorrectDate(String newDate) {//dd.mm.yyyy -> YYYY-MM-DD
		String[] dateArr = newDate.split("\\.");
		return dateArr[2] + "-" + dateArr[1] + "-" + dateArr[0];
	}

	private static void printResult(String line, Map<String, Float> euroRates) {
		String tokens[] = line.split(" ");
		if (tokens.length != 3) {
			System.out.println("Wrong input format please see the prompt for the correct one");
			return;
		}
		Float rateTo = euroRates.get(tokens[1]);
		if (rateTo == null) {
			System.out.println("Wrong currency 'To'");
			return;
		}
		Float rateFrom = euroRates.get(tokens[0]);
		if (rateFrom == null) {
			System.out.println("Wrong currency 'From'");
			return;
		}
		try {
			float amount = Float.parseFloat(tokens[2]);
			System.out.println(amount / rateFrom * rateTo);
		} catch (NumberFormatException e) {
			System.out.println("Wrong amount");
		}
	}

	private static void printPrompt(Map<String, Float> euroRates, Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		System.out.println("Exchange rates based on Euro for " + dateFormat.format(date));
		System.out.print("Currency names as follows:");
		int ind = 0;
		for (String currency : euroRates.keySet()) {
			if (ind % CURRENCIES_PER_LINE == 0) {
				System.out.println();
			}
			System.out.print(currency + ' ');
			ind++;
		}
		System.out.println("\nenter one of the following:" + "\n   - <currency from> <currency to> <amount>"
				+ "\n   - change date " + "\n   - exit");

	}

	private static void getRates() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("user-agent", "");
		HttpEntity<String> request = new HttpEntity<>(headers);
		try {
			ResponseEntity<CurrencyRates> response = restTemplate.exchange(url, HttpMethod.GET, request, CurrencyRates.class);
			CurrencyRates res = response.getBody();
			euroRates = res.getRates();
			euroRates.put("EUR", 1f);
			date = res.getDate();
		} catch (Throwable e) {
			System.out.println(e.getMessage());
			euroRates = null;
			date = null;
		}
	}
	
	private static void getArchiveRates(String archiveDate) {//archiveDate = YYYY-MM-DD
		HttpHeaders headers = new HttpHeaders();
		headers.add("user-agent", "");
		url = "http://api.fixer.io/"+archiveDate;
		HttpEntity<String> request = new HttpEntity<>(headers);
		try {
			ResponseEntity<CurrencyRates> response = restTemplate.exchange(url, HttpMethod.GET, request, CurrencyRates.class);
			CurrencyRates res = response.getBody();
			euroRates = res.getRates();
			euroRates.put("EUR", 1f);
			date = res.getDate();
		} catch (Throwable e) {
			System.out.println(e.getMessage());
			euroRates = null;
			date = null;
		}
	}
}
