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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.FontWeight;

/**
 *
 * @author fears
 */
public class Login extends BorderPane{
    
    public Login (VueMain main){
        Label lMessage = new Label("Veuillez rentrer vos identifiants");
        lMessage.setTextAlignment(TextAlignment.CENTER);
        lMessage.setFont(Font.font("Montserra",FontWeight.BOLD,15));
        
        
        Label lMail = new Label("Mail : ");
        Label lPass = new Label("Mot de passe : ");
        Label lErreur = new Label("");
        lErreur.setFont(Font.font("Montserra",12));
        lErreur.setTextFill(Color.RED);
        TextField tMail = new TextField();
        PasswordField pPass = new PasswordField();
        HBox hbMail = new HBox(lMail,tMail);
        HBox hbPass = new HBox(lPass,pPass);
        Button bConnexion = new Button("Connexion");
        VBox vbMid = new VBox(lMessage,hbMail,hbPass,bConnexion,lErreur);
        vbMid.setAlignment(Pos.CENTER);
        hbMail.setAlignment(Pos.CENTER);
        hbPass.setAlignment(Pos.CENTER);
        vbMid.setSpacing(15);
        ImageView view = new ImageView (new Image(getClass().getResourceAsStream("home2.png")));
        view.setFitHeight(50);
        view.setFitWidth(50);
        Button bHome = new Button("Retour à l'accueil");
        bHome.setGraphic(view);
        this.setTop(bHome);
        this.setCenter(vbMid);
        
        
        bConnexion.setOnAction((t) -> {
            Connection con = main.getInfoSession().getConBdd();
            try {
                int userId = gestionBddGUI.connectuser(con,
                        tMail.getText(),pPass.getText());
                main.getInfoSession().setCurrentUserId(userId);
                main.getInfoSession().setCurrentUserName(gestionBddGUI.returnNomUtilisateur(con, userId));
                
                if (userId != -1){
                    main.setCenter(new Encheres(main));
                }else{
                    lErreur.setText("Mail ou mot de pass non reconnu, veuillez réessayer");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        bHome.setOnAction((t) -> {
            main.setCenter(new PageAccueil(main));
        });
    }
}
