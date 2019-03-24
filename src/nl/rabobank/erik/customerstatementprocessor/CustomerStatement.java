package nl.rabobank.erik.customerstatementprocessor;

import java.math.BigDecimal;
import java.util.Comparator;

public class CustomerStatement {
    public String reference;
    String accountNumber;
    String description;
    BigDecimal startBalance;
    BigDecimal mutation;
    BigDecimal endBalance;

    public CustomerStatement(String csvLine){
        String[] csvData=csvLine.split(",");
        reference=csvData[0];
        accountNumber=csvData[1];
        description=csvData[2];
        startBalance= new BigDecimal(csvData[3]);
        mutation=new BigDecimal(csvData[4]);
        endBalance= new BigDecimal(csvData[5]);
    }

    public boolean hasValidEndBalance(){
        return (startBalance.add(mutation).equals(endBalance));
    }


}
