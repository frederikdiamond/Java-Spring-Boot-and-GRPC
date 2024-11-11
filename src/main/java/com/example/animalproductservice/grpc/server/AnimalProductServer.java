package com.example.animalproductservice.grpc.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.io.IOException;

@Component
public class AnimalProductServer {
    private Server server;
    private final DataSource dataSource;

    @Value("${grpc.server.port:50051}")
    private int grpcPort;

    @Autowired
    public AnimalProductServer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    private void start() throws IOException {
        server = ServerBuilder.forPort(grpcPort)
                .addService(new AnimalProductServiceImpl(dataSource))
                .build()
                .start();

        System.out.println("Server started, listening on " + grpcPort);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** Shutting down gRPC server");
            AnimalProductServer.this.stop();
            System.err.println("*** Server shut down");
        }));
    }

    @PreDestroy
    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

//    private void blockUntilShutdown() throws InterruptedException {
//        if (server != null) {
//            server.awaitTermination();
//        }
//    }

//    public static void main(String[] args) throws IOException, InterruptedException {
//        final AnimalProductServer server = new AnimalProductServer();
//        server.start();
//        server.blockUntilShutdown();
//    }
}
