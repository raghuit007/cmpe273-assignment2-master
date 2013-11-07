package edu.sjsu.cmpe.procurement.domain;

import java.util.ArrayList;
import java.util.List;

public class ShippedBook {
        private List<Book> shipped_books = new ArrayList<Book>();
        
        /**
     * @return the shipped_books
     */
        public List<Book> getShipped_books() {
                return shipped_books;
        }
        
        /**
     * @param shipped_books
     *            the shipped_books to set
     */
        public void setShipped_books(List<Book> shipped_books) {
                this.shipped_books = shipped_books;
        }

        
}
