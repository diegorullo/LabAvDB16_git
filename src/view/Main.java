package view;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.util.Version;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import java.io.*;
import java.util.*;

import edu.stanford.nlp.io.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;
import intoxicant.analytics.coreNlp.StopwordAnnotator;

import model.DBEngine;
import sentiment.Anger;

// dblp.Corpus;
//import dblp.Factory;
public class Main {

//	private static Corpus dblp;
//	public static Corpus getDblp() {
//		return dblp;
//	}
    public static void main(String[] argv) throws SQLException {
   
        DBEngine dbe;
        Connection connection;
        connection = DBEngine.getOracleConnection();
        Statement statement = null;
        if (connection != null) {
            System.out.println("Connessione OK!");
            System.out.println("1. Popolamento dizionario Anger (risorse lessicali)");
            TreeMap<String, Integer> dictKeywordSetWithOccurrences = Anger.createAngerDictKeywordSetWithOccurrences(connection);
            // recupera tutti i tweet del corpus Anger, splitta, lemmizza ed elimina le stopwords
            System.out.println("2. Creazione struttura indice basata sui token del corpus tweet Anger");
            TreeMap<String, Integer> tweetAngerKeywordNoStopwordSetWithOccurrences = Anger.createTweetAngerKeywordNoStopwordSetWithOccurrences(connection);
        }
        else {
            System.out.println("Connessione fallita!");
        }
    }
}
