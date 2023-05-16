import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import javax.xml.transform.Source;

import java.io.FileWriter;
import org.json.JSONObject;
import org.json.JSONArray;

import org.apache.lucene.document.Document;

// import javax.naming.directory.SearchResult;

import org.apache.lucene.queryparser.classic.ParseException;

public class JsonTester {

    public static void main(String[] args) throws IOException, ParseException {

        // String indexDir = "/Users/farisnajid/Documents/MSFT/MH6301 - Information Retrieval & Analysis/java codes/index";
        String indexDir = "index/";
        String dataDir = "yelp_dataset/";

        // Create a JsonIndexer object and index the JSON files
        JsonIndexer indexer = new JsonIndexer(indexDir);
        indexer.indexJsonFiles(dataDir);

        // Create a JsonSearcher object
        JsonSearcher searcher = new JsonSearcher(indexDir);

        // Create a scanner object to read user input
        Scanner scanner = new Scanner(System.in);

        // Loop to keep asking for queries until user types "exit"
        while (true) {

            System.out.println("What type of Query? (enter \"b\" for boolean query, \"r\" for ranking query, or \"exit\" to quit):");
            String queryType = scanner.nextLine();
            boolean isRankingQuery = queryType.equals("r") ? true: false;

            if (queryType.equals("exit")) {
                break;
            }

            System.out.println("Enter the query (type \"exit\" to quit):");
            String query = scanner.nextLine();

            if (query.equals("exit")) {
                break;
            }

            // Perform the search and print the results
            List<Document> results = searcher.search(query, isRankingQuery, 20);
            int resultSize = results.size();

            if (resultSize == 0) {
                System.out.println("No results found.");
            } else {
                JSONArray jsonArray = new JSONArray();
                System.out.println("Total results: " + resultSize);
                for (Document result : results) {

                    // System.out.println("{" + " review_id: " + result.get("review_id") + ", user_id: " + result.get("user_id") + ", stars: " + result.get("stars") + ", useful: " + result.get("useful") + ", funny: " + result.get("funny") + ", cool: " + result.get("cool") + ", date: " + result.get("date") + ", text: " + result.get("text") + " }");
                    System.out.println("{" + " business_id: " + result.get("business_id") + ", name: " + result.get("name") + ", address: " + result.get("address") + ", city: " + result.get("city") + ", state: " + result.get("state") + ", postal_code: " + result.get("postal_code") + ", latitude: " + result.get("latitude") + ", longitude: " + result.get("longitude") + ", stars: " + result.get("stars") + ", review_count: " + result.get("review_count") + ", is_open: " + result.get("is_open") + " }");
                    JSONObject jsonObj = new JSONObject();

                    jsonObj.put("business_id", result.get("business_id"));
                    jsonObj.put("name", result.get("name"));
                    jsonObj.put("address", result.get("address"));
                    jsonObj.put("city", result.get("city"));
                    jsonObj.put("state", result.get("state"));
                    jsonObj.put("postal_code", result.get("postal_code"));
                    jsonObj.put("longitude", Double.parseDouble(result.get("longitude")));
                    jsonObj.put("latitude", Double.parseDouble(result.get("latitude")));
                    jsonObj.put("stars", Double.parseDouble(result.get("stars")));
                    jsonObj.put("review_count", Integer.parseInt(result.get("review_count")));
                    jsonObj.put("is_open", Integer.parseInt(result.get("is_open")));

                    System.out.println(jsonObj);
                    jsonArray.put(jsonObj);

                }

                System.out.println("Save results? (enter \"y\" for yes, \"n\" for no):");
                String saveResults = scanner.nextLine();

                if (saveResults.equals("y")) {
                    long unixTime = System.currentTimeMillis() / 1000L;
                    FileWriter file = new FileWriter(unixTime+"_results.json");
                    file.write(jsonArray.toString());
                    file.close();
                } else {
                    System.out.println("Results not saved");
                }
            }
        }

        // Close the scanner
        scanner.close();
    }
}

