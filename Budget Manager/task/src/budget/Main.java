package budget;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Main {
    public static void main(String[] args) {
        // write your code here
        Budget budget = new Budget("My budget", "$", 0);
        BudgetManager budgetManager = new BudgetManager();
        budgetManager.inputPurchases(budget);
        budgetManager.printPurchases(budget);

    }
}

class Purchase {
    String description;
    double amount;
    public Purchase(String description, double amount) {
        this.description = description;
        this.amount = amount;
    }
}

class Income extends Purchase {
    public Income(String description, double amount) {
        super(description, amount);
    }
}

class Budget {

    String description;
    double totalExpense;
    double totalIncome;
    double totalBudget;
    String currency;

    ArrayList<Purchase> purchases = new ArrayList<>();
    ArrayList<Income> incomes = new ArrayList<>();

    public Budget(String description, String currency, double amount) {
        this.description = description;
        this.currency = currency;
        this.totalBudget = amount;
    }

    public void addPurchase(Purchase purchase) {
        this.totalExpense += purchase.amount;
        purchases.add(purchase);
    }

    public void addIncome(Income income) {
        this.totalIncome += income.amount;
        incomes.add(income);
    }


}

class BudgetManager {

    public static void inputPurchases(Budget budget) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String purchaseString = scanner.nextLine();
            if  (purchaseString.isEmpty()) {
                break;
            }
            Map<String, Double> purchase = getPurchaseFromString(purchaseString, budget.currency);
            if (purchase.isEmpty()) {
                System.out.println("Please enter a valid description of purchase");
                continue;
            }
            for (String key : purchase.keySet()) {
                budget.purchases.add(new Purchase(key, purchase.get(key)));
                break;
            }
        }
    }

    public static void printPurchases(Budget budget) {
        double sum = 0;
        for (Purchase purchase : budget.purchases) {
            System.out.println(purchase.description + " " + budget.currency + String.format("%.2f", purchase.amount));
            sum += purchase.amount;
        }
        System.out.println();
        System.out.println("Total: " + budget.currency + String.format("%.2f", sum));
    }

    public static Map<String, Double> getPurchaseFromString(String purchaseString, String currency) {

        Map<String, Double> result = new HashMap<>();

        String regex = "[" + currency + "][0-9]+.[0-9]{2}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(purchaseString);

        if (matcher.find()) {
            int index = matcher.start();
            String match =  matcher.group();
            result.put(purchaseString.substring(0, index-1), Double.parseDouble(match.substring(currency.length(), match.length())));
        }

        return result;
    }
}


