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
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write("{\"message\":\"Internal server error\"}");
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
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST is not supported yet");
	}

}
