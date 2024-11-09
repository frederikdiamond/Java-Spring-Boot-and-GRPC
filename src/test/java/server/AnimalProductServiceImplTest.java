package server;

import com.via.pro3.animalproductservice.AnimalProductServiceGrpc;
import com.via.pro3.animalproductservice.AnimalProductServiceOuterClass;
import grpc.server.AnimalProductServiceImpl;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;

import static org.junit.Assert.*;

public class AnimalProductServiceImplTest {
    private static Server server;
    private static ManagedChannel channel;
    private static BasicDataSource dataSource;

    private static void setUpTestDatabase() throws SQLException, IOException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            String sqlScript = getSqlScript();
            String[] statements = sqlScript.split(";");
            for (String statement : statements) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement);
                }
            }
        }
    }

    private static String getSqlScript() throws IOException {
        try (InputStream inputStream = AnimalProductServiceImplTest.class.getResourceAsStream("/test_schema.sql")) {
            if (inputStream == null) {
                throw new IOException("test_schema.sql not found in resources");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setDriverClassName("org.h2.Driver");

        setUpTestDatabase();

        String serverName = InProcessServerBuilder.generateName();
        server = InProcessServerBuilder.forName(serverName).directExecutor()
                .addService(new AnimalProductServiceImpl(dataSource))
                .build()
                .start();

        channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        channel.shutdownNow();
        server.shutdownNow();
    }

    @Test
    public void testGetAnimalsByProduct_Success() {
        AnimalProductServiceGrpc.AnimalProductServiceBlockingStub stub =
                AnimalProductServiceGrpc.newBlockingStub(channel);
        AnimalProductServiceOuterClass.ProductRequest request = AnimalProductServiceOuterClass.ProductRequest.newBuilder().setProductId(1).build();

        AnimalProductServiceOuterClass.AnimalListResponse response = stub.getAnimalsByProduct(request);

        assertNotNull("Response should not be null", response);
        assertFalse("Animals list should not be empty", response.getAnimalsList().isEmpty());

        AnimalProductServiceOuterClass.Animal animal = response.getAnimalsList().get(0);
        assertEquals("Animal ID should be 1", 1, animal.getId());
        assertEquals("Animal species should match", "Cow", animal.getSpecies());
        assertEquals("Animal weight should match", 1201, animal.getWeight(), 0.001);
    }

    @Test
    public void testGetAnimalsByProduct_InvalidProductId() {
        AnimalProductServiceGrpc.AnimalProductServiceBlockingStub stub =
                AnimalProductServiceGrpc.newBlockingStub(channel);
        AnimalProductServiceOuterClass.ProductRequest request = AnimalProductServiceOuterClass.ProductRequest.newBuilder().setProductId(9999).build();

        try {
            stub.getAnimalsByProduct(request);
            fail("Expected a NOT_FOUND exception to be thrown");
        } catch (StatusRuntimeException e) {
            assertEquals("Status code should be NOT_FOUND", Status.NOT_FOUND.getCode(), e.getStatus().getCode());
            assertTrue("Status description should mention no animals found",
                    e.getStatus().getDescription().contains("No animals found for product ID"));
        }
    }

    @Test
    public void testGetProductsByAnimal_Success() {
        AnimalProductServiceGrpc.AnimalProductServiceBlockingStub stub =
                AnimalProductServiceGrpc.newBlockingStub(channel);
        AnimalProductServiceOuterClass.AnimalRequest request = AnimalProductServiceOuterClass.AnimalRequest.newBuilder().setAnimalId(1).build();

        AnimalProductServiceOuterClass.ProductListResponse response = stub.getProductsByAnimal(request);

        assertNotNull("Response should not be null", response);
        assertFalse("Products list should not be empty", response.getProductsList().isEmpty());

        AnimalProductServiceOuterClass.Product product = response.getProductsList().get(0);
        assertEquals("Product ID should be 1", 1, product.getId());
        assertEquals("Product name should match", "Premium Beef Pack", product.getName());
    }

    @Test
    public void testGetProductsByAnimal_InvalidAnimalId() {
        AnimalProductServiceGrpc.AnimalProductServiceBlockingStub stub =
                AnimalProductServiceGrpc.newBlockingStub(channel);
        AnimalProductServiceOuterClass.AnimalRequest request = AnimalProductServiceOuterClass.AnimalRequest.newBuilder().setAnimalId(9999).build();

        try {
            stub.getProductsByAnimal(request);
            fail("Expected a NOT_FOUND exception to be thrown");
        } catch (StatusRuntimeException e) {
            assertEquals("Status code should be NOT_FOUND", Status.NOT_FOUND.getCode(), e.getStatus().getCode());
            assertTrue("Status description should mention no products found",
                    e.getStatus().getDescription().contains("No products found for animal ID"));
        }
    }

    @Test
    public void testRecallProductsByAnimal_Success() {
        AnimalProductServiceGrpc.AnimalProductServiceBlockingStub stub =
                AnimalProductServiceGrpc.newBlockingStub(channel);
        AnimalProductServiceOuterClass.AnimalRequest request = AnimalProductServiceOuterClass.AnimalRequest.newBuilder().setAnimalId(1).build();

        AnimalProductServiceOuterClass.RecallResponse response = stub.recallProductsByAnimal(request);

        assertNotNull("Response should not be null", response);
        assertEquals("Message should indicate successful recall", "Products successfully recalled.", response.getMessage());
        assertFalse("Recalled products list should not be empty", response.getRecalledProductIdsList().isEmpty());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT recalled FROM products WHERE id = ?")) {
            for (int productId : response.getRecalledProductIdsList()) {
                stmt.setInt(1, productId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        assertTrue("Product should be recalled", rs.getBoolean("recalled"));
                    } else {
                        fail("Product ID " + productId + " not found in database");
                    }
                }
            }
        } catch (SQLException e) {
            fail("Database verification failed: " + e.getMessage());
        }
    }

    @Test
    public void testRecallProductsByAnimal_InvalidAnimalId() {
        AnimalProductServiceGrpc.AnimalProductServiceBlockingStub stub =
                AnimalProductServiceGrpc.newBlockingStub(channel);
        AnimalProductServiceOuterClass.AnimalRequest request = AnimalProductServiceOuterClass.AnimalRequest.newBuilder().setAnimalId(9999).build();

        try {
            stub.recallProductsByAnimal(request);
            fail("Expected a NOT_FOUND exception to be thrown");
        } catch (StatusRuntimeException e) {
            assertEquals("Status code should be NOT_FOUND", Status.NOT_FOUND.getCode(), e.getStatus().getCode());
            assertTrue("Status description should mention no products found",
                    e.getStatus().getDescription().contains("No products found for animal ID"));
        }
    }
}
