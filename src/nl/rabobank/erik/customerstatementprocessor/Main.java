package nl.rabobank.erik.customerstatementprocessor;

import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.toCollection;

public class Main {
    private static List<CustomerStatement> customerStatements = new ArrayList<CustomerStatement>();


    private static void reportEndBalanceFailures(){
        for (CustomerStatement customerStatement:customerStatements) {
            if (! customerStatement.hasValidEndBalance()) {
                System.out.println("InvalidEndBalance, " + customerStatement.reference + "," + customerStatement.description);
            }
        }
    }

    private static void reportDuplicateReferences(){
        HashMap<String, Integer> referenceCounts = new HashMap<String, Integer>();

        // Count the occurrences of each reference
        for (CustomerStatement customerStatement:customerStatements) {
            if (referenceCounts.get(customerStatement.reference)== null) {
                referenceCounts.put(customerStatement.reference,1);
            }
            else {
                referenceCounts.put(customerStatement.reference,referenceCounts.get(customerStatement.reference)+1);
            }
        }

        // Get only references with a count > 1
        ArrayList<String> duplicateReferences = referenceCounts.entrySet()
                .parallelStream()
                .filter(reference->reference.getValue()>1)
                .map(reference -> reference.getKey())
                .collect(toCollection(ArrayList::new));

        // For each reference identified to have duplicates, collect all descriptions
        for (String duplicateReference : duplicateReferences){
            ArrayList<String> descriptions=customerStatements.parallelStream()
                    .filter(customerStatement -> customerStatement.reference.equals(duplicateReference))
                    .map(customerStatement -> customerStatement.description)
                    .collect(toCollection(ArrayList::new));

            for (String description: descriptions) {
                System.out.println("DuplicateReference, " + duplicateReference + "," + description);
            }
        }


    }

    private static void readInputFile(String fileName) {
        BufferedReader bufferedReader = null;
        try {
            String csvLine = "";
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"ISO-8859-1"));
            int lineCount=1;
            while ((csvLine = bufferedReader.readLine()) != null) {
                if (lineCount>1) {
                    CustomerStatement customerStatement = new CustomerStatement(csvLine);
                    customerStatements.add(customerStatement);
                }
                lineCount++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            //e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean validArguments(String[] args){
        return (args.length >0);
    }

    public static void main(String[] args) {
        if (validArguments(args)) {
            readInputFile(args[0]);
            reportEndBalanceFailures();
            reportDuplicateReferences();
        }
        else {
            System.out.println("No inputfile specified");
        }
    }
}
