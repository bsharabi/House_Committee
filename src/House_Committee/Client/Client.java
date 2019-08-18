package House_Committee.Client;

import House_Committee.Encoder;
import House_Committee.MainView;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class Client {

    // ------------------------------------------ Member Variable ----------------------------------------------//

    public static String LineBreak = "\\#\\$";
    static HashMap<String, String> userDetails = new HashMap<>(); //The HashMap responsible to the details of client
    static Scanner scanner = new Scanner(System.in);// This variable responsible to the read
    static JPanel mainPanel; //This variable responsible to the gui
    static JPanel panel;//This variable responsible to the gui
    static MainView mainView;
    static DataOutputStream outToServer;
    static BufferedReader inFromServer;
    static Socket clientSocket;// server ip and port
    static String strFromServer;// This variable may contain the response from the server
    // ---------------------------------------------- Function ------------------------------------------------//

    public static void main(String argv[]) throws Exception {

        clientSocket = new Socket("localhost", 12000); // server ip and port

        // pipe for send data to the server
        outToServer = new DataOutputStream(clientSocket.getOutputStream());
        // pipe for get data from the server
        inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        try {
            System.out.println(inFromServer.readLine()); // getting from server

            while (true) {
                strFromServer = inFromServer.readLine(); //Read string from client {"Login","Registered","connected"}

                switch (strFromServer) {
                    case "Login":
                        userDetails = loginOrRegister();
                        while (userDetails == null) // while user not connected
                            userDetails = loginOrRegister();
                        break;
                    // in case of successful registration (this come only from the server!!)
                    case "Registered":
                        Massage("Registered  Successfully, Please log in to your account\n", "Registered  Successfully", "Ok");
                        continue;
                    case "connected":
                        String resp = inFromServer.readLine();
                        if (resp.startsWith("true")) //&& userDetails.get("role").equals("Tenant")
                        {
                            Massage("You are connected\n", "Connected  Successfully", "Ok");
                            getMainMenu(outToServer, inFromServer, resp);
                        }
                        break;
                    case "true true":
                    case "true false":
                        getMainMenu(outToServer, inFromServer, strFromServer);
                        break;

                }
                switch (userDetails.get("Operation")) {
                    case "Exit":
                        // if the user close the form from the X or Cancel Button
                        Massage("GoodBey!", "You are clicked to EXIT!", "Ok");
                        exit(inFromServer, outToServer);
                        break;
                    case "Register": // get more info from the user by the command line
                        userDetails = getRegDetail(userDetails);
                        if (userDetails == null)
                            exit(inFromServer, outToServer);
                        // there is no break because we need to send userDetails to the server in both cases

                    case "Login":

                        // in case the user choose login option
                        // convert the array to string and sent it to server
                        outToServer.writeBytes(arrToStr(userDetails));
                        if (userDetails.get("Operation").equals("Register")) {
                            break;
                        }
                        String resp = inFromServer.readLine();
                        if (resp.startsWith("false")) {
                            Massage("Authentication failed, username or password incorrect.\n" +
                                    "please try again", inFromServer.readLine(), "Error");
                        } else if (resp.startsWith("true")) //&& userDetails.get("role").equals("Tenant")
                        {
                            Massage(inFromServer.readLine(), "The connection was successful", "Ok");
//                            getMainMenu(outToServer, inFromServer, resp);
                        } else
                            userDetails = null;
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------//

    private static void getMainMenu(DataOutputStream outToServer, BufferedReader inFromServer, String resp) {
        try {
            String response;
            int option;
            userDetails.put("Operation", "Menu");

            outToServer.writeBytes(arrToStr(userDetails)); // send to server the menu request
            response = inFromServer.readLine().replaceAll(LineBreak, "\n");//Get from server response

            boolean flag = true;
            while (flag) {
                mainPanel = new JPanel();
                mainPanel.setLayout(new GridLayout(0, 1));
                panel = new JPanel();
                panel.setLayout(new GridLayout(0, 1));
                panel.add(new JLabel(response));
                panel.add(new JLabel("Welcome to the House Committee Program!"));
                mainPanel.add(panel);

                if (resp.split(" ")[1].equals("false")) { // if the user is Tenant

                    option = JOptionPane.showOptionDialog(null, mainPanel, "Menu",
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                            null, new String[]{"Change your Password", "get your payment history", "Logout"}, null);

                    switch (option) {
                        case 0:
                            userDetails = changePassword();
                            while (userDetails.get("Operation").equals("Faild"))
                                userDetails = changePassword();
                            if (userDetails.get("Operation").equals("Back")) {
                                outToServer.writeBytes("choice:99" + "\n");
                                flag = false;
                                break;
                            }
                            outToServer.writeBytes("0:0 " + arrToStr(userDetails));
                            userDetails.remove("oldPassword");
                            userDetails.remove("newPassword");
                            Massage(inFromServer.readLine(), "Massage", "Ok");
                            flag = false;
                            break;
                        case 1:
                            outToServer.writeBytes("1\n");
                            new MainView(inFromServer.readLine(), inFromServer.readLine());
                            flag = false;
                            break;
                        case JOptionPane.CLOSED_OPTION:
                        case 2:
                            System.out.println("BYE");
                            exit(inFromServer, outToServer);
                            flag = false;
                            break;

                        default:
                            System.out.println("not a valid Choice");
                            continue;
                    }

                } else // if the user is committee
                {
                    option = JOptionPane.showOptionDialog(null, mainPanel, "Menu",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                            null, new String[]{"Change your Password", "get your payment history", "get all payment", "insert Payment by Apartment", "get all  summarised payment", "Logout"}, "0");
                    switch (option) {
                        case 0:
                            userDetails = changePassword();
                            while (userDetails.get("Operation").equals("Faild"))
                                userDetails = changePassword();
                            if (userDetails.get("Operation").equals("Back")) {
                                outToServer.writeBytes("choice:99" + "\n");
                                flag = false;
                                break;
                            }
                            outToServer.writeBytes("choice:0 " + arrToStr(userDetails));
                            userDetails.remove("oldPassword");
                            userDetails.remove("newPassword");
                            flag = false;
                            break;
                        case 1:
                            mainPanel = new JPanel();
                            mainPanel.setLayout(new GridLayout(0, 1));
                            panel = new JPanel();
                            panel.setLayout(new GridLayout(0, 1));

                            panel.add(new JLabel("Please enter Apartment Number Id"));
                            JTextField Sentence = new JTextField(15);
                            panel.add(Sentence);
                            mainPanel.add(panel);
                            while (true) {
                                JOptionPane.showOptionDialog(null, mainPanel, "Choose Apartment",
                                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                                        null, new String[]{"Send"}, "0");
                                if (!Sentence.getText().equals(""))
                                    break;
                                else
                                    Massage("Please try again", "Error", "Error");
                            }
                            outToServer.writeBytes("choice:1 ApartmentNumber:" + Sentence.getText() + "\n");
                            flag = false;
                            break;
                        case 2:
                            outToServer.writeBytes("choice:2\n");
                            flag = false;
                            break;
                        case 3:
                            mainPanel = new JPanel();
                            mainPanel.setLayout(new GridLayout(0, 1));
                            panel = new JPanel();
                            panel.setLayout(new GridLayout(0, 1));

                            panel.add(new JLabel("Please enter Apartment Number"));
                            JTextField Apartment = new JTextField(15);
                            panel.add(Apartment);


                            panel.add(new JLabel("Please enter payment Sum"));
                            JTextField payment = new JTextField(15);
                            panel.add(payment);


                            panel.add(new JLabel("Please enter payment Date: (yyyy-MM-dd)"));
                            JTextField Date = new JTextField(15);
                            panel.add(Date);
                            mainPanel.add(panel);
                            while (true) {
                                JOptionPane.showOptionDialog(null, mainPanel, "Insert Payment",
                                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                                        null, new String[]{"Send"}, "0");
                                if (Apartment.getText().equals("") || payment.getText().equals("") || Date.getText().equals(""))
                                    Massage("Please try again", "Error", "Error");
                                else
                                    break;
                            }

                            outToServer.writeBytes("choice:3 ApartmentNumber:" + Apartment.getText());
                            outToServer.writeBytes(" paymentSum:" + payment.getText());
                            outToServer.writeBytes(" paymentDate:" + Date.getText() + "\n");
                            flag = false;
                            break;
                        case 4:
                            outToServer.writeBytes("choice:4\n");
                            flag = false;
                            break;
                        case JOptionPane.CLOSED_OPTION:
                        case 5:
                            System.out.println("BYE");
                            exit(inFromServer, outToServer);
                            flag = false;
                            break;
                        default:
                            System.out.println("not a valid Choice");
                            continue;
                    }
                    String FromServer = inFromServer.readLine();
                    if (FromServer.startsWith("**"))
                        new MainView(FromServer, inFromServer.readLine());
                    else if (!userDetails.get("Operation").equals("Back"))
                        Massage(FromServer, "Massage", "Ok");

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------//

    private static String arrToStr(HashMap<String, String> list) {
        String listString = "";

        for (String x : list.keySet()) {
            listString += x + ":" + list.get(x) + " ";
        }
        return listString + "\n";
    }

    //-----------------------------------------------------------------------------------------------------------------------------//

    private static HashMap<String, String> getRegDetail(HashMap<String, String> userDetails) {

        String seniority = null, roomNum = null;
        int optionPanel;
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 1));

        panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        panel.add(new JLabel("Please choose the most appropriate option"));

        mainPanel.add(panel);


        int option = JOptionPane.showOptionDialog(null, mainPanel, "Choose what you are?",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, new String[]{"Committee", "Tenant"}, null);

        JLabel NameLabel = new JLabel("First name:");
        JLabel lastNameLabel = new JLabel("Last name:");
        JLabel seniorityLabel = new JLabel("Seniority:");
        JLabel BuildingLabel = new JLabel("Building Number:");
        JLabel ApartmentLabel = new JLabel("Apartment Number:");
        JLabel roomsLabel = new JLabel("Number room:");

        JTextField nameText = new JTextField(15);
        JTextField lastNameText = new JTextField(15);
        JTextField seniorityText = new JTextField(15);
        JTextField BuildingText = new JTextField(15);
        JTextField ApartmentText = new JTextField(15);
        JTextField roomsText = new JTextField(15);


        switch (option) {
            case 0: // in case the choose is committee

                mainPanel = new JPanel();
                mainPanel.setLayout(new GridLayout(0, 1));

                panel = new JPanel();
                panel.setLayout(new GridLayout(0, 1));

                panel.add(NameLabel);
                panel.add(nameText);

                panel.add(lastNameLabel);
                panel.add(lastNameText);

                panel.add(seniorityLabel);
                panel.add(seniorityText);

                panel.add(BuildingLabel);
                panel.add(BuildingText);

                panel.add(ApartmentLabel);
                panel.add(ApartmentText);

                mainPanel.add(panel);

                optionPanel = JOptionPane.showOptionDialog(null, mainPanel, "Login Form",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, new String[]{"Back", "OK", "Cancel"}, null);

                if (optionPanel == 0) {
                    userDetails = getRegDetail(userDetails);
                } else if (optionPanel == 1) {
                    userDetails.put("firstName", nameText.getText());
                    userDetails.put("lastName", lastNameText.getText());
                    userDetails.put("role", "Committee");

                    try {
                        seniority = Integer.parseInt(seniorityText.getText()) + ""; // try the input to int in order to validate
                        userDetails.put("seniority", seniority);
                    } catch (NumberFormatException e) {
                        System.out.println("not a valid input, please try again");
                        userDetails = getRegDetail(userDetails);
                    }
                    userDetails.put("buildingNumber", BuildingText.getText());
                    userDetails.put("apartmentNumber", ApartmentText.getText());
                } else {
                    return null;
                }

                break;

            case 1: // in case the choose is Tenant
                mainPanel = new JPanel();
                mainPanel.setLayout(new GridLayout(0, 1));

                panel = new JPanel();
                panel.setLayout(new GridLayout(0, 1));

                panel.add(NameLabel);
                panel.add(nameText);

                panel.add(lastNameLabel);
                panel.add(lastNameText);

                panel.add(roomsLabel);
                panel.add(roomsText);

                panel.add(BuildingLabel);
                panel.add(BuildingText);

                panel.add(ApartmentLabel);
                panel.add(ApartmentText);

                mainPanel.add(panel);

                optionPanel = JOptionPane.showOptionDialog(null, mainPanel, "Login Form",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, new String[]{"Back", "OK", "Cancel"}, null);
                if (optionPanel == 0) {
                    userDetails = getRegDetail(userDetails);
                } else if (optionPanel == 1) {
                    userDetails.put("firstName", nameText.getText());
                    userDetails.put("lastName", lastNameText.getText());
                    userDetails.put("role", "Tenant");

                    try {
                        roomNum = Integer.parseInt(roomsText.getText()) + ""; // try the input to int in order to validate
                        userDetails.put("monthlyPayment", roomNum);
                    } catch (NumberFormatException e) {
                        System.out.println("not a valid input, please try again");
                        userDetails = getRegDetail(userDetails);
                    }
                    userDetails.put("buildingNumber", BuildingText.getText());
                    userDetails.put("apartmentNumber", ApartmentText.getText());
                } else {
                    return null;
                }
                break;
            case JOptionPane.CLOSED_OPTION:
                Massage("GoodBye", "Closed", "Ok");
                return null;
            default:
                return null;
        }

        return userDetails;
    }

    //-----------------------------------------------------------------------------------------------------------------------------//

    private static HashMap<String, String> changePassword() throws IOException {

        int optionPanel;
        HashMap<String, String> res = new HashMap<>();
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 1));

        panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        JLabel oldPassLabel = new JLabel("Old Password:");
        JLabel passLabel1 = new JLabel("Enter a new  password:");
        JLabel passLabel2 = new JLabel("Retype the new password:");

        JTextField oldPassText = new JPasswordField(10);
        JPasswordField pass1Text = new JPasswordField(10);
        JPasswordField pass2Text = new JPasswordField(10);

        panel.add(oldPassLabel);
        panel.add(oldPassText);
        panel.add(passLabel1);
        panel.add(pass1Text);
        panel.add(passLabel2);
        panel.add(pass2Text);

        mainPanel.add(panel);
        optionPanel = JOptionPane.showOptionDialog(null, mainPanel, "Login Form",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, new String[]{"OK", "Back"}, null);

        switch (optionPanel) // pressing OK button
        {
            case 0:
                String password = new String(pass1Text.getPassword());
                String password2 = new String(pass2Text.getPassword());
                if (password.equals(password2) && password.length() > 0 && !oldPassText.getText().equals("")) {
                    res.put("Operation", "PasswordChange");
                    res.put("oldPassword", Encoder.strEncoder(oldPassText.getText(), "SHA-256"));
                    res.put("newPassword", Encoder.strEncoder(password, "SHA-256"));
                    return res;
                } else {
                    Massage("two Password must be equals or old pass worng", "Error", "Error");
                    res.put("Operation", "Faild");
                    return res;
                }
            case JOptionPane.CLOSED_OPTION: // on form close
                exit(inFromServer, outToServer);
            case 1: // on Cancel click
                res.put("Operation", "Back");
                return res;
        }
        return null;
    }

    //-----------------------------------------------------------------------------------------------------------------------------//

    private static HashMap<String, String> register() {

        HashMap<String, String> res = new HashMap<>();
        int optionPanel;
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 1));

        panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        JLabel userLabel = new JLabel("user name:");
        JLabel passLabel1 = new JLabel("Enter a password:");
        JLabel passLabel2 = new JLabel("Retype password:");

        JTextField userText = new JTextField(10);
        JPasswordField pass1 = new JPasswordField(10);
        JPasswordField pass2 = new JPasswordField(10);

        panel.add(userLabel);
        panel.add(userText);
        panel.add(passLabel1);
        panel.add(pass1);
        panel.add(passLabel2);
        panel.add(pass2);

        mainPanel.add(panel);

        optionPanel = JOptionPane.showOptionDialog(null, mainPanel, "Register Form",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, new String[]{"OK", "Back"}, null);

        switch (optionPanel) // pressing OK button
        {
            case 0:
                String password = new String(pass1.getPassword());
                String password2 = new String(pass2.getPassword());
                if (password.equals(password2) && password.length() > 0) {
                    res.put("Operation", "Register");
                    res.put("userName", userText.getText());
                    res.put("Password", Encoder.strEncoder(password, "SHA-256"));
                    return res;
                } else {
                    Massage("two Password must be equals", "Error", "Error");
                    return register();
                }
            case JOptionPane.CLOSED_OPTION: // on form close
                res.put("Operation", "Exit");
                return res;
            case 1: // on Cancel click
                return null;
        }
        return null;
    }

    //-----------------------------------------------------------------------------------------------------------------------------//

    private static HashMap<String, String> loginOrRegister() {

        HashMap<String, String> res = new HashMap<>();

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 1));

        panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        JLabel userlabel = new JLabel("user name:");
        JLabel passlabel = new JLabel("Enter a password:");

        JTextField userText = new JTextField(10);
        JPasswordField passText = new JPasswordField(10);

        panel.add(userlabel);
        panel.add(userText);

        panel.add(passlabel);
        panel.add(passText);

        mainPanel.add(panel);

        int option = JOptionPane.showOptionDialog(null, mainPanel, "Login Form",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, new String[]{"Login", "Register", "Cancel"}, null);

        switch (option) // pressing OK button
        {
            case 0:
                res.put("Operation", "Login");
                res.put("userName", userText.getText());
                res.put("Password", Encoder.strEncoder(new String(passText.getPassword()), "SHA-256"));
                if (userText.getText().equals("") || passText.getPassword().length <= 0) {
                    Massage("Something went wrong, Please Try again!", "Error", "Error");
                    res = null;
                }
                return res;
            case 1: // on click on Register
                return register();

            case JOptionPane.CLOSED_OPTION: // on form close
            case 2: // on Cancel
                res.put("Operation", "Exit");
                return res;
        }
        return null;
    }

    //-----------------------------------------------------------------------------------------------------------------------------//

    private static void Massage(String massage, String title, String option) {
        if (option.equals("Error")) {
            JOptionPane.showMessageDialog(null,
                    massage,
                    title,
                    JOptionPane.ERROR_MESSAGE);
        } else if (option.equals("Ok")) {

            JOptionPane.showMessageDialog(null,
                    massage,
                    title,
                    JOptionPane.PLAIN_MESSAGE);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------//

    // This function responsible to the close the client
    private static void exit(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException {
        inFromServer.close(); // close all the connection
        outToServer.close();
        System.exit(1); // Exit from the process
    }

    //-----------------------------------------------------------------------------------------------------------------------------//
}
