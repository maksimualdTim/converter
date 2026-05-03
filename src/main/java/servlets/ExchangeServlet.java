package servlets;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

import javax.sql.DataSource;

import config.DataSourceProvider;
import exceptions.NotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mappers.ExchangeJsonMapper;
import repository.ExchangeRatesRepository;
import services.ExchangeService;

/**
 * Servlet implementation class ExchangeServlet
 */
@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private ExchangeService exchangeService;
	
    @Override
    public void init() throws ServletException {
        DataSource dataSource = DataSourceProvider.getDataSource();
        ExchangeRatesRepository exchangeRatesRepository = new ExchangeRatesRepository(dataSource);
        this.exchangeService = new ExchangeService(exchangeRatesRepository);
    }


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fromCode = request.getParameter("from");
		String toCode = request.getParameter("to");
		String amountString = request.getParameter("amount");
		
		if (Utils.isBlank(fromCode)) {
	        JsonErrorResponse.prepareResponse(HttpServletResponse.SC_BAD_REQUEST, "The from param is required", response);
	        return;
		}
		
		if (Utils.isBlank(toCode)) {
	        JsonErrorResponse.prepareResponse(HttpServletResponse.SC_BAD_REQUEST, "The to param is required", response);
	        return;
		}
		
		if (Utils.isBlank(amountString)) {
	        JsonErrorResponse.prepareResponse(HttpServletResponse.SC_BAD_REQUEST, "The amount param is required", response);
	        return;
		}
		
		try {
			BigDecimal amount = new BigDecimal(amountString);
			response.getWriter().write(
					ExchangeJsonMapper.toJson(
							exchangeService.exchange(fromCode, toCode, amount)
					)
				);
		} catch (NumberFormatException e) {
			JsonErrorResponse.prepareResponse(HttpServletResponse.SC_BAD_REQUEST, "Incorrect amount", response);
		} catch (NotFoundException e) {
			JsonErrorResponse.prepareResponse(HttpServletResponse.SC_NOT_FOUND, e.getMessage(), response);
		} catch (SQLException e) {
			e.printStackTrace();
			JsonErrorResponse.prepareResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error", response);
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

}
