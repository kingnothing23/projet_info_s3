/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.guyem.gui;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author fears
 */
public class PageAccueil extends BorderPane{
    
    public PageAccueil (VueMain main){
        Label lMessage = new Label("Bienvenue sur le meilleur site d'enchère du monde !\nEbuy.fr");
        lMessage.setTextAlignment(TextAlignment.CENTER);
        lMessage.setFont(Font.font("Montserra",FontWeight.BOLD,30));
 //       lMessage.setTextFill(javafx.scene.paint.Color.BLUE);
        
        Button bLogin = new Button("Login");
        Button bNewUser = new Button("Nouvel utilisateur");
        HBox hbOptions = new HBox(bLogin,bNewUser);
        VBox vbAccueil = new VBox(lMessage,hbOptions);
        vbAccueil.setAlignment(Pos.CENTER);
        hbOptions.setAlignment(Pos.CENTER);
        hbOptions.setSpacing(10);
        vbAccueil.setSpacing(10);
        this.setCenter(vbAccueil);
        
        bLogin.setOnAction((t) -> {
            main.setCenter(new Login(main));
        });
        bNewUser.setOnAction((t) -> {
            try {
                main.setCenter(new NouvelUtilisateur(main));
            } catch (SQLException ex) {
                Logger.getLogger(PageAccueil.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        
    }
    
}
