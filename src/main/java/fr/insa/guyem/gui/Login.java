/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.guyem.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author fears
 */
public class Login extends BorderPane{
    
    public Login (VueMain main){
        Label lMessage = new Label("Veuillez rentrer vos identifiants");
        lMessage.setTextAlignment(TextAlignment.CENTER);
        lMessage.setFont(new Font("Arial",15));
        
        
        Label lNom = new Label("Nom : ");
        Label lPass = new Label("Mot de passe : ");
        TextField tNom = new TextField();
        PasswordField pPass = new PasswordField();
        HBox hbNom = new HBox(lNom,tNom);
        HBox hbPass = new HBox(lPass,pPass);
        Button bConnexion = new Button("Connexion");
        VBox vbMid = new VBox(lMessage,hbNom,hbPass,bConnexion);
        vbMid.setAlignment(Pos.CENTER);
        hbNom.setAlignment(Pos.CENTER);
        hbPass.setAlignment(Pos.CENTER);
        vbMid.setSpacing(15);
        this.setCenter(vbMid);
    }
}
