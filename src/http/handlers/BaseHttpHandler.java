package http.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected HttpExchange h;

    protected void sendText(String msg, int statusCode) throws IOException {
        byte[] response = msg.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(statusCode, response.length);
        h.getResponseBody().write(response);
        h.close();
    }

    protected void sendNotFound() throws IOException {
        byte[] response = "Not Found".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "text/plain");
        h.sendResponseHeaders(404, response.length);
        h.getResponseBody().write(response);
        h.close();
    }

    protected void sendIntersection() throws IOException {
        byte[] response = "Not Acceptable".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "text/plain");
        h.sendResponseHeaders(406, response.length);
        h.getResponseBody().write(response);
        h.close();
    }
}
