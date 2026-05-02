package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mappers.CurrencyJsonMapper;
import models.Currency;
import repository.CurrencyRepository;
import services.CurrencyService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import config.DataSourceProvider;
import exceptions.CurrencyAlreadyExistsException;

/**
 * Servlet implementation class CurrenciesServlet
 */
@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private CurrencyService currencyService;
	
	@Override
	public void init() throws ServletException {
		DataSource dataSource = DataSourceProvider.getDataSource();
		CurrencyRepository repo = new CurrencyRepository(dataSource);
	    this.currencyService = new CurrencyService(repo);
	}


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json;charset=UTF-8");
		
		List<Currency> currencies;
		try {
			currencies = currencyService.findAll();
		} catch (SQLException e) {
			e.printStackTrace();
			JsonErrorResponse.prepareResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error", response);
			return;
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(CurrencyJsonMapper.toJson(currencies));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	    
	    String name = trim(request.getParameter("name"));
	    String code = trim(request.getParameter("code"));
	    String sign = trim(request.getParameter("sign"));
		
	    if (isBlank(name)) {
	        JsonErrorResponse.prepareResponse(HttpServletResponse.SC_BAD_REQUEST, "The name field is required", response);
	        return;
	    }

	    if (isBlank(code)) {
	        JsonErrorResponse.prepareResponse(HttpServletResponse.SC_BAD_REQUEST, "The code field is required", response);
	        return;
	    }

	    if (isBlank(sign)) {
	        JsonErrorResponse.prepareResponse(HttpServletResponse.SC_BAD_REQUEST, "The sign field is required", response);
	        return;
	    }
		
		Currency currency = new Currency();
		currency.setCode(code);
		currency.setFullName(name);
		currency.setSign(sign);
		
		Currency createdCurrency;
		
		try {
			createdCurrency = currencyService.create(currency);
		} catch (CurrencyAlreadyExistsException e) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			response.getWriter().write("{\"message\":\"" + e.getMessage() + "\"}");
			return;
		}
		catch (SQLException e) {
			e.printStackTrace();
			JsonErrorResponse.prepareResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error", response);
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.getWriter().write(CurrencyJsonMapper.toJson(createdCurrency));
	}
	
	private String trim(String value) {
	    return value == null ? null : value.trim();
	}

	private boolean isBlank(String value) {
	    return value == null || value.isBlank();
	}

}
