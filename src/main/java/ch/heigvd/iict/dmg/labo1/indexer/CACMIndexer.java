package ch.heigvd.iict.dmg.labo1.indexer;

import ch.heigvd.iict.dmg.labo1.parsers.ParserListener;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static ch.heigvd.iict.dmg.labo1.Main.INDEX_FOLDER;

public class CACMIndexer implements ParserListener {
	
	private Directory 	dir 			= null;
	private IndexWriter indexWriter 	= null;
	
	private Analyzer 	analyzer 		= null;
	private Similarity 	similarity 		= null;
	
	public CACMIndexer(Analyzer analyzer, Similarity similarity) {
		this.analyzer = analyzer;
		this.similarity = similarity;
	}
	
	public void openIndex() {
		// 1.2. create an index writer config
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE); // create and replace existing index
		iwc.setUseCompoundFile(false); // not pack newly written segments in a compound file: 
		//keep all segments of index separately on disk
		if(similarity != null)
			iwc.setSimilarity(similarity);
		// 1.3. create index writer
		Path path = FileSystems.getDefault().getPath(INDEX_FOLDER);
		try {
			this.dir = FSDirectory.open(path);
			this.indexWriter = new IndexWriter(dir, iwc);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onNewDocument(Long id, String authors, String title, String summary) {
		// Make a new empty document
		Document doc = new Document();

		// Create all fields for all informations
		Field storeIdField = new StoredField("storedId", id);
		Field titleField = new TextField("title",title,Field.Store.YES);

		// Parse authors name
		String[] authorsField = authors.split(";");
		for (String s : authorsField) {
			if (!s.equals(""))
				doc.add(new StringField("authors", s, Field.Store.YES));
		}

		// 3.1 Indexing - question 2
//		FieldType ft = new FieldType(TextField.TYPE_STORED);
//		ft.setStoreTermVectors(true);
//		Field authorsField = new Field("authors", authors, ft);

		// The summary is not stored
		if (summary != null)
			doc.add(new TextField("summary", summary, Field.Store.NO));

		doc.add(storeIdField);
		doc.add(titleField);

		try {
			this.indexWriter.addDocument(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void finalizeIndex() {
		if(this.indexWriter != null)
			try { this.indexWriter.close(); } catch(IOException e) { /* BEST EFFORT */ }
		if(this.dir != null)
			try { this.dir.close(); } catch(IOException e) { /* BEST EFFORT */ }
	}
	
}
