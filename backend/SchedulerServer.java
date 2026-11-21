import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class SchedulerServer {

    public static void main(String[] args) throws Exception {

        int port = 8080;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/simulate", new SimulateHandler());

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.println("Server running currently");
        System.out.println("Press Ctrl+C to stop");

    }
}
