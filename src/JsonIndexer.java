import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
// import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
// import org.json.JSONArray;
import org.json.JSONObject;


public class JsonIndexer {
    private String indexPath;

    public JsonIndexer(String indexPath) {
        this.indexPath = indexPath;
    }

    public void indexJsonFiles(String jsonDirPath) throws IOException {
        Directory indexDir = FSDirectory.open(Paths.get(indexPath));
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(indexDir, config);

        File jsonDir = new File(jsonDirPath);
        for (File file : jsonDir.listFiles()) {
            if (file.isFile() && file.getName().endsWith("business.json")) {
                indexJsonFile(file, writer);
            }
        }

        writer.close();
    }

    private void indexJsonFile(File jsonFile, IndexWriter writer) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(jsonFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                JSONObject jsonObj = new JSONObject(line);

                String business_id = jsonObj.getString("business_id");
                String name = jsonObj.getString("name");
                String address = jsonObj.getString("address");
                String city = jsonObj.getString("city");
                String state = jsonObj.getString("state");
                String postal_code = jsonObj.getString("postal_code");
                // String categories = jsonObj.getString("categories");
                // String hours = jsonObj.getString("hours");

                double latitude_double = jsonObj.getDouble("latitude");
                double longitude_double = jsonObj.getDouble("longitude");
                double stars_double = jsonObj.getDouble("stars");
                int review_count_int = jsonObj.getInt("review_count");
                int is_open_int = jsonObj.getInt("is_open");

                String latitude = Double.toString(latitude_double);
                String longitude = Double.toString(longitude_double);
                String stars = Double.toString(stars_double);
                String review_count = Integer.toString(review_count_int);
                String is_open = Integer.toString(is_open_int);

                Document doc = new Document();

                doc.add(new TextField("business_id", business_id, Field.Store.YES));
                doc.add(new TextField("name", name, Field.Store.YES));
                doc.add(new TextField("address", address, Field.Store.YES));
                doc.add(new TextField("city", city, Field.Store.YES));
                doc.add(new TextField("state", state, Field.Store.YES));
                doc.add(new TextField("postal_code", postal_code, Field.Store.YES));
                // doc.add(new TextField("categories", categories, Field.Store.YES));
                doc.add(new TextField("latitude", latitude, Field.Store.YES));
                doc.add(new TextField("longitude", longitude, Field.Store.YES));
                doc.add(new TextField("stars", stars, Field.Store.YES));
                doc.add(new TextField("review_count", review_count, Field.Store.YES));
                doc.add(new TextField("is_open", is_open, Field.Store.YES));

                writer.addDocument(doc);
            }
        }
    }
}
