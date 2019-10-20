package ch.heigvd.iict.dmg.labo1;

import ch.heigvd.iict.dmg.labo1.indexer.CACMIndexer;
import ch.heigvd.iict.dmg.labo1.parsers.CACMParser;
import ch.heigvd.iict.dmg.labo1.queries.QueriesPerformer;
import ch.heigvd.iict.dmg.labo1.similarities.MySimilarity;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.Similarity;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static jdk.nashorn.internal.parser.TokenType.AND;

public class Main {

	public static final String INDEX_FOLDER = "Index";

	public static void main(String[] args) {

		long startTime = System.currentTimeMillis();

		// 1.1. create an analyzer
		Analyzer analyser = null;
		try {
			analyser = getAnalyzer();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// TODO student "Tuning the Lucene Score"
//		Similarity similarity = null;//new MySimilarity();
		Similarity similarity = new MySimilarity();
		
		CACMIndexer indexer = new CACMIndexer(analyser, similarity);
		indexer.openIndex();
		CACMParser parser = new CACMParser("documents/cacm.txt", indexer);
		parser.startParsing();
		indexer.finalizeIndex();
		
		QueriesPerformer queriesPerformer = new QueriesPerformer(analyser, similarity);

		// Section "Reading Index"
		readingIndex(queriesPerformer);

		// Section "Searching"
		searching(queriesPerformer);

		queriesPerformer.close();

		long endTime = System.currentTimeMillis();

		System.out.println("Temps : " + (endTime - startTime) + " ms");
		
	}

	private static void readingIndex(QueriesPerformer queriesPerformer) {
		queriesPerformer.printTopRankingTerms("authors", 50);
		queriesPerformer.printTopRankingTerms("title", 10);
	}

	private static void searching(QueriesPerformer queriesPerformer) {
		// Example
		queriesPerformer.query("(\"Information Retrieval\")");
		queriesPerformer.query("(\"Information\" AND \"Retrieval\")");
		queriesPerformer.query("(+\"Retrieval\" \"Information\" !\"Database\")");
		queriesPerformer.query("(Info*)");
		queriesPerformer.query("(\"Information Retrieval\"~5)");






		// TODO student
        // queriesPerformer.query(<containing the term Information Retrieval>);
		// queriesPerformer.query(<containing both Information and Retrieval>);
        // and so on for all the queries asked on the instructions...
        //
		// Reminder: it must print the total number of results and
		// the top 10 results.


	}

	private static Analyzer getAnalyzer() throws IOException {
		// For StopAnalyzer uncomment next line
		//Path path = FileSystems.getDefault().getPath("common_words.txt");
		return new EnglishAnalyzer();
	}

}
