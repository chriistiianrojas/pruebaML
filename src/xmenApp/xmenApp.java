package xmenApp;

import java.util.ArrayList;
import java.util.Date;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.ipAddress;
import static spark.Spark.staticFiles;

import java.net.ConnectException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import xmenApp.engine.PoolConnect;
import spark.Request;
import spark.Response;
import xmenApp.services.XmenService;
import xmenApp.util.PropertiesLoadUtil;

public class xmenApp {

	/**
	 * Instance the running the service
	 */
	private static xmenApp instance = null;

	protected PoolConnect CONNECT = null;// new PoolConnect();

	/**
	 * Parameter by store caches
	 */
	private boolean notCache = true;

	/**
	 * Load de configurations the file
	 */
	public final PropertiesLoadUtil PROPS = new PropertiesLoadUtil("server.properties");

	/**
	 * Date initial running the services
	 */
	private Date initial = null;

	/**
	 * Singleton
	 */
	static {
		if (instance == null) {
			instance = new xmenApp();
		}
	}

	/**
	 * get the instance of Singleton by service
	 *
	 * @return
	 */
	public static xmenApp getInstance() {
		return instance;
	}

	public String getProperty(String name) {
		return PROPS.getValue(name);
	}

	public Connection getConnection() throws ConnectException {
		return CONNECT.getConnection();
	}

	/**
	 * Define the behavior the response by any request that arrive to server
	 */
	public void load() {
		before("/*", (req, res) -> {
			System.out.println("?> 74->  " + req.params());
			System.out.println("?> 76->  " + req.ip());
			if (notCache) {
				// This interceptor will called always, and tells the browser to
				// don’t cache your pages,
				// adding some headers that expires the page.
				res.header("Expires", "Wed, 31 Dec 1969 21:00:00 GMT");
				res.header("Cache-Control", "no-store, no-cache, must-revalidate");
				res.header("Cache-Control", "post-check=0, pre-check=0");
				res.header("Pragma", "no-cache");
			}
		});
	}

	/**
	 * Listener any request that arrive to service
	 */
	public void root() {
		get("/*", (request, response) -> {
			System.out.println("?> 91" + request.pathInfo());
			return null;
		});
	}

	private void xmen() {

		// TODO::Request check the service running
		post("/mutant/", (Request request, Response response) -> {
			response.type("application/json");
			JSONObject body = new JSONObject(request.body());
			System.out.println("112" + request.body());
			System.out.println("112" + body.toString());
			if (body.get("dna") != null) {
				JSONArray dna = body.getJSONArray("dna");
				JSONObject result = XmenService.getService().validateDNA(dna);
				if (result.getBoolean("error")) {
					response.status(400);
				} else {
					if (result.getBoolean("isMutant")) {
						response.status(200);
					} else {
						response.status(403);
					}
				}

			}
			return "";

		});
		// TODO::Request check the service running
		post("/stats", (Request request, Response response) -> {
			response.type("application/json");
			JSONObject result = XmenService.getService().statc();
			return result.toString();
		});

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		xmenApp instance = getInstance();
		if (instance.PROPS.isLoaded()) {
			if (instance.PROPS.getValue("db.user") != null && instance.PROPS.getValue("db.pass") != null) {
				instance.CONNECT = new PoolConnect();
				try {
					instance.CONNECT.getConnection();
					instance.load();
					instance.root();
					instance.xmen();
				} catch (Exception e) {
					System.err.println("ERROR: al intentar conectar con base: " + e.getMessage());
				}
			}
		} else {
			System.err.println("FATAL: El archivo de parametros server.properties no fue encontrado.");
		}

	}

}
