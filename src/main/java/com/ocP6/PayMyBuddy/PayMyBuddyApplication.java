package com.ocP6.PayMyBuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

import java.util.Scanner;

@RestController
@SpringBootApplication
public class PayMyBuddyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayMyBuddyApplication.class, args);

        // Attendre un ordre de l'utilisateur avant de fermer l'application
        Scanner scanner = new Scanner(System.in);
        System.out.println("Appuyez sur 'Entrée' pour fermer l'application...");
        scanner.nextLine();  // L'application attend que l'utilisateur appuie sur 'Entrée'
        System.exit(0);  // Fermeture de l'application
    }



}
