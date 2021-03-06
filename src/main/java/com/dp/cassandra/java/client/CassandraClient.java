package com.dp.cassandra.java.client;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.dp.cassandra.java.client.domain.Book;
import com.dp.cassandra.java.client.repository.BookRepository;
import com.dp.cassandra.java.client.repository.KeyspaceRepository;
import com.dp.common.ReadPropertiesFile;

public class CassandraClient {

    public static void main(String args[]) {
    	
    	  /* Create object of ReadResourceFile */
        ReadPropertiesFile objPropertiesFile = new ReadPropertiesFile();

        String host = objPropertiesFile.readKey("host","127.0.0.1");
        int port = Integer.parseInt(objPropertiesFile.readKey("port","9042"));
        /* Will give you 'null' in case key not available */
        System.out.println("Connecting to Database on: "+ host);
      
        
        CassandraConnector connector = new CassandraConnector();
      	
        connector.connect(host,port);
        Session session = connector.getSession();

        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.createKeyspace("library", "SimpleStrategy", 1);
        sr.useKeyspace("library");

        BookRepository br = new BookRepository(session);
        br.createTable();
        br.alterTablebooks("publisher", "text");

        br.createTableBooksByTitle();

        Book book = new Book(UUIDs.timeBased(), "Effective Java", "Joshua Bloch", "Programming");
        br.insertBookBatch(book);

        br.selectAll().forEach(o ->  System.out.println("Title in books: " + o.getTitle()));
        br.selectAllBookByTitle().forEach(o ->  System.out.println("Title in booksByTitle: " + o.getTitle()));

        br.deletebookByTitle("Effective Java");
        br.deleteTable("books");
        br.deleteTable("booksByTitle");

        sr.deleteKeyspace("library");

        connector.close();
    }
}
