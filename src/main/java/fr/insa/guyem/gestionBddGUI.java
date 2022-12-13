/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.guyem;

import fr.insa.guyem.gui.Encheres;
import fr.insa.guyem.gui.PageObjet;
import fr.insa.guyem.gui.VueMain;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
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

    //Permet de se connecter avec un utilisateur en utilisant le mdp et le mail, renvoi l'id de l'utilisateur
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
    
    public static int createUtilisateur(Connection con, String nom, String prenom,String pass, String mail,String codepostal) throws SQLException {
        try ( PreparedStatement pst = con.prepareStatement(
                """
                insert into utilisateur (nom,prenom,pass,mail,codepostal) values (?,?,?,?,?)
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, nom);
            pst.setString(2, prenom);
            pst.setString(3, pass);
            pst.setString(4,mail);
            pst.setString(5,codepostal);
            pst.executeUpdate();

            // je peux alors récupérer les clés créées comme un result set :
            try ( ResultSet rid = pst.getGeneratedKeys()) {
                // et comme ici je suis sur qu'il y a une et une seule clé, je
                // fait un simple next 
                rid.next();
                // puis je récupère la valeur de la clé créé qui est dans la
                // première colonne du ResultSet
                int id = rid.getInt(1);
                return id;
            }
        }
    }
   
    //permet de creer la colonne des offres des objets, en fonction d'une requete sql
    public static void affichageQuery(Connection con,Encheres mainEncheres,VueMain main,String query) throws SQLException {
        try ( Statement st = con.createStatement()) {

            try ( ResultSet tlu = st.executeQuery(query)) {

                // ici, on veut lister toutes les lignes, d'où le while
                ArrayList<Pane> listePane = new ArrayList<>();
                while (tlu.next()) {

                    int id = tlu.getInt("ido");
                    // ou par son numéro (la première colonne a le numéro 1)
                    String nom = tlu.getString(2);
                    String courtedescri = tlu.getString(3);
                    String longuedescri = tlu.getString(4);
                    String prixbase = tlu.getString(5);
                    String categorie = tlu.getString(6);
                    String vendeur = tlu.getString(7);
                    String debut = tlu.getString(8);
                    String fin = tlu.getString(9);
                    float prixActuel = priceActual(con,id);

                    
                    Label lNom = new Label(nom);
                    lNom.setFont(Font.font("Montserra", FontWeight.BOLD, 20));
                    lNom.setMaxWidth(180);
                    lNom.setWrapText(true);
                    Label lDescription = new Label(courtedescri);
                    lDescription.setFont(Font.font("Montserra", FontWeight.MEDIUM, 10));
                    lDescription.setMaxWidth(140);
                    lDescription.setWrapText(true);
                    Label lVendeur = new Label ("Vendu par : "+gestionBddGUI.returnNomUtilisateur(con, Integer.valueOf(vendeur)));
                    lVendeur.setFont(Font.font("Montserra", FontWeight.BOLD, 12));

                    
                    Label lPrix = new Label(prixActuel + " €");
                    Label lMessagePrix = new Label("Enchère la plus haute\nactuellement :");
                    lPrix.setFont(Font.font("Montserra", FontWeight.EXTRA_BOLD, 26));
                    lMessagePrix.setFont(Font.font("Montserra", FontWeight.BOLD, 10));
                    Label lAcheteurPlusHaut = new Label("");
                    lAcheteurPlusHaut.setFont(Font.font("Montserra", FontWeight.BOLD, 10));
                    
                    if (main.getInfoSession().getCurrentUserId() == Integer.parseInt(vendeur)){
                        lAcheteurPlusHaut.setText("Vous êtes le vendeur \nde cet objet");
                    }
                    
                    
                    VBox vb1 = new VBox(lNom, lDescription,lVendeur);
                    VBox vb2 = new VBox(lMessagePrix,lPrix,lAcheteurPlusHaut);
                    vb1.setLayoutX(15);
                    vb1.setSpacing(5);
                    vb2.setLayoutX(200);
                    vb2.setSpacing(5);
                    
                    Pane pOffreSingle = new Pane(vb1, vb2);
                    pOffreSingle.setStyle("-fx-padding: 10; -fx-background-color: cornsilk;");
                    pOffreSingle.setMaxWidth(315);
                    pOffreSingle.setMinHeight(120);
                    
                    pOffreSingle.setOnMouseClicked((t) -> {
                        mainEncheres.setCenter(new PageObjet(main,mainEncheres,id,nom,courtedescri,longuedescri,prixActuel));
                    });
                    listePane.add(pOffreSingle);
                    
                    
                }
                ScrollPane scrollableOffers = new ScrollPane(); //déclaration Scrollpane et VBox qui contiennent les offres
                scrollableOffers.setPadding(new Insets(10));
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

    public static void filtreCategories (Connection con, VueMain main, Encheres mainEncheres, ArrayList<String> listeIdCat) throws SQLException {
        String finQuery="";
        if (listeIdCat.size()>0){
            finQuery = finQuery+listeIdCat.get(0);
            for (int i=1;i<listeIdCat.size();i++){
                finQuery = finQuery+ " or categorie = "+ listeIdCat.get(i);
            }
            String query = "select * from objets where categorie = "+finQuery;
            affichageQuery(con,mainEncheres,main,query);

        }else{
            affichageQuery(con,mainEncheres,main,"select * from objets where 1=0");
        }
        
    }
    
    //affiche tous les objets, notamment quand on arrive sur la session
    public static void tousLesObjets(Connection con, Encheres mainEncheres,VueMain main)throws SQLException{
        String query = "select * from objets";
        affichageQuery(con,mainEncheres,main,query);
    }
    
    //Methode pour creer un nouvel objet
    public static int createObjets(Connection con, String nom, double prixbase, String petitedesc,
            String longuedesc, int idc, int idv, LocalDateTime debutvente, LocalDateTime finvente) throws SQLException {
        try ( PreparedStatement pst = con.prepareStatement(
                """
                insert into objets (nom,petitedescri,longuedescri,prixbase,categorie,vendeur,debut,fin) values (?,?,?,?,?,?,?,?)
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, nom);
            pst.setString(2, petitedesc);
            pst.setString(3, longuedesc);
            pst.setDouble(4, prixbase);
            pst.setInt(5, idc);
            pst.setInt(6, idv);
            Timestamp timestamp = Timestamp.valueOf(debutvente);
            Timestamp timestampe = Timestamp.valueOf(finvente);
            pst.setTimestamp(7, timestamp);
            pst.setTimestamp(8, timestampe);
            pst.executeUpdate();

            // je peux alors récupérer les clés créées comme un result set :
            try ( ResultSet rid = pst.getGeneratedKeys()) {
                // et comme ici je suis sur qu'il y a une et une seule clé, je
                // fait un simple next 
                rid.next();
                // puis je récupère la valeur de la clé créé qui est dans la
                // première colonne du ResultSet
                int id = rid.getInt(1);
                return id;
            }
        }
    }
    
    //Converti une date et un temps en string en LocalDateTime
    public static LocalDateTime convertDateTime(String enteredDate,String timeString) {
        LocalDateTime datetime;
        LocalDate date = LocalDate.parse(enteredDate);
       
        LocalTime time = LocalTime.parse(timeString);
        datetime = date.atTime(time);

        return datetime;
    }
    
    //Renvoi le montant de l'enchère la plus haute sur un certain objet:
    public static float priceActual(Connection con, int obj) throws SQLException {
        int yesin;
        float actual;
        try ( Statement st = con.createStatement()) {
            String query = "select count(sur) from enchere where sur=" + obj;
            try ( ResultSet tlu = st.executeQuery(query)) {
                tlu.next();
                yesin = tlu.getInt(1);
            }
        }

        if (yesin == 0) {
            try ( Statement st = con.createStatement()) {
                String query = "select prixbase from objets where ido=" + obj;
                try ( ResultSet tlu = st.executeQuery(query)) {
                    tlu.next();
                    actual = tlu.getFloat(1);
                }
            }

        } else {
            try ( PreparedStatement st = con.prepareStatement("select max(montant) from enchere where enchere.sur=?")) {
                st.setInt(1, obj);
                try ( ResultSet tlu = st.executeQuery()) {
                    tlu.next();
                    actual = tlu.getFloat(1);
                }
            }

        }
        return actual;
    }
    
    //Permet de créer une nouvelle enchère sur un objet existant
    public static void createEnchere(Connection con, int obj, int de, float price, LocalDateTime dateenchere) throws SQLException {

        try ( PreparedStatement pst = con.prepareStatement(
                """
                insert into enchere (de,sur,montant,date) values (?,?,?,?)
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, de);
            pst.setInt(2, obj);
            pst.setFloat(3, price);

            Timestamp timestamp = Timestamp.valueOf(dateenchere);
            pst.setTimestamp(4, timestamp);
            pst.executeUpdate();
            
        }
    }
    
    public static String returnNomUtilisateur(Connection con,int idUtilisateur)throws SQLException{
        try ( PreparedStatement pst = con.prepareStatement(
                "select nom,prenom from utilisateur where id = ?")) {
            pst.setInt(1, idUtilisateur);
            ResultSet res = pst.executeQuery();
            res.next();
            String nomUtilisateur = res.getString(1) +" "+ res.getString(2);
            return nomUtilisateur;
        }
    }
    
    public static ArrayList<String> returnIdCategories(Connection con) throws SQLException{
        try ( Statement st = con.createStatement()) {
            String query = "select idc from categorie";
            try ( ResultSet tlu = st.executeQuery(query)) {
                ArrayList<String> listeCat = new ArrayList<String>();
                while (tlu.next()){
                    listeCat.add(tlu.getString(1));
                }
                return listeCat;
            }
        }
    }
    
    public static String returnNomCategorie(Connection con, String id) throws SQLException{
        try ( Statement st = con.createStatement()) {
            String query = "select nom from categorie where idc="+id;
            try ( ResultSet tlu = st.executeQuery(query)) {
                tlu.next();
                return tlu.getString(1);
            }
        }
    }
    
    
    
    
    //A FAIRE : RETURN ID VENDEUR DEPUIS ID OBJET
    //A FAIRE : ID UTILISATEUR QUI A L'ENCHERE LA PLUS HAUTE A PARTIR D'UN ID OBJET
    //A FAIRE : COMMENT RECUP ID CATEGORIE A PARTIR D'UNE CATEGORIE SELECTIONNEE
}
