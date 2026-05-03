package servlets;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import config.DataSourceProvider;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
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
        try {
        	String codes = validatePathAndReturnCodes(request);
			ExchangeRate exchangeRate = exchangeRatesService.findByCodes(codes.substring(0, 3), codes.substring(3));
			response.getWriter().write(ExchangeRatesJsonMapper.toJson(exchangeRate));
		} catch (NotFoundException e) {
			JsonErrorResponse.prepareResponse(HttpServletResponse.SC_NOT_FOUND, e.getMessage(), response);
		} catch (BadRequestException e) {
			JsonErrorResponse.prepareResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), response);
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
	
	@Override
	protected void doPatch(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
		
		String rateString = getBodyParam(request, "rate");
		
		if (Utils.isBlank(rateString)) {
	        JsonErrorResponse.prepareResponse(HttpServletResponse.SC_BAD_REQUEST, "The rate field is required", response);
	        return;
		}
		
	    try {
	    	BigDecimal rate = new BigDecimal(rateString);
	    	String codes = validatePathAndReturnCodes(request);
	    	ExchangeRate exchangeRate = exchangeRatesService.findByCodes(codes.substring(0, 3), codes.substring(3));
	    	response.getWriter().write(
	    			ExchangeRatesJsonMapper.toJson(
	    					exchangeRatesService.update(exchangeRate, rate)
	    					)
	    			);
		} catch (NumberFormatException e) {
			JsonErrorResponse.prepareResponse(HttpServletResponse.SC_BAD_REQUEST, "Incorrect rate", response);
		} catch (BadRequestException e) {
			JsonErrorResponse.prepareResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), response);
		} catch (NotFoundException e) {
			JsonErrorResponse.prepareResponse(HttpServletResponse.SC_NOT_FOUND, e.getMessage(), response);
		} catch (SQLException e) {
			e.printStackTrace();
			JsonErrorResponse.prepareResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error", response);
		}
	}
	
	private String validatePathAndReturnCodes(HttpServletRequest request) throws BadRequestException{
		String pathInfo = request.getPathInfo();
		
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isBlank()) {
            throw new BadRequestException("Code is required");
        }
        
        String[] params = pathInfo.substring(1).split("/");
        if (params.length != 1 || params[0].isBlank()) {
            throw new BadRequestException("Invalid currency path");
        }
        
        String codes = params[0].toUpperCase();
        if (!codes.matches("[A-Za-z]{6}")) {
            throw new BadRequestException("Invalid currency codes");
        }
        return codes;
	}
	
	private String getBodyParam(HttpServletRequest request, String paramName) throws IOException {
	    String body = request.getReader()
	            .lines()
	            .collect(Collectors.joining("&"));

	    Map<String, String> params = new HashMap();

	    for (String pair : body.split("&")) {
	        String[] keyValue = pair.split("=", 2);

	        if (keyValue.length == 2) {
	            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
	            String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);

	            params.put(key, value);
	        }
	    }

	    return params.get(paramName);
	}

}
