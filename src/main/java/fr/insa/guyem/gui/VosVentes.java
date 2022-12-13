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
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 *
 * @author fears
 */
public class VosVentes extends BorderPane{
    public VosVentes(VueMain main,Connection con) throws SQLException{
        Label lMsgVentes = new Label("Vos ventes :");
        lMsgVentes.setFont(Font.font("Montserra",FontWeight.BOLD,20));
        lMsgVentes.setPadding(new Insets(10));
        Button bHome = new Button("Retour au tableau des ventes");
        ImageView view = new ImageView (new Image(getClass().getResourceAsStream("home2.png")));
        view.setFitHeight(50);
        view.setFitWidth(50);
        bHome.setGraphic(view);
        this.setTop(lMsgVentes);
        this.setLeft(bHome);
        
        ////////////////////////////////////////////////
        //Def Boutons utilisateur
        Button bDeco = new Button("Se déconnecter");
        bDeco.setStyle("-fx-background-color: #E1341E");
        bDeco.setFont(Font.font("Montserra",FontWeight.BOLD,14));
        Button bVentes = new Button ("Mes ventes");
        bVentes.setFont(Font.font("Montserra",FontWeight.BOLD,14));
        Button bEncheres = new Button ("Mes enchères");
        bEncheres.setFont(Font.font("Montserra",FontWeight.BOLD,14));
        VBox vbBoutonUtilisateur = new VBox(bEncheres,bVentes,bDeco);
        
        TitledPane tpUtilisateur = new TitledPane("Utilisateur :\n"+
                gestionBddGUI.returnNomUtilisateur(con,main.getInfoSession().getCurrentUserId()),vbBoutonUtilisateur);
        tpUtilisateur.setFont(Font.font("Montserra",FontWeight.BOLD,16));
        tpUtilisateur.setExpanded(false);
        Button bNouvelleVente = new Button("Créer une vente");
        bNouvelleVente.setFont(Font.font("Montserra",FontWeight.BOLD,16));
        VBox vbRight = new VBox(tpUtilisateur,bNouvelleVente);
        this.setRight(vbRight);
        /////////////////////////////////////////////
        
        String query ="select * from objets where vendeur ="+main.getInfoSession().getCurrentUserId();
        gestionBddGUI.affichageQuery(con, this, main, query);
        //Evenements boutons
        bDeco.setOnMouseClicked((t) -> {
            main.setCenter(new PageAccueil(main));
        });
        bNouvelleVente.setOnMouseClicked((t) -> {
            this.setCenter(new NouvelleVente(main,this));
            this.setTop(null);
        });
        bVentes.setOnMouseClicked((t) -> {
            try {
                main.setCenter(new VosVentes(main,con));
            } catch (SQLException ex) {
                Logger.getLogger(Encheres.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        bHome.setOnAction((t) -> {
            try {
                main.setCenter(new Encheres(main));
            } catch (SQLException ex) {
                Logger.getLogger(NouvelleVente.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
}
