package House_Committee;


import java.sql.Timestamp;

public class Committee extends Person{

    private String seniority;

    public Committee(String id,String firstName, String lastName, String userName, String hashedPassword,
                     String seniority, Timestamp lastLogin, Timestamp registrationDate,String apartmentNumber,
                     String buildingNumber, String role) {
        super(id,firstName, lastName, userName, hashedPassword,lastLogin,registrationDate,apartmentNumber,buildingNumber,role );
        this.seniority = seniority;

    }

    public void getMonthlyPaymentById(int apartmentNumber)
    {

    }
    public void getAllMonthlyPayments()
    {

    }
    public void getPaymentByMonth()
    {

    }
    public void setPayments()
    {

    }

    @Override
    public String toString() {
        return super.toString()+ " Committee{" +
                "seniority='" + seniority + '\'' +
                '}';
    }
}
