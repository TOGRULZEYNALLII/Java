package service;
import DomainClass.*;
import Exceptions.AccountNotFound;
import interfaces.Repository;
import Exceptions.NegativeAmount;
import java.util.List;
import java.util.UUID;

public class BankService {
     private final Repository<Account> accountRepository;
        private final Repository<Transaction> transactionRepository;
        public BankService(Repository<Account> accountRepository,Repository<Transaction> transactionRepository) {
            this.accountRepository = accountRepository;
            this.transactionRepository = transactionRepository;
        }
         public void createAccount(Account account) {
            accountRepository.save(account);
        }
        public void recordTransaction(Transaction transaction) {
            transactionRepository.save(transaction);
        }
        public List<Account> getAllAccounts() {
            return accountRepository.findAll();
        }
        public  List<Transaction> getAllTransactions() {
            return transactionRepository.findAll();
        }

        public void  findAccountById(UUID id) {
             accountRepository.findById(id).orElseThrow(() -> new AccountNotFound("Account not found"));
        }

        public void  deposit(UUID id, double amount) {
            Account account = accountRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Account not found"));
            if (amount<=0) {
                throw new NegativeAmount("Deposit amount must be positive");
            }
            double newBalance = account.getBalance() + amount;
            account.setBalance(newBalance);
            Transaction transaction = new Transaction(java.util.UUID.randomUUID(), account.getId(), enums.TransactionType.DEPOSIT, amount, java.time.LocalDate.now().toString());
            transactionRepository.save(transaction);
        }
        public void withdraw(UUID id, double amount) {
            Account account = accountRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Account not found"));
            if (amount<=0) {
                throw new NegativeAmount("Withdrawal amount must be positive");
            }
            if (account.getBalance() < amount) {
                throw new NegativeAmount("Insufficient balance");
            }else{
                double newBalance = account.getBalance() - amount;
                account.setBalance(newBalance);
            }
            Transaction transaction = new Transaction(java.util.UUID.randomUUID(), account.getId(), enums.TransactionType.WITHDRAW, amount, java.time.LocalDate.now().toString());
            transactionRepository.save(transaction);
        }



}
