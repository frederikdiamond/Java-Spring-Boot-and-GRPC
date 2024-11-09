package grpc.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.stereotype.Component;
import util.DatabaseUtil;

import javax.sql.DataSource;
import java.io.IOException;

@Component
public class AnimalProductServer {
    private Server server;

    private void start() throws IOException {
        int port = 50051;
        DataSource dataSource = DatabaseUtil.getDataSource();

        server = ServerBuilder.forPort(port)
                .addService(new AnimalProductServiceImpl(dataSource))
                .build()
                .start();

        System.out.println("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** Shutting down gRPC server");
            AnimalProductServer.this.stop();
            System.err.println("*** Server shut down");
        }));
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final AnimalProductServer server = new AnimalProductServer();
        server.start();
        server.blockUntilShutdown();
    }
}
