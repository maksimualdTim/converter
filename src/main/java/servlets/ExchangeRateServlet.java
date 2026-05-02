package servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import config.DataSourceProvider;
import exceptions.ExchangeRateNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mappers.ExchangeRatesJsonMapper;
import models.ExchangeRate;
import repository.ExchangeRatesRepository;
import services.ExchangeRatesService;

/**
 * Servlet implementation class ExchangeRateServlet
 */
@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
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
    public ExchangeRateServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Code is required\"}");
            return;
        }
        
        String[] params = pathInfo.substring(1).split("/");
        if (params.length != 1 || params[0].isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Invalid currency path\"}");
            return;
        }
        
        String codes = params[0].toUpperCase();
        if (!codes.matches("[A-Za-z]{6}")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Invalid currency codes\"}");
            return;
        }
		
        try {
			ExchangeRate exchangeRate = exchangeRatesService.findByCodes(codes.substring(0, 3), codes.substring(3));
			response.getWriter().write(ExchangeRatesJsonMapper.toJson(exchangeRate));
		} catch (ExchangeRateNotFoundException e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write("{\"message\": \"" + e.getMessage() + "\"}");
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
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

}
