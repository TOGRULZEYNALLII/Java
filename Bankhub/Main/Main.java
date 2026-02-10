package Main;
import DomainClass.Account;
import DomainClass.Transaction;
import java.util.*;
import java.util.stream.Collectors;
import interfaces.Repository;
import org.jetbrains.annotations.NotNull;
import repository.InMemoryRepository;
import service.BankService;
import service.BankException;
import enums.AccountType;

import static java.lang.System.in;

public class Main {
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        Repository<Account> accRepo = new InMemoryRepository<>();
        Repository<Transaction> txRepo = new InMemoryRepository<>();
        BankService svc = new BankService(accRepo, txRepo);

        Seed(svc);

        while (true) {
            System.out.println("""
                    \n=== BankHub ===
                    1) Create account
                    2) Deposit
                    3) Withdraw
                    4) Transfer
                    5) List accounts (sorted)
                    6) Filter accounts (balance > 1000)
                    7) Show owners (unique, sorted)
                    8) Show transactions grouped by account
                    9) Trigger common errors (demo)
                    0) Exit
                    """);
            System.out.print("Choose: ");
            switch (in.nextLine().trim()) {
                case "1" -> createAccountFlow(svc);
                case "2" -> depositFlow(svc);
                case "3" -> withdrawFlow(svc);
                case "4" -> transferFlow(svc);
                case "5" -> listSortedFlow(svc);
                case "6" -> filterFlow(svc);
                case "7" -> ownersFlow(svc);
                case "8" -> groupedFlow(svc);
                case "9" -> demoErrors(svc);
                case "0" -> {
                    System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Unknown.");
            }
        }
    }

    private static void createAccountFlow(BankService svc) {
        try {
            System.out.print("Owner name: ");
            String owner = in.nextLine().trim();

            System.out.print("Account type (CHECKING, SAVINGS, BUSINESS): ");
            String typeStr = in.nextLine().trim().toUpperCase();
            AccountType type;
            try {
                type = AccountType.valueOf(typeStr);
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid account type, defaulting to CHECKING.");
                type = AccountType.CHECKING;
            }

            System.out.print("Initial balance: ");
            double balance = Double.parseDouble(in.nextLine().trim());

            // DomainClass.Account expects a string for the type, pass the enum name
            Account acc = new Account(UUID.randomUUID(), owner, type.name(), balance);
            svc.createAccount(acc);
            System.out.println("Created: " + acc.format());
        } catch (Exception e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }
    private static void depositFlow(BankService svc){
        try {
            System.out.print("Account ID: ");
            UUID accId = UUID.fromString(in.nextLine().trim());
            System.out.print("Amount to deposit: ");
            double amount = Double.parseDouble(in.nextLine().trim());
            svc.deposit(accId, amount);
            System.out.println("Deposited $" + String.format("%.2f", amount) + " to account " + accId);
        } catch (Exception e) {
            System.out.println("Error during deposit: " + e.getMessage());
        }
    }

    private static void Seed(@NotNull BankService svc) {
        // Create some sample accounts
        svc.createAccount(new Account(UUID.randomUUID(), "Alice", "CHECKING", 1500));
        svc.createAccount(new Account(UUID.randomUUID(), "Bob", "SAVINGS", 2500));
        svc.createAccount(new Account(UUID.randomUUID(), "Charlie", "BUSINESS", 5000));
        //transactions
        List<Account> accounts = svc.getAllAccounts();
        if (accounts.size() >= 2) {
            svc.deposit(accounts.get(0).getId(), 500);
            svc.withdraw(accounts.get(1).getId(), 300);
            svc.deposit(accounts.get(2).getId(), 1000);
        }


    }
private static void withdrawFlow(BankService svc){
    try {
        System.out.print("Account ID: ");
        UUID accId = UUID.fromString(in.nextLine().trim());
        System.out.print("Amount to withdraw: ");
        double amount = Double.parseDouble(in.nextLine().trim());
        System.out.println("Withdrew $" + String.format("%.2f", amount) + " from account " + accId);
    } catch (Exception e) {
        System.out.println("Error during withdrawal: " + e.getMessage());
    }
}
private static void transferFlow(BankService svc){
    try {
        System.out.print("From Account ID: ");
        UUID fromId = UUID.fromString(in.nextLine().trim());
        System.out.print("To Account ID: ");
        UUID toId = UUID.fromString(in.nextLine().trim());
        System.out.print("Amount to transfer: ");
        double amount = Double.parseDouble(in.nextLine().trim());

        // Withdraw from source account
        svc.withdraw(fromId, amount);
        // Deposit to destination account
        svc.deposit(toId, amount);
        // supeer simple transfer logic, no rollback on failure, just for demo
        System.out.println("Transferred $" + String.format("%.2f", amount) + " from account " + fromId + " to account " + toId);
    } catch (Exception e) {
        System.out.println("Error during transfer: " + e.getMessage());
    }
}

// not null ,null veri uzerinde calismayi engellemek icin kullanilir
 private  static void listSortedFlow(@org.jetbrains.annotations.NotNull BankService svc){
     List<Account> accounts = svc.getAllAccounts();
     List<Account> sorted = accounts.stream()
             .sorted(Comparator.comparing(Account::getBalance))
             .toList();
     System.out.println("Accounts sorted by balance: ");
     sorted.forEach(acc -> System.out.println(acc.format()));
 }


private static void filterFlow(@NotNull BankService svc){
    List<Account> accounts = svc.getAllAccounts();
    System.out.println("Enter minimum balance to filter accounts: ");
    int amount = Integer.parseInt(in.nextLine().trim());
    List<Account> filtered = accounts.stream()
            .filter(acc -> acc.getBalance() > amount)
            .toList();
    System.out.println("Accounts with balance > " + amount + ": ");
    filtered.forEach(acc -> System.out.println(acc.format()));
}

 private static void ownersFlow(@NotNull BankService svc){
     List<Account> accounts = svc.getAllAccounts();
     List<String> owners = accounts.stream()
             .map(Account::getOwnerName)
             .distinct()
             .sorted()
             .toList();
     System.out.println("Unique sorted account owners: ");
     owners.forEach(System.out::println);
 }
public static void groupedFlow(@NotNull BankService svc){
    List<Account> accounts = svc.getAllAccounts();
    List<Transaction> transactions = svc.getAllTransactions();
    Map<UUID, List<Transaction>> grouped = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getAccountId));
    System.out.println("Transactions grouped by account: ");
    grouped.forEach((accId, txs) -> {
        System.out.println("Account ID: " + accId);
        txs.forEach(tx -> System.out.println("  " + tx.format()));
    });
}
public static void demoErrors(BankService svc){
    System.out.println("Triggering common errors...");
    try {
        System.out.println("1) Creating account with negative balance...");
        Account acc = new Account(UUID.randomUUID(), "Error User", "CHECKING", -100);
        svc.createAccount(acc);
    } catch (Exception e) {
        System.out.println("Caught error: " + e.getMessage());
    }
    try {
        System.out.println("2) Withdrawing more than balance...");
        List<Account> accounts = svc.getAllAccounts();
        if (!accounts.isEmpty()) {
            Account acc = accounts.getFirst();
            svc.withdraw(acc.getId(), acc.getBalance() + 1000); // Withdraw more than available
        } else {
            System.out.println("No accounts to test withdrawal error.");
        }
    } catch (Exception e) {
        System.out.println("Caught error: " + e.getMessage());
    }
    try {
        System.out.println("3) Depositing to non-existent account...");
        svc.deposit(UUID.randomUUID(), 100);
    } catch (Exception e) {
        System.out.println("Caught error: " + e.getMessage());
    }
}


//son
}





