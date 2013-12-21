package edu.sjsu.cmpe.procurement.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;
import edu.sjsu.cmpe.procurement.ProcurementService;

import edu.sjsu.cmpe.procurement.domain.BookRequest;
import edu.sjsu.cmpe.procurement.domain.ShippedBook;
import edu.sjsu.cmpe.procurement.stomp.ApolloSTOMPConfig;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * This job will run at every 5 second.
 */
@Every("300s")
public class ProcurementSchedulerJob extends Job {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void doJob() {
	// Job 1
                 
                System.out.println("Job 1 : Start "); 
                ApolloSTOMPConfig stompInstance = new ApolloSTOMPConfig();
            BookRequest bookRequest;
            Connection connection;
            try {
                    connection = stompInstance.makeConnection();
                        bookRequest = stompInstance.reveiveQueueMessage(connection);
                    connection.close();
                    if (bookRequest.getOrder_book_isbns().size() != 0){
                            System.out.println("HTTP POST to Publisher");
                        Client client = Client.create();
                        String url = "http://54.219.156.168:9000/orders";        
                            WebResource webResource = client.resource(url);
                            ClientResponse response = webResource.accept("application/json")
                                            .type("application/json").entity(bookRequest, "application/json").post(ClientResponse.class);
                            System.out.println(response.getEntity(String.class));
                        }
                        } catch ( Exception e) {
                                e.printStackTrace();
                        }        
            System.out.println("Job 1 : Ends ");
            
            // Job 2:
              
            System.out.println("Job 2 : Start ");
            try {
                    
                    Client client = Client.create();
                    String url = "http://54.219.156.168:9000/orders/78594";        
                    WebResource webResource = client.resource(url);
                    ShippedBook response = webResource.accept("application/json")
                                    .type("application/json").get(ShippedBook.class);
                    connection = stompInstance.makeConnection();
                    stompInstance.publishTopicMessage(connection, response);
                     
                        } catch ( Exception e) {
                                e.printStackTrace();
                        }
            System.out.println("Job 2 : Ends ");
    }
}
