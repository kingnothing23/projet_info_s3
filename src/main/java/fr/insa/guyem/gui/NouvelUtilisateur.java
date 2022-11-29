/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.guyem.gui;

import fr.insa.guyem.gestionBddGUI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author fears
 */
public class NouvelUtilisateur extends GridPane{
    public NouvelUtilisateur (VueMain main) throws SQLException{
        Label lMessage = new Label("L'équipe de Ebuy.fr est heureuse de faire votre connaissance !\n"
                + "Ne vous inquiétez pas, cela ne prendra que quelques clics.");
        lMessage.setTextAlignment(TextAlignment.CENTER);
        lMessage.setFont(Font.font("Montserra",FontWeight.BOLD,20));
        this.add(lMessage, 0, 0,2,1);
        Label lMessage2 = new Label("Veuillez rentrer vos informations : ");
        lMessage2.setTextAlignment(TextAlignment.CENTER);
        lMessage2.setFont(Font.font("Montserra",FontWeight.BOLD,15));
        this.add(lMessage2, 0, 1);
        
        Label lNom = new Label("Nom : ");
        this.add(lNom,0,2);
        Label lPrenom = new Label ("Prenom : ");
        this.add(lPrenom, 0, 3);
        Label lPass = new Label("Mot de passe : ");
        this.add(lPass,0,4);
        Label lConfirmPass = new Label("Confirmation du mot de passe : ");
        this.add(lConfirmPass,0,5);
        Label lEmail = new Label("Email : ");
        this.add(lEmail,0,6);
        Label lCodePostal = new Label("Code Postal : ");
        this.add(lCodePostal,0,7);
        TextField tNom = new TextField();
        this.add(tNom,1,2);
        TextField tPrenom = new TextField();
        this.add(tPrenom,1,3);
        PasswordField pPass = new PasswordField();
        this.add(pPass,1,4);
        PasswordField pConfirmPass = new PasswordField();
        this.add(pConfirmPass,1,5);
        TextField tEmail = new TextField();
        this.add(tEmail, 1, 6);
        TextField tCodePostal = new TextField();
        this.add(tCodePostal, 1, 7);
        ToggleButton tbConnexion = new ToggleButton("Création du compte");
        this.add(tbConnexion,0,10);
        CheckBox cb = new CheckBox();
        this.add(cb, 1, 9);
        Label lCb = new Label("J'accepte les CGU :");
        this.add(lCb, 0, 9);
        this.setAlignment(Pos.CENTER);
        this.setVgap(10);
        this.setHgap(10);
        tbConnexion.setDisable(true);
        
        cb.setOnAction((t) -> {
            if (tbConnexion.isDisabled()){
                tbConnexion.setDisable(false);
            }else{
                tbConnexion.setDisable(true);
            }
            
        });
        
        
        tbConnexion.setOnAction((t) -> {
            if (tNom.getText().isEmpty()==false && tPrenom.getText().isEmpty()==false && pPass.getText().isEmpty()==false
                    && tEmail.getText().isEmpty() == false && tCodePostal.getText().isEmpty() == false){
                if (pConfirmPass.getText().equals(pPass.getText())) {
                    try {
                        Connection con = main.getInfoSession().getConBdd();
                        gestionBddGUI.createUtilisateur(con, tNom.getText(), tPrenom.getText(),
                                pPass.getText(), tEmail.getText(), tCodePostal.getText());

                        int userId = gestionBddGUI.connectuser(con,
                                tEmail.getText(), pPass.getText());
                        main.getInfoSession().setCurrentUserId(userId);
                        if (userId != -1) {
                            main.setCenter(new Encheres(main));
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(NouvelUtilisateur.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }}
        });
    }
}
