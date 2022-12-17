/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.guyem.gui;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author fears
 */

public class PageObjet extends ScrollPane {
    
    public float nouveauPrixActuel;
    
    public PageObjet (VueMain main, BorderPane mainEncheres,int idObjet,String nomObjet,String petiteDescri,String longueDescri
    ,float prixActuel) throws SQLException{
        Connection con =main.getInfoSession().getConBdd(); //Ref lien a la bdd
        
        GridPane gp = new GridPane();
        Label lMessage = new Label("Présentation de l'objet en vente : ");
        lMessage.setTextAlignment(TextAlignment.CENTER);
        lMessage.setFont(Font.font("Montserra",FontWeight.BOLD,20));
        Label lNomObjet = new Label (nomObjet);
        lNomObjet.setFont(Font.font("Montserra",FontWeight.EXTRA_BOLD,34));
        Label lNomCategorie = new Label("Catégorie : "+gestionBddGUI.returnNomCategorieFromIdObjet(con, String.valueOf(idObjet)));
        lNomCategorie.setFont(Font.font("Montserra",FontWeight.BOLD,16));
        Label lPetiteDescri1 = new Label("Description courte de l'objet : ");
        lPetiteDescri1.setFont(Font.font("Montserra",FontWeight.BOLD,16));
        Label lPetiteDescri2 = new Label(petiteDescri);
        lPetiteDescri2.setFont(Font.font("Montserra",FontWeight.MEDIUM,14));
        Label lLongueDescri1 = new Label("Description longue de l'objet : ");
        lLongueDescri1.setFont(Font.font("Montserra",FontWeight.BOLD,16));
        TextArea lLongueDescri2 = new TextArea(longueDescri);
        lLongueDescri2.setFont(Font.font("Montserra",FontWeight.MEDIUM,14));
        lLongueDescri2.setWrapText(true);
        lLongueDescri2.setMaxWidth(300);
        lLongueDescri2.setMaxHeight(60);
        lLongueDescri2.setEditable(false);
        Label lPrixActuel1 = new Label ("Enchère la plus haute actuellement :");
        lPrixActuel1.setFont(Font.font("Montserra",FontWeight.BOLD,16));
        Label lPrixActuel2 = new Label(Float.toString(prixActuel)+" €");
        lPrixActuel2.setFont(Font.font("Montserra",FontWeight.BOLD,16));
        Label lVotreEnchere = new Label ("Votre enchère est de "+gestionBddGUI.returnEnchereUtil(con,String.valueOf( main.getInfoSession().getCurrentUserId()),String.valueOf(idObjet), main)+" €");
        lVotreEnchere.setFont(Font.font("Montserra",FontWeight.BOLD,16));
        
        
        Label lCb = new Label("De combien voulez-vous enchérir ?");
        TextField tCb = new TextField();
        ToggleButton bCb = new ToggleButton("Valider immédiatement l'enchère");
        bCb.setDisable(true);
        
        
        
        VBox vbEncherir = new VBox(lCb,tCb,bCb);
        TitledPane tpEncherir = new TitledPane("Encherir",vbEncherir);
        tpEncherir.setExpanded(false);
        vbEncherir.setSpacing(5);
        
        gp.add(lMessage, 0, 0,2,1);
        gp.add(lNomObjet, 0, 1);
        gp.add(lNomCategorie,0,2);
        gp.add(lPetiteDescri1,0,3);
        gp.add(lPetiteDescri2, 1, 3);
        gp.add(lLongueDescri1,0,4);
        gp.add(lLongueDescri2, 1, 4);
        gp.add(lPrixActuel1,0,5);
        gp.add(lPrixActuel2, 1, 5);
        gp.add(lVotreEnchere, 0, 6);
        gp.add(tpEncherir, 0, 7);
        
        gp.setAlignment(Pos.CENTER);
        gp.setVgap(10);
        gp.setHgap(10);
        
        //Creation bouton retour et ajout au rendu final de la page
        Button bHome = new Button("Retour au tableau des ventes");
        Button bRefresh = new Button("Rafraichir la page");
        ImageView view = new ImageView (new Image(getClass().getResourceAsStream("home2.png")));
        ImageView view2 = new ImageView (new Image(getClass().getResourceAsStream("refreshIcon.png")));
        view.setFitHeight(50);
        view.setFitWidth(50);
        view2.setFitHeight(50);
        view2.setFitWidth(50);
        bHome.setGraphic(view);
        bRefresh.setGraphic(view2);
        HBox hbButtons = new HBox(bHome,bRefresh);
        VBox vbConteneur = new VBox(hbButtons,gp);
        hbButtons.setSpacing(5);
        vbConteneur.setSpacing(10);
        vbConteneur.setStyle("-fx-padding: 10; -fx-background-color: cornsilk;");
        this.setContent(vbConteneur);
       
        //Gestion des evenements
        bHome.setOnAction((t) -> {
            try {
                main.setCenter(new Encheres(main));
            } catch (SQLException ex) {
                Logger.getLogger(NouvelleVente.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        tCb.textProperty().addListener(observable -> {
            try{
                nouveauPrixActuel = gestionBddGUI.priceActual(con, idObjet); //On prend bien en compte le prix à l'instantanéé
                if(Float.valueOf(tCb.getText()) > nouveauPrixActuel +1){
                    bCb.setDisable(false);
                }else{
                    bCb.setDisable(true);
                }
            }catch (NumberFormatException nfe){
                bCb.setDisable(true);
            } catch (SQLException ex) {
                Logger.getLogger(PageObjet.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        bCb.setOnAction((t) -> {
            try {
                gestionBddGUI.createEnchere(con, idObjet, main.getInfoSession().getCurrentUserId(), Float.valueOf(tCb.getText()), LocalDateTime.now());
                this.refreshPage(main, mainEncheres, idObjet, nomObjet, petiteDescri, longueDescri, Float.valueOf(tCb.getText()));
                
            } catch (SQLException ex) {
                Logger.getLogger(PageObjet.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        bRefresh.setOnAction((t) -> {
            try {
                nouveauPrixActuel = gestionBddGUI.priceActual(con, idObjet);
            } catch (SQLException ex) {
                Logger.getLogger(PageObjet.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                this.refreshPage(main, mainEncheres, idObjet, nomObjet, petiteDescri, longueDescri, nouveauPrixActuel);
            } catch (SQLException ex) {
                Logger.getLogger(PageObjet.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public void refreshPage(VueMain main, BorderPane mainEncheres,int idObjet,String nomObjet,String petiteDescri,String longueDescri
    ,float nouveauPrixActuel) throws SQLException{
        
        mainEncheres.setCenter(new PageObjet(main,mainEncheres,idObjet,nomObjet,petiteDescri,longueDescri,nouveauPrixActuel));
    }
}
