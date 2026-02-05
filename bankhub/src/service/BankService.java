package service;
import DomainClass.*;
import interfaces.Repository;

import java.util.List;

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
        public  deposit(Account account, double amount) {
            double newBalance = account.getBalance() + amount;
            Account updatedAccount = new Account(account.getId(), account.getOwnerName(), account.getAccountType(), newBalance);
            accountRepository.save(updatedAccount);
            Transaction transaction = new Transaction(java.util.UUID.randomUUID(), account.getId(), enums.TransactionType.DEPOSIT, amount, java.time.LocalDate.now().toString());
            transactionRepository.save(transaction);
        }
        public withdraw(Account account, double amount) {
            if (account.getBalance() < amount) {
                throw new IllegalArgumentException("Insufficient funds");
            }
            double newBalance = account.getBalance() - amount;
            Account updatedAccount = new Account(account.getId(), account.getOwnerName(), account.getAccountType(), newBalance);
            accountRepository.save(updatedAccount);
            Transaction transaction = new Transaction(java.util.UUID.randomUUID(), account.getId(), enums.TransactionType.WITHDRAW, amount, java.time.LocalDate.now().toString());
            transactionRepository.save(transaction);
        }



}
