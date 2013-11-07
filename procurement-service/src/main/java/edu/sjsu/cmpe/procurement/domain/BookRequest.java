package edu.sjsu.cmpe.procurement.domain;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BookRequest {
        
        @JsonProperty
        private String id = "73186";
        
        @JsonProperty
        private  List<Integer> order_book_isbns = new ArrayList<Integer>();
        
        /**
     * @return the id
     */
        public String getId() {
                return id;
        }
        
        /**
     * @param id
     *            the id to set
     */
        public void setId(String id) {
                this.id = id;
        }
        
        /**
     * @return the order_book_isbns
     */
        public  List<Integer> getOrder_book_isbns() {
                return order_book_isbns;
        }
        
        /**
     * @param order_book_isbns
     *            the order_book_isbns to set
     */
        public void setOrder_book_isbns(List<Integer> order_book_isbns) {
                this.order_book_isbns = order_book_isbns;
        }

}
