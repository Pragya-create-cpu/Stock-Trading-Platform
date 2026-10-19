import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final String type;
    private final String symbol;
    private final int quantity;
    private final double price;
    private final LocalDateTime dateTime;

    public Transaction(String type, String symbol, int quantity, double price) {
        this(type, symbol, quantity, price, LocalDateTime.now());
    }

    public Transaction(String type, String symbol, int quantity,
                       double price, LocalDateTime dateTime) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.dateTime = dateTime;
    }

    public String getType() {
        return type;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotal() {
        return quantity * price;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return dateTime.format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
                + " | " + type
                + " | " + symbol
                + " | Quantity: " + quantity
                + " | Price: Rs. " + String.format("%.2f", price)
                + " | Total: Rs. " + String.format("%.2f", getTotal());
    }

    public String toFileString() {
        return dateTime + "|" + type + "|" + symbol + "|"
                + quantity + "|" + price;
    }

    public static Transaction fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid transaction record.");
        }

        return new Transaction(
                parts[1],
                parts[2],
                Integer.parseInt(parts[3]),
                Double.parseDouble(parts[4]),
                LocalDateTime.parse(parts[0])
        );
    }
}