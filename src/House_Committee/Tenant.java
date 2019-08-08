package House_Committee;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

public class Tenant extends Person {


    private double monthlyPayment;
    private ArrayList<HashMap<String ,String>> paymentsArr;

    public Tenant(String id,String firstName, String lastName, String userName, String hashedPassword,
                  Timestamp lastLogin, Timestamp registrationDate,String apartmentNumber,
                  String buildingNumber, String role,double monthlyPayment ) {
        // call to main Person Object Constractor
        super(id,firstName, lastName, userName, hashedPassword,lastLogin,registrationDate,apartmentNumber,buildingNumber, role);
        this.monthlyPayment = monthlyPayment;
    }

    public void setMonthlyPayment(double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public int getAllPayment() {

        return 0;
    }


    @Override
    public String toString() {
        return super.toString()+ " Tenant{" +
                "monthlyPayment=" + monthlyPayment +
                ", paymentsArr=" + paymentsArr +
                '}';
    }
}
