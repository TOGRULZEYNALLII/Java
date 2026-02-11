package DomainClass;
import enums.TransactionType;
import interfaces.Identifiable;
import interfaces.Formatter;
import java.util.UUID;
public class Transaction implements Identifiable,Formatter {
//    id, accountId, type [DEPOSIT/WITHDRAW/TRANSFER], amount, date
        private final UUID id;
        private final UUID accountId;
        private final TransactionType type;
        private final double amount;
        private final String date;
        public Transaction(UUID id, UUID accountId, TransactionType type, double amount, String date) {
            this.id = id;
            this.accountId = accountId;
            this.type = type;
            this.amount = amount;
            this.date = date;
        }
        public UUID getAccountId() {
            return accountId;
        }
        public TransactionType getType() {
            return type;}
        public double getAmount() {
            return amount;
        }
        public String getDate() {
            return date;
        }
        @Override
        public String format() {
            return "Transaction ID: " + id +
                    ", Account ID: " + accountId +
                    ", Type: " + type +
                    ", Amount: $" + String.format("%.2f", amount) +
                    ", Date: " + date;
        }
        @Override
        public UUID getId() {
            return id;
        }

}
