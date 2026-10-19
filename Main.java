import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, Stock> market = new LinkedHashMap<>();
    private static User user;

    public static void main(String[] args) {
        initializeMarket();

        System.out.println("======================================");
        System.out.println("       STOCK TRADING PLATFORM");
        System.out.println("======================================");

        System.out.print("Enter your username: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            username = "Investor";
        }

        user = new User(username, 100000.00);

        try {
            if (user.loadData(market)) {
                System.out.println("Previous account loaded successfully.");
            } else {
                System.out.println("New account created with Rs. 100000.00.");
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Could not load saved account: " + e.getMessage());
            System.out.println("Starting with a new in-memory account.");
            user = new User(username, 100000.00);
        }

        int choice;

        do {
            showMenu();
            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    displayStocks();
                    break;
                case 2:
                    buyStock();
                    break;
                case 3:
                    sellStock();
                    break;
                case 4:
                    showPortfolio();
                    break;
                case 5:
                    showTransactions();
                    break;
                case 6:
                    saveData();
                    break;
                case 7:
                    updateStockPrice();
                    break;
                case 0:
                    saveData();
                    System.out.println("Thank you for using the platform!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 0);

        scanner.close();
    }

    private static void initializeMarket() {
        market.put("TCS",
                new Stock("TCS", "Tata Consultancy Services", 3500.00));
        market.put("INFY",
                new Stock("INFY", "Infosys", 1500.00));
        market.put("RELIANCE",
                new Stock("RELIANCE", "Reliance Industries", 2900.00));
        market.put("HDFCBANK",
                new Stock("HDFCBANK", "HDFC Bank", 1700.00));
        market.put("ITC",
                new Stock("ITC", "ITC Limited", 450.00));
        market.put("WIPRO",
                new Stock("WIPRO", "Wipro Limited", 500.00));
    }

    private static void showMenu() {
        System.out.println("\n============== MAIN MENU ==============");
        System.out.println("1. View available stocks");
        System.out.println("2. Buy stocks");
        System.out.println("3. Sell stocks");
        System.out.println("4. View portfolio");
        System.out.println("5. View transaction history");
        System.out.println("6. Save account data");
        System.out.println("7. Simulate stock price change");
        System.out.println("0. Exit");
        System.out.println("=======================================");
    }

    private static void displayStocks() {
        System.out.println("\n--------------- STOCK MARKET ----------------");
        System.out.printf("%-12s %-30s %s%n",
                "SYMBOL", "COMPANY", "PRICE");

        for (Stock stock : market.values()) {
            System.out.printf("%-12s %-30s Rs. %.2f%n",
                    stock.getSymbol(),
                    stock.getCompanyName(),
                    stock.getPrice());
        }
    }

    private static void buyStock() {
        displayStocks();

        System.out.print("\nEnter stock symbol to buy: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        Stock stock = market.get(symbol);

        if (stock == null) {
            System.out.println("Stock not found.");
            return;
        }

        int quantity = readInt("Enter quantity: ");

        if (user.buyStock(stock, quantity)) {
            System.out.println("Stock purchased successfully!");
            System.out.printf("Purchase value: Rs. %.2f%n",
                    stock.getPrice() * quantity);
        }
    }

    private static void sellStock() {
        showPortfolio();

        System.out.print("\nEnter stock symbol to sell: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        Stock stock = market.get(symbol);

        if (stock == null) {
            System.out.println("Stock not found.");
            return;
        }

        int quantity = readInt("Enter quantity to sell: ");

        if (user.sellStock(stock, quantity)) {
            System.out.println("Stock sold successfully!");
            System.out.printf("Sale value: Rs. %.2f%n",
                    stock.getPrice() * quantity);
        }
    }

    private static void showPortfolio() {
        Portfolio portfolio = user.getPortfolio();

        System.out.println("\n============== YOUR PORTFOLIO ==============");
        System.out.println("Username: " + user.getUsername());
        System.out.printf("Available cash: Rs. %.2f%n",
                portfolio.getCashBalance());
        System.out.println("\nStock holdings:");

        boolean hasHoldings = false;

        for (Map.Entry<String, Integer> entry
                : portfolio.getHoldings().entrySet()) {
            Stock stock = market.get(entry.getKey());

            if (stock != null) {
                hasHoldings = true;
                double value = stock.getPrice() * entry.getValue();

                System.out.printf(
                        "%-10s | Quantity: %d | Price: Rs. %.2f | Value: Rs. %.2f%n",
                        stock.getSymbol(), entry.getValue(),
                        stock.getPrice(), value);
            }
        }

        if (!hasHoldings) {
            System.out.println("No stocks purchased yet.");
        }

        System.out.printf("%nTotal stock value: Rs. %.2f%n",
                portfolio.getStockValue(market));
        System.out.printf("Total portfolio value: Rs. %.2f%n",
                portfolio.getTotalValue(market));
        System.out.println("============================================");
    }

    private static void showTransactions() {
        System.out.println("\n============= TRANSACTION HISTORY =============");

        if (user.getTransactions().isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }

        for (Transaction transaction : user.getTransactions()) {
            System.out.println(transaction);
        }
    }

    private static void saveData() {
        try {
            user.saveData(market);
            System.out.println("Account data saved successfully.");
        } catch (IOException e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }

    private static void updateStockPrice() {
        displayStocks();

        System.out.print("\nEnter stock symbol to update: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        Stock stock = market.get(symbol);

        if (stock == null) {
            System.out.println("Stock not found.");
            return;
        }

        double newPrice = readDouble("Enter simulated new price: ");

        if (newPrice <= 0) {
            System.out.println("Price must be greater than zero.");
            return;
        }

        stock.setPrice(newPrice);
        System.out.println("Simulated price updated successfully.");
        System.out.println("This is a simulation, not live market data.");
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                int value = Integer.parseInt(scanner.nextLine().trim());

                if (value < 0) {
                    System.out.println("Please enter zero or a positive number.");
                    continue;
                }

                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                double value = Double.parseDouble(scanner.nextLine().trim());

                if (!Double.isFinite(value)) {
                    System.out.println("Please enter a finite number.");
                    continue;
                }

                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}