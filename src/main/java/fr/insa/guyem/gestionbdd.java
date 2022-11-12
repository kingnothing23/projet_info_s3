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
                            if (!nomUtilisateurExiste(con, nom)) {
                                exist = false;
                                createUtilisateur(con, nom, "P" + ((int) (Math.random() * 10000)), mail);
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
                    float pessi = PriceActual(con, obj);
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
                        pass varchar(30) not null,
                        mail varchar(30) not null
                        
                    )
                    """);

            st.executeUpdate(
                    """
                    create table objets (
                     ido integer not null primary key 
                     generated always as identity,
                     nom varchar(30) not null,
                     descri varchar(100),
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

    public static void main(String[] args) {

        try {
            Connection lol = defautConnect();
            System.out.println("Connexion reussie");
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

    private static int createUtilisateur(Connection con, String nom, String pass, String mail) throws SQLException {
        try ( PreparedStatement pst = con.prepareStatement(
                """
                insert into utilisateur (nom,pass,mail) values (?,?,?)
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, nom);
            pst.setString(2, pass);
            pst.setString(3, mail);
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
                    String pass = tlu.getString(3);
                    String mail = tlu.getString(4);
                    System.out.println(id + " : " + nom + " (" + pass + ") mail :" + mail);
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
                    String descri = tlu.getString(3);
                    String prixbase = tlu.getString(4);
                    String categorie = tlu.getString(5);
                    String vendeur = tlu.getString(6);
                    String debut = tlu.getString(7);
                    String fin = tlu.getString(8);
                    System.out.println(id + " : " + nom + " prix(" + prixbase + ") vendeur :" + vendeur + " description : " + descri + ", debut de l'enchere :" + debut + ", fin de l'enchere :" + fin);
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
            System.out.println("description");
            String desc;
            desc = Lire.S();
            System.out.println("id vendeur");
            int idv = Lire.i();
            System.out.println("categorie ");
            affichecategorie(con);
            System.out.println("0 pour créer une nouvelle categorie ");
            int idc = Lire.i();
            if (idc == 0) {
                String nomm = Lire.S();
                idc = createcategorie(con, nomm);
            }
            System.out.println("entrer la date de debut de vente");
            LocalDateTime debutvente = enterdate();
            System.out.println("entrer la date de fin de vente");
            LocalDateTime finvente = enterdate();

            createObjets(con, nom, prix, desc, idc, idv, debutvente, finvente);
            ok = false;
        }
    }

    private static void demandeNouvelUtilisateur(Connection con) throws SQLException {
        boolean ok = true;
        while (ok) {
            System.out.println("--- creation nouvel utilisateur");
            System.out.println("nom");
            String nom = Lire.S();
            System.out.println("pass");
            String pass = Lire.S();
            System.out.println("mail");
            String mail = Lire.S();
            ok = nomUtilisateurExiste(con, nom);
            if (ok) {
                System.out.println("ce nom existe deja, choisissez en un autre");
            } else {
                createUtilisateur(con, nom, pass, mail);
            }
            ok = false;
        }

    }

    private static int createObjets(Connection con, String nom, double prixbase, String desc, int idc, int idv, LocalDateTime debutvente, LocalDateTime finvente) throws SQLException {
        try ( PreparedStatement pst = con.prepareStatement(
                """
                insert into objets (nom,descri,prixbase,categorie,vendeur,debut,fin) values (?,?,?,?,?,?,?)
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, nom);
            pst.setDouble(3, prixbase);
            pst.setString(2, desc);
            pst.setInt(4, idc);
            pst.setInt(5, idv);
            Timestamp timestamp = Timestamp.valueOf(debutvente);
            Timestamp timestampe = Timestamp.valueOf(finvente);
            pst.setTimestamp(6, timestamp);
            pst.setTimestamp(7, timestampe);
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

    private static float PriceActual(Connection con, int obj) throws SQLException {
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

   private static int createcategorie(Connection con ,String name) throws SQLException {
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
}
