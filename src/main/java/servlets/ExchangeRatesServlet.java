package servlets;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import config.DataSourceProvider;
import exceptions.AlreadyExistsException;
import exceptions.NotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mappers.ExchangeRatesJsonMapper;
import models.Currency;
import models.ExchangeRate;
import repository.CurrencyRepository;
import repository.ExchangeRatesRepository;
import services.CurrencyService;
import services.ExchangeRatesService;

/**
 * Servlet implementation class ExchangeRatesServlet
 */
@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    private ExchangeRatesService exchangeRatesService;
    private CurrencyService currencyService;

    @Override
    public void init() throws ServletException {
        DataSource dataSource = DataSourceProvider.getDataSource();
        ExchangeRatesRepository exchangeRatesRepository = new ExchangeRatesRepository(dataSource);
        CurrencyRepository currencyRepository = new CurrencyRepository(dataSource);
        this.exchangeRatesService = new ExchangeRatesService(exchangeRatesRepository);
        this.currencyService = new CurrencyService(currencyRepository);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<ExchangeRate> exchangeRates;
		try {
			exchangeRates = exchangeRatesService.findAll();
			response.getWriter().write(ExchangeRatesJsonMapper.toJson(exchangeRates));
		} catch (SQLException e) {
			e.printStackTrace();
			JsonErrorResponse.prepareResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error", response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String baseCurrencyCode = Utils.trim(request.getParameter("baseCurrencyCode"));
	    String targetCurrencyCode = Utils.trim(request.getParameter("targetCurrencyCode"));
	    String rateString =  Utils.trim(request.getParameter("rate"));
	    
		if (Utils.isBlank(baseCurrencyCode)) {
	        JsonErrorResponse.prepareResponse(HttpServletResponse.SC_BAD_REQUEST, "The baseCurrencyCode field is required", response);
	        return;
		}
		
		if (Utils.isBlank(targetCurrencyCode)) {
	        JsonErrorResponse.prepareResponse(HttpServletResponse.SC_BAD_REQUEST, "The targetCurrencyCode field is required", response);
	        return;
		}
		
		if (Utils.isBlank(rateString)) {
	        JsonErrorResponse.prepareResponse(HttpServletResponse.SC_BAD_REQUEST, "The rateString field is required", response);
	        return;
		}
	    
	    ExchangeRate exchangeRate = new ExchangeRate();
	    
	    try {
	    	BigDecimal rate = new BigDecimal(rateString);
	    	
	    	Currency targetCurrency = currencyService.findByCode(targetCurrencyCode);
	    	Currency baseCurrency = currencyService.findByCode(baseCurrencyCode);
	    	
	    	exchangeRate.setBaseCurrency(baseCurrency);
	    	exchangeRate.setTargetCurrency(targetCurrency);
	    	exchangeRate.setRate(rate);
	    	
	    	exchangeRate = exchangeRatesService.create(exchangeRate);
	    	response.setStatus(HttpServletResponse.SC_CREATED);
	    	response.getWriter().write(ExchangeRatesJsonMapper.toJson(exchangeRate));
		} catch (NumberFormatException e) {
			JsonErrorResponse.prepareResponse(HttpServletResponse.SC_BAD_REQUEST, "Incorrect rate", response);
		} catch (NotFoundException e) {
			JsonErrorResponse.prepareResponse(HttpServletResponse.SC_NOT_FOUND, e.getMessage(), response);
		} catch (AlreadyExistsException e) {
			JsonErrorResponse.prepareResponse(HttpServletResponse.SC_CONFLICT, e.getMessage(), response);
		} catch (SQLException e) {
			JsonErrorResponse.prepareResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error", response);
			e.printStackTrace();
		}
	}

}
