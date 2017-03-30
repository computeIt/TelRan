package telran.currency.controller;

import java.util.Date;
import java.util.Map;

public class CurrencyRates {
	Date date;
	Map<String, Float> rates;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Map<String, Float> getRates() {
		return rates;
	}

	public void setRates(Map<String, Float> rates) {
		this.rates = rates;
	}
}
