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
        
        
        Label lFiltre = new Label("Filtre de catégories :");
        Button bAppliquer = new Button("Appliquer les filtres");
        Button bToutSelectionner = new Button("Tout sélectionner");
        Button bToutDeselectionner = new Button("Tout déselectionner");
        bAppliquer.setFont(Font.font("Montserra",FontWeight.BOLD,14));
        lFiltre.setFont(Font.font("Montserra",FontWeight.BOLD,14));
        
        VBox vbFiltre = new VBox(lFiltre);
        ArrayList<String> listeCatSelected = new ArrayList<String>();
        ArrayList<String> listeIdCatExisting = gestionBddGUI.returnIdCategories(con);
        ArrayList<RadioButton> listeRbCat = new ArrayList<RadioButton>();
        
        //ajout des radiobuttons correspondants a toutes les categories et definitions de leur comportement
        for (int i=0;i<listeIdCatExisting.size();i++){
            String idc = listeIdCatExisting.get(i);
            RadioButton rb=new RadioButton(gestionBddGUI.returnNomCategorie(con, idc));
            listeRbCat.add(rb);
            vbFiltre.getChildren().add(rb);
            rb.setSelected(true);
            listeCatSelected.add(idc);
            //Gestion Selection par rapport au filtre
            rb.setOnMouseClicked((t) -> {
                if (rb.isSelected()){
                    listeCatSelected.add(idc);
                }else{
                    listeCatSelected.remove(idc);
                }
            });
        }
        
        
        vbFiltre.getChildren().add(bToutSelectionner);
        vbFiltre.getChildren().add(bToutDeselectionner);
        vbFiltre.getChildren().add(bAppliquer);
        vbFiltre.setSpacing(5);
        vbFiltre.setPadding(new Insets(5));
        this.setLeft(vbFiltre);
        
        
        
        //Evenements boutons
        bDeco.setOnMouseClicked((t) -> {
            main.setCenter(new PageAccueil(main));
        });
        
        bNouvelleVente.setOnMouseClicked((t) -> {
            this.setCenter(new NouvelleVente(main,this));
        });
        
        bAppliquer.setOnMouseClicked((t) -> {
            try {
                gestionBddGUI.filtreCategories(con, main, this, listeCatSelected);
            } catch (SQLException ex) {
                Logger.getLogger(Encheres.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        bToutSelectionner.setOnMouseClicked((t) -> {
            listeCatSelected.clear();
            for (int i=0;i<listeRbCat.size();i++){
                listeRbCat.get(i).setSelected(true);
            }
            for (int i=0;i<listeRbCat.size();i++){
                listeCatSelected.add(listeIdCatExisting.get(i));
            } 
            
        });
        
        bToutDeselectionner.setOnMouseClicked((t) -> {
            for (int i=0;i<listeRbCat.size();i++){
                listeRbCat.get(i).setSelected(false);
            }
            listeCatSelected.clear();
        });
        
        //Affichage de tous les objets du site
        gestionBddGUI.tousLesObjets(con,this,main);
    }
    
}
