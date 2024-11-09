package grpc.server;

import com.via.pro3.animalproductservice.AnimalProductServiceGrpc;
import com.via.pro3.animalproductservice.AnimalProductServiceOuterClass.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnimalProductServiceImpl extends AnimalProductServiceGrpc.AnimalProductServiceImplBase {
    private final DataSource dataSource;

    public AnimalProductServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void getAnimalsByProduct(ProductRequest request, StreamObserver<AnimalListResponse> responseObserver) {
        int productId = request.getProductId();
        List<Animal> animals = new ArrayList<>();

        String sql = "SELECT DISTINCT a.id, a.weight, a.arrival_time, a.species " +
                "FROM animals a " +
                "JOIN animal_parts ap ON a.id = ap.animal_id " +
                "JOIN product_parts pp ON ap.part_id = pp.part_id " +
                "WHERE pp.product_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Animal animal = Animal.newBuilder()
                        .setId(rs.getInt("id"))
                        .setWeight(rs.getDouble("weight"))
                        .setArrivalTime(rs.getTimestamp("arrival_time").toInstant().toString())
                        .setSpecies(rs.getString("species"))
                        .build();
                animals.add(animal);
            }

            if (animals.isEmpty()) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("No animals found for product ID: " + productId)
                        .asRuntimeException());
                return;
            }

            AnimalListResponse response = AnimalListResponse.newBuilder()
                    .addAllAnimals(animals)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (SQLException e) {
            e.printStackTrace();
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Database error: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getProductsByAnimal(AnimalRequest request, StreamObserver<ProductListResponse> responseObserver) {
        int animalId = request.getAnimalId();
        List<Product> products = new ArrayList<>();

        String sql = "SELECT DISTINCT p.id, p.name, p.weight " +
                "FROM products p " +
                "JOIN product_parts pp ON p.id = pp.product_id " +
                "JOIN animal_parts ap ON pp.part_id = ap.part_id " +
                "WHERE ap.animal_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, animalId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = Product.newBuilder()
                        .setId(rs.getInt("id"))
                        .setName(rs.getString("name"))
                        .setWeight(rs.getDouble("weight"))
                        .build();
                products.add(product);
            }

            if (products.isEmpty()) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("No products found for animal ID: " + animalId)
                        .asRuntimeException());
                return;
            }

            ProductListResponse response = ProductListResponse.newBuilder()
                    .addAllProducts(products)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (SQLException e) {
            e.printStackTrace();
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Database error: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void recallProductsByAnimal(AnimalRequest request, StreamObserver<RecallResponse> responseObserver) {
        int animalId = request.getAnimalId();
        List<Integer> recalledProductIds = new ArrayList<>();

        String selectSql = "SELECT DISTINCT p.id " +
                "FROM products p " +
                "JOIN product_parts pp ON p.id = pp.product_id " +
                "JOIN animal_parts ap ON pp.part_id = ap.part_id " +
                "WHERE ap.animal_id = ?";
        String updateSql = "UPDATE products SET recalled = TRUE WHERE id = ?";

        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, animalId);
                ResultSet rs = selectStmt.executeQuery();

                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    while (rs.next()) {
                        int productId = rs.getInt("id");
                        recalledProductIds.add(productId);

                        updateStmt.setInt(1, productId);
                        updateStmt.executeUpdate();
                    }
                }
            }

            if (recalledProductIds.isEmpty()) {
                conn.rollback();
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("No products found for animal ID: " + animalId)
                        .asRuntimeException());
                return;
            }

            conn.commit();

            RecallResponse response = RecallResponse.newBuilder()
                    .setMessage("Products successfully recalled.")
                    .addAllRecalledProductIds(recalledProductIds)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Failed to rollback transaction: " + rollbackEx.getMessage());
                }
            }
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Database error: " + e.getMessage())
                    .asRuntimeException());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Failed to close connection: " + closeEx.getMessage());
                }
            }
        }
    }
}
