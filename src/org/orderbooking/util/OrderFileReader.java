package org.orderbooking.util;

import org.orderbooking.model.Order;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
public class OrderFileReader {

    public static List<Order> loadOrders(String filePath)
            throws IOException {

        List<Order> orders = new ArrayList<>();

        List<String> lines =
                Files.readAllLines(
                        Paths.get(filePath)
                );

        for(String line : lines) {

            String[] parts =
                    line.split("\\s+");

            orders.add(
                    new Order(
                            parts[0],
                            Order.Side.valueOf(parts[1]),
                            Double.parseDouble(parts[2]),
                            Integer.parseInt(parts[3])
                    )
            );
        }

        return orders;
    }
}