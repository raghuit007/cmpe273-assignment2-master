package edu.sjsu.cmpe.library.stomp;

import java.net.URL;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;

import edu.sjsu.cmpe.library.domain.Book;
import edu.sjsu.cmpe.library.domain.Book.Status;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;

public class ApolloSTOMP {
	private String apolloUser;
	private String apolloPassword;
	private String apolloHost;
	private int apolloPort;
	private String libraryName;
	private String queueName;
	private String topicName;
	private BookRepositoryInterface bookRepository;
	
	/**
     * constructor
     */
	public ApolloSTOMP(String apolloUser, String apolloPassword,
			String apolloHost, int apolloPort, String libraryName, String queueName, String topicName, BookRepositoryInterface bookRepository) {
		this.apolloUser = apolloUser;
		this.apolloPassword = apolloPassword;
		this.apolloHost = apolloHost;
		this.apolloPort = apolloPort;
		this.libraryName = libraryName;
		this.queueName = queueName;
		this.topicName = topicName;
		this.bookRepository= bookRepository;
	}
	
	/**
     * connect to Apollo broker
     */
	public Connection createConnection() throws Exception {
		StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
        factory.setBrokerURI("tcp://" + apolloHost + ":" + apolloPort);
        Connection connection = factory.createConnection(apolloUser, apolloPassword);
        return connection;
	}
	
	/**
     * send message to queue
     */
	public void sendMsgQueue(Connection connection, long ISBN) throws Exception {
		connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination dest = new StompJmsDestination(queueName);
        MessageProducer producer = session.createProducer(dest);
        TextMessage msg = session.createTextMessage(libraryName+":" + ISBN);
        msg.setLongProperty("id", System.currentTimeMillis());
        producer.send(msg);
	}
	
	/**
     * Method to subscribe to the Topic
     */
	public void subscribeTopic(Connection connection) throws Exception {
		connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination dest = new StompJmsDestination(topicName);
        MessageConsumer consumer = session.createConsumer(dest);
        
        while (true) {
            Message msg = consumer.receive();
            System.out.println("Received Book------->"+ ((TextMessage)msg).getText());
            
            // parsing the message
            String[] updateMessage=((TextMessage)msg).getText().toString().split(":", 4); 
            Long isbn = Long.valueOf(updateMessage[0]);
            Status status = Status.available;

            Book book = bookRepository.getBookByISBN(isbn);
            
            // book received from Publisher and is in lost status
            if (book != null && book.getStatus()==Status.lost) {
            	book.setStatus(status);
            	System.out.println("Updated status as AVAILABLE for book with ISBN " + book.getIsbn());
            }
            
            // Add book to hashMap if not present
            else if (book == null){
            	String title = updateMessage[1];
            	String category = updateMessage[2];
            	URL coverImage = new URL(updateMessage[3]);
            	book = new Book();
            	book.setIsbn(isbn);
            	book.setTitle(title);
            	book.setCategory(category);
            	book.setCoverimage(coverImage);
            	bookRepository.saveBook(book);
            	System.out.println("Added new book with ISBN " + book.getIsbn() + " to library");
            }
            
            // already in library
            else {
            	System.out.println("Book with ISBN " + book.getIsbn() + " already AVAILABLE in Library");
            }
        }
	}
	
	/**
     * close connection
     */
	public void closeConnection(Connection connection) throws Exception {
		connection.close();
	}
}
