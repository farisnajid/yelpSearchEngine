import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
// import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class JsonSearcher {
    private IndexSearcher searcher;
    private MultiFieldQueryParser queryParser;

    public JsonSearcher(String indexDir) throws IOException {
        Directory directory = FSDirectory.open(Paths.get(indexDir));
        IndexReader reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
        // queryParser = new MultiFieldQueryParser(new String[] { "review_id", "user_id", "stars","useful","funny","cool","text","date" }, new StandardAnalyzer());
        queryParser = new MultiFieldQueryParser(new String[] { "business_id", "name", "address", "city", "state", "postal_code", "latitude", "longitude", "stars", "review_count", "is_open" }, new StandardAnalyzer());
    }

    public List<Document> search(String queryString, boolean isRankingQuery, int numResults) throws ParseException, IOException {
        if (isRankingQuery) {
            return searchRankingQuery(queryString, numResults);
        } else {
            return searchBooleanQuery(queryString, numResults);
        }
    }

    private List<Document> searchBooleanQuery(String queryString, int numResults) throws ParseException, IOException {
        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
        String[] queryTerms = queryString.split("\\s+");
        for (String term : queryTerms) {
            if (term.contains(":")) {
                String[] splitTerm = term.split(":");
                booleanQueryBuilder.add(new TermQuery(new Term(splitTerm[0], splitTerm[1])), BooleanClause.Occur.SHOULD);
            } else if (term.equalsIgnoreCase("AND")) {
                booleanQueryBuilder.add(new BooleanClause(booleanQueryBuilder.build(), BooleanClause.Occur.MUST));
            } else if (term.equalsIgnoreCase("OR")) {
                booleanQueryBuilder.add(new BooleanClause(booleanQueryBuilder.build(), BooleanClause.Occur.SHOULD));
            } else if (term.equalsIgnoreCase("NOT")) {
                booleanQueryBuilder.add(new BooleanClause(booleanQueryBuilder.build(), BooleanClause.Occur.MUST_NOT));
            } else {
                booleanQueryBuilder.add(queryParser.parse(term), BooleanClause.Occur.SHOULD);
            }
        }

        TopDocs topDocs = searcher.search(booleanQueryBuilder.build(), numResults, Sort.RELEVANCE); 
        // TopDocs topDocs = searcher.search(booleanQueryBuilder.build(), numResults, new Sort(new SortField("rank", SortField.Type.INT, true)));
        List<Document> results = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            results.add(doc);
        }
        return results;
    }

    private List<Document> searchRankingQuery(String queryString, int numResults) throws ParseException, IOException {
        TopDocs topDocs = searcher.search(queryParser.parse(queryString), numResults, new Sort(new SortField("rank", SortField.Type.INT, true)));
        List<Document> results = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            results.add(doc);
        }
        return results;
    }
}

