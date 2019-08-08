package House_Committee.db;
import House_Committee.Committee;
import House_Committee.Person;
import House_Committee.Server.Server;
import House_Committee.Tenant;

import java.sql.*;


public class sqlHandler {

    private static Connection connect;
    private static final String DBNAME = "house_committee";

    public static Boolean[] userLogin(String userName, String hashedPassword) {
        Boolean[] b= new Boolean[]{false,false};
        try {
            String user = "select userId, userName, hashedPassword, role from "+DBNAME+".users where userName = ?";
            PreparedStatement statement = connect.prepareStatement(user);
            statement.setString(1, userName);
            ResultSet result = statement.executeQuery();

            if (result.next())
            {
                System.out.println("hashedPassword " +result.getString("hashedPassword"));
                if(result.getString("userName").equals(userName) &&
                        result.getString("hashedPassword").equals(hashedPassword))
                {
                    statement = connect.prepareStatement("UPDATE "+DBNAME+".users SET lastLogin =?  WHERE userId = "+result.getString("userId"));
                    statement.setTimestamp(1,new Timestamp(System.currentTimeMillis()));
                    statement.execute();
                    if(result.getString("role").equals("Tenant")) {
                        b[0] = true; // successful login
                        b[1] = false; // is Committee
                    }
                    else if(result.getString("role").equals("Committee")) {
                        b[0] = true; // successful login
                        b[1] = true; // is Committee
                    }
                }
            }


        } catch (SQLException e) {
              e.printStackTrace();
        }
        return b;
    }

    public static void insert_user(Person person) {

        String sqlInsert = "insert into "+DBNAME+".users (`userName`,`lastName`,`firstName`,`hashedPassword`,`registrationDate`,`lastLogin`,`buildingNumber`,`apartmentNumber`,`role`)" +
                " values (?,?,?,?,?,?,?,?,?)";
        System.out.println("person.getRole() "+person.getRole());
        try {
            PreparedStatement pst = connect.prepareStatement(sqlInsert);
            pst.setString(1, person.getUserName());
            pst.setString(2, person.getLastName());
            pst.setString(3, person.getFirstName());
            pst.setString(4, person.getHashedPassword());
            pst.setTimestamp(5,  person.getRegistrationDate());
            pst.setTimestamp(6,  person.getLastLogin());
            pst.setString(7, person.getBuildingNumber());
            pst.setString(8, person.getApartmentNumber());
            pst.setString(9, person.getRole());
            pst.execute();



        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public static void ChangePassword(String userName, String oldPassword,String newPass) throws Exception {

        try {
            if(userLogin(userName, oldPassword)[0]) // check if old password is correct
            {
                String query = "UPDATE "+DBNAME+".users SET hashedPassword =? WHERE userName = ?";
                PreparedStatement statement = connect.prepareStatement(query);
                statement.setString(1,newPass);
                statement.setString(2,userName);
                statement.execute();
            }
            else
            {
                throw new Exception("wrong username or password");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public static void set_monthly_payment(String monthlyPayment) {
        String query = "insert into "+DBNAME+".tenants (`userId`, `monthlyPayment`)"+
                " values(LAST_INSERT_ID(), ?)";
        try {
            PreparedStatement pst = connect.prepareStatement(query);
            pst.setString(1,monthlyPayment);
            pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void set_seniority(String seniority) {
        String query = "insert into "+DBNAME+".committees (`userId`, `seniority`)"+
                " values(LAST_INSERT_ID(), ?)";
        try {
            PreparedStatement pst = connect.prepareStatement(query);
            pst.setString(1,seniority);
            pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String printResultSet(ResultSet result, PreparedStatement statement) throws SQLException {
        String table ="**";
        ResultSetMetaData meta = statement.getMetaData();
        int columnCount = meta.getColumnCount();
        int i=0;
        while(result.next())
            {
                if(i ==0) {
                    for (int column = 1; column <= columnCount; ++column) {

                        table += (meta.getColumnName(column) + "^");
                    }
                }
                table += Server.SPACIALLINEBREAK;

                i++;
                for (int column = 1; column <= columnCount; ++column) {
                    Object value = result.getObject(column);
                        table += (value + "^");
                }
            }
        return  table;
    }
    public static Person getTenantByUserName(String userName)
    {

        String query = "select `idCommittee` as \"id\",`firstName`,  `lastName`,  `userName`,  `hashedPassword`, `lastLogin`,  `registrationDate`,`seniority`, `apartmentNumber`,`buildingNumber`,  `role` , \"\" as \"monthlyPayment\"\n" +
                "from "+DBNAME+".users\n" +
                "join "+DBNAME+".committees on  users.userId = committees.userId\n" +
                "where userName = ? \n" +
                "union\n" +
                "select `idTenants`,`firstName`,  `lastName`,  `userName`,  `hashedPassword`, `lastLogin`,  `registrationDate`,''as \"seniority\", `apartmentNumber`,`buildingNumber`,  `role`, `monthlyPayment`\n" +
                "from "+DBNAME+".users\n" +
                "join "+DBNAME+".tenants on users.userId = tenants.userId\n" +
                "where userName = ?";


        try {
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setString(1, userName);
            statement.setString(2, userName);
            ResultSet result = statement.executeQuery();

            String type;
            ResultSetMetaData meta = statement.getMetaData();
            int columnCount = meta.getColumnCount();
            int i=0;
            while(result.next())
            {
                type = result.getString("role");
                if(type.equals("Tenant"))
                {
                    Person tenant = new Tenant(
                            result.getString("id"),
                            result.getString("firstName"),
                            result.getString("lastName"),
                            result.getString("userName"),
                            result.getString("hashedPassword"),
                            result.getTimestamp("lastLogin"),
                            result.getTimestamp("registrationDate"),
                            result.getString("apartmentNumber"),
                            result.getString("buildingNumber"),
                            result.getString("role"),
                            result.getDouble("monthlyPayment")
                            );
                    return tenant;
                }
                else if (type.equals("Committee"))
                {
                    Person committee = new Committee(
                            result.getString("id"),
                            result.getString("firstName"),
                            result.getString("lastName"),
                            result.getString("userName"),
                            result.getString("hashedPassword"),
                            result.getString("seniority"),
                            result.getTimestamp("lastLogin"),
                            result.getTimestamp("registrationDate"),
                            result.getString("apartmentNumber"),
                            result.getString("buildingNumber"),
                            result.getString("role")
                    );
                    return committee;
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return null;
    }
    // 1
    public static String getPaymentByTenantId(String apartmentNumber,String buildingNumber)
    {
        String query ="select *\n" +
                "from(\n" +
                "select paymentDate, paymentSum, tenants.idTenants,users.apartmentNumber, users.buildingNumber\n" +
                "from "+DBNAME+".payments\n" +
                "join "+DBNAME+".tenants on "+DBNAME+".tenants.idTenants = "+DBNAME+".payments.idTenants\n" +
                "join "+DBNAME+".users on "+DBNAME+".users.userId = "+DBNAME+".tenants.userId\n" +
                ") as u\n" +
                "where u.buildingNumber = ? and u.apartmentNumber = ?\n" +
                "order by paymentDate;";

        try {
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setString(1, buildingNumber);
            statement.setString(2, apartmentNumber);
            return select_query(statement);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // 2
    public static String getAllPaymentsByBuilding(String buildingNumber) {

        String query ="select *\n" +
                "from (\n" +
                "select paymentDate, paymentSum,users.apartmentNumber,concat(users.firstName,\" \", users.lastName) as \"name\", users.buildingNumber  -- tenants.idTenants as \"idTenants\"\n" +
                "from "+DBNAME+".payments\n" +
                "join "+DBNAME+".tenants on tenants.idTenants = payments.idTenants\n" +
                "join "+DBNAME+".users on users.userId = tenants.userId\n" +
                ") as u\n" +
                "where u.buildingNumber = ?" +
                "order by u.paymentDate;";
        try {
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setString(1, buildingNumber);

            return select_query(statement);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // 3
    public static int setPaymentByTenantId(String apartmentNumber, Double paymentSum, String paymentDate,String buildingNumber ) {

        String query ="INSERT into "+DBNAME+".payments\n" +
                "(`paymentSum`,\n" +
                "`idTenants`,\n" +
                "`paymentDate`)\n" +
                "select ?, u.idTenants, ?\n" +
                "from \n" +
                "(select idTenants\n" +
                "from "+DBNAME+".users\n" +
                "join "+DBNAME+".tenants\n" +
                "on "+DBNAME+".users.userId = "+DBNAME+".tenants.userId\n" +
                    "where role = \"Tenant\" and buildingNumber = ? and "+DBNAME+".users.apartmentNumber = ?) as u";

        try {
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setDouble(1, paymentSum);
            
            statement.setString(2,paymentDate);
            statement.setString(3,buildingNumber);
            statement.setString(4,apartmentNumber);
            return statement.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }
    // 4
    public static String getSumPaymentsByBuilding(String buildingNumber) {

        String query ="select sum(paymentSum) as \"sum\", month(paymentDate) as \"month\"\n" +
                "from (\n" +
                "select paymentDate, paymentSum, tenants.idTenants as \"idTenants\", users.buildingNumber\n" +
                "from "+DBNAME+".payments\n" +
                "join "+DBNAME+".tenants on tenants.idTenants = payments.idTenants\n" +
                "join "+DBNAME+".users on users.userId = tenants.userId\n" +
                ") as u\n" +
                "where u.buildingNumber = ?\n" +
                "group by month(u.paymentDate)";
        try {
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setString(1, buildingNumber);

            return select_query(statement);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String select_query(PreparedStatement statement) {

        try {
          //  PreparedStatement statement = connect.prepareStatement("select * from " + DBNAME + ".users");
            ResultSet result = statement.executeQuery();
            return printResultSet(result, statement);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static void connection()
    {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connected to jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void ConnectingToSQL()
    {
        connection();
        String host = "jdbc:mysql://127.0.0.1:3306?serverTimezone=UTC";
        String username = "root";
        String password = ""; //

        try {
            connect = DriverManager.getConnection(host, username, password);
            System.out.println("Connected to SQL");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }




}
