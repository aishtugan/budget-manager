package budget;
import java.io.*;
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
    public String pathToFile = "purchases.txt";

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

    public void setBalance(double balance) {
        this.totalBalance = balance;
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
                    System.out.println("Total sum: " + budget.currency + String.format("%.2f", sum));
                } else {
                    System.out.println("The purchase list is empty!");
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

    public static void savePurchasesToFile(Budget budget, String pathToFile) {

        StringBuilder sb = new StringBuilder();
        String splitter = "|";

        sb.append("Balance ").append(String.format("%.2f", budget.totalIncome)).append("\n");

        for (Purchase purchase : budget.purchases) {
            sb.append(purchase.category).append(splitter).append(purchase.description).append(splitter);
            sb.append(budget.currency).append(splitter).append(String.format("%.2f", purchase.amount)).append("\n");
        }

        saveStringsToFile(sb.toString(), pathToFile);
    }

    public static void loadPurchasesFromFile(Budget budget, String pathToFile) {

        String fileText = loadStringsFromFile(pathToFile);

        try (BufferedReader reader = new BufferedReader(new StringReader(fileText))) {
            String row;
            boolean firstRow = true;
            while ((row = reader.readLine()) != null) {
                if (firstRow) {
                    firstRow = false;
                    String[] arrayFirstRow = row.split(" ");
                    if (arrayFirstRow.length == 2 && arrayFirstRow[0].equals("Balance")) {
                        budget.setBalance(Double.parseDouble(arrayFirstRow[1]));
                    }
                } else {
                    String[] arrayOfRow = row.split("\\|");
                    if (arrayOfRow.length == 4) {
                        String category = arrayOfRow[0];
                        String description = arrayOfRow[1];
                        double amount = Double.parseDouble(arrayOfRow[3]);
                        budget.addPurchase(new Purchase(category, description, amount));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public static void saveStringsToFile(String textToSave, String pathToFile) {

        File file = new File(pathToFile);

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(textToSave + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadStringsFromFile(String pathToFile) {

        StringBuilder sb = new StringBuilder();
        File file = new File(pathToFile);

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                sb.append(scanner.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("No file found: " + pathToFile);
        }

        return sb.toString();
    }

    public static void sortPurchases(Budget budget) {
        Scanner scanner = new Scanner(System.in);

        Map<Integer, String > sortMenu = new HashMap<>();

        sortMenu.put(1, "Sort all purchases");
        sortMenu.put(2, "Sort by type");
        sortMenu.put(3, "Sort certain type");
        sortMenu.put(4, "Back");

        Map<Integer, String > purchaseTypes = new HashMap<>();

        purchaseTypes.put(1, "Food");
        purchaseTypes.put(2, "Clothes");
        purchaseTypes.put(3, "Entertainment");
        purchaseTypes.put(4, "Other");

        System.out.println("How do you want to sort?");

        for (int key : sortMenu.keySet()) {
            System.out.println(key + ") " + sortMenu.get(key));
        }

        while (scanner.hasNextLine()) {

            int userInput = scanner.nextInt();
            System.out.println();

            switch (userInput) {

                case 1: // Sort all purchases

                    System.out.println("All:");

                    if (!budget.purchases.isEmpty()) {
                        double sum = 0;
                        ArrayList<Purchase> sortedPurchases = sortPurchasesByCategory(budget.purchases, "All");
                        for (Purchase purchase : sortedPurchases) {
                            System.out.println(purchase.description + " " + budget.currency + String.format("%.2f", purchase.amount));
                            sum += purchase.amount;
                        }
                        System.out.println("Total: " + budget.currency + String.format("%.2f", sum));
                    } else {
                        System.out.println("The purchase list is empty!");
                    }
                    break;

                case 2: // Sort by type

                    System.out.println("Types:");
                    ArrayList<Purchase> types = new ArrayList<>();
                    for (int key : purchaseTypes.keySet()) {
                        double sum = 0;
                        for (Purchase purchase : budget.purchases) {
                            if (purchase.category.equals(purchaseTypes.get(key))) {
                                sum += purchase.amount;
                            }
                        }
                        types.add(new Purchase(purchaseTypes.get(key), purchaseTypes.get(key), sum));
                    }
                    ArrayList<Purchase> sortedTypes = sortPurchasesByCategory(types, "All");
                    for (Purchase purchase : sortedTypes) {
                        System.out.println(purchase.description + " - " + budget.currency + String.format("%.2f", purchase.amount));
                    }
                    break;

                case 3: //Sort certain type
                    System.out.println("Choose the type of purchase");
                    for (int key : purchaseTypes.keySet()) {
                        System.out.println(key + ") " + purchaseTypes.get(key));
                    }
                    int newUserInput = scanner.nextInt();
                    System.out.println();
                    if (newUserInput == 1 || newUserInput == 2 || newUserInput == 3 || newUserInput == 4) {
                        if (!budget.purchases.isEmpty()) {
                            ArrayList<Purchase> sortedPurchases = sortPurchasesByCategory(budget.purchases, purchaseTypes.get(newUserInput));
                            if (!sortedPurchases.isEmpty()) {
                                System.out.println(purchaseTypes.get(newUserInput)+":");
                                double sum = 0;
                                for (Purchase purchase : sortedPurchases) {
                                    System.out.println(purchase.description + " " + budget.currency + String.format("%.2f", purchase.amount));
                                    sum += purchase.amount;
                                }
                                System.out.println("Total sum: " + budget.currency + String.format("%.2f", sum));
                            } else {
                                System.out.println("The purchase list is empty!");
                            }

                        } else {
                            System.out.println("The purchase list is empty!");
                        }
                    } else {
                        System.out.println("Wrong input!");
                    }
                    break;

                case 4: //back
                    return;

                default:
                    System.out.println("Wrong input");
            }

            System.out.println();
            System.out.println("How do you want to sort?");

            for (int key : sortMenu.keySet()) {
                System.out.println(key + ") " + sortMenu.get(key));
            }

        }
    }

    public static ArrayList<Purchase> sortPurchasesByCategory(ArrayList<Purchase> purchases, String category) {

        ArrayList<Purchase> sortedPurchases = new ArrayList<>();

        for (Purchase purchase : purchases) {
            if (purchase.category.equals(category) || category.equals("All")) {
                sortedPurchases.add(purchase);
            }
        }
        int n = sortedPurchases.size();

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < n - i - 1; j++) {
                if (sortedPurchases.get(j).amount < sortedPurchases.get(j + 1).amount) {
                    Purchase temp = sortedPurchases.get(j);
                    sortedPurchases.set(j, sortedPurchases.get(j + 1));
                    sortedPurchases.set(j + 1, temp);
                    swapped = true;
                }
            }

            if (!swapped) {
                break;
            }
        }

        return sortedPurchases;
    }
    public static void budgetMenu(Budget budget) {

        Scanner scanner = new Scanner(System.in);

        Map<Integer, String > menu = new HashMap<>();

        menu.put(1, "Add income");
        menu.put(2, "Add purchase");
        menu.put(3, "Show list of purchases");
        menu.put(4, "Balance");
        menu.put(5, "Save");
        menu.put(6, "Load");
        menu.put(7, "Analyze (Sort)");
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

                case 5: //save
                    savePurchasesToFile(budget, budget.pathToFile);
                    System.out.println("Purchases were saved!\n");
                    break;

                case 6: //load
                    loadPurchasesFromFile(budget, budget.pathToFile);
                    System.out.println("Purchases were loaded!\n");
                    break;
                case 7: //sort
                    sortPurchases(budget);
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


