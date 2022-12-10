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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author fears
 */
public class NouvelUtilisateur extends VBox{
    public NouvelUtilisateur (VueMain main) throws SQLException{
        GridPane gp = new GridPane();
        Label lMessage = new Label("L'équipe de Ebuy.fr est heureuse de faire votre connaissance !\n"
                + "Ne vous inquiétez pas, cela ne prendra que quelques clics.");
        lMessage.setTextAlignment(TextAlignment.CENTER);
        lMessage.setFont(Font.font("Montserra",FontWeight.BOLD,20));
        gp.add(lMessage, 0, 0,2,1);
        Label lMessage2 = new Label("Veuillez rentrer vos informations : ");
        lMessage2.setTextAlignment(TextAlignment.CENTER);
        lMessage2.setFont(Font.font("Montserra",FontWeight.BOLD,15));
        gp.add(lMessage2, 0, 1);
        
        Label lNom = new Label("Nom : ");
        gp.add(lNom,0,2);
        Label lPrenom = new Label ("Prenom : ");
        gp.add(lPrenom, 0, 3);
        Label lPass = new Label("Mot de passe : ");
        gp.add(lPass,0,4);
        Label lConfirmPass = new Label("Confirmation du mot de passe : ");
        gp.add(lConfirmPass,0,5);
        Label lEmail = new Label("Email : ");
        gp.add(lEmail,0,6);
        Label lCodePostal = new Label("Code Postal : ");
        gp.add(lCodePostal,0,7);
        TextField tNom = new TextField();
        gp.add(tNom,1,2);
        TextField tPrenom = new TextField();
        gp.add(tPrenom,1,3);
        PasswordField pPass = new PasswordField();
        gp.add(pPass,1,4);
        PasswordField pConfirmPass = new PasswordField();
        gp.add(pConfirmPass,1,5);
        TextField tEmail = new TextField();
        gp.add(tEmail, 1, 6);
        TextField tCodePostal = new TextField();
        gp.add(tCodePostal, 1, 7);
        ToggleButton tbConnexion = new ToggleButton("Création du compte");
        gp.add(tbConnexion,0,11);
        CheckBox cb = new CheckBox();
        gp.add(cb, 1, 9);
        Label lCb = new Label("J'accepte les CGU :");
        Label lErreur = new Label("");
        gp.add(lErreur, 0, 10,2,1);
        lErreur.setFont(Font.font("Montserra",12));
        lErreur.setTextFill(Color.RED);
        gp.add(lCb, 0, 9);
        gp.setAlignment(Pos.CENTER);
        gp.setVgap(10);
        gp.setHgap(10);
        tbConnexion.setDisable(true);
        
        Button bHome = new Button("Retour à l'accueil");
        ImageView view = new ImageView (new Image(getClass().getResourceAsStream("home2.png")));
        view.setFitHeight(50);
        view.setFitWidth(50);
        bHome.setGraphic(view);
        gp.add(bHome,0,0);
        this.getChildren().addAll(bHome,gp);
        this.setSpacing(10);
        
        cb.setOnAction((t) -> {
            if (tbConnexion.isDisabled()){
                tbConnexion.setDisable(false);
            }else{
                tbConnexion.setDisable(true);
            }
            
        });
        
        bHome.setOnAction((t) -> {
            main.setCenter(new PageAccueil(main));
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
                        lErreur.setText("Il y a eu une erreur dans la création de votre compte, veuillez réessayer");
                    }
                }}
        });
    }
}
