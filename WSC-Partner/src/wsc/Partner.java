package wsc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.sforce.soap.partner.DescribeGlobalResult;
import com.sforce.soap.partner.DescribeGlobalSObjectResult;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.PicklistEntry;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.FieldType;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class Partner {
	
	private static PartnerConnection partnerConnection = null;
    private static BufferedReader reader = 
    	new BufferedReader(new InputStreamReader(System.in));
    
    public static void main(String[] args) {
        Partner partner = new Partner();
        if (partner.login()) {
            partner.describeGlobal();
            partner.describeSObjectsSample();
        }
    } 
    
    private String getUserInput(String prompt) {
        String result = "";
        try {
          System.out.print(prompt);
          result = reader.readLine();
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
        return result;
    }
    
    private boolean login() {
        boolean success = false;
        String username = getUserInput("Enter username: ");
        String password = getUserInput("Enter password: ");
        String authEndPoint = getUserInput("Enter auth end point: ");

        try {
          ConnectorConfig config = new ConnectorConfig();
          config.setUsername(username);
          config.setPassword(password);
          
          config.setAuthEndpoint(authEndPoint);
          config.setTraceFile("traceLogs.txt");
          config.setTraceMessage(true);
          config.setPrettyPrintXml(true);

          partnerConnection = new PartnerConnection(config);          

          success = true;
        } catch (ConnectionException ce) {
          ce.printStackTrace();
        } catch (FileNotFoundException fnfe) {
          fnfe.printStackTrace();
        }

        return success;
      }

	public void describeGlobal() {
		try {
			// Make the describeGlobal() call
			DescribeGlobalResult describeGlobalResult = 
					partnerConnection.describeGlobal();

			// Get the sObjects from the describe global result
			DescribeGlobalSObjectResult[] sobjectResults = 
					describeGlobalResult.getSobjects();

			// Write the name of each sObject to the console
			for (int i = 0; i < sobjectResults.length; i++) {
				System.out.println(sobjectResults[i].getName());
			}
		} catch (ConnectionException ce) {
			ce.printStackTrace();
		}
	}

	public void describeSObjectsSample() {
		try {
			// Call describeSObjectResults and pass it an array with
			// the names of the objects to describe.
			DescribeSObjectResult[] describeSObjectResults = 
					partnerConnection.describeSObjects(
							new String[] {"account"});

			// Iterate through the list of describe sObject results
			for (int i=0;i < describeSObjectResults.length; i++)
			{
				DescribeSObjectResult desObj = describeSObjectResults[i];
				// Get the name of the sObject
				String objectName = desObj.getName();
				System.out.println("sObject name: " + objectName);

				// For each described sObject, get the fields
				Field[] fields = desObj.getFields();

				// Get some other properties
				if (desObj.getActivateable()) System.out.println("\tActivateable");

				// Iterate through the fields to get properties for each field
				for(int j=0;j < fields.length; j++)
				{                        
					Field field = fields[j];
					System.out.println("\tField: " + field.getName());
					System.out.println("\t\tLabel: " + field.getLabel());
					if (field.isCustom()) 
						System.out.println("\t\tThis is a custom field.");
					System.out.println("\t\tType: " + field.getType());
					if (field.getLength() > 0)
						System.out.println("\t\tLength: " + field.getLength());
					if (field.getPrecision() > 0)
						System.out.println("\t\tPrecision: " + field.getPrecision());

					// Determine whether this is a picklist field
					if (field.getType() == FieldType.picklist)
					{                            
						// Determine whether there are picklist values
						PicklistEntry[] picklistValues = field.getPicklistValues();
						if (picklistValues != null && picklistValues[0] != null)
						{
							System.out.println("\t\tPicklist values = ");
							for (int k = 0; k < picklistValues.length; k++)
							{
								System.out.println("\t\t\tItem: " + picklistValues[k].getLabel());
							}
						}
					}

					// Determine whether this is a reference field
					if (field.getType() == FieldType.reference)
					{                            
						// Determine whether this field refers to another object
						String[] referenceTos = field.getReferenceTo();
						if (referenceTos != null && referenceTos[0] != null)
						{
							System.out.println("\t\tField references the following objects:");
							for (int k = 0; k < referenceTos.length; k++)
							{
								System.out.println("\t\t\t" + referenceTos[k]);
							}
						}
					}
				}            
			}
		} catch(ConnectionException ce) {
			ce.printStackTrace();  
		}
	}

}