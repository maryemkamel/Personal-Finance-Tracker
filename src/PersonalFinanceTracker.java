import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PersonalFinanceTracker {
    private static void addCategory(ArrayList<Category> categories, String name, double amount) {
        // Check if the category exists in the list
        boolean existCategory = false;
        for (Category category : categories) {
            if (category.getName().equals(name)) {
                category.setAmount(category.getAmount() + amount);
                existCategory = true;
                break;
            }
        }
        // If the category doesn't exist, add it to the list
        if (!existCategory) {
            categories.add(new Category(name, amount));
        }
    }


    public static void main(String [] args){
        //String PATH_FILE = "C:/Users/HP/Desktop/Bootcamp/End of java module challenge/src/transactions.csv";
        Scanner scanner=new Scanner(System.in);
        System.out.println("Welcome to your Personal Finance Tracker ");
        System.out.println("Please enter the path to the input transaction file");
        String TRANSACTION_PATH =scanner.nextLine();
        // Validate input file path
        File inputFile = new File(TRANSACTION_PATH);
        if (!inputFile.exists() || !inputFile.isFile()) {
            System.out.println("Error: Invalid input file path!!!");
            return;
        }
        else {
            System.out.println("Valid input file path");
        }
        System.out.println("--------------------------------------------");
        ArrayList<Transaction> transactions=new ArrayList<>();
        // 1. Transaction Input Processing
        try(BufferedReader br = new BufferedReader(new FileReader(TRANSACTION_PATH))) {
            String line ;
            System.out.println("Transaction details ");
            while ((line = br.readLine()) != null){
                String [] portions = line.split(",");
                String date = portions[0];
                String type = portions[1];
                String category = portions[2];
                double amount = Double.parseDouble(portions[3]);
                transactions.add(new Transaction(date, type, category, amount));
                System.out.println(line);
            }
            // 2. Data Categorization and Summarization
            ArrayList<Category> expenses= new ArrayList<>();
            ArrayList<Category> incomes = new ArrayList<>();
            double totalIncomes=0;
            double totalExpenses=0;
            for (Transaction transaction:transactions){
                if(transaction.getType().equalsIgnoreCase("expense"))
                {
                    totalExpenses += transaction.getAmount();
                    addCategory(incomes, transaction.getCategory(), transaction.getAmount());
                }else if (transaction.getType().equalsIgnoreCase("income"))
                {
                    totalIncomes += transaction.getAmount();
                    addCategory(expenses, transaction.getCategory(), transaction.getAmount());
                                  }
            }
            //calculate net savings
            double netSavings = totalIncomes-totalExpenses;
            System.out.println("net savings is : "+ netSavings+ "$");
            //Summarize expenses by category
            Map<String,Double> expensesByCategory =new HashMap<>();

            for(Transaction transaction:transactions){
                String category = transaction.getCategory();
                double amount=transaction.getAmount();
                expensesByCategory.put(category,expensesByCategory.getOrDefault(category,0.0)+amount);

            }
            //display Summary
            System.out.println("--------------------------------------------");
            System.out.println("Summary expenses by category");
            for (Map.Entry<String,Double> entry:expensesByCategory.entrySet()){
                System.out.println(entry.getKey() +" $"+ entry.getValue());
            }
            System.out.println("--------------------------------------------");
            // Write report to file
            System.out.println("Enter the desired output path for the report: ");
            String outputFilePath = scanner.nextLine();

            // Validate output file path
            File outputFile = new File(outputFilePath);
            if (outputFile.isDirectory()) {
                System.out.println("Error: Output path should specify a file, not a directory.");
                return;
            }
            //example file name:financial_report.txt

            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath))) {
                // Write report header
                writer.println("Personal Financial Report");
                writer.println("Total Income: $" + totalIncomes);
                writer.println("Total Expenses: $" + totalExpenses);
                writer.println("Net Savings: $" + netSavings);
                writer.println();
                // Write breakdown of expenses by category
                writer.println("Expenses by Category:");
                for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
                    writer.println(entry.getKey() + ": $" + entry.getValue());
                }System.out.println("--------------------------------------------");
                System.out.println("Processing current transactions...");

            } catch (IOException e) {
                System.err.println("Error writing report to file: " + e.getMessage());
            }

        }catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }




}
