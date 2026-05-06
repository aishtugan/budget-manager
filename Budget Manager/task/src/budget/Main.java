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

        BudgetManager.budgetMenu(budget);

    }
}

class Purchase {
    String description;
    double amount;
    String category;

    public Purchase(String category, String description, double amount) {
        this.category = category;
        this.description = description;
        this.amount = amount;
    }
}

class Income {
    String description;
    double amount;
    public Income(String description, double amount) {
        this.description = description;
        this.amount = amount;
    }
}

class Budget {

    String description;
    double totalExpense = 0;
    double totalIncome = 0;
    double totalBalance = 0;
    String currency;

    ArrayList<Purchase> purchases = new ArrayList<>();
    ArrayList<Income> incomes = new ArrayList<>();

    public Budget(String description, String currency, double amount) {
        this.description = description;
        this.currency = currency;
        this.totalBalance = amount;
    }

    public void addPurchase(Purchase purchase) {
        purchases.add(purchase);
        if  (purchase.amount > totalBalance) {
            purchase.amount =  totalBalance;
        }
        this.totalExpense += purchase.amount;
        this.totalBalance -= purchase.amount;
    }

    public void addIncome(Income income) {
        this.totalIncome += income.amount;
        incomes.add(income);
        this.totalBalance += income.amount;
    }

    public double totalBalance() {
        return totalBalance;
    }
}

class BudgetManager {

    public static void inputPurchases1st(Budget budget) {
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
                budget.purchases.add(new Purchase("food", key, purchase.get(key)));
                break;
            }
        }
    }

    public static void inputPurchase(Budget budget, String category) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter purchase name:");

        String purchaseString = scanner.nextLine();
        if  (purchaseString.isEmpty()) {
            return;
        }

        System.out.println("Enter its price:");

        double purchasePrice = scanner.nextDouble();

        budget.addPurchase(new Purchase(category, purchaseString, purchasePrice));

        System.out.println("Purchase was added!");

    }

    public static void inputPurchases(Budget budget) {

        Scanner scanner = new Scanner(System.in);
        Map<Integer, String > purchaseTypes = new HashMap<>();

        purchaseTypes.put(1, "Food");
        purchaseTypes.put(2, "Clothes");
        purchaseTypes.put(3, "Entertainment");
        purchaseTypes.put(4, "Other");
        purchaseTypes.put(5, "Back");

        System.out.println("Choose the type of purchases");

        for (int key : purchaseTypes.keySet()) {
            System.out.println(key + ") " + purchaseTypes.get(key));
        }

        while (scanner.hasNextLine()) {

            int input = scanner.nextInt();
            System.out.println();

            if (input == 1 || input == 2 || input == 3 || input == 4) {
                inputPurchase(budget, purchaseTypes.get(input));
            } else if (input == 5) {
                return;
            } else {
                System.out.println("Wrong input");
            }
            System.out.println();
            System.out.println("Choose the type of purchase");

            for (int key : purchaseTypes.keySet()) {
                System.out.println(key + ") " + purchaseTypes.get(key));
            }

        }

    }

    public static void printPurchases(Budget budget) {

        Scanner scanner = new Scanner(System.in);

        Map<Integer, String > purchaseTypes = new HashMap<>();

        purchaseTypes.put(1, "Food");
        purchaseTypes.put(2, "Clothes");
        purchaseTypes.put(3, "Entertainment");
        purchaseTypes.put(4, "Other");
        purchaseTypes.put(5, "All");
        purchaseTypes.put(6, "Back");

        System.out.println("Choose the type of purchase");

        for (int key : purchaseTypes.keySet()) {
            System.out.println(key + ") " + purchaseTypes.get(key));
        }

        while (scanner.hasNextLine()) {

            int input = scanner.nextInt();
            System.out.println();

            if (input == 1 || input == 2 || input == 3 || input == 4 || input == 5) {
                System.out.println(purchaseTypes.get(input) + ":");
                double sum = 0;
                if (!budget.purchases.isEmpty()) {
                    for (Purchase purchase : budget.purchases) {
                        if (input == 5 || purchase.category.equals(purchaseTypes.get(input))) {
                            System.out.println(purchase.description + " " + budget.currency + String.format("%.2f", purchase.amount));
                            sum += purchase.amount;
                        }
                    }
                }
                if (sum == 0) {
                    System.out.println("The purchase list is empty!");
                } else {
                    System.out.println("Total sum: " + budget.currency + String.format("%.2f", sum));
                }

            } else if (input == 6) {
                return;
            } else {
                System.out.println("Wrong input");
            }

            System.out.println();
            System.out.println("Choose the type of purchase");

            for (int key : purchaseTypes.keySet()) {
                System.out.println(key + ") " + purchaseTypes.get(key));
            }

        }

    }

    public static void printBalance(Budget budget) {
        System.out.println("Balance: " + budget.currency + String.format("%.2f", budget.totalBalance()));
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

    public static void budgetMenu(Budget budget) {

        Scanner scanner = new Scanner(System.in);

        Map<Integer, String > menu = new HashMap<>();

        menu.put(1, "Add income");
        menu.put(2, "Add purchase");
        menu.put(3, "Show list of purchases");
        menu.put(4, "Balance");
        menu.put(10, "Exit");

        System.out.println("Choose your action:");

        for (int key : menu.keySet()) {
            System.out.println(key % 10 + ") " + menu.get(key));
        }

        while (scanner.hasNextLine()) {

            int userInput = scanner.nextInt();
            System.out.println();

            switch (userInput) {
                case 0: //exit
                    System.out.println("Bye!");
                    return;

                case 1: // add income
                    System.out.println("Enter income:");
                    double incomeAmount = scanner.nextDouble();
                    budget.addIncome(new Income("", incomeAmount));
                    System.out.println("Income was added!\n");
                    break;

                case 2: // add purchase
                    inputPurchases(budget);
                    break;

                case 3: //list of purchases
                    printPurchases(budget);
                    break;

                case 4: //balance
                    printBalance(budget);
                    System.out.println();
                    break;

                default:
                    System.out.println("Wrong input!\n");
                    break;

            }

            System.out.println("Choose your action:");

            for (int key : menu.keySet()) {
                System.out.println(key % 10 + ") " + menu.get(key));
            }

        }
    }
}


