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

import javax.sql.DataSource;

import config.DataSourceProvider;
import exceptions.NotFoundException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private CurrencyService currencyService;

    @Override
    public void init() throws ServletException {
        DataSource dataSource = DataSourceProvider.getDataSource();
        CurrencyRepository repo = new CurrencyRepository(dataSource);
        this.currencyService = new CurrencyService(repo);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isBlank()) {
            JsonErrorResponse.prepareResponse(HttpServletResponse.SC_BAD_REQUEST, "Code is required", response);
            return;
        }

        String[] params = pathInfo.substring(1).split("/");
        if (params.length != 1 || params[0].isBlank()) {
            JsonErrorResponse.prepareResponse(HttpServletResponse.SC_BAD_REQUEST, "Invalid currency path", response);
            return;
        }

        String code = params[0];

        if (!code.matches("[A-Za-z]{3}")) {
            JsonErrorResponse.prepareResponse(HttpServletResponse.SC_BAD_REQUEST, "Invalid currency code", response);
            return;
        }

        try {
            Currency currency = currencyService.findByCode(code);
            response.getWriter().write(CurrencyJsonMapper.toJson(currency));
        } catch (NotFoundException e) {
        	JsonErrorResponse.prepareResponse(HttpServletResponse.SC_NOT_FOUND, "Currency not found", response);
		} catch (SQLException e) {
            e.printStackTrace();
            JsonErrorResponse.prepareResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error", response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST is not supported yet");
    }
}