package servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import config.DataSourceProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mappers.ExchaneRatesJsonMapper;
import models.ExchangeRate;
import repository.ExchangeRatesRepository;
import services.ExchangeRatesService;

/**
 * Servlet implementation class ExchangeRatesServlet
 */
@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    private ExchangeRatesService exchangeRatesService;

    @Override
    public void init() throws ServletException {
        DataSource dataSource = DataSourceProvider.getDataSource();
        ExchangeRatesRepository exchangeRatesRepository = new ExchangeRatesRepository(dataSource);
        this.exchangeRatesService = new ExchangeRatesService(exchangeRatesRepository);
    }

    /**
     * Default constructor. 
     */
    public ExchangeRatesServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<ExchangeRate> exchangeRates;
		try {
			exchangeRates = exchangeRatesService.findAll();
			response.getWriter().write(ExchaneRatesJsonMapper.toJson(exchangeRates));
		} catch (SQLException e) {
			e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\":\"Internal server error\"}");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
