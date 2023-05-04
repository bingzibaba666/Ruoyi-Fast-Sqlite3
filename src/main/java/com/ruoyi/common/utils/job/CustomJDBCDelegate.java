package com.ruoyi.common.utils.job;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.quartz.impl.jdbcjobstore.StdJDBCDelegate;

public class CustomJDBCDelegate  extends StdJDBCDelegate {
	
	  @Override
	  protected Object getObjectFromBlob(ResultSet rs, String colName) throws ClassNotFoundException, IOException, SQLException {
	    byte[] bytes = rs.getBytes(colName);
	    Object map = null;
	    ByteArrayInputStream bais = null;
	    ObjectInputStream ois = null;
	    try {
	      bais = new ByteArrayInputStream(bytes);
	      ois = new ObjectInputStream(bais);
	      map = ois.readObject();
	    } catch (EOFException ex1) {
	      bais.close();
	    } catch (IOException e) {
	      // Error in de-serialization
	      e.printStackTrace();
	    }

	    return map;
	  }

	  @Override
	  protected Object getJobDataFromBlob(ResultSet rs, String colName) throws ClassNotFoundException, IOException, SQLException {
	    return getObjectFromBlob(rs, colName);
	  }
}
