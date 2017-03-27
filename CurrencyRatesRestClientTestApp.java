//archive information available not for all difference currencies
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class CurrencyRatesRestClientTestApp {
	static RestTemplate restTemplate = new RestTemplate();	 	

	public static void main(String[] args) throws IOException {
		String url = "http://api.fixer.io/latest";
		HttpHeaders headers = new HttpHeaders();
		headers.add("user-agent", " ");
		
		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEntity<Info> response = restTemplate.exchange(url, 	//url - address for request
				HttpMethod.GET, 									//method
				request, 											//request
				Info.class);
		
		String avCur = getAvailableCurrencies(response);
		System.out.println("available currencies: " + avCur);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		String dateRequest = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		while(true){
			try{
				dateRequest = "latest";
				System.out.println("if you need archive info type Y ");
				String choice = reader.readLine();
				if(choice.equalsIgnoreCase("y")){
					System.out.println("input date in format YYYY-MM-DD");
					dateRequest = reader.readLine();					
				}
				
				System.out.println("choose base currency (3 BIG letters) or type EXIT");				
				String baseCurrency = reader.readLine();
				if(baseCurrency.equalsIgnoreCase("exit")){
					System.out.println("Bye!");
					break;
				}
				if(!avCur.contains(baseCurrency))
					throw new IllegalArgumentException();
								
				System.out.println("choose quantity of base currency or type EXIT");
				String quan = reader.readLine();
				if(quan.equalsIgnoreCase("exit")){
					System.out.println("Bye!");
					break;
				}
				
				double quantity = Double.parseDouble(quan);
				
				System.out.println("choose destination currency (3 BIG letters) or type EXIT");
				String destCurrency = reader.readLine();
				if(destCurrency.equalsIgnoreCase("exit")){
					System.out.println("Bye!");
					break;
				}
				if(!avCur.contains(destCurrency))
					throw new IllegalArgumentException();
				
				url = "http://api.fixer.io/" + dateRequest + "?base=" + baseCurrency;
				response = restTemplate.exchange(url, HttpMethod.GET, request, Info.class);
				
				double result = response.getBody().getRates().get(destCurrency) * quantity;
				
				System.out.println(quantity + baseCurrency + " = " + result + destCurrency + " actual date " + dateFormat.format(response.getBody().getDate()));
			}catch(Exception e){
				System.out.println("something`s wrong. try again ");
			}
		}
		reader.close();		
	}

	private static String getAvailableCurrencies(ResponseEntity<Info> response) {
		List<String> list = new ArrayList<>();
		for(String s: response.getBody().getRates().keySet()){
			list.add(s);			
		}
		list.add("EUR");
		return list.toString();
	}
}
