/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.guyem.gui;

import fr.insa.guyem.gestionBddGUI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint ;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 *
 * @author fears
 */
public class Encheres extends BorderPane{
    
    public Encheres (VueMain main) throws SQLException{
        Connection con =main.getInfoSession().getConBdd(); //Ref lien a la bdd
        
        
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
        
        gestionBddGUI.creationFiltre(main, this, con);
        
        
        //Evenements boutons
        bDeco.setOnMouseClicked((t) -> {
            main.setCenter(new PageAccueil(main));
        });
        
        bNouvelleVente.setOnMouseClicked((t) -> {
            try {
                this.setLeft(null);
                this.setTop(null);
                this.setCenter(new NouvelleVente(main,this));
            } catch (SQLException ex) {
                Logger.getLogger(Encheres.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
   
        bVentes.setOnMouseClicked((t) -> {
            try {
                main.setCenter(new VosVentes(main,con));
            } catch (SQLException ex) {
                Logger.getLogger(Encheres.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        //Affichage de tous les objets du site
        gestionBddGUI.tousLesObjets(con,this,main,Integer.toString(main.getInfoSession().getCurrentUserId()));
    }
    
}
