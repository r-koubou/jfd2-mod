package com.nullfish.app.jfd2.util.thumbnail;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;

public class ResultSetOutputStream extends BufferedOutputStream {
	private ResultSet rs;
	
	public ResultSetOutputStream(OutputStream out, ResultSet rs) {
		super(out);
		this.rs = rs;
	}

	public void close() throws IOException {
		super.close();
		try {
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
