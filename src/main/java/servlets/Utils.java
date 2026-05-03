package servlets;

public class Utils {
	public static String trim(String value) {
	    return value == null ? null : value.trim();
	}
	
	public static boolean isBlank(String value) {
	    return value == null || value.isBlank();
	}
}
