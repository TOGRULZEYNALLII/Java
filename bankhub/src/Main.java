package bankhub;
import DomainClass.Account;
import DomainClass.Transaction;
import java.util.*;
import java.util.stream.Collectors;
import interfaces.Repository;
import repository.InMemoryRepository;
import service.BankService;
import service.BankException;
import enums.AccountType;
public class Main {
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        Repository<Account> accRepo = new InMemoryRepository<>();
        Repository<Transaction> txRepo = new InMemoryRepository<>();
        BankService svc = new BankService(accRepo, txRepo);

        try {
            Seed.loadSample(svc);
        } catch (BankException e) {
            System.out.println("Seed error: " + e.getMessage());
        }

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
             .deposit(svc, accId, amount);
            System.out.println("Deposited $" + String.format("%.2f", amount) + " to account " + accId);
        } catch (Exception e) {
            System.out.println("Error during deposit: " + e.getMessage());
        }
    }

    }
