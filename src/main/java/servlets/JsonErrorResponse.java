package servlets;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

public class JsonErrorResponse {
	public static void prepareResponse(int code, String message, HttpServletResponse response) throws IOException {
        String safeMessage = message
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
        
        response.setStatus(code);
        response.getWriter().write("{\"message\":\"" + safeMessage + "\"}");
	}
}
