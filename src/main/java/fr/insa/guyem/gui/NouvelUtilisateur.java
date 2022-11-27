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
public class NouvelUtilisateur extends BorderPane{
    public NouvelUtilisateur (VueMain main){
        Label lMessage = new Label("L'équipe de Ebuy.fr est heureuse de faire votre connaissance !\n"
                + "Ne vous inquiétez pas, cela ne prendra que quelques cliques.");
        lMessage.setTextAlignment(TextAlignment.CENTER);
        lMessage.setFont(new Font("Arial",20));
        Label lMessage2 = new Label("Veuillez rentrer vos informations : ");
        lMessage2.setTextAlignment(TextAlignment.CENTER);
        lMessage2.setFont(new Font("Arial",15));
        
        Label lNom = new Label("Nom : ");
        Label lPass = new Label("Mot de passe : ");
        Label lConfirmPass = new Label("Confirmation du mot de passe : ");
        Label lEmail = new Label("Email : ");
        TextField tNom = new TextField();
        PasswordField pPass = new PasswordField();
        PasswordField pConfirmPass = new PasswordField();
        TextField tEmail = new TextField();
        HBox hbNom = new HBox(lNom,tNom);
        HBox hbPass = new HBox(lPass,pPass);
        HBox hbConfirmPass = new HBox(lConfirmPass,pConfirmPass);
        HBox hbEmail = new HBox(lEmail,tEmail);
        Button bConnexion = new Button("Création du compte");
        VBox vbMid = new VBox(lMessage,hbNom,hbEmail,hbPass,hbConfirmPass,bConnexion);
        vbMid.setAlignment(Pos.CENTER);
        hbNom.setAlignment(Pos.CENTER);
        hbPass.setAlignment(Pos.CENTER);
        hbEmail.setAlignment(Pos.CENTER);
        hbConfirmPass.setAlignment(Pos.CENTER);
        vbMid.setSpacing(15);
        this.setCenter(vbMid);
    }
}
