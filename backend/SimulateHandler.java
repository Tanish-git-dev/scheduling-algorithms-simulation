import com.google.gson.Gson;
import com.sun.net.httpserver.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This receive a json body telling which algorithm to run and what
 * processes to schedule. Also runs the simulation and responds with a
 * Gantt chart in json + stat.
 */
public class SimulateHandler implements HttpHandler {

    private static final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin",  "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendError(exchange, 405, "Only POST allowed");
            return;
        }

        String body;
        try (InputStream is = exchange.getRequestBody()) {
            body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        SimulateRequest req;
        try {
            req = gson.fromJson(body, SimulateRequest.class);
            if (req == null) throw new IllegalArgumentException("Request body was empty");
        } catch (Exception e) {
            sendError(exchange, 400, "Invalid JSON: " + e.getMessage());
            return;
        }

        String algorithm = (req.algorithm != null ? req.algorithm : "FCFS").toUpperCase();
        int    quantum   = req.quantum; // already an int, default 2 from SimulateRequest

        if (req.processes == null || req.processes.isEmpty()) {
            sendError(exchange, 400, "No processes provided");
            return;
        }

        List<Process> processes;
        try {
            processes = req.processes.stream()
                .map(dto -> new Process(dto.pid, dto.arrival, dto.burst, dto.priority))
                .collect(Collectors.toList());
        } catch (Exception e) {
            sendError(exchange, 400, "Could not parse processes: " + e.getMessage());
            return;
        }

        if (processes.isEmpty()) {
            sendError(exchange, 400, "No processes parsed");
            return;
        }

        GanttResult result;
        try {
            switch (algorithm) {
                case "FCFS":        result = FCFS.schedule(processes);                   break;
                case "SJF":         result = SJF.schedule(processes);                    break;
                case "SRTF":        result = SRTF.schedule(processes);                   break;
                case "RR":          result = RoundRobin.schedule(processes, quantum);    break;
                case "PRIORITY_NP": result = PriorityNonPreemptive.schedule(processes); break;
                case "PRIORITY_P":  result = PriorityPreemptive.schedule(processes);    break;
                default:
                    sendError(exchange, 400, "Unknown algorithm: " + algorithm);
                    return;
            }
        } catch (Exception e) {
            sendError(exchange, 500, "Simulation error: " + e.getMessage());
            return;
        }

        String json  = result.toJson();
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }

        System.out.println("[" + algorithm + "] " + processes.size() + " processes → responded");
    }

    private void sendError(HttpExchange ex, int code, String msg) throws IOException {
        String err   = gson.toJson(Map.of("error", msg));
        byte[] bytes = err.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }
}
