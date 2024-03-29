package House_Committee.Server;

import House_Committee.Committee;
import House_Committee.Person;
import House_Committee.Tenant;
import House_Committee.db.sqlHandler;


import java.io.*;
import java.net.DatagramPacket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.HashMap;

public class socketHandler extends Thread {
    //---------------------------------------------------- Member Variable --------------------------------------------------------------//
    private Socket incoming;
    static sqlHandler sql;
    private int _port;
    public Boolean[] isLoggedIn = new Boolean[]{false, false};
    private final int PAYMET_PER_ROOM = 70;
    private String fromClient;                                  // This variable may contain the response from the client
    private HashMap<String, String> details = new HashMap<>();  //The HashMap responsible to the details of client
    private String[] clientArr = null;                          // This variable may contain the response from the client in array String
    private Person person;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;

    //---------------------------------------------------- Constructor --------------------------------------------------------------//

    public socketHandler(Socket _in, sqlHandler _sql, int port) {
        incoming = _in;
        sql = _sql;
        _port = port;
    }

//--------------------------------------------------------------------------------------------------------------------------------//

    public void run() {

        try {

            // pipe for get data from the client
            inFromClient = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
            // pipe for send data to the client
            outToClient = new DataOutputStream(new DataOutputStream(incoming.getOutputStream()));

            outToClient.writeBytes("connected to server " + incoming.getInetAddress() + "/" + _port + "\n");

            while (true) {
                // First of all check if the user is logged in and if not Send him login Request
                if (!isLoggedIn[0]) // the zero place store the connection state
                    outToClient.writeBytes("Login\n");
                else
                    outToClient.writeBytes(isLoggedIn[0] + " " + isLoggedIn[1] + "\n");

                fromClient = inFromClient.readLine();

                if (fromClient != null) {
                    System.out.println("fromClient " + fromClient);
                    clientArr = fromClient.split(" ");
                    details = stringToHashMap(clientArr, details); // convert the string from the client to hash map
                    switch (details.get("Operation")) {
                        case "Login":
                            System.out.println("Login in SocketHandler");
                            isLoggedIn = sqlHandler.userLogin(details.get("userName"), details.get("Password"));
                            outToClient.writeBytes(isLoggedIn[0] + " " + isLoggedIn[1] + "\n");
                            if (isLoggedIn[0])
                                outToClient.writeBytes("connected successful!!\n");
                            else
                                outToClient.writeBytes("Connection failed!!\n");
                            break;
                        case "Register":
                            System.out.println("register");
                            sqlHandler.insert_user(new Person(
                                    null,
                                    details.get("firstName"),
                                    details.get("lastName"),
                                    details.get("userName"),
                                    details.get("Password"),
                                    new Timestamp(System.currentTimeMillis()),
                                    new Timestamp(System.currentTimeMillis()),
                                    details.get("apartmentNumber"),
                                    details.get("buildingNumber"),
                                    details.get("role")));
                            if (details.get("role").equals("Committee")) {
                                // add seniority to db
                                sqlHandler.set_seniority(details.get("seniority"));
                            } else if (details.get("role").equals("Tenant")) {
                                // add monthly payment to db
                                sqlHandler.set_monthly_payment(Integer.parseInt(details.get("monthlyPayment")) * PAYMET_PER_ROOM + "");
                            }
                            outToClient.writeBytes("Registered\n");
                            break;
                        case "Menu":
                            person = sqlHandler.getTenantByUserName(details.get("userName"));
                            String result;

                            outToClient.writeBytes("Hi " + person.getFirstName() + "!" + Server.SPACIALLINEBREAK + "\n");

                            if (person instanceof Tenant) {

                                fromClient = inFromClient.readLine();
                                System.out.println(fromClient);
                                if (fromClient.equals("1")) {
                                    outToClient.writeBytes(sqlHandler.getPaymentByTenantId(person.getApartmentNumber(), person.getBuildingNumber()) + "\n");
                                    outToClient.writeBytes("Sum of Payments to Apartment Number: " + person.getApartmentNumber() + "\n");
                                    break;
                                } else if (fromClient.startsWith("0")) {
                                    outToClient.writeBytes(changePassword(clientArr, fromClient, details, person) + "\n");
                                    isLoggedIn[0] = false;
                                    isLoggedIn[1] = false;
                                    break;
                                } else
                                    break;
                            } else if (person instanceof Committee) {

                                fromClient = inFromClient.readLine();
                                details = stringToHashMap(fromClient.split(" "), details);
                                switch (details.get("choice")) {
                                    case "0":
                                        outToClient.writeBytes(changePassword(clientArr, fromClient, details, person) + "\n");
                                        isLoggedIn[0] = false;
                                        isLoggedIn[1] = false;
                                        break;
                                    case "1":
                                        result = sqlHandler.getPaymentByTenantId(details.get("ApartmentNumber"), person.getBuildingNumber());

                                        if (result.length() <= 0) {

                                            outToClient.writeBytes("Not found" + "\n");
                                        } else {
                                            outToClient.writeBytes(result + "\n");
                                            outToClient.writeBytes("Sum of Payments to Apartment Number: " + details.get("ApartmentNumber") + "\n");

                                        }
                                        break;
                                    case "2":
                                        outToClient.writeBytes(sqlHandler.getAllPaymentsByBuilding(person.getBuildingNumber()) + "\n");
                                        outToClient.writeBytes("all payment for your building: " + person.getBuildingNumber() + "\n");

                                        break;
                                    case "3":
                                        int r = sqlHandler.setPaymentByTenantId(details.get("ApartmentNumber"), Double.parseDouble(details.get("paymentSum")),
                                                details.get("paymentDate"), person.getBuildingNumber());
                                        if (r > 0)
                                            outToClient.writeBytes("The listing was successful " + "\n");
                                        else
                                            outToClient.writeBytes("Apartment number not found!" + "\n");
                                        break;
                                    case "4":

                                        outToClient.writeBytes(sqlHandler.getSumPaymentsByBuilding(person.getBuildingNumber()) + "\n");
                                        outToClient.writeBytes("sum of payments for your building: " + person.getBuildingNumber() + "\n");
                                        break;
                                    case "99":
                                        outToClient.writeBytes("back\n");
                                        break;
                                }
                            }
                            break;
                    }
                }
            }
        } catch (SocketException e) {
            handleDisconnect();
        } catch (NullPointerException e) {
            handleDisconnect();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------------//

    private String changePassword(String[] clientArr, String fromClient, HashMap<String, String> details, Person person) {
        try {
            clientArr = fromClient.split(" ");
            details = stringToHashMap(clientArr, details);
            sqlHandler.ChangePassword(person.getUserName(), details.get("oldPassword"), details.get("newPassword"));
            return "Password was changed successfully, Please login again";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }
    //--------------------------------------------------------------------------------------------------------------------------------//

    private void handleDisconnect() {
        System.out.println("client Disconnected");
        synchronized (Server.waitObject) {
            Server.connected--;
        }
    }

    //---------------------------------------------------- Function  --------------------------------------------------------------//
    private HashMap<String, String> stringToHashMap(String[] arr, HashMap<String, String> details) {
        for (String s : arr) {
            String key, value;
            String[] f = s.split(":");
            key = f[0];
            value = f[1];
            details.put(key, value);
        }
        return details;
    }
}
