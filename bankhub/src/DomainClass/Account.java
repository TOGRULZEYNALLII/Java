package DomainClass;
import interfaces.Identifiable;
import java.util.UUID;
import interfaces.Formatter;
public class Account implements Identifiable, Formatter {
//    id, ownerName, AccountType, balance

    private final UUID id;
    private final String ownerName;
    private final String accountType;
    private final double balance;

    public Account(UUID id, String ownerName, String accountType, double balance) {
        this.id = id;
        this.ownerName = ownerName;
        this.accountType = accountType;
        this.balance = balance;
    }
    @Override
    public UUID getId() {
        return id;
    }
    public String getOwnerName() {
        return ownerName;
    }
    public String getAccountType() {
        return accountType;}
    public double getBalance() {
        return balance;}
    @Override
    public String format() {
        return "Account ID: " + id.toString() +
                ", Owner Name: " + ownerName +
                ", Account Type: " + accountType +
                ", Balance: $" + String.format("%.2f", balance);
    }


}
