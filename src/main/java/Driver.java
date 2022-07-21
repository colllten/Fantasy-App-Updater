import Model.College;
import Model.Player;
import Model.PlayerHashtable;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import me.tongfei.progressbar.ProgressBar;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.google.auth.oauth2.GoogleCredentials;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Driver {

    static ProgressBar pb = new ProgressBar("Updating Firestore Database", 100);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        FileInputStream serviceAccount =
                new FileInputStream("/Users/coltenglover/Java Projects/FantasyApp/src/main/resources/college" +
                        "-fantasy-football-firebase-adminsdk-z6oe2-06d23889ee.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);

        Firestore db = FirestoreClient.getFirestore();

////        DocumentReference docRef = db.collection("Colleges").document("3MiyQtDJjpltAa7xrUUk");
////// asynchronously retrieve the document
////        ApiFuture<DocumentSnapshot> future = docRef.get();
////        DocumentSnapshot document = future.get();
////        System.out.println(document.getString("school"));
//
//
//        // Create a reference to the cities collection
//        CollectionReference cities = db.collection("Colleges");
//// Create a query against the collection.
//        Query query = cities.whereEqualTo("school", "Purdue");
//// retrieve  query results asynchronously using query.get()
//        ApiFuture<QuerySnapshot> querySnapshot = query.get();
//
//        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
//            System.out.println(document.getString("mascot"));
//        }


        Scanner in = new Scanner(System.in);
        String response = "";
        //Menu for Java database
        System.out.println("1) Update all teams and players' stats through week #\n2) Update from CFDB\n3) Use local storage");
        if (in.nextLine().equals("1")) {
            getFBSTeams(); //Pull from CFDB and write info to files
            Player.getPlayers(); //Pull all rosters and put into files
            System.out.println("Enter what week you would like to update through");
            updateWeekStats(Integer.parseInt(in.nextLine()));
        } else if (in.nextLine().equals("2")) {
            getFBSTeams(); //Pull from CFDB and write info to files
            Player.getPlayers(); //Pull all rosters and put into files
        } else {
            College.fillCollegeList(); //Read from file and fill college list
            Player.fillOfflineRosters(); // Read from file and fill all college rosters
            System.out.printf("There are %d players in the hashtable\n", Player.playerTable.playerCount);
        }
        do {
            System.out.print("" +
                    "*******************************************************\n" +
                    "What would you like to do?\n" +
                    "1) Search for player by first name\n" +
                    "2) Search for player by last name\n" +
                    "3) Search for player by first and last name\n" +
                    "4) Search for player by id number\n" +
                    "5) Retrieve team roster\n" +
                    "6) Retrieve all FBS teams\n" +
                    "7) Check if school is in local storage\n" +
                    "8) Retrieve all schools in a conference\n" +
                    "9) Write all FBS teams' data to Firestore\n" +
                    "*******************************************************\n\n");
            response = in.nextLine();

            switch (response) {
                case ("1"):
                    System.out.println("Enter the player's first name:");
                    response = in.nextLine();
                    ArrayList<Player> result = searchPlayerFirstName(response);
                    System.out.printf("There are %d players with that first name:\n", result.size());
                    for (int i = 0; i < result.size(); i++) {
                        System.out.printf("%s %s | %s | %s | %d\n", result.get(i).getFirstName(),
                                result.get(i).getLastName(), result.get(i).getPosition(),
                                result.get(i).getTeam(), result.get(i).getId());
                    }
                    break;

                case ("2"):
                    System.out.println("Enter the player's last name:");
                    response = in.nextLine();
                    result = searchPlayerLastName(response);
                    System.out.printf("There are %d players with that last name:\n", result.size());
                    for (int i = 0; i < result.size(); i++) {
                        System.out.printf("%s %s | %s | %s | %d\n", result.get(i).getFirstName(),
                                result.get(i).getLastName(), result.get(i).getPosition(),
                                result.get(i).getTeam(), result.get(i).getId());
                    }
                    break;
                case ("3"):
                    System.out.println("Enter the player's first name:");
                    response = in.nextLine();
                    result = searchPlayerFirstName(response);
                    System.out.println("Enter the player's last name:");
                    response = in.nextLine();
                    ArrayList<Player> filteredResult = new ArrayList<>();
                    for (Player player : result) {
                        if (player.getLastName().equals(response)) {
                            filteredResult.add(player);
                        }
                    }
                    for (int i = 0; i < filteredResult.size(); i++) {
                        System.out.printf("%s %s | %s | %s | %d\n", filteredResult.get(i).getFirstName(),
                                filteredResult.get(i).getLastName(), filteredResult.get(i).getPosition(),
                                filteredResult.get(i).getTeam(), filteredResult.get(i).getId());
                    }
                    break;
                case ("4"):
                    System.out.println("Enter the player id:");
                    response = in.nextLine();
                    for (College college : College.colleges) {
                        for (Player player : college.getRoster()) {
                            if (player.getId() == Integer.parseInt(response)) {
                                System.out.printf("%s %s | %s | %s | %d\n", player.getFirstName(),
                                        player.getLastName(), player.getPosition(),
                                        player.getTeam(), player.getId());
                                break;
                            }
                        }
                    }
                    break;
                case ("5"):
                    System.out.println("Enter the team for which roster you want to see:");
                    response = in.nextLine();
                    for (College college : College.colleges) {
                        if (college.getSchool().equals(response)) {
                            for (Player player : college.getRoster()) {
                                System.out.printf("%s %s | %s | %s | %d\n", player.getFirstName(),
                                        player.getLastName(), player.getPosition(),
                                        player.getTeam(), player.getId());
                            }
                            break;
                        }
                    }
                    break;
                case ("6"):
                    for (College college : College.colleges) {
                        System.out.printf("%s %s | %s \n", college.getSchool(), college.getMascot(),
                                college.getConference());
                    }
                    break;
                case ("8"):
                    System.out.println("Enter a conference:");
                    response = in.nextLine();
                    for (College college : College.colleges) {
                        if (college.getConference().equals(response)) {
                            System.out.printf("%s %s | %s \n", college.getSchool(), college.getMascot(),
                                    college.getConference());
                        }
                    }
                    break;
                case ("9"):
                    writeTeamsToFirestore(db);
                    break;
            }
        } while (!response.equals("q"));
        in.close();
    }

    /***
     * Gets all FBS teams from CFDB, writes them to a file, and fills the static array of Colleges
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
        StringBuilder input = new StringBuilder();
        try {
            URL myUrl = new URL(httpsURL);
            HttpsURLConnection conn = (HttpsURLConnection) myUrl.openConnection();
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

    /***
     * Intended for use in Driver to search for all players with the first name
     * @param firstName - first name of player
     * @return - ArrayList of all players with the first name
     */
    public static ArrayList<Player> searchPlayerFirstName(String firstName) {
        ArrayList<Player> result = new ArrayList<>();
        for (int i = 0; i < College.colleges.size(); i++) {
            for (int j = 0; j < College.colleges.get(i).getRoster().size(); j++) {
                if (College.colleges.get(i).getRoster().get(j).getFirstName().equals(firstName)) {
                    result.add(College.colleges.get(i).getRoster().get(j));
                }
            }
        }
        return result;
    }

    /***
     * Intended for use in Driver to search for all players with the last name
     * @param lastName - last name of desired player
     * @return - ArrayList of all players with last name matching parameter
     */
    public static ArrayList<Player> searchPlayerLastName(String lastName) {
        ArrayList<Player> result = new ArrayList<>();
        for (int i = 0; i < College.colleges.size(); i++) {
            for (int j = 0; j < College.colleges.get(i).getRoster().size(); j++) {
                if (College.colleges.get(i).getRoster().get(j).getLastName().equals(lastName)) {
                    result.add(College.colleges.get(i).getRoster().get(j));
                }
            }
        }
        return result;
    }

    public static void writeTeamsToFirestore(Firestore db) {
        ProgressBar pb = new ProgressBar("Writing teams to Firestore", College.colleges.size());
        pb.start();
        for (College college : College.colleges) {
            //By now, every college has their roster finished and can now be written to the DB
            db.collection("Colleges").add(college);
            pb.step();
        }
        pb.stop();

//        Map<String, Object> docData = new HashMap<>();
//        docData.put("abbrv", "iu");
//        docData.put("conference", "Big Ten");
//        docData.put("mascot", "Hoosiers");
//        ArrayList<Player> players = new ArrayList<>();
//        Map<String, Object> p1 = new HashMap<>();
//        players.add(new Player(1, "C", "G", "indiana university", 1, 3, "WR"));
//        docData.put("roster", players);
//        docData.put("school", "indiana university");
//        ApiFuture<DocumentReference> future = db.collection("Colleges").add(docData);
    }

    /***
     * Goes through every college and searches that week's game and fills their roster's stats
     * @param weekNum
     */
    public static void updateWeekStats(int weekNum) {
        System.out.printf("There are %d players in hashtable BEFORE updating stats\n", Player.playerTable.playerCount);
        for (int i = 1; i <= weekNum; i++) {
            //Track what teams have been updated
            Set<String> updatedTeams = new HashSet<>();
            for (College college : College.colleges) {
                String collegeName = college.getSchool();
                //Turn into url-safe college name
                collegeName = College.urlSafeName(collegeName);
                //Pull from API
                String url = "https://api.collegefootballdata.com/games/players?year=2021&week=" + i
                        + "&seasonType=regular&team=" + collegeName;
                //General array
                JSONArray jsonData = new JSONArray(readData(url));
                //game ID + teams
                JSONObject safeMatchup = null;
                try {
                    safeMatchup = jsonData.getJSONObject(0);
                } catch (JSONException ignored) {

                }
                if (safeMatchup == null) {
                    continue;
                }
                //Teams involved in matchup
                JSONArray teams = safeMatchup.getJSONArray("teams");
                //First team in array
                JSONObject team = teams.getJSONObject(0);
                if (!updatedTeams.contains(team.getString("school"))) { //If the school isn't in the set...
                    updatedTeams.add(team.getString("school"));
                    //Categories array
                    JSONArray categories = team.getJSONArray("categories");
                    //All kicking stats
                    JSONObject kicking = categories.getJSONObject(1);
                    //Types of kicking stats
                    JSONArray kickingTypes = kicking.getJSONArray("types");
                    //PTS
                    JSONObject extraPoints = kickingTypes.getJSONObject(1);
                    JSONArray athletes = extraPoints.getJSONArray("athletes");
                    if (athletes.length() == 0) {
                        continue;
                    }
                    for (int j = 0; j < athletes.length(); j++) {
                        JSONObject athlete = athletes.getJSONObject(j);
                        String firstName = athlete.getString("name").substring(0, athlete.getString("name").indexOf(" "));
                        String lastName = athlete.getString("name").substring(athlete.getString("name").indexOf(" ") + 1);
                        ArrayList<Player> players = Player.playerTable.search(firstName, lastName);
                        for (int k = 0; k < players.size(); k++) {
                            if (players.get(k).getTeam().equals(team.getString("school"))) {
                                players.get(k).playerGameStats.get(i).week = i;
                                players.get(k).playerGameStats.get(i).year = 2021;
                                try {
                                    players.get(k).playerGameStats.get(i).extraPointMakes = Integer.parseInt
                                            (athlete.getString("stat").substring(0, athlete.getString("stat").indexOf("/")));
                                    players.get(k).playerGameStats.get(i).extraPointAttempts = Integer.parseInt
                                            (athlete.getString("stat").substring(athlete.getString("stat").indexOf("/") + 1));
                                } catch (StringIndexOutOfBoundsException ignored) {}
                                Player.playerTable.put(players.get(k));
                                break;
                            }
                        }
                    }
                }
            }
        }
        System.out.printf("After updating stats, there are %d players in hashtable\n", Player.playerTable.playerCount);
    }
}
