/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.guyem;

import fr.insa.guyem.gui.Encheres;
import java.awt.Color;
import java.awt.Paint;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 *
 * @author fears
 */
public class gestionBddGUI {

    //Permet de se connecter une Bdd
    public static Connection connectGeneralPostGres(String host, int port, String database, 
            String user, String pass) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection con = DriverManager.getConnection(
                "jdbc:postgresql://" + host + ":" + port
                + "/" + database,
                user, pass);
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        return con;
    }
    
    //Permet de se connecter a la Bdd de base
    public static Connection defautConnect() 
            throws ClassNotFoundException, SQLException {
        return connectGeneralPostGres("localhost", 5439, "postgres", "postgres", "pass");
    }

    //Permet de se connecter avec un utilisateur en utilisant le mdp et le nom, renvoi l'id de l'utilisateur
    public static int connectuser(Connection con, String mail, String mdp) throws SQLException {
        int connectyes;
        try ( Statement st = con.createStatement()) {
            String query = "select count(1) from utilisateur where mail like '" + mail + "' and pass like '" + mdp + "' group by pass";
            try ( ResultSet tlu = st.executeQuery(query)) {
                if (tlu.next() == false) {
                    connectyes = 0;
                } else {
                    connectyes = tlu.getInt(1);
                }
            }
        }
        if (connectyes != 1) {
            System.out.println("Utilisateur non reconnu");
            return -1;
        } else {
            int userid;
            try ( Statement st = con.createStatement()) {
                String query = "select id from utilisateur where mail like '" + mail + "' and pass like '" + mdp + "'";
                try ( ResultSet tlu = st.executeQuery(query)) {
                    tlu.next();
                    userid = tlu.getInt(1);
                }
            }
            return userid;
        }
    }   
    
    
    //permet de creer la colonne des offres des objets, en fonction d'une requete sql
    public static void affichageQuery(Connection con,Encheres mainEncheres,String query) throws SQLException {
        try ( Statement st = con.createStatement()) {

            try ( ResultSet tlu = st.executeQuery(query)) {

                // ici, on veut lister toutes les lignes, d'où le while
                ArrayList<Pane> listePane = new ArrayList<>();
                while (tlu.next()) {

                    int id = tlu.getInt("ido");
                    // ou par son numéro (la première colonne a le numéro 1)
                    String nom = tlu.getString(2);
                    String descri = tlu.getString(3);
                    String prixbase = tlu.getString(4);
                    String categorie = tlu.getString(5);
                    String vendeur = tlu.getString(6);
                    String debut = tlu.getString(7);
                    String fin = tlu.getString(8);
                    

                    
                    Label lNom = new Label(nom);
                    lNom.setFont(Font.font("Montserra", FontWeight.BOLD, 20));
                    Label lDescription = new Label(descri);
                    lDescription.setFont(Font.font("Montserra", FontWeight.MEDIUM, 10));
                    lDescription.setMaxWidth(140);
                    lDescription.setWrapText(true);

                    VBox vb1 = new VBox(lNom, lDescription);
                    Label lPrix = new Label(prixbase + " €");
                    lPrix.setFont(Font.font("Montserra", FontWeight.EXTRA_BOLD, 26));
                    lPrix.setLayoutX(200);
                    vb1.setLayoutX(15);
                    Pane pOffreSingle = new Pane(vb1, lPrix);
                    vb1.setSpacing(5);
                    pOffreSingle.setStyle("-fx-padding: 10; -fx-background-color: cornsilk;");
                    pOffreSingle.setMaxWidth(300);
                    pOffreSingle.setMinHeight(70);
                    
                    listePane.add(pOffreSingle);
                    
                    
                }
                ScrollPane scrollableOffers = new ScrollPane(); //déclaration Scrollpane et VBox qui contiennent les offres
                VBox vbOffresFinal = new VBox();
                for (int i=0;i<listePane.size();i++){
                    vbOffresFinal.getChildren().add(listePane.get(i)); //remplissage des offres dans le scrollpane
                }
                vbOffresFinal.setSpacing(15);
                vbOffresFinal.setAlignment(Pos.CENTER);
                scrollableOffers.setContent(vbOffresFinal);
                scrollableOffers.setFitToHeight(true);  //permet de centrer le scrollpane
                scrollableOffers.setFitToWidth(true);
                mainEncheres.setCenter(scrollableOffers);
            }
        }
    }
    
    //affiche tous les objets, notamment quand on arrive sur la session
    public static void tousLesObjets(Connection con, Encheres mainEncheres)throws SQLException{
        String query = "select * from objets";
        affichageQuery(con,mainEncheres,query);
    }
    
}
