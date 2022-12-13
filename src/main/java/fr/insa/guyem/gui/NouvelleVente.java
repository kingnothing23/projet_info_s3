/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.guyem.gui;

import fr.insa.guyem.gestionBddGUI;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author fears
 */
public class NouvelleVente extends VBox{
    public NouvelleVente(VueMain main,BorderPane mainEncheres){
        Connection con =main.getInfoSession().getConBdd(); //Ref lien a la bdd
        
        GridPane gp = new GridPane();
        Label lMessage = new Label("Vous souhaitez lancez une enchère sur une de vos possessions ?\n"
                + "Pas de problèmes, cela ne prendra que quelques instants");
        lMessage.setTextAlignment(TextAlignment.CENTER);
        lMessage.setFont(Font.font("Montserra",FontWeight.BOLD,18));
        gp.add(lMessage, 0, 0,2,1);
        Label lMessage2 = new Label("Veuillez remplir ces informations : ");
        lMessage2.setTextAlignment(TextAlignment.CENTER);
        lMessage2.setFont(Font.font("Montserra",FontWeight.BOLD,15));
        gp.add(lMessage2, 0, 1);
        
        Label lNom = new Label("Nom : ");
        gp.add(lNom,0,2);
        Label lPetitedesc = new Label ("Une petite description de cet objet : ");
        gp.add(lPetitedesc, 0, 3);
        Label lLonguedesc = new Label("Une grande description de cet objet : ");
        gp.add(lLonguedesc,0,4);
        Label lPrixInitial = new Label("Prix initial de la vente : ");
        gp.add(lPrixInitial,0,5);
        Label lDateDebut = new Label("Debut de la vente (format: AAAA-MM-JJ): ");
        gp.add(lDateDebut,0,6);
        Label lDateFin = new Label("Fin de la vente (format: AAAA-MM-JJ): ");
        gp.add(lDateFin,0,7);
        
        Label lHourDebut = new Label("Heure du début de la vente (format: HH:MM:SS): ");
        gp.add(lHourDebut,0,8);
        Label lHourFin = new Label("Fin de la vente (format: HH:MM:SS): ");
        gp.add(lHourFin,0,9);
        
        Label lCategorie = new Label("Catégorie de l'objet : ");
        gp.add(lCategorie, 0, 10);
        ChoiceBox cbCategorie = new ChoiceBox(); //POUVOIR RECUPERER LES CATEGORIES SOUS FORME DE LISTE DE STRING
        gp.add(cbCategorie,1,10);
        TextField tNom = new TextField();
        gp.add(tNom,1,2);
        TextField tPetitedesc = new TextField();
        gp.add(tPetitedesc,1,3);
        TextArea tLonguedesc = new TextArea();
        gp.add(tLonguedesc,1,4);
        tLonguedesc.setMaxWidth(300);
        tLonguedesc.setMaxHeight(40);
        tLonguedesc.setWrapText(true);
        TextField tPrixInitial = new TextField();
        gp.add(tPrixInitial,1,5);
        TextField tDateDebut = new TextField();
        gp.add(tDateDebut, 1, 6);
        TextField tDateFin = new TextField();
        gp.add(tDateFin, 1, 7);
        TextField tHourDebut = new TextField();
        gp.add(tHourDebut, 1, 8);
        TextField tHourFin = new TextField();
        gp.add(tHourFin, 1, 9);
        ToggleButton tbCreationVente = new ToggleButton("Création de la vente");
        gp.add(tbCreationVente,0,12);
        CheckBox cb = new CheckBox();
        gp.add(cb, 1, 11);
        Label lCb = new Label("J'accepte les Conditions générales de Vente :");
        gp.add(lCb, 0, 11);
        gp.setAlignment(Pos.CENTER);
        gp.setVgap(10);
        gp.setHgap(10);
        tbCreationVente.setDisable(true);
        
        Button bHome = new Button("Retour au tableau des ventes");
        ImageView view = new ImageView (new Image(getClass().getResourceAsStream("home2.png")));
        view.setFitHeight(50);
        view.setFitWidth(50);
        bHome.setGraphic(view);
        this.getChildren().addAll(bHome,gp);
        this.setSpacing(10);
        
        cb.setOnAction((t) -> {
            if (tbCreationVente.isDisabled()){
                tbCreationVente.setDisable(false);
            }else{
                tbCreationVente.setDisable(true);
            }
            
        });
        
        bHome.setOnAction((t) -> {
            try {
                main.setCenter(new Encheres(main));
            } catch (SQLException ex) {
                Logger.getLogger(NouvelleVente.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        tbCreationVente.setOnAction((t) -> {
            try {
                LocalDateTime ldtDateDebut = gestionBddGUI.convertDateTime(tDateDebut.getText(),tHourDebut.getText());
                LocalDateTime ldtDateFin = gestionBddGUI.convertDateTime(tDateFin.getText(),tHourFin.getText());
                gestionBddGUI.createObjets(con, tNom.getText(), Double.parseDouble(tPrixInitial.getText()), tPetitedesc.getText(),
                        tLonguedesc.getText(), 3,main.getInfoSession().getCurrentUserId() , ldtDateDebut, ldtDateFin);
                gestionBddGUI.tousLesObjets(con, mainEncheres,main,Integer.toString(main.getInfoSession().getCurrentUserId()));
            } catch (SQLException ex) {
                Logger.getLogger(NouvelleVente.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
