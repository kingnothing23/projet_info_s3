/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.guyem;

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
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * coucousdfgh test merge
 *
 * @author Utilisateur
 */
public class gestionbdd {

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

    public static Connection defautConnect()
            throws ClassNotFoundException, SQLException {
        return connectGeneralPostGres("localhost", 5439, "postgres", "postgres", "pass");
    }

    public static void menu(Connection con) throws SQLException {
        int rep = -1;
        while (rep != 0) {  
            System.out.println("Menu BdD Enchere");
            System.out.println("=============");
            System.out.println("1) créer/recréer la BdD initiale");
            System.out.println("2) liste des utilisateurs");
            System.out.println("3) liste des objets");
            System.out.println("4) ajouter un utilisateur");
            System.out.println("5) ajouter un objet");
            System.out.println("6) ajouter n utilisateurs aléatoires");
            System.out.println("7) placer  une enchere sur  un objet");
            System.out.println("8) liste des encheres");
            System.out.println("9) bilan utilisateur donné");
            System.out.println("10) ajouter une categorie");
            System.out.println("11) afficher les categories ");
            System.out.println("12) filtrer les objets par categorie");
            System.out.println("0) quitter");
            System.out.println("Votre choix ?");
            rep = Lire.i();
            try {
                if (rep == 1) {
                    recreeTout(con);
                } else if (rep == 2) {
                    afficheTousLesUtilisateur(con);
                } else if (rep == 3) {
                    afficheObjets(con);
                } else if (rep == 4) {
                    demandeNouvelUtilisateur(con);
                } else if (rep == 5) {
                    demandeNouvelObjet(con);
                } else if (rep == 6) {
                    System.out.println("création d'utilisateurs 'aléatoires'");
                    System.out.println("combien ?");
                    int combien = Lire.i();
                    for (int i = 0; i < combien; i++) {
                        boolean exist = true;
                        while (exist) {
                            String nom = "U" + ((int) (Math.random() * 10000));
                            String mail = "R" + ((int) (Math.random() * 10000));
                            String prenom = "Aleatoire";
                            String codepostal = "67100";
                            if (!nomUtilisateurExiste(con, nom)) {
                                exist = false;
                                createUtilisateur(con, nom, prenom, "P" + ((int) (Math.random() * 10000)), mail,codepostal);
                            }
                        }

                    }
                } else if (rep == 7) {
                    afficheObjets(con);
                    System.out.println("entrer l'id de  l'objet");
                    int obj = Lire.i();
                    System.out.println("entrer l'id de  l'utilisateur");
                    int utis = Lire.i();
                    System.out.println("le prix de  l objet est :");
                    double pessi = PriceActual(con, obj);
                    System.out.println(pessi);
                    System.out.println("entrer le montant de  votre enchere");
                    float price = Lire.f();
                    while (price <= pessi) {
                        System.out.println(" montant  inferieur ,entrer le montant de  votre enchere");
                        price = Lire.f();
                    }

                    LocalDateTime dateenchere = LocalDateTime.now();

                    CreateEnchere(con, obj, utis, price, dateenchere);
                } else if (rep == 8) {
                    afficheToutesLesEncheres(con);
                } else if (rep == 9) {
                    afficheTousLesUtilisateur(con);
                    System.out.println("entrer l'id de  l'utilisateur");
                    int uti = Lire.i();
                    Bilanutil(con, uti);
                } else if (rep == 10) {
                    System.out.println("entrer le nom de  la categorie :");
                    String name = Lire.S();
                    createcategorie(con, name);
                } else if (rep == 11) {
                    affichecategorie(con);

                }  else if (rep == 12) {
                   ArrayList<Integer>Liste =  catselect(con);
                   if (Liste.size() != 0){
                   catsearch(con,Liste);}
                   
                }          

            } catch (SQLException ex) {
                throw new Error(ex);
            }

        }
        int k = Lire.i();
        System.out.print(k);

    }

    public static void creeSchema(Connection con)
            throws SQLException {
        // je veux que le schema soit entierement créé ou pas du tout
        // je vais donc gérer explicitement une transaction
        con.setAutoCommit(false);
        try ( Statement st = con.createStatement()) {
            // creation des tables
            st.executeUpdate(
                    """
                    create table utilisateur (
                        id integer not null primary key
                        generated always as identity,
                        nom varchar(30) not null,
                        prenom varchar(30) not null,
                        pass varchar(30) not null,
                        mail varchar(30) not null unique,
                        codepostal varchar(30) not null
                        
                    )
                    """);

            st.executeUpdate(
                    """
                    create table objets (
                     ido integer not null primary key 
                     generated always as identity,
                     nom varchar(30) not null,
                     petitedescri varchar(50) not null,
                     longuedescri varchar(300),
                     prixbase real not null ,
                     categorie integer not null,
                     vendeur integer not null,
                     debut timestamp not null,
                     fin timestamp not null
                    )
                    """);
            st.executeUpdate(
                    """
                    create table enchere (
                        ide integer not null primary key 
                        generated always as identity,
                        de integer not null,
                        sur integer not null,
                        montant real not null,
                        date timestamp not null
                    
                    )
                    """);
            st.executeUpdate(
                    """
                    create table categorie (
                        idc integer not null primary key 
                        generated always as identity,
                        nom varchar(45) not null
                    
                    )
                    """);
            // je defini les liens entre les clés externes et les clés primaires
            // correspondantes
            st.executeUpdate("""
                   alter table objets
                        add constraint fk_objets_vendeur
                       foreign key (vendeur) references utilisateur(id),
                      add constraint fk_objets_categorie
                        foreign key (categorie) references categorie(idc)                        
                  """);
            st.executeUpdate("""
                   alter table enchere
                        add constraint fk_enchere_de
                       foreign key (de) references utilisateur(id),
                      add constraint fk_enchere_sur
                        foreign key (sur) references objets(ido)                        
                  """);
            createAllCategories(con);
//           st.executeUpdate(
//                   """
//                   alter table aime
//                      add constraint fk_aime_u2
//                     foreign key (u2) references utilisateur(id)
//                    """);
            // si j'arrive jusqu'ici, c'est que tout s'est bien passé
            // je confirme (commit) la transaction
            con.commit();
            // je retourne dans le mode par défaut de gestion des transaction :
            // chaque ordre au SGBD sera considéré comme une transaction indépendante
            con.setAutoCommit(true);
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        }
    }
    
    public static void createAllCategories(Connection con) throws SQLException{
        List<String> listeCat= Arrays.asList("Art et Collection","Auto & Moto","High-Tech","Maison & Jardin","Jouets & Jeux","Culture & Loisirs","Mode") ;
        for (int i =0;i<listeCat.size();i++){
            createcategorie(con,listeCat.get(i));
        }
    }
    
    public static void deleteSchema(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {
            // pour être sûr de pouvoir supprimer, il faut d'abord supprimer les liens
            // puis les tables
            // suppression des liens
            try {
                st.executeUpdate(
                        """
                    alter table objets
                        drop constraint fk_objets_vendeur,
                        drop constraint fk_objets_categorie
                             """);
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate(
                        """
                    alter table enchere
                        drop constraint fk_enchere_de,
                        drop constraint fk_enchere_sur
                    """);
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            // je peux maintenant supprimer les tables
            try {
                st.executeUpdate(
                        """
                    drop table objets
                    """);
                // je defini les liens entre les clés externes et les clés primaires
                // correspondantes
            } catch (SQLException ex) {
                // nothing to do : maybe the table was not created
            }
            try {
                st.executeUpdate(
                        """
                    drop table utilisateur
                    """);
            } catch (SQLException ex) {
                // nothing to do : maybe the table was not created
            }
            try {
                st.executeUpdate(
                        """
                    drop table enchere
                    """);
            } catch (SQLException ex) {
                // nothing to do : maybe the table was not created
            }
            try {
                st.executeUpdate(
                        """
                    drop table categorie
                    """);
            } catch (SQLException ex) {
                // nothing to do : maybe the table was not created
            }
        }
    }
    
   public static int connectuser(Connection con) throws SQLException  {
    System.out.println("entrer votre nom d'utilisateur :");
    String name = Lire.S();
    System.out.println("entrer votre mdp :");
    String mdp = Lire.S();
    int connectyes ;
    try ( Statement st = con.createStatement()) {
            String query = "select count(1) from utilisateur where nom like '" + name+"' and pass like '"+mdp+"' group by pass";
            try ( ResultSet tlu = st.executeQuery(query)) {
                if (tlu.next() == false){
                    connectyes = 0;
                } else {
                
                connectyes = tlu.getInt(1);}
            }
        }
    while (connectyes != 1){
       System.out.println(" pseudo ou mdp incorrect ");
       System.out.println("entrer votre nom d'utilisateur :");
     name = Lire.S();
    System.out.println("entrer votre mdp :");
     mdp = Lire.S();
        try ( Statement st = con.createStatement()) {
            String query = "select count(1) from utilisateur where nom like '" + name+"' and pass like '"+mdp+"' group by pass";
            try ( ResultSet tlu = st.executeQuery(query)) {
                if (tlu.next() == false){
                    connectyes = 0;
                } else {
                
                connectyes = tlu.getInt(1);}
            }
        }
        
        
        
    }
    System.out.println("Bienvenue "+name);
      int userid;
    try ( Statement st = con.createStatement()) {
          String query = "select id from utilisateur where nom like '" + name+"' and pass like '"+mdp+"'";
          try ( ResultSet tlu = st.executeQuery(query)) {
             tlu.next();
              userid = tlu.getInt(1);
          }
      } 
    
   
    
    return userid;
        }
   
    public static void menunormal (Connection con, int uti) throws SQLException{
         int rep = -1;
        while (rep != 0) {
            System.out.println("Menu BdD Enchere");
            System.out.println("=============");
            
            System.out.println("2) liste des utilisateurs");
            System.out.println("3) liste des objets");
            System.out.println("7) ajouter un objet");
            
            System.out.println("4) placer  une enchere sur  un objet");
            
            System.out.println("5) bilan");
           
            System.out.println("6) afficher les categories ");
            System.out.println("0) quitter");
            System.out.println("Votre choix ?");
            rep = Lire.i();
        try {
                if (rep == 1) {
                    recreeTout(con);
                } else if (rep == 2) {
                    afficheTousLesUtilisateur(con);
                } else if (rep == 3) {
                    afficheObjets(con);
                    } else if (rep == 4) {
                        afficheObjets(con);
                    System.out.println("entrer l'id de  l'objet");
                    int obj = Lire.i();
                    System.out.println("entrer l'id de  l'utilisateur");
                    int utis = Lire.i();
                    System.out.println("le prix de  l objet est :");
                    double pessi = PriceActual(con, obj);
                    System.out.println(pessi);
                    System.out.println("entrer le montant de  votre enchere");
                    float price = Lire.f();
                    while (price <= pessi) {
                        System.out.println(" montant  inferieur ,entrer le montant de  votre enchere");
                        price = Lire.f();
                    }

                    LocalDateTime dateenchere = LocalDateTime.now();

                    CreateEnchere(con, obj, utis, price, dateenchere);
                } else if (rep == 5) {
                    Bilanutil(con ,uti); }
                else if (rep == 6) {
                    affichecategorie(con);
                } else if (rep == 7) {
                    demandeNouvelObjet(con);
                }
    }catch (SQLException ex) {
                throw new Error(ex);
            }
    
        }
    
    
    
    }   
    
    
    

    public static void main(String[] args) {

        try {
            Connection lol = defautConnect();
            System.out.println("Connexion reussie");
           // int userid =connectuser(lol);
            //menunormal(lol ,userid);
            menu(lol);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(test2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(test2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static boolean nomUtilisateurExiste(Connection con, String nom) throws SQLException {
        try ( PreparedStatement pst = con.prepareStatement(
                "select id from utilisateur where nom = ?")) {
            pst.setString(1, nom);
            ResultSet res = pst.executeQuery();
            return res.next();

        }
    }

    private static int createUtilisateur(Connection con, String nom, String prenom,String pass, String mail,String codepostal) throws SQLException {
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

    private static void afficheTousLesUtilisateur(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {

            try ( ResultSet tlu = st.executeQuery("select * from utilisateur")) {

                System.out.println("liste des utilisateurs :");
                System.out.println("------------------------");
                // ici, on veut lister toutes les lignes, d'où le while
                while (tlu.next()) {

                    int id = tlu.getInt("id");
                    // ou par son numéro (la première colonne a le numéro 1)
                    String nom = tlu.getString(2);
                    String prenom = tlu.getString(3);
                    String pass = tlu.getString(4);
                    String mail = tlu.getString(5);
                    System.out.println(id + " : " + nom + prenom + "(" + pass + ") mail :" + mail);
                }
            }
        }
    }

    private static void recreeTout(Connection con) throws SQLException {
        try {
            deleteSchema(con);
//            System.out.println("ancien schéma supprimé");
        } catch (SQLException ex) {
//            System.out.println("pas de suppression d'un ancien schéma");
        }
        creeSchema(con);
    }

    private static void afficheObjets(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {

            try ( ResultSet tlu = st.executeQuery("select * from objets")) {

                System.out.println("liste des objets :");
                System.out.println("------------------------");
                // ici, on veut lister toutes les lignes, d'où le while
                while (tlu.next()) {

                    int id = tlu.getInt("ido");
                    // ou par son numéro (la première colonne a le numéro 1)
                    String nom = tlu.getString(2);
                    String petitedescri = tlu.getString(3);
                    String longuedescri =tlu.getString(4);
                    String prixbase = tlu.getString(5);
                    String categorie = tlu.getString(6);
                    String vendeur = tlu.getString(7);
                    String debut = tlu.getString(8);
                    String fin = tlu.getString(9);
                    System.out.println(id + " : " + nom + " prix(" + prixbase + ") vendeur :" + vendeur + " petite description : " + petitedescri + " ,longue description : " +
                            longuedescri +", debut de l'enchere :" + debut + ", fin de l'enchere :" + fin);
                }
            }
        }
    }

    private static void demandeNouvelObjet(Connection con) throws SQLException {
        boolean ok = true;
        while (ok) {
            System.out.println("--- creation nouvel objet");
            System.out.println("nom");
            String nom = Lire.S();
            System.out.println("prixbase");
            double prix = Lire.f();
            System.out.println("petite description");
            String petitedesc;
            petitedesc = Lire.S();
            System.out.println("Longue description :");
            String longuedesc = Lire.S();
            System.out.println("id vendeur");
            int idv = Lire.i();
            System.out.println("categorie ");
            affichecategorie(con);
            System.out.println("0 pour créer une nouvelle categorie ");
            int idc = Lire.i();
            if (idc == 0) {
                System.out.println("Veuillez entrer le nom de la categorie");
                String nomm = Lire.S();
                idc = createcategorie(con, nomm);
            }
            System.out.println("entrer la date de debut de vente");
            LocalDateTime debutvente = enterdate();
            System.out.println("entrer la date de fin de vente");
            LocalDateTime finvente = enterdate();

            createObjets(con, nom, prix, petitedesc, longuedesc, idc, idv, debutvente, finvente);
            ok = false;
        }
    }
   private static LocalDateTime enterdate() {
        LocalDateTime datetime;
        System.out.println("entrer la date yyyy-mm-jj");
        String entereddate = Lire.S();
        LocalDate date = LocalDate.parse(entereddate);

        System.out.println("entrer HH:mm:SS ");
        DateTimeFormatter parseFormate = DateTimeFormatter.ofPattern("H:mm:ss");
        Scanner scs = new Scanner(System.in);
        String timeString = scs.nextLine();
        LocalTime time = LocalTime.parse(timeString, parseFormate);
        System.out.println(time);
        datetime = date.atTime(time);
        System.out.println(datetime);

        return datetime;
    }
    private static void demandeNouvelUtilisateur(Connection con) throws SQLException {
        boolean ok = true;
        while (ok) {
            System.out.println("--- creation nouvel utilisateur");
            System.out.println("nom");
            String nom = Lire.S();
            System.out.println("prenom");
            String prenom = Lire.S();
            System.out.println("pass");
            String pass = Lire.S();
            System.out.println("mail");
            String mail = Lire.S();
            System.out.println("code postal :");
            String codepostal = Lire.S();
            ok = nomUtilisateurExiste(con, nom);
            if (ok) {
                System.out.println("ce nom existe deja, choisissez en un autre");
            } else {
                createUtilisateur(con, nom, prenom, pass, mail,codepostal);
            }
            ok = false;
        }

    }

    private static int createObjets(Connection con, String nom, double prixbase, String petitedesc, String longuedesc, int idc, int idv, LocalDateTime debutvente, LocalDateTime finvente) throws SQLException {
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

    private static double PriceActual(Connection con, int obj) throws SQLException {
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
                    System.out.println("prix actuel :");
                    tlu.next();
                    actual = tlu.getFloat(1);
                }
            }

        } else {

//            try ( Statement st = con.createStatement()) {
//                String query = "select max(montant) from enchere where enchere.sur=" + obj;
//                try ( ResultSet tlu = st.executeQuery(query)) {
//                    System.out.println("prix actuel :");
//                    actual = tlu.getFloat(1);
//
//                    System.out.println(actual);
//
//                }
//            }
            try ( PreparedStatement st = con.prepareStatement("select max(montant) from enchere where enchere.sur=?")) {
                st.setInt(1, obj);
                try ( ResultSet tlu = st.executeQuery()) {
                    System.out.println("prix actuel :");
                    tlu.next();
                    actual = tlu.getFloat(1);

                    System.out.println(actual);

                }
            }

        }
        return actual;
    }

    private static int CreateEnchere(Connection con, int obj, int utis, float price, LocalDateTime dateenchere) throws SQLException {

        try ( PreparedStatement pst = con.prepareStatement(
                """
                insert into enchere (de,sur,montant,date) values (?,?,?,?)
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, utis);
            pst.setInt(2, obj);
            pst.setFloat(3, price);

            Timestamp timestamp = Timestamp.valueOf(dateenchere);
            pst.setTimestamp(4, timestamp);
            pst.executeUpdate();

            // je peux  alors récupérer les clés créées comme un result set :
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
    private static void research(Connection con) throws SQLException {
        System.out.println("entrez le nom de  l'objet ");
        String nom = Lire.S();
        ArrayList<Integer> result = new ArrayList<Integer>();
        try ( Statement st = con.createStatement()) {
            String queryssg = """
                                  select ido from objets where nom like '%
                                  """ + nom + "%'";

            try ( ResultSet tlu = st.executeQuery(queryssg)) {
                while (tlu.next()) {
                    int id = tlu.getInt("ido");
                    // ou par son numéro (la première colonne a le numéro 1)
                    String nomb = tlu.getString(2);
                    String descri = tlu.getString(3);
                    String prixbase = tlu.getString(4);
                    String categorie = tlu.getString(5);
                    String vendeur = tlu.getString(6);
                    String debut = tlu.getString(7);
                    String fin = tlu.getString(8);
                    System.out.println(id + " : " + nomb + " prix(" + prixbase + ") vendeur :" + vendeur + " description : " + descri + ", debut de l'enchere :" + debut + ", fin de l'enchere :" + fin);

                }

            }
        }
    }
        
        
        
        
        
        
        
        
        
        
    
    private static void afficheToutesLesEncheres(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {

            try ( ResultSet tlu = st.executeQuery("""
                select ide,montant,sur, (SELECT nom from objets where objets.ido=enchere.sur) as objet,
                    de, (SELECT nom from utilisateur where utilisateur.id=enchere.de)as acheteur ,date
                    from enchere  """)) {

                System.out.println("liste des enchere :");
                System.out.println("------------------------");
                // ici, on veut lister toutes les lignes, d'où le while
                while (tlu.next()) {

                    int id = tlu.getInt("ide");
                    //ou par son numéro (la première colonne a le numéro 1)
                    String nom = tlu.getString(1);
                    String prixbase = tlu.getString(2);
                    String vendeurid = tlu.getString(5);
                    String vendeur = tlu.getString(6);
                    String objetid = tlu.getString(3);
                    String objet = tlu.getString(4);
                    String date = tlu.getString(7);
                    System.out.println("id : " + id + " montant :"
                            + prixbase + " sur :" + objetid + " " + objet + " de " + vendeurid + " " + vendeur + " ,date " + date);
                }
            }
        }
    }
    private static ArrayList<Integer> catselect(Connection con) throws SQLException{
        ArrayList<Integer> liste = new ArrayList<Integer>();
        try ( Statement st = con.createStatement()) {
         String query ="select * from categorie";
            try ( ResultSet tlu = st.executeQuery(query)) {
               while (tlu.next()) {
               int idc = tlu.getInt(1);
               String nom = tlu.getString(2);
                   System.out.println("id "+idc+" nom: "+nom);
                   
               }
            }
            
        }
        int yesno=-1;
        System.out.println("entrez l'id des categorie que vous souhaiter selectionnée (tapez 0 pour mettre fin à la selection ");
        yesno=Lire.i();
        while (yesno!=0){
          liste.add(yesno);
            System.out.print("catégorie suivante :");
                yesno=Lire.i();
            
            
            
        }
        if (liste!= null){
           return liste; 
        }else {
        return liste;}
        
        
    }
    
    
private static void catsearch(Connection con,ArrayList<Integer> liste) throws SQLException{
    
    ArrayList obj = new ArrayList<Integer>();
    String cta ;
   int inter ;
   int i ; 
    
    
    
    System.out.println(liste.size());
        
     for( i=0 ; i< liste.size();) {
          try ( Statement stt = con.createStatement()) {    
         String queri = "select * from objets where categorie='"+liste.get(i)+"'";
         try ( ResultSet tlu = stt.executeQuery(queri)) {
              while (tlu.next()==true) {
                 String nom = tlu.getString(2);
                    String petitedescri = tlu.getString(3);
                    String longuedescri =tlu.getString(4);
                    String prixbase = tlu.getString(5);
                    String categorie = tlu.getString(6);
                    String vendeur = tlu.getString(7);
                    String debut = tlu.getString(8);
                    String fin = tlu.getString(9);
                    int id = tlu.getInt("ido");
                    System.out.println(id + " : " + nom + " prix(" + prixbase + ") vendeur :" + vendeur + " petite description : " + petitedescri + " ,longue description : " +
                            longuedescri +", debut de l'enchere :" + debut + ", fin de l'enchere :" + fin+"categorie"+ categorie);
                         
              }
             
         }
         
     
        
          }
    
        
          }
        
    
    
    }
        
          
private static void Bilanutil(Connection con, int uti) throws SQLException {
        
      
        try ( Statement st = con.createStatement()) {
            String query = """
                           select ide , montant,(SELECT nom from objets where objets.ido=enchere.sur) as objet ,
                                                                             (SELECT prixbase from objets where objets.ido=enchere.sur) as prixbase ,
                                                                             
                                                                             (SELECT debut from objets where objets.ido=enchere.sur) as debut ,
                                                                             
                                                                             (SELECT fin from objets where objets.ido=enchere.sur) as fin,sur 
                                                                                from enchere where enchere.de=""" + uti;
            try ( ResultSet tlu = st.executeQuery(query)) {
               
                while (tlu.next()) {
                     String catsn ;
                     String catsid ;
                     float max ;
                     String usmaxid ;
                     String usmaxn ;
                    int id = tlu.getInt("ide");
                    //ou par son numéro (la première colonne a le numéro 1)
                    String prixbase = tlu.getString(4);
                    String montant = tlu.getString(2);
                    String fin = tlu.getString(5);
                    
                    String objet = tlu.getString(3);
                    String debut = tlu.getString(6);
                    int ido = tlu.getInt(7);
                    try ( Statement sst = con.createStatement()) {
                      String   querysu = """
                                  select categorie from objets where ido =
                                  """+  ido ;
                                 
                        try ( ResultSet tlusa = sst.executeQuery(querysu)) {
                            
                            tlusa.next();
                            catsid = tlusa.getString(1);
                            System.out.println(catsid);
                        }}
                    try ( Statement ss = con.createStatement()) {
                      String   querys = """
                                  select nom from categorie where idc =
                                  """+  catsid ;
                                 
                        try ( ResultSet tlus = ss.executeQuery(querys)) {
                            
                            tlus.next();
                            catsn = tlus.getString(1);
                            System.out.println(catsn);
                        }}
                       try ( Statement sq = con.createStatement()) {
                      String   queryss = """
                                  select max(montant) from enchere where sur=
                                  """+  ido ;
                                 
                        try ( ResultSet tlusq = sq.executeQuery(queryss)) {
                           tlusq.next();
                            max = tlusq.getFloat(1);
                            System.out.println(max);
                        } 
                        
                    }
//                      try ( Statement sqs = con.createStatement()) {
//                      String   querysst = """
//                                  select de from enchere where montant=
//                                  """+ max ;
//                                 
//                        try ( ResultSet tlust = sqs.executeQuery(querysst)) {
//                           
//                            usmaxid = tlust.getString("de");
//                            System.out.println(usmaxid);
//                        } 
//                        
//                    } 
 try ( Statement sq = con.createStatement()) {
                      String   queryss = """
                                  select de from enchere where montant=
                                  """+  max ;
                                 
                        try ( ResultSet tlusq = sq.executeQuery(queryss)) {
                           tlusq.next();
                            usmaxid = tlusq.getString(1);
                            System.out.println(usmaxid);
                        } 
                        
                    }
                       try ( Statement sqt = con.createStatement()) {
                      String   queryssg = """
                                  select nom from utilisateur where id=
                                  """+  usmaxid ;
                                 
                        try ( ResultSet tlut = sqt.executeQuery(queryssg)) {
                          tlut.next();
                            usmaxn = tlut.getString(1);
                            System.out.println(usmaxn);
                        } 
                        
                    }
                    System.out.println("id : " + id + " montant de votre enchere :"
                            + montant +   "  ojbet : " + objet + " prixbase" + prixbase + " debut   " + debut + "fin "+ fin + "montant maximal "+ max +" user "+usmaxn);
                    
                
            
        }
    }
    }
    }

    private static int createcategorie(Connection con, String name) throws SQLException {
        try ( PreparedStatement pst = con.prepareStatement(
                """
                insert into categorie (nom) values (?)
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, name);

            pst.executeUpdate();

            // je peux  alors récupérer les clés créées comme un result set :
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

    private static void affichecategorie(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {

            try ( ResultSet tlu = st.executeQuery("select * from categorie")) {

                System.out.println("liste des categories :");
                System.out.println("------------------------");
                // ici, on veut lister toutes les lignes, d'où le while
                while (tlu.next()) {

                    int id = tlu.getInt("idc");
                    // ou par son numéro (la première colonne a le numéro 1)
                    String nom = tlu.getString(2);

                    System.out.println(id + " : " + nom);
                }
            }
        }
    }
    private static void multisearch(Connection con ) throws SQLException{
        System.out.println("entrez le nom de  l'objet ");
        String nom = Lire.S();
        ArrayList<String> motcle = new ArrayList<String>() ;
        int i ;
        System.out.println("entrez un mot clé à chercher :");
        motcle.add(Lire.S());
        System.out.println(" nouveau mot clé ? ( 0 pour non , autre nombre  pour oui");
        i=Lire.i();
        while(i!= 0){
          System.out.println("entrez un mot clé à chercher :");
        motcle.add(Lire.S());
        System.out.println(" nouveau mot clé ? ( 0 pour non , autre nombre  pour oui");
        i=Lire.i();  
            
            
            
            
            
        }
        
        if (!motcle.isEmpty()){
        String query = "select * from objets where nom like '%"+nom+"%'";
          query = query+ " or longuedescri like '%"+motcle.get(0)+"%'";
       
          if(motcle.size()!= 1){
          for (int g = 1 ; g<motcle.size();g++){
         query = query+ " or longuedescri like '%"+motcle.get(i)+"%'";
            
            
        }
       }
          
          try ( Statement st = con.createStatement()) {
            try ( ResultSet tlu = st.executeQuery(query)) {
                while(tlu.next()) {
            
              int id = tlu.getInt("ido");
                    // ou par son numéro (la première colonne a le numéro 1)
                    String noms = tlu.getString(2);
                    String petitedescri = tlu.getString(3);
                    String longuedescri =tlu.getString(4);
                    String prixbase = tlu.getString(5);
                    String categorie = tlu.getString(6);
                    String vendeur = tlu.getString(7);
                    String debut = tlu.getString(8);
                    String fin = tlu.getString(9);
                    System.out.println(id + " : " + noms + " prix(" + prixbase + ") vendeur :" + vendeur + " petite description : " + petitedescri + " ,longue description : " +
                            longuedescri +", debut de l'enchere :" + debut + ", fin de l'enchere :" + fin);
              
          } 
                
                
                
                
                
                
                
            }
        }
       }
        
        
    }
    private static void searchdescri(Connection con) throws SQLException{
        ArrayList<String> motcle = new ArrayList<String>() ;
        int i ;
        System.out.println("entrez un mot clé à chercher :");
        motcle.add(Lire.S());
        System.out.println(" nouveau mot clé ? ( 0 pour non , autre nombre  pour oui");
        i=Lire.i();
        while(i!= 0){
          System.out.println("entrez un mot clé à chercher :");
        motcle.add(Lire.S());
        System.out.println(" nouveau mot clé ? ( 0 pour non , autre nombre  pour oui");
        i=Lire.i();  
            
            
            
            
            
        }
       if (motcle.size()!= 0){
        String query = "select * from objets where";
          query = query+ "longuedescri like '%"+motcle.get(0)+"%'";
       
          if(motcle.size()!= 1){
          for (int g = 1 ; g<motcle.size();g++){
         query = query+ " or longuedescri like '%"+motcle.get(i)+"%'";
            
            
        }
       }
          
          try ( Statement st = con.createStatement()) {
            try ( ResultSet tlu = st.executeQuery(query)) {
                while(tlu.next()) {
            
              int id = tlu.getInt("ido");
                    // ou par son numéro (la première colonne a le numéro 1)
                    String nom = tlu.getString(2);
                    String petitedescri = tlu.getString(3);
                    String longuedescri =tlu.getString(4);
                    String prixbase = tlu.getString(5);
                    String categorie = tlu.getString(6);
                    String vendeur = tlu.getString(7);
                    String debut = tlu.getString(8);
                    String fin = tlu.getString(9);
                    System.out.println(id + " : " + nom + " prix(" + prixbase + ") vendeur :" + vendeur + " petite description : " + petitedescri + " ,longue description : " +
                            longuedescri +", debut de l'enchere :" + debut + ", fin de l'enchere :" + fin);
              
          } 
                
                
                
                
                
                
                
            }
        }
       }
       
        
       
       
       
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
private static void filprice(Connection con) throws SQLException{
    System.out.println("entrer la valeur de prix minimale(0 pour pas de  limite");
    Float min = Lire.f();
    
    ArrayList<Integer> objets = new ArrayList<Integer>() ;
    
    System.out.println("entrer la valeur de prix maximale(0 si pas de  limite");
    double max = Lire.f();
    if(max==0){
        max = 1000000000;
    }
    try ( Statement st = con.createStatement()) {
        String   query = " select ido from objets";
       try ( ResultSet tlu = st.executeQuery(query)) {
          while(tlu.next()) {
            objets.add(tlu.getInt(1));
              
              
          }
           
           
           
       } 
       double lol;
        for(int i =0 ; i<objets.size();i++){
        int m = Integer.parseInt(objets.get(i).toString());
        
            lol= PriceActual(con, m);
            if(min <=lol && lol<=max){
                try ( Statement sts = con.createStatement()) {
                    String   querys = " select * from objets";
                    try ( ResultSet tlu = st.executeQuery(query)) {
          while(tlu.next()) {
            
              int id = tlu.getInt("ido");
                    // ou par son numéro (la première colonne a le numéro 1)
                    String nom = tlu.getString(2);
                    String petitedescri = tlu.getString(3);
                    String longuedescri =tlu.getString(4);
                    String prixbase = tlu.getString(5);
                    String categorie = tlu.getString(6);
                    String vendeur = tlu.getString(7);
                    String debut = tlu.getString(8);
                    String fin = tlu.getString(9);
                    System.out.println(id + " : " + nom + " prix(" + prixbase + ") vendeur :" + vendeur + " petite description : " + petitedescri + " ,longue description : " +
                            longuedescri +", debut de l'enchere :" + debut + ", fin de l'enchere :" + fin);
              
          }
                    
                    
                    
                    
                    
                }
                
            }
            
        }
        
        
        
        
        
        
    }
    
    
    
    
}
 

    
    
}
}