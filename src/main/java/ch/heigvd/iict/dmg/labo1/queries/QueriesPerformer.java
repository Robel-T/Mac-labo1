package ch.heigvd.iict.dmg.labo1.queries;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static ch.heigvd.iict.dmg.labo1.Main.INDEX_FOLDER;

public class QueriesPerformer {
	
	private Analyzer		analyzer		= null;
	private IndexReader 	indexReader 	= null;
	private IndexSearcher 	indexSearcher 	= null;

	public QueriesPerformer(Analyzer analyzer, Similarity similarity) {
		this.analyzer = analyzer;
		Path path = FileSystems.getDefault().getPath(INDEX_FOLDER);
		Directory dir;
		try {
			dir = FSDirectory.open(path);
			this.indexReader = DirectoryReader.open(dir);
			this.indexSearcher = new IndexSearcher(indexReader);
			if(similarity != null)
				this.indexSearcher.setSimilarity(similarity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void printTopRankingTerms(String field, int numTerms) {
		// TODO student
		// This methods print the top ranking term for a field.
		// See "Reading Index"
        try {
            HighFreqTerms.DocFreqComparator cmp = new HighFreqTerms.DocFreqComparator();
            TermStats[] highFreqTerms = HighFreqTerms.getHighFreqTerms(indexReader, numTerms, field, cmp);

            List<String> terms = new ArrayList<>(highFreqTerms.length);
            for (TermStats ts : highFreqTerms) {
                terms.add(ts.termtext.utf8ToString());
            }

            if (field.equals("authors")) {
				System.out.println("Top ranking terms for field [" + field + "] are: " + terms.get(0) + " with " + highFreqTerms[0].totalTermFreq + " publications.");
			} else if (field.equals("title")) {
				System.out.println("Top 10 title terms : ");
            	for (int j = 0; j < highFreqTerms.length; ++j) {
					System.out.println((j+1) + ". " + highFreqTerms[j].termtext.utf8ToString() + " (" + highFreqTerms[j].totalTermFreq + " times).");
				}
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void query(String q) {
		// TODO student
		// See "Searching" section

		System.out.println("Searching for [" + q +"]");
	}
	 
	public void close() {
		if(this.indexReader != null)
			try { this.indexReader.close(); } catch(IOException e) { /* BEST EFFORT */ }
	}
	
}
