package wsc;

import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.DescribeGlobalResult;
import com.sforce.soap.partner.DescribeGlobalSObjectResult;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.PicklistEntry;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.FieldType;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class Main {

	static final String USERNAME = "bpatterson@cebglobal.com.cldshps";
	static final String PASSWORD = "password+token";
	static PartnerConnection connection;

	public static void main(String[] args) {

		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(USERNAME);
		config.setPassword(PASSWORD);
		//config.setTraceMessage(true);

		try {

			connection = Connector.newConnection(config);

			// display some current settings
			System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
			System.out.println("Service EndPoint: "+config.getServiceEndpoint());
			System.out.println("Username: "+config.getUsername());
			System.out.println("SessionId: "+config.getSessionId());

			describeGlobal();
			describeSObjectsSample();

		} catch (ConnectionException e1) {
			e1.printStackTrace();
		}  

	}

	public static void describeGlobal() {
		try {
			// Make the describeGlobal() call
			DescribeGlobalResult describeGlobalResult = 
					connection.describeGlobal();

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

	public static void describeSObjectsSample() {
		try {
			// Call describeSObjectResults and pass it an array with
			// the names of the objects to describe.
			DescribeSObjectResult[] describeSObjectResults = 
					connection.describeSObjects(
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