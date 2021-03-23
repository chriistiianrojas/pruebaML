package xmenApp.engine;

import java.sql.Connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import xmenApp.xmenApp;

import java.net.ConnectException;

/**
 * Implement the database access used Hikari CP
 *
 * @see ConnectException
 * @version 1.0.0
 * @since 1.0.0
 */
public class PoolConnect {

	/**
	 * Configuration the Hikari
	 */
	private final HikariConfig config = new HikariConfig();

	/**
	 * Datasources the Hikari
	 */
	private HikariDataSource datasource = null;

	/**
	 * initialize configurations the Hikari
	 */
	private void initConfiguration() {
		// config.setMinimumIdle(0);
		config.setMinimumIdle(5);
		config.setMaximumPoolSize(100);
		config.setIdleTimeout(1000); // minutes
		config.setMaxLifetime(5000);
		config.setConnectionTimeout(2000); // millis
		config.setDriverClassName("org.postgresql.Driver");
		// localhost

		System.out.println("jdbc:postgresql://localhost:" + xmenApp.getInstance().getProperty("db.port") + "/"
				+ xmenApp.getInstance().getProperty("db.name"));
		config.setJdbcUrl("jdbc:postgresql://localhost:" + xmenApp.getInstance().getProperty("db.port") + "/"
				+ xmenApp.getInstance().getProperty("db.name"));
		config.addDataSourceProperty("user", xmenApp.getInstance().getProperty("db.user"));
		config.addDataSourceProperty("password", xmenApp.getInstance().getProperty("db.pass"));
	}

	/**
	 * get the data source based in the configuration
	 */
	private void getDatasource() {
		try {
			datasource = new HikariDataSource(config);
		} catch (Exception e) {
			System.out.println("AAA" + e.getMessage());
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Constructor
	 */
	public PoolConnect() {
		initConfiguration();
		getDatasource();
	}

	/**
	 * Get connections the data source by all system
	 *
	 * @return Connection by the system
	 * @throws ConnectException when to can get the connections to database
	 */
	public Connection getConnection() throws ConnectException {
		try {
			return datasource.getConnection();
		} catch (Exception e) {
			throw new ConnectException("Not can connect to database ->" + e);
		}
	}

	/**
	 * Get connections the data source by all system
	 *
	 * @return Connection by the system
	 * @throws ConnectException when to can get the connections to database
	 */
	public Connection getClose() {
		try {
			datasource.close();
		} catch (Exception e) {

		}
		return null;
	}

}
