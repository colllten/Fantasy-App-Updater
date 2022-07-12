import Model.College;
import Model.Player;
import com.google.firebase.cloud.FirestoreClient;
import me.tongfei.progressbar.ProgressBar;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.json.JSONArray;
import org.json.JSONObject;

public class Driver {

    static ProgressBar pb = new ProgressBar("Updating Firestore Database", 100);

    public static void main(String[] args) throws IOException {
//        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(credentials)
//                .setProjectId("college-fantasy-football")
//                .build();
//        FirebaseApp.initializeApp(options);
//
//        Firestore db = FirestoreClient.getFirestore();


//        pb.start();
       getFBSTeams();
      Player.getPlayers();
//        pb.stop();
    }

    /***
     * Gets all FBS teams from CFDB, writes them to a file, and fills the static array of Colleges
     * @throws IOException
     */
    public static void getFBSTeams() {
        //URL to pull 2021 data
        //TODO: Make 2022
        String httpsURL = "https://api.collegefootballdata.com/teams/fbs?year=2021";
        String jsonData = readData(httpsURL);
        writeDataToFile(jsonData, "/Users/coltenglover/Java Projects/FantasyApp/src/main/resources/FBSTeams.txt");
        College.fillCollegeList();
    }

    /***
     * Goes to CFDB API and retrieves data using the given url
     * @param httpsURL - The endpoint to retrieve JSON data
     * @return - The JSON string from CFDB
     */
    public static String readData(String httpsURL) {
        System.out.println("Fetching data from CFDB");
        StringBuilder input = new StringBuilder();
        try {
            URL myUrl = new URL(httpsURL);
            HttpsURLConnection conn = (HttpsURLConnection)myUrl.openConnection();
            conn.setRequestProperty("Authorization", Constants.API_KEY);
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String inputLine; //Holds current line returned from API
            while ((inputLine = br.readLine()) != null) {
                pb.step();
                input.append(inputLine);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return input.toString();
    }

    /***
     * Takes the JSON data that was read and writes it to the specified file
     * @param jsonData - Data to write to file
     * @param fileName - File to have JSON data written to
     */
    public static void writeDataToFile(String jsonData, String fileName) {
        System.out.println("Writing data to file");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            bw.write(jsonData);
            bw.close();
            pb.step();
        } catch (IOException e) {
            System.out.println("ERROR occurred while writing json to + " + fileName);
        }
    }
}
