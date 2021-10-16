import java.awt.*;
import java.awt.event.*;
import javax.swing.JButton;
import javax.swing.JFrame;  
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLayeredPane;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Vector;

//javac *.java 
//This command tells the file where to find the postgres jar which it needs to execute postgres commands, then executes the code
//Windows: java -cp ".;postgresql-42.2.8.jar" jdbcpostgreSQL

public class User implements ActionListener{
    
    JFrame frame;  //main frame
    JTextField textfield;   //search bar
    
    String[] btnNames = new String[] {"Home"," ","Sci-Fi", "Action", "Talk-Show", "Documentry","Drama","Crime"," Music",
                                    "Romance", "Adventure", "Family", "War", "Biography","Musical", "Comedy",
                                    "Fantasy", "Sport", "Animation", "Adult", "Thriller", "History",
                                    "Game", "News", "Reality", "Mystrey" };

    int numGenre = btnNames.length;
    int numBtns = numGenre+5;
    JButton[] functionButtons = new JButton[numBtns];    //arrany for genre options

    Font myFont = new Font("Ink Free", Font.BOLD, 17);
    JPanel panelLeft, panelSearch, panelGenre;     //panel for the left side
    JPanel panelMain, panelOption, panelIcon;      //main panel header
    JPanel panelMovies, panelR1, panelR2, panelR3;   //panel for movie options
    JPanel panelProfile;            
    JLabel label1, label2, label3;
    JButton MovieBtn, TVBtn;    
    JButton profileBtn, searchBtn, saveBtn;
    JPanel panelPicked;
    JTextField textGenre, textType;
    String titleSearched;

    JPanel userPanel;
    JLabel profileLabel, IDLabel,nameLabel,emailLabel,historyLabel, saveLabel;
    JTextField IDText, nameText, emailText, historyText;   

    String savedID, savedName, savedEmail;

    int prevGenre = 0;    //tracks the prev pick button to change color
    int prevType = 0;     //tracks the prev pick button to change color

  
    Boolean searching = false;

    JLayeredPane lpane = new JLayeredPane();

     //Building the connection with your credentials
     Connection conn = null;
     String teamNumber = "12";
     String sectionNumber = "903";
     String dbName = "csce315" + sectionNumber + "_" + teamNumber + "db";
     String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
     String userName = "csce315" + sectionNumber + "_" + teamNumber + "user";
     String userPassword = "new_password";

     public void openDB(){
          //Connecting to the database 
          conn = null;
          String teamNumber = "12";
          String sectionNumber = "903";
          String dbName = "csce315" + sectionNumber + "_" + teamNumber + "db";
          String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
          String userName = "csce315" + sectionNumber + "_" + teamNumber + "user";
          String userPassword = "new_password";
  
          // Connecting to the database
          try {
              conn = DriverManager.getConnection(dbConnectionString, userName, userPassword);
          } catch (Exception e) {
              e.printStackTrace();
              System.err.println(e.getClass().getName() + ": " + e.getMessage());
              System.exit(0);
          }
     }

     public void closeDB(){
        try {
            conn.close();
        } catch (Exception e) {
            System.out.println("Connection NOT Closed.");
        } // end try catch
     }

    public ArrayList<String> recentArray(String userid){
        //Connecting to the database 
        // try {
        //     conn = DriverManager.getConnection(dbConnectionString,userName, userPassword);
        // } catch (Exception e) {
        //     e.printStackTrace();
        //     System.err.println(e.getClass().getName()+": "+e.getMessage());
        //     System.exit(0);
        // }
        openDB();
        System.out.println("Opened database successfully");
        String date = "";
        String mediaid = "";
        ArrayList<String> top_recent = new ArrayList<String>();
        try{
        //create a statement object
        Statement stmt = conn.createStatement();
        IDText.setText(userid);
        List<Tuple<String, Date>> recent_watchlist = new ArrayList<Tuple<String, Date>>();

        //get dates and mediaid for user

        Comparator<Tuple<String, Date>> comparator = new Comparator<Tuple<String, Date>>(){

            public int compare(Tuple<String, Date> tupleA, Tuple<String, Date> tupleB){
                return tupleA.getDate().compareTo(tupleB.getDate());
            }
            
        };

        String sqlStatement_watchlist = "SELECT * FROM userwatchlist WHERE userid = '" + userid + "';";

        ResultSet result = stmt.executeQuery(sqlStatement_watchlist);
        while (result.next()) {
            date = result.getString("date");
            String date_temp = date;
            String year = date_temp.substring(0, 4);
            String day = date_temp.substring(8);
            String month = date_temp.substring(5, 7);
            String new_date = day + "/" + month + "/" + year;
            //System.out.println(new_date);
            mediaid = result.getString("mediaid");
            java.util.Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(new_date);  
            java.sql.Date sqlDate = new java.sql.Date(date1.getTime());
            
            recent_watchlist.add(new Tuple<String, Date>(mediaid, sqlDate));

        }
        //System.out.println("out");
        result.close();

        Collections.sort(recent_watchlist, comparator);

        List<Tuple<String, Date>> top_recent_watchlist = recent_watchlist.subList(recent_watchlist.size()-20, recent_watchlist.size());
        for (int i = top_recent_watchlist.size(); i-- > 0; ) {
            String sqlStatement_getTitle = "SELECT title FROM titles WHERE mediaid = '" + top_recent_watchlist.get(i).getTitle() + "';";
            ResultSet result_title = stmt.executeQuery(sqlStatement_getTitle);
            result_title.next();
            String title = result_title.getString("title");
            //System.out.println(title);
            result_title.close();
            top_recent.add(title);
        }

        for(int i = 0; i < top_recent.size(); i++){
            System.out.println(top_recent.get(i));
        }



    } catch (Exception e){
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
    }
        
        //closing the connection
        closeDB();
        // try {
        // conn.close();
        // System.out.println("Connection Closed.");
        // }
        // catch(Exception e) {
        //   System.out.println("Connection NOT Closed.");
        // }//end try catch
            return top_recent;
    }

    //user title
    public Vector<String> movieinfo(String titleSearched) {
        Vector<String> result= new Vector<String>();
        String tempmedia = "";

        String rate = "";
        String type = "";
        String year = "";
        String runtime = "";
        String genres = "";

        String sqlString = "Select * "
                         + "From titles "
                         + "Where title = '" + titleSearched + "' "
                         + "Order by Cast(startyear as INT) desc;";
        int count = 0;
        openDB();

        try {
            Statement stmt = conn.createStatement();

            ResultSet res = stmt.executeQuery(sqlString);

            while(res.next()) {
                count++;
                rate = res.getString("rating");
                type = res.getString("mediatype");
                year = res.getString("startyear");
                runtime = res.getString("runtime");
                genres = res.getString("genres").replace("!",", ");
                tempmedia = res.getString("mediaid");
                if (count == 1) {
                    break;
                }
            }

            // sqlString = "Select directors "
            //          + "From production "
            //          + "Where mediaid = '" + tempmedia + "'; ";

            // res = stmt.executeQuery(sqlString);

            // while(res.next()) {

            // }

        } catch (Exception e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        closeDB();

        result.add(rate);
        result.add(type);
        result.add(year);
        result.add(runtime);
        result.add(genres);

        return result;
    }


    User(){

        //Entire frame
        frame = new JFrame("Movie");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1800,800);
        frame.setLayout(null);
        
        frame.setLayout(new BorderLayout());
        lpane.setBounds(0, 0, 600, 400);
        ////////////////////////////////

        //left panel with genre options and search bar
        panelLeft = new JPanel();
        panelLeft.setBounds(0, 0,250,800);
        panelLeft.setLayout(new GridLayout(4,4,10,10));
        panelLeft.setBackground(Color.black);
        
        panelGenre = new JPanel();
        panelGenre.setBounds(5, 250,240,450);
        panelGenre.setLayout(new GridLayout(13,2,0,0));
        for (int i = 0; i < numGenre; i++){
            functionButtons[i] = new JButton(btnNames[i]);
            panelGenre.add(functionButtons[i]);
        }
        //main screen
        panelMain = new JPanel();
        panelMain.setBounds(270, 0,1500,800);
        panelMain.setBackground(Color.black);

        panelOption = new JPanel();
        panelOption.setBounds(300, 50,800,50);

        MovieBtn = new JButton("Movie");
        TVBtn = new JButton("TVShows");

        functionButtons[numGenre] = MovieBtn;
        functionButtons[numGenre+1] = TVBtn;

        MovieBtn.setFocusable(false);
        TVBtn.setFocusable(false);
        panelOption.setLayout(new GridLayout(1,2,0,0));
        MovieBtn.setFont(new Font("Serif", Font.BOLD, 30));
        TVBtn.setFont(new Font("Serif", Font.BOLD, 30));
        panelOption.add(MovieBtn);
        panelOption.add(TVBtn);
        // //search bar
        panelSearch = new JPanel();
        panelSearch.setBounds(5,50,200,30);
        panelSearch.setBackground(Color.GRAY);

        textfield = new JTextField();
        textfield.setBounds(5,50,150,30);
        textfield.setFont(myFont);

        searchBtn = new JButton("search");
        searchBtn.setBounds(150,50,90,30);
        functionButtons[numGenre+2] = searchBtn;


        searchBtn.setFont(new Font("Dialog", Font.BOLD, 7));
   
        // //picked options
        // // textGenre, textType
        textGenre = new JTextField();
        textGenre.setBounds(300, 130,150,50);
        textGenre.setFont(myFont);
        textGenre.setEditable(false);

        textType = new JTextField();
        textType.setBounds(450, 130,150,50);
        textType.setFont(myFont);
        textType.setEditable(false);

        // ///////////////////////////////
        // //movie options
        // //first row of movie option
        panelR1 = new JPanel();
        panelR1.setBounds(270, 200,1500,170);
        panelR1.setBackground(Color.white);        
        label1 = new JLabel();
        label1.setText("Global Recommendation");
        label1.setFont(new Font("Serif", Font.BOLD, 20));
        panelR1.add(label1);
        //movie options
        //second row of movie option
        panelR2 = new JPanel();
        panelR2.setBounds(270, 400,1500,170);
        panelR2.setBackground(Color.white);        
        label2 = new JLabel();
        label2.setText("Your Recommendation");
        label2.setFont(new Font("Serif", Font.BOLD, 20));
        panelR2.add(label2);

        //  //movie options
        // //third row of movie option
        panelR3 = new JPanel();
        panelR3.setBounds(270, 600,1500,170);
        panelR3.setBackground(Color.white);        
        label3 = new JLabel();
        label3.setText("Recently Watched");
        label3.setFont(new Font("Serif", Font.BOLD, 20));
        panelR3.add(label3);
      
        // //profile on the top left
        panelProfile = new JPanel();
        panelProfile.setBounds(1400, 50,100,40);
        panelProfile.setBackground(Color.black);

        profileBtn = new JButton("PROFILE");
        profileBtn.setFont(new Font("Serif", Font.BOLD, 20));
        profileBtn.setFocusable(false);
        panelProfile.add(profileBtn);
        functionButtons[numGenre+3] = profileBtn;

        //user profile//////////////////////
        userPanel = new JPanel();
        userPanel.setVisible(false);
        userPanel.setBounds(450, 200,900,500);
        userPanel.setBackground(Color.white);

        IDLabel = new JLabel();
        IDLabel.setText("User ID: ");
        IDLabel.setBounds(600,250,100,20);
        IDLabel.setFont(new Font("Serif", Font.BOLD, 20));
        IDLabel.setVisible(false);

        IDText = new JTextField();
        IDText.setBounds(700,250,200,30);
        IDText.setText( "2625420");
        IDText.setVisible(false);

        nameLabel = new JLabel();
        nameLabel.setText("Name: ");
        nameLabel.setBounds(600,280,150,20);
        nameLabel.setFont(new Font("Serif", Font.BOLD, 20));
        nameLabel.setVisible(false);

        nameText = new JTextField();
        nameText.setBounds(700,280,200,30);
        nameText.setVisible(false);

        emailLabel = new JLabel();
        emailLabel.setText("email: ");
        emailLabel.setBounds(600,310,150,20);
        emailLabel.setFont(new Font("Serif", Font.BOLD, 20));
        emailLabel.setVisible(false);

        emailText = new JTextField();
        emailText.setBounds(700,310,200,30);
        emailText.setVisible(false);

        historyLabel = new JLabel();
        historyLabel.setText("history: ");
        historyLabel.setBounds(600,350,150,20);
        historyLabel.setFont(new Font("Serif", Font.BOLD, 20));
        historyLabel.setVisible(false);

        historyText = new JTextField();
        historyText.setBounds(700,350,500,60);
        historyText.setVisible(false);

        saveBtn = new JButton("Updata & Save");
        saveBtn.setFont(new Font("Serif", Font.BOLD, 40));
        saveBtn.setBounds(700,450,200,60);
        saveBtn.setFocusable(false);
        saveBtn.setVisible(false);

        saveLabel = new JLabel();
        saveLabel.setText("Information Saved");
        saveLabel.setBounds(700,650,250,30);
        saveLabel.setFont(new Font("Serif", Font.BOLD, 30));
        saveLabel.setBackground(Color.GREEN);
        saveLabel.setVisible(false);

        functionButtons[numGenre+4] = saveBtn;

        // //adding functions to each button and setting
        for (int i = 0; i < numBtns; i++){
            functionButtons[i].addActionListener(this);
            functionButtons[i].setFont(myFont);
            functionButtons[i].setFocusable(false);
        }
        // //layering for putting panel over another panel
        lpane.add(panelLeft,0, 0);
        lpane.add(panelSearch, 1, 0);
        lpane.add(textfield, 2, 0);
        lpane.add(searchBtn, 3, 0);
        lpane.add(panelGenre,2, 0);
        lpane.add(panelMain,1, 0);
        lpane.add(panelOption,2, 0);
        lpane.add(panelR1,2, 0);
        lpane.add(panelR2,2, 0);
        lpane.add(panelR3,3, 0);
        lpane.add(panelProfile,2, 0);
        lpane.add(textGenre,2, 0);
        lpane.add(textType,2, 0);
        lpane.add(userPanel,2, 0);
        lpane.add(IDLabel, 3, 0);
        lpane.add(IDText, 3, 0);
        lpane.add(nameLabel,3,0);
        lpane.add(nameText,3,0);
        lpane.add(emailLabel,3,0);
        lpane.add(emailText,3,0);
        lpane.add(historyLabel,3,0);
        lpane.add(historyText,3,0);
        lpane.add(saveBtn,3, 0);
        lpane.add(saveLabel, 3,0);
        frame.pack();
            
        frame.add(lpane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static void main(String[] args){
       User user = new User();
    }

    public void displayProfile(){
        panelR1.setVisible(false);
        panelR2.setVisible(false);
        panelR3.setVisible(false);
        functionButtons[prevType].setBackground(null);
        functionButtons[prevGenre].setBackground(null);
                        
        userPanel.setVisible(true);

        IDLabel.setVisible(true);
        IDText.setVisible(true);
        IDText.setText(savedID);

        nameLabel.setVisible(true);
        nameText.setVisible(true);
        nameText.setText(savedName);

        emailLabel.setVisible(true);
        emailText.setVisible(true);
        emailText.setText(savedEmail);

        historyLabel.setVisible(true);
        historyText.setVisible(true);
        saveBtn.setVisible(true);
    }

    public void displayMain(String label){
        panelR1.setVisible(true);
        panelR2.setVisible(true);
        panelR3.setVisible(true);

        label1.setText(label);
        label2.setText("Gloabl recomendation");
        label3.setText("Your recomendation");

        userPanel.setVisible(false);

        IDLabel.setVisible(false);
        IDText.setVisible(false);

        nameLabel.setVisible(false);
        nameText.setVisible(false);

        emailLabel.setVisible(false);
        emailText.setVisible(false);

        historyLabel.setVisible(false);
        historyText.setVisible(false);

        saveBtn.setVisible(false);
        saveLabel.setVisible(false);
    }

    public void save(){
        if (exists()){
            saveLabel.setText("exists");
            savedID = IDText.getText();
            savedName = nameText.getText();
            savedEmail = emailText.getText();
            displayRecently(savedID);
        }
        else{
            saveLabel.setText("Invalid ID");
            JOptionPane.showMessageDialog(null, "ID does not exist", "Retry search", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void displayRecently(String inputID){
        ArrayList<String> recentHistory;
        recentHistory = recentArray(inputID);
        String emptyHistory = "";
        for (int i = 0; i < recentHistory.size();i++){
            emptyHistory += recentHistory.get(i);
            emptyHistory += "  ,  ";
        }
        historyText.setText(emptyHistory);   
        historyText.setFont(new Font("Serif", Font.BOLD, 25));
    }

    ///////////////////////////////////////////////////////////////////////////
    public boolean exists(){
        String userID = IDText.getText();
        String sqlString = "Select * "
                     + "From userwatchlist "
                     + "Where userid = '" + userID + "'; ";
        int count = 0;
        openDB();

        try {
            Statement stmt = conn.createStatement();

            ResultSet res = stmt.executeQuery(sqlString);

            while(res.next()) {
                count++;
            }

        } catch (Exception e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        // db_close();
        closeDB();

        System.out.println(count);

        if(count > 0) {
            return true;
        } 

        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void actionPerformed(ActionEvent e) {
        String userPicked;
        String displayPicked;
        //events for when genre is picked update the textGenre
        for (int i = 0; i < numBtns; i++){
                
                if (i < numGenre){  //when the button clicked is in gerne options
                    if (e.getSource() == functionButtons[i]){
                        searching = false;
                        functionButtons[prevGenre].setBackground(null);
                        textGenre.setText(String.valueOf(functionButtons[i].getText()));
                        functionButtons[i].setBackground(Color.orange);
                        displayPicked = textGenre.getText() + " " + textType.getText();
                        displayMain(displayPicked);
                        prevGenre = i;
                    }
                }
                else if (i < numBtns-3){   //when the button clicked is in type of show TV/movie button
                    if (e.getSource() == functionButtons[i]){
                        searching = false;
                        functionButtons[prevType].setBackground(null);
                        textType.setText(String.valueOf(functionButtons[i].getText()));
                        functionButtons[i].setBackground(Color.yellow);
                        displayPicked = textGenre.getText() + " " + textType.getText();
                        displayMain(displayPicked);
                        prevType = i;
                    }
                }
                else if (i < numBtns-2){   //submit button
                    searching = true;
                    if (e.getSource() == functionButtons[i]){
                        functionButtons[prevType].setBackground(null);
                        functionButtons[prevGenre].setBackground(null);
                        textGenre.setText("Search:");
                        textType.setText(textfield.getText());
                        displayPicked = textGenre.getText() + " " + textType.getText();
                        // displayMain(displayPicked);
                        titleSearched = textType.getText();
                        Vector <String> infoTitle = movieinfo(titleSearched);
                        String information = "";
                        for (int j = 0; j < infoTitle.size();j++){
                            information += infoTitle.get(j);
                        }
                        label1.setText(information);
                    }
                }
             
                else if (i < numBtns-1){  //clicked to profile setting
                    if (e.getSource() == functionButtons[i]){
                        savedID = IDText.getText();
                        displayRecently(savedID);
                        displayProfile();
                    }
                }
                else{
                    if (e.getSource() == functionButtons[i]){
                        save();
                        saveLabel.setVisible(true);
                    }
                    //save button was clicked
                }
            textGenre.setFont(new Font("Serif", Font.BOLD, 25));
            textType.setFont(new Font("Serif", Font.BOLD, 25));
        }
    }
}
