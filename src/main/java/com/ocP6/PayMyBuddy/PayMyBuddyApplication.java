package com.ocP6.PayMyBuddy;

import com.ocP6.PayMyBuddy.model.Transaction;
import com.ocP6.PayMyBuddy.model.User;
import com.ocP6.PayMyBuddy.service.TransactionService;
import com.ocP6.PayMyBuddy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;


@Slf4j
@SpringBootApplication
//@ComponentScan("com.ocP6.PayMyBuddy")
public class PayMyBuddyApplication {


    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(PayMyBuddyApplication.class, args);

//        // Attendre un ordre de l'utilisateur avant de fermer l'application
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Appuyez sur 'Entrée' pour fermer l'application...");
//        scanner.nextLine();  // L'application attend que l'utilisateur appuie sur 'Entrée'
//        System.exit(0);  // Fermeture de l'application

        // Obtenir le UserServiceImpl à partir du contexte Spring
        UserService userService = context.getBean(UserService.class);

        // Récupérer et afficher le nombre de Users
        long usersCount = userService.countUsers();
        log.info("Nombre d'utilisateurs dans la base de données : " + usersCount);

        log.info("\n");
        log.info("\n");

        // Récupérer et afficher le nom du User ayant l'id 1
        String username = userService.findUsernameById(1L);
        log.info("Le User ayant l'id 1 à pour username : " + username);

        log.info("\n");
        log.info("\n");

        // Récupérer et afficher les noms de tous les Users
        List<User> users = userService.findAll();
        users.forEach(user -> log.info("User Id : " + user.getId() + "\n" + "User username : " + user.getUsername()));


        log.info("\n");
        log.info("\n");

        // Obtenir le TransactionServiceImpl à partir du contexte Spring
        TransactionService transactionService = context.getBean(TransactionService.class);

        // Récupérer et afficher le nombre de Transactions
        long transactionsCount = transactionService.countTransactions();
        log.info("Nombre de transactions dans la base de données : " + transactionsCount);

        log.info("\n");
        log.info("\n");

        // Récupérer et afficher les transactions
        List<Transaction> transactions = transactionService.findAll();
        transactions.forEach(transaction -> log.info("Transaction id : " + transaction.getId() + "  ---  " + "description : " + transaction.getDescription() + "  ---  " + "Amount : " + transaction.getAmount()
                + "\n" + "Transaction sender : " + transaction.getSender().getUsername() + "  (id : " + transaction.getSender().getId() + ")"
                + "  ---  " + "receiver : " + transaction.getReceiver().getUsername() + "  (id : " + transaction.getReceiver().getId() + ")"));

        log.info("\n");
        log.info("\n");

        // Récupérer et afficher les amis (user_connections)
        log.info("Liste des amis des users - Méthode A");
        List<User> usersFriends = userService.findAll();
        usersFriends.forEach(user -> {
            log.info("User id : " + user.getId() + "  ---  username : " + user.getUsername() +
                    "  ---  nombre d'amis : " + userService.getConnectionsByUserId(Long.valueOf(user.getId())).size());
            List<User> friends = userService.getConnectionsByUserId(Long.valueOf(user.getId()));                // Récupérer les amis de l'utilisateur
            friends.forEach(friend -> {                                                                         // Afficher l'id et le username de chaque ami
                log.info("  --- Ami id : " + friend.getId() + "  ---  Ami username : " + friend.getUsername());
            });
        });

        log.info("\n");
        log.info("\n");

        log.info("Liste des amis du user ayant l'id 1 - Méthode B");
        List<User> user1Connection = userService.getConnectionsByUserIdMethodB(1L);
        user1Connection.forEach(user -> log.info("username : " + user.getUsername()));

        log.info("\n");
        log.info("\n");
    }

}
