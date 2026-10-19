import java.util.LinkedHashMap;
import java.util.Map;

public class Portfolio {
    private double cashBalance;
    private final Map<String, Integer> holdings = new LinkedHashMap<>();

    public Portfolio(double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative.");
        }
        cashBalance = initialBalance;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public int getQuantity(String symbol) {
        return holdings.getOrDefault(symbol.toUpperCase(), 0);
    }

    public Map<String, Integer> getHoldings() {
        return new LinkedHashMap<>(holdings);
    }

    public boolean buyStock(Stock stock, int quantity) {
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than zero.");
            return false;
        }

        double cost = stock.getPrice() * quantity;

        if (cost > cashBalance) {
            System.out.println("Insufficient cash balance.");
            return false;
        }

        cashBalance -= cost;
        holdings.merge(stock.getSymbol(), quantity, Integer::sum);
        return true;
    }

    public boolean sellStock(Stock stock, int quantity) {
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than zero.");
            return false;
        }

        String symbol = stock.getSymbol();
        int owned = getQuantity(symbol);

        if (quantity > owned) {
            System.out.println("You do not own enough shares.");
            return false;
        }

        cashBalance += stock.getPrice() * quantity;

        if (quantity == owned) {
            holdings.remove(symbol);
        } else {
            holdings.put(symbol, owned - quantity);
        }

        return true;
    }

    public double getStockValue(Map<String, Stock> market) {
        double value = 0;

        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            Stock stock = market.get(entry.getKey());

            if (stock != null) {
                value += stock.getPrice() * entry.getValue();
            }
        }

        return value;
    }

    public double getTotalValue(Map<String, Stock> market) {
        return cashBalance + getStockValue(market);
    }

    // Used when restoring a saved account.
    public void restoreCashBalance(double balance) {
        if (!Double.isFinite(balance) || balance < 0) {
            throw new IllegalArgumentException("Invalid saved balance.");
        }
        cashBalance = balance;
    }

    public void restoreHolding(String symbol, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Invalid saved quantity.");
        }
        holdings.put(symbol.toUpperCase(), quantity);
    }
}