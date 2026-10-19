import java.io.*;
import java.nio.file.*;
import java.util.*;

public class User {
    private final String username;
    private final Portfolio portfolio;
    private final List<Transaction> transactions = new ArrayList<>();

    public User(String username, double initialBalance) {
        this.username = username;
        this.portfolio = new Portfolio(initialBalance);
    }

    public String getUsername() {
        return username;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public boolean buyStock(Stock stock, int quantity) {
        if (portfolio.buyStock(stock, quantity)) {
            transactions.add(new Transaction(
                    "BUY", stock.getSymbol(), quantity, stock.getPrice()));
            return true;
        }
        return false;
    }

    public boolean sellStock(Stock stock, int quantity) {
        if (portfolio.sellStock(stock, quantity)) {
            transactions.add(new Transaction(
                    "SELL", stock.getSymbol(), quantity, stock.getPrice()));
            return true;
        }
        return false;
    }

    private Path getSavePath() throws IOException {
        Path directory = Paths.get("data");
        Files.createDirectories(directory);

        String safeName = username.replaceAll("[^a-zA-Z0-9_-]", "_");
        return directory.resolve(safeName + ".txt");
    }

    public void saveData(Map<String, Stock> market) throws IOException {
        Path file = getSavePath();

        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(file))) {

            out.println(portfolio.getCashBalance());

            for (Map.Entry<String, Integer> entry
                    : portfolio.getHoldings().entrySet()) {
                out.println("HOLDING|" + entry.getKey()
                        + "|" + entry.getValue());
            }

            for (Transaction transaction : transactions) {
                out.println("TRANSACTION|" + transaction.toFileString());
            }
        }
    }

    public boolean loadData(Map<String, Stock> market) throws IOException {
        Path directory = Paths.get("data");
        String safeName = username.replaceAll("[^a-zA-Z0-9_-]", "_");
        Path file = directory.resolve(safeName + ".txt");

        if (!Files.exists(file)) {
            return false;
        }

        List<String> lines = Files.readAllLines(file);

        if (lines.isEmpty()) {
            return false;
        }

        // Parse first, before modifying the account.
        double balance = Double.parseDouble(lines.get(0));
        if (!Double.isFinite(balance) || balance < 0) {
            throw new IOException("Invalid saved cash balance.");
        }

        Map<String, Integer> savedHoldings = new LinkedHashMap<>();
        List<Transaction> savedTransactions = new ArrayList<>();

        try {
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.startsWith("HOLDING|")) {
                    String[] parts = line.split("\\|", -1);
                    if (parts.length != 3) {
                        throw new IOException("Invalid holding record.");
                    }

                    String symbol = parts[1].toUpperCase();
                    int quantity = Integer.parseInt(parts[2]);

                    if (!market.containsKey(symbol) || quantity <= 0) {
                        throw new IOException("Invalid saved holding: " + symbol);
                    }

                    savedHoldings.put(symbol, quantity);

                } else if (line.startsWith("TRANSACTION|")) {
                    String record = line.substring("TRANSACTION|".length());
                    savedTransactions.add(
                            Transaction.fromFileString(record));
                } else {
                    throw new IOException("Unknown record in saved account.");
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("Saved account data is invalid.", e);
        }

        portfolio.restoreCashBalance(balance);

        for (Map.Entry<String, Integer> entry : savedHoldings.entrySet()) {
            portfolio.restoreHolding(entry.getKey(), entry.getValue());
        }

        transactions.clear();
        transactions.addAll(savedTransactions);
        return true;
    }
}