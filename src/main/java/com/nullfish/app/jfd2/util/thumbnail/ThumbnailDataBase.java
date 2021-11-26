package com.nullfish.app.jfd2.util.thumbnail;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.imageio.ImageIO;

import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.exception.WrongPathException;

public class ThumbnailDataBase {
	private Connection conn;

	private boolean inited = false;
	
	private static final ThumbnailDataBase instance = new ThumbnailDataBase();
	
	private VFile iconDir;
	
	private boolean available = false;
	
	/**
	 * テーブル作成SQL
	 */
	private static final String CREATE_TABLE_QUERY = 
		"CREATE CACHED TABLE IF NOT EXISTS thumbnail_cache ("
			+ "filename VARCHAR(256) PRIMARY KEY,"
			+ "filesystem VARCHAR(256) NOT NULL,"
			+ "file_timestamp TIMESTAMP,"
			+ "last_checked TIMESTAMP NOT NULL, "
			+ "stream BLOB" 
		+ ");";

	/**
	 * 更新クエリ
	 */
	private PreparedStatement updateStatement;

	private static final String UPDATE_QUERY = 
		"UPDATE thumbnail_cache SET "
			+ "file_timestamp = ?, " 
			+ "last_checked = SYSDATE, "
			+ "stream = ? " 
			+ "WHERE filename = ? ";

	/**
	 * 登録クエリ
	 */
	private PreparedStatement insertStatement;

	private static final String INSERT_QUERY = 
		"INSERT INTO thumbnail_cache "
			+ "(filename, filesystem, file_timestamp, last_checked, stream) "
			+ "VALUES (?, ?, ?, SYSDATE, ?) ";

	private PreparedStatement selectStatement;

	private static final String SELECT_QUERY = 
		"SELECT * FROM thumbnail_cache "
			+ "WHERE filename = ? ";

	private PreparedStatement countStatement;

	private static final String COUNT_QUERY = 
		"SELECT COUNT(*) CNT FROM thumbnail_cache";

	private PreparedStatement removeStatement;

	private static final String REMOVE = 
		"DELETE FROM thumbnail_cache "
			+ "WHERE filename = ? ";
			
	private PreparedStatement removeOldestStatement;

	private static final String REMOVE_OLDEST = 
		"DELETE FROM thumbnail_cache "
			+ "WHERE last_checked = "
			+ "(SELECT MIN(last_checked) FROM thumbnail_cache)";

	private static final String REMOVE_ALL = 
		"DELETE FROM thumbnail_cache ";
			
	private int maxRowCount = 10000;

	private ThumbnailDataBase() {
	}

	public static ThumbnailDataBase getInstance() {
		return instance;
	}
	
	public void setIconDir(VFile iconDir) {
		this.iconDir = iconDir;
	}
	
	private synchronized void init() {
		try {
			if(inited) {
				return;
			}
			
			if(!iconDir.exists()) {
				iconDir.createDirectory();
			}
			
			Class.forName("org.h2.Driver");
			String dirUrl = iconDir.getURI().toString() + "icon_cache";
			dirUrl = URLDecoder.decode(dirUrl, Charset.defaultCharset().name()); 
	
			conn = DriverManager.getConnection("jdbc:h2:" + dirUrl, "sa", "");
			initTable();
			initStatement();
			
			inited = true;
		} catch (Exception e) {
e.printStackTrace();
			available = false;
		}
	}

	private void initTable() throws SQLException {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.execute(CREATE_TABLE_QUERY);
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
			}
		}
	}

	private void initStatement() throws SQLException {
		updateStatement = conn.prepareStatement(UPDATE_QUERY);
		insertStatement = conn.prepareStatement(INSERT_QUERY);
		selectStatement = conn.prepareStatement(SELECT_QUERY);
		countStatement = conn.prepareStatement(COUNT_QUERY);
		removeStatement = conn.prepareStatement(REMOVE);
		removeOldestStatement = conn.prepareStatement(REMOVE_OLDEST);
	}

	public synchronized void close() {
		try	{updateStatement.close(); } catch (Exception e) {}
		try	{insertStatement.close(); } catch (Exception e) {}
		try	{selectStatement.close(); } catch (Exception e) {}
		try	{countStatement.close(); } catch (Exception e) {}
		try	{removeStatement.close(); } catch (Exception e) {}
		try	{removeOldestStatement.close(); } catch (Exception e) {}
		try {conn.close(); } catch (Exception e) {}
	}

	public synchronized BufferedImage getImage(VFile file) throws SQLException {
		init();
		
		ResultSet rs = null;
		try {
			selectStatement.setString(1, file.getAbsolutePath());
			rs = selectStatement.executeQuery();
			if(!rs.next()) {
				return null;
			}
			
			Timestamp date = rs.getTimestamp("file_timestamp");
			if(date != null) {
				try {
					java.util.Date fileTimestamp = file.getTimestamp();
					if(fileTimestamp != null && fileTimestamp.getTime() != date.getTime()) {
						return null;
					}
				} catch (VFSException e) {
				}
			}
			
			BufferedImage rtn = ImageIO.read(rs.getBinaryStream("stream"));
			return rtn;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {rs.close();} catch (Exception e) {}
		}
	}

	public synchronized boolean contains(VFile file) throws SQLException {
		init();
		
		ResultSet rs = null;
		try {
			selectStatement.setString(1, file.getAbsolutePath());
			rs = selectStatement.executeQuery();
			if(!rs.next()) {
				return false;
			}
			
			Timestamp date = rs.getTimestamp("file_timestamp");
			if(date != null) {
				try {
					java.util.Date fileTimestamp = file.getTimestamp();
					if(fileTimestamp != null && fileTimestamp.getTime() != date.getTime()) {
						return false;
					}
				} catch (VFSException e) {
					e.printStackTrace();
				}
			}
			
			return true;
		} finally {
			try {rs.close();} catch (Exception e) {}
		}
	}

	public synchronized void store(VFile file, BufferedImage image)
			throws SQLException {
		init();
		
		remove(file);
		insert(file, image);
	}
		
	private void insert(VFile file, BufferedImage image) throws SQLException {
		insertStatement.setString(1, file.getAbsolutePath());
		try {
			insertStatement.setString(2, file.getFileSystem().getVFS().getFile(file.getFileSystem().getRootName()).getAbsolutePath());
		} catch (WrongPathException e) {
			// 発生しない
			e.printStackTrace();
		}
		
		try {
			insertStatement.setTimestamp(3, new Timestamp(file.getTimestamp().getTime()));
		} catch (VFSException e) {
			insertStatement.setTimestamp(3, null);
		}
		
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", bos);
			byte[] bytes = bos.toByteArray();
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			
			insertStatement.setBinaryStream(4, bis, bytes.length);
		} catch (Exception e) {
			e.printStackTrace();
			insertStatement.setBinaryStream(4, null, 0);
		} finally {
			try {bos.close();} catch (Exception e) {}
		}
		
		insertStatement.executeUpdate();
		adjustRowNumber();
	}

	private void remove(VFile file) throws SQLException {
		removeStatement.setString(1, file.getAbsolutePath());
		
		removeStatement.executeUpdate();
	}
	
	public void removeAll() throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(REMOVE_ALL);
		stmt.executeUpdate();
		stmt.close();
	}
	
	private void update(VFile file, BufferedImage image) throws SQLException {
		try {
			updateStatement.setTimestamp(1, new Timestamp(file.getTimestamp().getTime()));
		} catch (VFSException e) {
			updateStatement.setTimestamp(1, null);
		}
		
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", bos);
			byte[] bytes = bos.toByteArray();
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			
			updateStatement.setBinaryStream(2, bis, bytes.length);
		} catch (Exception e) {
			e.printStackTrace();
			updateStatement.setBinaryStream(3, null, 0);
		} finally {
			try {bos.close();} catch (Exception e) {}
		}

		updateStatement.setString(3, file.getAbsolutePath());
		
		updateStatement.executeUpdate();
	}

	/**
	 * キャッシュ上限値を上回る場合、レコードを削除する。
	 * @throws SQLException
	 */
	private void adjustRowNumber() throws SQLException {
		while (true) {
			ResultSet rs = null;
			try {
				rs = countStatement.executeQuery();
				rs.next();
				int rowCount = rs.getInt("cnt");
				if (rowCount <= maxRowCount) {
					return;
				}

				removeOldestStatement.executeUpdate();
			} finally {
				try {rs.close();} catch (Exception e) {}
			}
		}
	}
}
