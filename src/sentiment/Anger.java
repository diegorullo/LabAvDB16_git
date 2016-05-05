/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentiment;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import intoxicant.analytics.coreNlp.StopwordAnnotator;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import org.apache.lucene.analysis.StopAnalyzer;

/**
 *
 * @author Administrator
 */
public class Anger {
    private static Statement statement;
    public static TreeMap<String, Integer> createAngerDictKeywordSetWithOccurrences(Connection connection) throws SQLException {
        // popolamento indice (hasmmap locale)

        TreeMap<String, Integer> dictKeywordSetWithOccurrences = new TreeMap<String, Integer>();
        String selectTableSQL = "SELECT * from LEXSRC_EMOSN_ANGER";
        dictKeywordSetWithOccurrences = addLexiconSourcesToDictKeywordSetWithOccurrences(dictKeywordSetWithOccurrences, connection, selectTableSQL);               
        selectTableSQL = "SELECT * from LEXSRC_NRC_ANGER";
        dictKeywordSetWithOccurrences = addLexiconSourcesToDictKeywordSetWithOccurrences(dictKeywordSetWithOccurrences, connection, selectTableSQL);               
        selectTableSQL = "SELECT * from LEXSRC_SENTISENSE_ANGER";
        dictKeywordSetWithOccurrences = addLexiconSourcesToDictKeywordSetWithOccurrences(dictKeywordSetWithOccurrences, connection, selectTableSQL);               

        return dictKeywordSetWithOccurrences;
        

    }
    
    private static TreeMap<String, Integer> addLexiconSourcesToDictKeywordSetWithOccurrences(TreeMap<String, Integer> dict, Connection connection, String selectTableSQL) throws SQLException {               
        try {

            Statement statement = connection.createStatement();
            System.out.println(selectTableSQL);
            // esegue lo statement SQL 
            ResultSet rs = statement.executeQuery(selectTableSQL);

            //inizializzo strutture per indice keyword sentimento (dizionario)...
            //TreeMap<String, Integer> dictKeywordSetWithOccurrences = new TreeMap<String, Integer>();
            while (rs.next()) {

                String id = rs.getString("ID");
                String word = rs.getString("WORD").toLowerCase();

                System.out.println("id : " + id);
                System.out.println("word : " + word);

                String lemma = word;

                System.out.println("-----------------------------------------");
                System.out.println("[addLexiconSourcesToDictKeywordSetWithOccurrences]:");

                if (!dict.containsKey(lemma)) {
                    dict.put(lemma, 1);
                } 
                else {
                    dict.put(lemma, dict.get(lemma) + 1);
                }
            }
            System.out.println("Indice anger da dizionario: " + dict.toString());        
        } catch (SQLException e) {
            System.out.println(e.getMessage());                  
        } finally {      
            if (statement != null) {
                statement.close();
            }      
        }  
        return dict;
    }
    
    
    /**
     * recupera tutti i tweet del corpus Anger, splitta, lemmizza ed elimina le stopwords
     * @param connection   la connessione al db
     * @return TreeMap 	restituisce i token conteggiati, a livello di corpus
     * @throws SQLException 
     */
    public static TreeMap<String, Integer> createTweetAngerKeywordNoStopwordSetWithOccurrences(Connection connection)throws SQLException {
        
        String selectTweetsTableSQL = "SELECT * from TWEETS_DATASET_DT_ANGER where rownum <= 10";
        //inizializzo strutture per indice keyword sentimento...
        TreeMap<String, Integer> tweetKeywordSetWithOccurrences = new TreeMap<String, Integer>();
        //inizializzo strutture per indice(no  stopword) keyword sentimento...
        TreeMap<String, Integer> tweetKeywordNoStopwordSetWithOccurrences = new TreeMap<String, Integer>();
        Integer countStopwords = 0;
        Integer countStopwordsLemma = 0;
        try {
            statement = connection.createStatement();
            System.out.println(selectTweetsTableSQL);
            // esegue lo statement SQL 
            ResultSet rs = statement.executeQuery(selectTweetsTableSQL);


            //ArrayList<String> tweetKeywordsList = this.getTweetKeywords();
            Properties props = new Properties();
            //props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment, stopword");
            props.setProperty("annotators", "tokenize, ssplit, pos, lemma, stopword");
            
            props.setProperty("customAnnotatorClass.stopword", "intoxicant.analytics.coreNlp.StopwordAnnotator");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
            

            while (rs.next()) {

                String id = rs.getString("ID");
                String tweetText = rs.getString("TWEETTEXT");

                System.out.println("id : " + id);
                System.out.println("tweet : " + tweetText);

                //inizio elaborazione NLP...
                String text = tweetText;

                // impostazione delle propriet� da passare alla classe
                // StanfordCore NLP
                Annotation annotation = pipeline.process(text);

                //fixme: eliminare...annotation = new Annotation(tweetText);   
                //fixme: scoprire quali sono le funzionalit� di  "annotate"...
                //pipeline.annotate(annotation);

                /* Per ora non mi interessa la prettyPrint di annotation...
                            out.println("1. prettyPrint di annotation tweetText:");
                            pipeline.prettyPrint(annotation, out);
                            out.println("fine prettyPrint di annotation tweetText.");
                 */
                System.out.println("-----------------------------------------");
                System.out.println("parole e lemmi per frase di tweetText:");
               
                List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);            
                Set<Object> customStopWords = createCustomStopWordsAndExcludeTokenSet();             
                for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                    for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                        String word = token.get(CoreAnnotations.TextAnnotation.class);
                        String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                        Pair<Boolean, Boolean> stopword = token.get(StopwordAnnotator.class);
                        word = token.word().toLowerCase();                     
                        lemma = token.lemma().toLowerCase();
                        System.out.println("Parola: " + word + " --> versione lemmizzata:" + lemma); 
                        /*
                        if (stopWords.contains(word)) {
                            System.out.println("(word)stopword: " + word);
                            countStopwords++;
                        } else {
                            System.out.println("(word)non stopword: " + word);

                        }
                        if (stopWords.contains(lemma)) {
                            System.out.println("(lemma)stopword: " + lemma);
                            countStopwordsLemma++;
                        } else {
                            System.out.println("(lemma)non stopword: " + lemma);

                        }
                        */
                        // aggiorna struttura indice con lemma...
                        if (!tweetKeywordSetWithOccurrences.containsKey(lemma)) {
                            tweetKeywordSetWithOccurrences.put(lemma, 1);
                        } else {
                            tweetKeywordSetWithOccurrences.put(lemma, tweetKeywordSetWithOccurrences.get(lemma) + 1);
                        }
                        if (customStopWords.contains(lemma)) {
                            System.out.println("trovato lemma stopword: " + lemma);
                        } else {
                            System.out.println("aggiungo a indice il lemma: " + lemma);
                            if (!tweetKeywordNoStopwordSetWithOccurrences.containsKey(lemma)) {
                                tweetKeywordNoStopwordSetWithOccurrences.put(lemma, 1);
                                
                            } else {
                                tweetKeywordNoStopwordSetWithOccurrences.put(lemma, tweetKeywordNoStopwordSetWithOccurrences.get(lemma) + 1);
                            }
                        }    
                    }
                }
                

                //  Una Annotation � una Map che � possibile recuperare e analizzare in svariati modi
                // ecco ad esempio il parse tree della prima frase...

                /*
                List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
                    if (sentences != null && sentences.size() > 0) {
                      CoreMap sentence = sentences.get(0);
                      Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
                      out.println();
                      out.println("La prima frase parsificata �:");
                      tree.pennPrint(out);
                    }
                    out.println("----------------------------");
                    out.println("3. analisi sentimenti frasi tweet:");

                for (CoreMap sentence : sentences) {
                    String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
                    System.out.println(sentiment + "\t" + sentence);
                }
                System.out.println("-----------------------------");
                 */
                /*
                //utilizz uno stopword set customizzato
                Set<?> stopWords = StopwordAnnotator.getStopWordList(Version.LUCENE_36, customStopWordList, true);
                StanfordCoreNLP pipeline2 = new StanfordCoreNLP(props);
                Annotation document = new Annotation("The pony is on the table");
                pipeline2.annotate(document);
                List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);
                for (CoreLabel token : tokens) {
                    //get the stopword annotation
                    Pair<Boolean, Boolean> stopword = token.get(StopwordAnnotator.class);
                    String word = token.word().toLowerCase();
                    if (stopWords.contains(word)) {
                        System.out.println("(word)contiene stopword.first: "+word);
                    }
                    else {
                        System.out.println("(word)non contiene stopword.first: "+word);
                    }
                    //il lemma non viene controllato, per cui sempre false...
                    //System.out.println("(lemma): "+stopword.second());
                }
                 */

            }
            System.out.println("Indice anger: " + tweetKeywordSetWithOccurrences.toString());
            System.out.println("Indice anger senza stopwords: " + tweetKeywordNoStopwordSetWithOccurrences.toString());
            //System.out.println("Stopwords: " + countStopwords);
            //System.out.println("StopwordsLemma: " + countStopwordsLemma);
        } catch (SQLException e) {

            System.out.println(e.getMessage());

        } finally {

            if (statement != null) {
                statement.close();
            }

            if (connection != null) {
                connection.close();
            }

        }
        return tweetKeywordNoStopwordSetWithOccurrences;
    }
    private static Set<Object> createCustomStopWordsAndExcludeTokenSet(){
        //recupera il set di stopword di lucene
        Set<?> stopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
        Set<Object> customStopWords;
        customStopWords = new HashSet<>();
        customStopWords.add(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
        customStopWords.add(".");
        customStopWords.add(";");
        customStopWords.add("!");
        customStopWords.add("?");
        customStopWords.add("'");
        customStopWords.add(",");
        customStopWords.add(":");
        return customStopWords;
    }
}
    
