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

        String code = params[0];

        if (!code.matches("[A-Za-z]{3}")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Invalid currency code\"}");
            return;
        }

        try {
            Currency currency = currencyService.findByCode(code);

            if (currency == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"message\":\"Currency not found\"}");
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(CurrencyJsonMapper.toJson(currency));
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\":\"Internal server error\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST is not supported yet");
    }
}