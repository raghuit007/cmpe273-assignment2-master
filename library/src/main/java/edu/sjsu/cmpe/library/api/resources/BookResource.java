package edu.sjsu.cmpe.library.api.resources;

import javax.jms.Connection;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URL;
import com.yammer.dropwizard.jersey.params.LongParam;
import com.yammer.metrics.annotation.Timed;

import edu.sjsu.cmpe.library.domain.Book;
import edu.sjsu.cmpe.library.domain.Book.Status;
import edu.sjsu.cmpe.library.dto.BookDto;
import edu.sjsu.cmpe.library.dto.BooksDto;
import edu.sjsu.cmpe.library.dto.LinkDto;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;
import edu.sjsu.cmpe.library.stomp.ApolloSTOMP;

@Path("/v1/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {
    private final BookRepositoryInterface bookRepository;
    
    private ApolloSTOMP stompInst; 
    
    /**
     * BookResource constructor
     * 
     * @param bookRepository
     *            a BookRepository instance
     * @param stompInst
     *            a ApolloSTOMP instance           
     *            
     */
    public BookResource(BookRepositoryInterface bookRepository, ApolloSTOMP stompInst) {
	this.bookRepository = bookRepository;
	this.stompInst = stompInst;
    }
    
    /*
     * view book API
     */
    @GET
    @Path("/{isbn}")
    @Timed(name = "view-book")
    public BookDto getBookByIsbn(@PathParam("isbn") LongParam isbn) {
    	
    	Book book = bookRepository.getBookByISBN(isbn.get());
	
    	BookDto bookResponse = new BookDto(book);
    	bookResponse.addLink(new LinkDto("view-book", "/books/" + book.getIsbn(), "GET"));
    	bookResponse.addLink(new LinkDto("update-book-status","/books/" + book.getIsbn(), "PUT"));
    	bookResponse.addLink(new LinkDto("delete-book","/books/" + book.getIsbn(), "DELETE"));
    	bookResponse.addLink(new LinkDto("view-all-books","/books/", "GET"));

    	return bookResponse;
    }
    
    /*
     * create book API
     */
    @POST
    @Timed(name = "create-book")
    public Response createBook(@Valid Book request) {
    	// Store the new book in the BookRepository so that we can retrieve it.
    	Book savedBook = bookRepository.saveBook(request);

    	String location = "/books/" + savedBook.getIsbn();
    	BookDto bookResponse = new BookDto(savedBook);
    	bookResponse.addLink(new LinkDto("view-book", location, "GET"));
    	bookResponse.addLink(new LinkDto("update-book-status", location, "PUT"));
    	bookResponse.addLink(new LinkDto("delete-book",location, "DELETE"));
    	bookResponse.addLink(new LinkDto("view-all-books","/books/", "GET"));

	return Response.status(201).entity(bookResponse).build();
    }
    
    /*
     *	view all books API
     */
    @GET
    @Timed(name = "view-all-books")
    public BooksDto getAllBooks() {
	
    	BooksDto booksResponse = new BooksDto(bookRepository.getAllBooks());
    	booksResponse.addLink(new LinkDto("create-book", "/books", "POST"));

	return booksResponse;
    }
    
    /*
     * update status API
     */
    @PUT
    @Path("/{isbn}")
    @Timed(name = "update-book-status")
    public Response updateBookStatus(@PathParam("isbn") LongParam isbn,
	    @DefaultValue("available") @QueryParam("status") Status status) throws Exception
	    {
    	//book instance
    	Book book = bookRepository.getBookByISBN(isbn.get());
    	
    	/*
    	 * 	
    	 */
    	if (status.getValue()=="lost") {
    		//set book status to 'lost'
    		book.setStatus(status);
    		
    		//make connection to Apollo Broker
    		Connection connect = stompInst.createConnection();
    		
    		//push lost isbn to queue
    		stompInst.sendMsgQueue(connect,book.getIsbn());
    		
    		//close connection
    		stompInst.closeConnection(connect);	
    	}
	
    	BookDto bookResponse = new BookDto(book);
    	String location = "/books/" + book.getIsbn();
    	bookResponse.addLink(new LinkDto("view-book", location, "GET"));
    	bookResponse.addLink(new LinkDto("delete-book",location, "DELETE"));
    	bookResponse.addLink(new LinkDto("view-all-books","/books/", "GET"));

	return Response.status(200).entity(bookResponse).build();
    }
    
    /*
     * delete book API
     */
    @DELETE
    @Path("/{isbn}")
    @Timed(name = "delete-book")
    public BookDto deleteBook(@PathParam("isbn") LongParam isbn) throws Exception {
    
    	bookRepository.delete(isbn.get());
    	BookDto bookResponse = new BookDto(null);
    	bookResponse.addLink(new LinkDto("create-book", "/books", "POST"));

	return bookResponse;
    }
    
    /*
     * update Library API
     */
    @POST
    @Path("/update")
    @Timed(name = "update-library")
    public Response updateLibrary(@Valid String msg) throws Exception {
    	
    	String[] updateMessage=msg.split(":", 4); 
        Long isbn = Long.valueOf(updateMessage[0]);
        Status status = Status.available;

        Book book = bookRepository.getBookByISBN(isbn);
        
        /**If book received from Publisher is equal to lost book, update status*/
        if (book != null && book.getStatus()==Status.lost) {
        	book.setStatus(status);
        	System.out.println("Book " + book.getIsbn() +" updated to 'available' status");
        }
        
        /**If book received from Publisher is new book, add to hashmap*/
        else if (book == null){
        	String title = updateMessage[1];
        	String category = updateMessage[2];
        	URL coverImage = new URL(updateMessage[3]);
        	Book newBook = new Book();
        	newBook.setIsbn(isbn);
        	newBook.setTitle(title);
        	newBook.setCategory(category);
        	newBook.setCoverimage(coverImage);
        	bookRepository.saveBook(newBook);
        	System.out.println("Book " + newBook.getIsbn() + " added to library");
        }
        
        // already in library
        else {
        	System.out.println("Book " + book.getIsbn() + " already in Library ");
        }

        return Response.ok().build();
    }
}
