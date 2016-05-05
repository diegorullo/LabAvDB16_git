
package model;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;



//import dblp.Author;
//import dblp.Corpus;
//import dblp.Paper;

public class DBEngine {
	private  static Connection conn;
	private  Statement stmt;
	private  static String dbAddress = "jdbc:oracle:thin:@localhost:1521:xe";
	private  static String username = "sp127567";
	private  static  String password = "password";
//      private static final String dbAddress = "jdbc:oracle:thin:@laboracle.educ.di.unito.it:1521:oralab";
//      private static final String username = "sp127567";
//      private static final String password = "password";
	
	private  DBEngine dbe;
	
	/**
	 * inizializza la connessione al db
	 */
	public static Connection getOracleConnection() throws SQLException {
                System.out.println("-------- TEST connessione Oracle JDBC  ------");
                try {
                    Class.forName("oracle.jdbc.driver.OracleDriver");
                } 
                catch (ClassNotFoundException e) {
                    System.out.println("JDBC Driver non trovati!");
                    e.printStackTrace();
                    return null;
                }
                System.out.println("Oracle JDBC Driver Registrati!");                
                if (conn == null) {
                    try {
			conn = DriverManager.getConnection(dbAddress, username, password);
                    } catch (SQLException e) {
                        System.out.println("Connessione fallita! verificare");
                        e.printStackTrace();
                        return null;
                    }   
		} 
                return conn;
	}
	
	/**
	 * chiude la connessione al db
	 */
	public void shutdown() throws SQLException {
		if(conn!=null)
			conn.close();
	}	

	
	/**
	 * istanzia un paper a partire dal relativo id
	 * @param paperID   id del paper
	 * @return paper	paper
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws Exception 
	 */
        
        /*
	public Paper newPaper(int paperID) throws SQLException, IOException {
		final String title;
		final int year;
		final String publisher;
		final String paperAbstract;
		final ArrayList<String> keywords;
		final ArrayList<String> titlesKeywords;
		final ArrayList<String> authorsNames = new ArrayList<String>();
		final ArrayList<Integer> authors = new ArrayList<Integer>();
		String query = "SELECT authors.*,papers.* FROM authors,papers,writtenby WHERE papers.paperid = "
				+ paperID
				+ " AND writtenby.personid=authors.personid AND writtenby.paperid=papers.paperid;";
		stmt = (Statement) conn.createStatement();		
		ResultSet res = stmt.executeQuery(query);
		
		res.next();
		title = res.getString("title");
		year = res.getInt("year");
		publisher = res.getString("publisher");
		paperAbstract = res.getString("abstract");
		
		keywords = TextProcessor.removeStopWordsAndStem(paperAbstract);	    
		titlesKeywords = TextProcessor.removeStopWordsAndStem(title);
		
		authorsNames.add(res.getString("name"));
		authors.add(res.getInt("personid"));
		
		while(res.next()) {
			authorsNames.add(res.getString("name"));
			authors.add(res.getInt("personid"));	
		}
		
		Collections.sort(authorsNames);
		Collections.sort(authors);
		Collections.sort(keywords);
		Collections.sort(titlesKeywords);
		
		Paper p = new Paper(paperID, title, year, publisher, paperAbstract, authorsNames, authors, keywords, titlesKeywords);	
		return p;
	}
	*/
        
        
	/**
	 * istanzia un autore a partire dal relativo id
	 * @param personID   id dell'autore
	 * @return author	 autore
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws Exception 
	 */
        
        /*
	public Author newAuthor(int personID) throws SQLException, IOException {
		final ArrayList<Paper> papers = new ArrayList<Paper>();
		final String name;
		
		String query = "SELECT authors.name,papers.paperid FROM authors left outer join writtenby on writtenby.personid=authors.personid left outer join papers on  writtenby.paperid=papers.paperid where authors.personid = " + personID;
		stmt = (Statement) conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);		
		ResultSet res = stmt.executeQuery(query);
		
		if(res.next()) {			
			name = res.getString("name");
			//Questo caso cattura l'eventualita' che esista un author senza papers
			//e quindi la query restituisce paperid = null
			if(res.getInt("paperid") != 0) {
				papers.add(newPaper(res.getInt("paperid")));
			}
			while(res.next()) {
				int id = res.getInt("paperid");
				Paper p = newPaper(id);
				papers.add(p);
			}
		}
		else throw new SQLException("An author with paperID " + personID + " is not in the DB.");

		Collections.sort(papers);
		
		Author a = new Author(personID, name, papers);	
		return a;
	}
	*/
        
        
	/**
	 * istanzia il corpus popolando:
	 * - l'elenco di tutti gli autori del corpus stesso
	 * - l'elenco di tutti i papers 
	 * - la cardinalità, numero di papers del corpus.
	 * 
	 * @return corpus	corpus con autori, papers e cardinalità
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws Exception 
	 */
        
        /*
	public Corpus newCorpus() throws SQLException, IOException {
		final ArrayList<Author> authors = new ArrayList<Author>();
		final ArrayList<Paper> papers = new ArrayList<Paper>();
		final int cardinality;		
				
		//String queryA = "SELECT personid FROM authors;";
		String queryA = "SELECT personid FROM authors WHERE personid IN (SELECT distinct personid FROM writtenby);";
		String queryP = "SELECT paperid FROM papers;";
		String queryC = "SELECT COUNT(*) FROM papers;";

		stmt = (Statement) conn.createStatement();
		ResultSet resA = stmt.executeQuery(queryA);			

		while(resA.next()) {
			authors.add(newAuthor(resA.getInt("personid")));
		}		
		
		ResultSet resP = stmt.executeQuery(queryP);
		while(resP.next()) {
			papers.add(newPaper(resP.getInt("paperid")));
		}
		
		ResultSet resC = stmt.executeQuery(queryC);			
		resC.next();
		cardinality = resC.getInt(1);

		Collections.sort(authors);
		Collections.sort(papers);
		
		Corpus corpus = new Corpus(authors, papers, cardinality);
		return corpus;
	}
	*/
}

