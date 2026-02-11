package Main;
import DomainClass.Account;
import DomainClass.Transaction;
import java.util.*;
import java.util.stream.Collectors;
import interfaces.Repository;
import org.jetbrains.annotations.NotNull;
import repository.InMemoryRepository;
import service.BankService;
import enums.AccountType;
import Exceptions.AccountNotFound;
import Exceptions.NegativeAmount;
public class Main {
    private static final Scanner in = new Scanner(System.in);

    static void main()  {
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
            if (owner.isEmpty()) {
                throw new IllegalArgumentException("Owner name cannot be empty.");
            }

            System.out.print("Account type (CHECKING, SAVINGS, BUSINESS): ");
            String typeStr = in.nextLine().trim().toUpperCase();
            AccountType type = AccountType.valueOf(typeStr); // Hatalıysa IllegalArgument fırlatır

            System.out.print("Initial balance: ");
            double balance = Double.parseDouble(in.nextLine().trim());
            if (balance < 0) {
            throw new NegativeAmount("Initial balance cannot be negative.");
            }

            // Hesap oluşturma
            Account acc = new Account(UUID.randomUUID(), owner, type.name(), balance);
            svc.createAccount(acc);

            // İlk işlemi kaydet
            Transaction tx = new Transaction(UUID.randomUUID(), acc.getId(), enums.TransactionType.DEPOSIT, balance, java.time.LocalDate.now().toString());
            svc.recordTransaction(tx);

            System.out.println("Success! Created: " + acc.format());

        } catch (IllegalArgumentException e) {
            System.out.println("Error: Invalid input (Wrong account type or number format)."+e.getMessage());
        }catch (NegativeAmount e){
            System.out.println("Error: " + e.getMessage());
        }catch (Exception e) {
            System.out.println("System Error: Could not create account.");
        }
        // Metod bitti, Java otomatik return yapıyor, kasmaya gerek yok!
    }

    private static void depositFlow(BankService svc) {
        UUID accId ;

        try {
            System.out.print("Account ID: ");
            accId = UUID.fromString(in.nextLine().trim());
            svc.findAccountById(accId);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid ID format!");
            return;
        }catch (AccountNotFound e) {
            System.out.println("Account not found for the provided ID.");
            return;
        }catch (Exception e) {
            System.out.println("An unexpected error occurred.");
            return;
        }

        try {
            System.out.print("Amount to deposit: ");
            double amount = Double.parseDouble(in.nextLine().trim());
            svc.deposit(accId, amount);
            System.out.println("Deposited $" + String.format("%.2f", amount) + " successfully.");
        }catch (IllegalArgumentException e){
            System.out.println("Invalid amount format!");
        }
        catch (NegativeAmount e) {
         System.out.println("Deposit Failed: " + e.getMessage());
        }catch (Exception e) {
            System.out.println("An unexpected error occurred during deposit.");
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


    private static void withdrawFlow(BankService svc) {
        try {
            System.out.print("Account ID: ");
            String inputId = in.nextLine().trim();

            Account acc = svc.getAllAccounts().stream()
                    .filter(a -> a.getId().toString().equals(inputId))
                    .findFirst()
                    .orElseThrow(() -> new AccountNotFound("Account with ID " + inputId + " not found!"));

            System.out.print("Amount to withdraw: ");
            String amountInput = in.nextLine().trim();

            if (amountInput.isEmpty()) throw new NegativeAmount("Amount cannot be empty.");

            double amount = Double.parseDouble(amountInput);

            svc.withdraw(acc.getId(), amount);

            System.out.println("Success! Withdrew $" + String.format("%.2f", amount));

        } catch (AccountNotFound e) {
            System.out.println("ID Error: " + e.getMessage());

        } catch (NumberFormatException e) {
            System.out.println("Format Error: Please enter a valid numerical amount.");
        } catch (NegativeAmount e) {
           System.out.println("Withdrawal Failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
        }
    }
    private static void transferFlow(BankService svc) {
        try {
            System.out.print("From Account ID: ");
            String fromInput = in.nextLine().trim();
            Account fromAcc = svc.getAllAccounts().stream()
                    .filter(a -> a.getId().toString().equals(fromInput))
                    .findFirst()
                    .orElseThrow(() -> new AccountNotFound("Source account not found!"));

            System.out.print("To Account ID: ");
            String toInput = in.nextLine().trim();
            Account toAcc = svc.getAllAccounts().stream()
                    .filter(a -> a.getId().toString().equals(toInput))
                    .findFirst()
                    .orElseThrow(() -> new AccountNotFound("Destination account not found!"));

            System.out.print("Amount to transfer: ");
            double amount = Double.parseDouble(in.nextLine().trim());

            svc.withdraw(fromAcc.getId(), amount);
            svc.deposit(toAcc.getId(), amount);

            System.out.println("Success! $" + amount + " transferred.");

        } catch (AccountNotFound e) {
            System.out.println("ID Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Format Error: Invalid amount entered.");
        } catch (NegativeAmount e) {
            System.out.println("Transfer Failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
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

    List<Transaction> transactions = svc.getAllTransactions();
    Map<UUID, List<Transaction>> grouped = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getAccountId));
    System.out.println("Transactions grouped by account: ");
    grouped.forEach((accId, txs) -> {
        System.out.println("Account ID: " + accId);
        txs.forEach(tx -> System.out.println("  " + tx.format()));
    });
}


//son
}





