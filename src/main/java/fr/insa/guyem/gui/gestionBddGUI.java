/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.guyem.gui;

import fr.insa.guyem.gui.Encheres;
import fr.insa.guyem.gui.NouvelleVente;
import fr.insa.guyem.gui.PageAccueil;
import fr.insa.guyem.gui.PageMesEncheres;
import fr.insa.guyem.gui.PageObjet;
import fr.insa.guyem.gui.VosVentes;
import fr.insa.guyem.gui.VueMain;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.controlsfx.control.RangeSlider;

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
        return connectGeneralPostGres("localhost", 5439, "postgres", "postgres", "emgu7747");
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
    public static void affichageQuery(Connection con,BorderPane mainEncheres,VueMain main,String query,float prixMin,float prixMax,boolean isExpired) throws SQLException {
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

                    if (gestionBddGUI.tempsRestantMillis(gestionBddGUI.convertStringToDateTime(fin))<0 
                          || prixMin>prixActuel || prixMax<prixActuel){
                        if (!isExpired){
                            continue;
                        }
                    }else{
                        if (isExpired){
                            continue;
                        }
                    }
                    Label lNom = new Label(nom);
                    lNom.setFont(Font.font("Montserra", FontWeight.BOLD, 20));
                    lNom.setMaxWidth(180);
                    lNom.setWrapText(true);
                    Label lDescription = new Label(courtedescri);
                    lDescription.setFont(Font.font("Montserra", FontWeight.MEDIUM, 10));
                    lDescription.setMaxWidth(140);
                    lDescription.setWrapText(true);
                    Label lVendeur = new Label ("Vendu par : "+gestionBddGUI.returnNomUtilisateur(con, Integer.valueOf(vendeur)));
                    lVendeur.setFont(Font.font("Montserra", FontWeight.BOLD, 10));

                    
                    Label lPrix = new Label(prixActuel + " €");
                    Label lMessagePrix = new Label("Enchère la plus haute\nactuellement :");
                    lPrix.setFont(Font.font("Montserra", FontWeight.EXTRA_BOLD, 26));
                    lMessagePrix.setFont(Font.font("Montserra", FontWeight.BOLD, 10));
                    Label lInfoComp = new Label("Il reste\n"+gestionBddGUI.stringTempsRestant(convertStringToDateTime(fin)));
                    lInfoComp.setFont(Font.font("Montserra", FontWeight.BOLD, 8));
                    
                    Label lCategorie = new Label ("Catégorie : "+gestionBddGUI.returnNomCategorieFromIdObjet(con, String.valueOf(String.valueOf(id))));
                    lCategorie.setFont(Font.font("Montserra", FontWeight.BOLD, 10));
                    
                    if (main.getInfoSession().getCurrentUserId() == Integer.parseInt(vendeur)){
                        lVendeur.setText("Vous êtes le vendeur \nde cet objet");
                    }
                    if (isExpired){
                        if (priceActual(con,id) == Float.valueOf(prixbase)){
                            lMessagePrix.setText("Cet objet n'a\npas été vendu\nEnchère initiale :");
                            lInfoComp.setText("");
                        }else{
                            lMessagePrix.setText("Vendu pour :");
                            lInfoComp.setText("Vendu à "+returnNomUtilisateur(con,Integer.valueOf(returnIdAcheteur(con,String.valueOf(id)))));
                        }
                    }
                    
                    VBox vb1 = new VBox(lNom, lDescription,lVendeur,lCategorie);
                    VBox vb2 = new VBox(lMessagePrix,lPrix,lInfoComp);
                    vb1.setLayoutX(15);
                    vb1.setSpacing(5);
                    vb2.setLayoutX(200);
                    vb2.setSpacing(5);
                    
                    Pane pOffreSingle = new Pane(vb1, vb2);
                    pOffreSingle.setStyle("-fx-padding: 10; -fx-background-color: cornsilk;");
                    pOffreSingle.setMaxWidth(315);
                    pOffreSingle.setMinHeight(140);
                    
                    pOffreSingle.setOnMouseClicked((t) -> {
                        try {
                            if (vendeur.equals(String.valueOf(main.getInfoSession().getCurrentUserId()))){
                                mainEncheres.setCenter(new PageObjet(main,mainEncheres,id,nom,courtedescri,longuedescri,prixActuel,true,convertStringToDateTime(fin)));
                            }else{
                                mainEncheres.setCenter(new PageObjet(main,mainEncheres,id,nom,courtedescri,longuedescri,prixActuel,false,convertStringToDateTime(fin)));
                            }
                            
                        } catch (SQLException ex) {
                            Logger.getLogger(gestionBddGUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        mainEncheres.setTop(null);
                        mainEncheres.setLeft(null);
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


    
    //Creation du bandeau utilisateur
    public static void bandeauUtilisateur (Connection con,VueMain main,BorderPane mainPage) throws SQLException{
        //Def Boutons utilisateur
        Button bDeco = new Button("Se déconnecter");
        bDeco.setStyle("-fx-background-color: #E1341E");
        bDeco.setFont(Font.font("Montserra",FontWeight.BOLD,14));
        Button bVentes = new Button ("Mes ventes");
        bVentes.setFont(Font.font("Montserra",FontWeight.BOLD,14));
        Button bEncheres = new Button ("Mes enchères");
        bEncheres.setFont(Font.font("Montserra",FontWeight.BOLD,14));
        VBox vbBoutonUtilisateur = new VBox(bEncheres,bVentes,bDeco);
        vbBoutonUtilisateur.setSpacing(6);
        
        TitledPane tpUtilisateur = new TitledPane("Utilisateur :\n"+
                gestionBddGUI.returnNomUtilisateur(con,main.getInfoSession().getCurrentUserId()),vbBoutonUtilisateur);
        tpUtilisateur.setFont(Font.font("Montserra",FontWeight.BOLD,16));
        tpUtilisateur.setExpanded(false);
        Button bNouvelleVente = new Button("Créer une vente");
        bNouvelleVente.setFont(Font.font("Montserra",FontWeight.BOLD,16));
        VBox vbRight = new VBox(tpUtilisateur,bNouvelleVente);
        mainPage.setRight(vbRight);
        
        //Evenements boutons
        bDeco.setOnMouseClicked((t) -> {
            main.setCenter(new PageAccueil(main));
        });
        
        bNouvelleVente.setOnMouseClicked((t) -> {
            try {
                mainPage.setLeft(null);
                mainPage.setTop(null);
                mainPage.setCenter(new NouvelleVente(main,mainPage));
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
        
        bEncheres.setOnMouseClicked((t) -> {
            try {
                main.setCenter(new PageMesEncheres(con,main));
            } catch (SQLException ex) {
                Logger.getLogger(gestionBddGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    //affiche tous les objets, notamment quand on arrive sur la session
    public static void tousLesObjets(Connection con, BorderPane mainEncheres,VueMain main,String idUtilisateur)throws SQLException{
        String query = "select * from objets where vendeur <>" +idUtilisateur;
        affichageQuery(con,mainEncheres,main,query,0,returnPrixPlusHaut(con,main),false);
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
    
    public static LocalDateTime convertStringToDateTime(String enteredLocalDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); 
        LocalDateTime dateTime = LocalDateTime.parse(enteredLocalDateTime, formatter);
        return dateTime;
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
    
    //Renvoie la liste des id des categories
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
    
    //Renvoie le nom de la catégorie avec un id donné
    public static String returnNomCategorie(Connection con, String id) throws SQLException{
        try ( Statement st = con.createStatement()) {
            String query = "select nom from categorie where idc="+id;
            try ( ResultSet tlu = st.executeQuery(query)) {
                tlu.next();
                return tlu.getString(1);
            }
        }
    }
    
    public static String returnNomCategorieFromIdObjet(Connection con,String ido)throws SQLException {
        try ( Statement st = con.createStatement()) {
            String query = "select categorie.nom from objets,categorie where objets.ido ="+ido+" and objets.categorie = categorie.idc";
            try ( ResultSet tlu = st.executeQuery(query)) {
                tlu.next();
                return tlu.getString(1);
            }
        }
    }
    
    public static void creationFiltre(VueMain main,BorderPane mainEncheres,Connection con,Image img) throws SQLException{
        // PARTIE CATEGORIES :
        Label lFiltre = new Label("Filtre de catégories :");
        Button bAppliquer = new Button("Appliquer les filtres");
        Button bToutSelectionner = new Button("Tout sélectionner");
        Button bToutDeselectionner = new Button("Tout déselectionner");
        bAppliquer.setFont(Font.font("Montserra",FontWeight.BOLD,14));
        lFiltre.setFont(Font.font("Montserra",FontWeight.BOLD,14));
        Label lFiltrePrix = new Label("Filtre par prix :");
        lFiltrePrix.setFont(Font.font("Montserra",FontWeight.BOLD,14));
        RangeSlider slider;
        if (returnPrixPlusHaut(con,main)>0){
            slider = new RangeSlider(0, returnPrixPlusHaut(con,main),0,returnPrixPlusHaut(con,main));
            slider.setMajorTickUnit(returnPrixPlusHaut(con,main)/5);
        }else{
            slider = new RangeSlider(0, 1000,0,1000);
            slider.setMajorTickUnit(100);
        }
        
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setBlockIncrement(10);
        slider.setMinWidth(200);
        
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
        
        vbFiltre.getChildren().add(lFiltrePrix);
        vbFiltre.getChildren().add(slider);
        vbFiltre.getChildren().add(bToutSelectionner);
        vbFiltre.getChildren().add(bToutDeselectionner);
        vbFiltre.getChildren().add(bAppliquer);
        vbFiltre.setSpacing(5);
        vbFiltre.setPadding(new Insets(15));
        
        mainEncheres.setLeft(vbFiltre);
        
        TextField tfRechercher = new TextField();
        
        //PARTIE RECHERCHE :
        //Creation logo:
        ImageView view = new ImageView (img);
        view.setFitHeight(108);
        view.setFitWidth(192);
        
        //creation barre de recherche :
        Label lRechercher = new Label("Rechercher sur Ebuy : ");
        
        Button bRechercher = new Button("Rechercher");
        bRechercher.setFont(Font.font("Montserra",FontWeight.BOLD,14));
        lRechercher.setFont(Font.font("Montserra",FontWeight.BOLD,18));
        HBox hbRechercher = new HBox(lRechercher,tfRechercher,bRechercher);
        HBox hbTop= new HBox(view,hbRechercher);
        hbRechercher.setSpacing(5);
        hbRechercher.setPadding(new Insets(20));
        hbRechercher.setAlignment(Pos.CENTER);
        
        mainEncheres.setTop(hbTop);
        
        //Evenements :
        bAppliquer.setOnMouseClicked((t) -> {
            applicationFiltre(listeCatSelected,tfRechercher,main,con,mainEncheres,(float)slider.getLowValue(),(float)slider.getHighValue());
        });
        
        bRechercher.setOnMouseClicked((t) -> {
            applicationFiltre(listeCatSelected,tfRechercher,main,con,mainEncheres,(float)slider.getLowValue(),(float)slider.getHighValue());
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
        
        
        
    }
    
    public static void applicationFiltre(ArrayList<String> listeCatSelected,TextField tfRechercher,VueMain main,Connection con,BorderPane mainEncheres,float prixMin,float prixMax){
        try {
                //Query finale :
                String bigQuery="";
                //Gestion de la recherche par categorie : 
                String finQuery = "";
                if (listeCatSelected.size() > 0) {
                    finQuery = finQuery + listeCatSelected.get(0);
                    for (int i = 1; i < listeCatSelected.size(); i++) {
                        finQuery = finQuery + " or categorie = " + listeCatSelected.get(i);
                    }
                    String query = "select * from objets where (categorie = " + finQuery + " )and vendeur <> " + main.getInfoSession().getCurrentUserId();
                    bigQuery=bigQuery+ query + "and upper(nom) like upper('%" + tfRechercher.getText() + "%')";

                } else {
                    bigQuery= "select * from objets where 1=0";
                }
                
                gestionBddGUI.affichageQuery(con, mainEncheres, main,bigQuery,prixMin,prixMax,false);
            } catch (SQLException ex) {
                Logger.getLogger(Encheres.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public static long tempsRestantMillis(LocalDateTime ldtFin){
        long resultat = ChronoUnit.MILLIS.between(LocalDateTime.now(), ldtFin);
        return resultat;
        
    }

    
    public static String stringTempsRestant(LocalDateTime ldtFin){
  
        LocalDateTime tempDateTime = LocalDateTime.now();
        String resultat ="";
        
        long years = tempDateTime.until(ldtFin, ChronoUnit.YEARS);
        tempDateTime = tempDateTime.plusYears(years);
        if (years!=0){
            resultat = resultat+years+" année(s) ";
        }
        long months = tempDateTime.until(ldtFin, ChronoUnit.MONTHS);
        tempDateTime = tempDateTime.plusMonths(months);
        if (years!=0 || months!=0){
                    resultat = resultat+months+" mois ";
                }
        long days = tempDateTime.until(ldtFin, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays(days);
        if (years!=0 || months!=0 || days!=0){
            resultat = resultat+days+" jour(s) ";
        }
        long hours = tempDateTime.until(ldtFin, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours(hours);
        if (years!=0 || months!=0 || days!=0 || hours!=0){
            resultat = resultat+hours+" heure(s)\n";
        }
        long minutes = tempDateTime.until(ldtFin, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes(minutes);
        if (years!=0 || months!=0 || days!=0 || hours!=0 || minutes!=0){
            resultat = resultat+minutes+" minute(s) ";
        }
        long seconds = tempDateTime.until(ldtFin, ChronoUnit.SECONDS);
        tempDateTime = tempDateTime.plusSeconds(seconds);
        if (years!=0 || months!=0 || days!=0 || hours!=0 || minutes!=0 || seconds!=0){
            resultat = resultat+seconds+" seconde(s) ";
        }
        
        return resultat;
    }
    
    public static void createHistoUtil(Connection con,VueMain main, BorderPane mainPage,boolean isExpired) throws SQLException{
        try ( Statement st = con.createStatement()) {
            
            try ( ResultSet tlu = st.executeQuery("select * from enchere,objets  where de ="+main.getInfoSession().getCurrentUserId()+" and objets.ido=enchere.sur order by sur asc, montant desc")) {

                //Création des contenants et éléments à afficher :
                Label lMsgChaqueEnchere = new Label();
                String mémoireObjet = "";
                String mémoireMontantPlusHautUtil="";
                String mémoireNomObjet="";
                
                
                VBox vbContenuTitledPane = new VBox (); //Contenu de chaque titled pane 
                ScrollPane spHistorique = new ScrollPane();
                VBox vbHistorique= new VBox();  //Tout l'historique 
                vbHistorique.setPadding(new Insets(40));
                vbHistorique.setSpacing(15);
                vbContenuTitledPane.setSpacing(5);
                 // ici, on veut lister toutes les lignes, d'où le while
                while (tlu.next()) {
                    
                    Label lEncherePlusHaute=new Label();
                    Label lVotreEnchere=new Label();
                    String objetActuel = tlu.getString(3);
                    String montantEnchere = tlu.getString(4);
                    String fin = tlu.getString(14);
                    String dateHeure = tlu.getString(5);
                    dateHeure = dateHeure.substring(0, 10) +"  à "+dateHeure.substring(10,19);
                    fin = fin.substring(0, 19);
                    
                    
                    //Si c'est un nouvel objet on créé un nouveau titled pane et tout les contenants/infos associés
                    if (!objetActuel.equals(mémoireObjet)){
                        if (isExpired && tempsRestantMillis(convertStringToDateTime(fin))>0) {
                           
                           continue;
                        }else if(!isExpired && tempsRestantMillis(convertStringToDateTime(fin))<0){
                            continue;
                        }
                        vbContenuTitledPane = new VBox();
                        mémoireMontantPlusHautUtil = tlu.getString(4);
                        //Si c'est un montant rond il faut rajouter manuellement la virgule
                        if(!mémoireMontantPlusHautUtil.contains(".")){
                            mémoireMontantPlusHautUtil=mémoireMontantPlusHautUtil+".0";
                        }
                        mémoireNomObjet = tlu.getString(7);
                        mémoireObjet = objetActuel;
                        String encherePlusHaute = Float.toString(priceActual(con, Integer.valueOf(objetActuel)));
                        
                        //creation d'une nouvelle ligne avec titled pane, enchère la plus haute..
                        TitledPane tpObjet = new TitledPane("Objet : "+ mémoireNomObjet,vbContenuTitledPane);
                        tpObjet.setExpanded(false);
                        if (isExpired){
                            if (encherePlusHaute.equals(mémoireMontantPlusHautUtil)){
                                lEncherePlusHaute.setText("Vous avez remporté cette enchère !\nVotre montant final était de : "+mémoireMontantPlusHautUtil+" €");
                                lVotreEnchere.setText("");
                            }else{
                                lEncherePlusHaute.setText("Vous n'avez pas remporté cette enchère\nl'enchère la plus haute était de : "
                                        + encherePlusHaute+" €");
                            }
                        }else{
                            if (encherePlusHaute.equals(mémoireMontantPlusHautUtil)){
                                lEncherePlusHaute.setText("Votre enchère de montant : "+mémoireMontantPlusHautUtil+" € est la plus haute sur cet objet");
                                lVotreEnchere.setText("");
                            }else{
                                lEncherePlusHaute = new Label("Enchère la plus haute : "+encherePlusHaute+" €");
                                lVotreEnchere = new Label("Votre enchère : "+mémoireMontantPlusHautUtil+" € ");
                            }
                        }
                        
                        lEncherePlusHaute.setFont(Font.font("Montserra",FontWeight.BOLD,14));
                        lVotreEnchere.setFont(Font.font("Montserra",FontWeight.BOLD,14));
                        VBox vbMessages;
                        
                        if(!isExpired){
                            Button bVoirPageObjet = new Button("Voir page objet");
                            vbMessages = new VBox(lEncherePlusHaute,lVotreEnchere,bVoirPageObjet);
                            try ( Statement st2 = con.createStatement()) {
                                String query ="select * from objets where ido ="+mémoireObjet;
                                try ( ResultSet tlu2 = st2.executeQuery(query)) {
                                    tlu2.next();
                                    int id = tlu2.getInt("ido");
                                    String nom = tlu2.getString(2);
                                    String courtedescri = tlu2.getString(3);
                                    String longuedescri = tlu2.getString(4);
                                    String fin2 = tlu2.getString(9);
                                    float prixActuel = priceActual(con, id);

                                    bVoirPageObjet.setOnAction((t) -> {
                                        try {
                                            mainPage.setCenter(new PageObjet(main, mainPage, id, nom,courtedescri,longuedescri,prixActuel,false,convertStringToDateTime(fin2)));
                                            bandeauUtilisateur(con,main,mainPage);
                                            mainPage.setTop(null);
                                        } catch (SQLException ex) {
                                            Logger.getLogger(NouvelleVente.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    });
                                }
                            }
                        }else{
                            vbMessages = new VBox(lEncherePlusHaute,lVotreEnchere);
                        }
                        
                        HBox hbTitledPane=new HBox(tpObjet,vbMessages);
                        hbTitledPane.setStyle("-fx-padding: 10; -fx-background-color: cornsilk;");
                        hbTitledPane.setSpacing(15);
                        vbHistorique.getChildren().add(hbTitledPane);
                        
                        
                    
                    //on créé la ligne d'infos de chaque enchère :
                    lMsgChaqueEnchere= new Label("Montant de l'enchère: " +montantEnchere+" €"+"  Date et heure : "+dateHeure);
                    lMsgChaqueEnchere.setStyle("-fx-padding: 10; -fx-background-color: #C4CBFC;");
                    lMsgChaqueEnchere.setFont(Font.font("Montserra",FontWeight.BOLD,10));
                    vbContenuTitledPane.getChildren().add(lMsgChaqueEnchere);
                }
                spHistorique.setContent(vbHistorique);
                mainPage.setCenter(spHistorique);
                }
            }
        }
    }
    
    public static String returnEnchereUtil(Connection con,String idUtil,String idObjet,VueMain main) throws SQLException{
        try ( Statement st = con.createStatement()) {
            
            try ( ResultSet tlu = st.executeQuery("select * from enchere  where de ="+idUtil+" and sur ="+idObjet+" order by montant desc")) {
                if (tlu.next()){
                    
                    String resultat = tlu.getString(4);
                    return resultat;
                }else{
                    return "0";
                }
                
            }
        }
        
    }
      
    public static float returnPrixPlusHaut(Connection con,VueMain main) throws SQLException {
        try ( Statement st = con.createStatement()) {
            float prixPlusHautEnchere=0;
            float prixPlusHautBase=0;
            try ( ResultSet tlu = st.executeQuery("select * from objets,enchere where enchere.sur=objets.ido and vendeur <> "+main.getInfoSession().getCurrentUserId()+" order by montant desc ")){
                while (tlu.next()){
                    prixPlusHautEnchere = Float.valueOf(tlu.getString(13));
                    String fin = tlu.getString(9);
                    if (gestionBddGUI.tempsRestantMillis(gestionBddGUI.convertStringToDateTime(fin))>0){
                        break;
                    }
                }
                
            }
            try ( ResultSet tlu = st.executeQuery("select * from objets where vendeur <> "+main.getInfoSession().getCurrentUserId()+" order by prixbase desc ")){
                while (tlu.next()){
                    prixPlusHautBase = Float.valueOf(tlu.getString(5));
                    String fin = tlu.getString(9);
                    if (gestionBddGUI.tempsRestantMillis(gestionBddGUI.convertStringToDateTime(fin))>0){
                        break;
                    }
                }
            }
            if (prixPlusHautEnchere>prixPlusHautBase){
                return prixPlusHautEnchere;
            }else{
                return prixPlusHautBase;
            }
        }
    }
       
    public static boolean emailExisting(Connection con,String email) throws SQLException {
        try ( Statement st = con.createStatement()) {
            try ( ResultSet tlu = st.executeQuery("select * from utilisateur where mail ='"+email+"'")){
                if (tlu.next()){
                    return true;
                }else {
                    return false;
                }
            }
        }
    }
    
    public static String returnIdAcheteur(Connection con,String ido) throws SQLException{
        try ( Statement st = con.createStatement()) {
            try ( ResultSet tlu = st.executeQuery("select * from enchere where sur ="+ido+" order by montant desc")){
                tlu.next();
                return tlu.getString(2);
            }
        }
    }
    
    public static boolean wasSold(Connection con,String ido)throws SQLException{
        try ( Statement st = con.createStatement()) {
            try ( ResultSet tlu = st.executeQuery("select * from enchere where sur ="+ido)){
                return tlu.next();
            }
        }
    }
    
    
    //Pas essentiel :
    //image par objet ?
    //roles des utilisateurs
}
