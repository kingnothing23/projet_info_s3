/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package fr.insa.guyem;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 *
 * @author Utilisateur
 */
public class S3_info_bdd {

    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println("Hello World!");
        int a = 0;
//LocalDateTime dateenchere =LocalDateTime.now(); 
//                   System.out.println(dateenchere);
LocalDateTime datetime ;
System.out.println("entrer la date yyyy-mm-jj");
String entereddate = Lire.S();
        LocalDate date = LocalDate.parse(entereddate);
        
          
            System.out.println("entrer HH:mm:SS ");
            DateTimeFormatter parseFormate = DateTimeFormatter.ofPattern("H:mm:ss");
    Scanner scs = new Scanner(System.in);
    String timeString = scs.nextLine();
    LocalTime time = LocalTime.parse(timeString, parseFormate);
    System.out.println(time);
    datetime = date.atTime(time) ;
    System.out.println(datetime );
    }
}