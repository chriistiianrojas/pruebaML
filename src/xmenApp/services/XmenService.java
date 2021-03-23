package xmenApp.services;

import java.net.ConnectException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iciql.Db;

import xmenApp.xmenApp;
import xmenApp.domain.Persons;

public class XmenService {

	/**
	 * Instance the service
	 */
	private static XmenService service = null;

	private final Persons PERSONS = new Persons();

	/**
	 * * Singleton
	 */
	static {
		if (service == null) {
			try {
				service = new XmenService();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println("EXCEPTION ");
			}
		}
	}

	/**
	 * iciql connector to database
	 */
	private Db engine = null;

	/**
	 * Constructor the database
	 *
	 * @throws ConnectException throws when fail in get connection to database
	 */
	public XmenService() throws ConnectException {
		engine = Db.open(xmenApp.getInstance().getConnection());
	}

	public static XmenService getService() {

		if (service == null) {
			try {
				service = new XmenService();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println("EXCEPTION: " + e.getMessage());
			}
		}
		return service;

	}

	/**
	 * Find in the database a record Person with the name defined
	 *
	 * @param title
	 * @return object Movie if exists or null in other case
	 */
	public Persons getPersonsDNA(String dna) {
		return engine.from(PERSONS).where(PERSONS.dna).is(dna).selectFirst();
	}

	/**
	 * Find in the database a record Movies with the name defined
	 *
	 * @return object Movie if exists or null in other case
	 */
	public Persons getPerson(long id) {
		return engine.from(PERSONS).where(PERSONS.id).is(id).selectFirst();
	}

	/**
	 * Find in the database a record Movies with the name defined
	 *
	 * @param title
	 * @return object Movie if exists or null in other case
	 */
	public Long getPersonCount(boolean isMutant) {
		return engine.from(PERSONS).where(PERSONS.mutant).is(isMutant).selectCount();
	}

	public JSONObject statc() {
		JSONObject result = new JSONObject();
		Long isMutant = getPersonCount(true);
		Long isNotMutant = getPersonCount(false);
		System.out.println("isMutant" + isMutant);
		Double ratio = (double) ((isMutant * 100 / (isMutant + isNotMutant)));
		try {
			result.put("count_mutant_dna", isMutant);
			result.put("count_human_dna", isNotMutant);
			result.put("ratio", (double) ratio / 100);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public JSONObject validateDNA(JSONArray dna) {
		String info = "";
		Pattern pat = Pattern.compile("[^ACGT]");
		Matcher mat;
		JSONObject result = new JSONObject();
		try {
			Map<Integer, String> mapHorizontal = new HashMap<>();
			Map<Integer, String> mapOblicua = new HashMap<>();
			JSONArray listOpcion = new JSONArray();
			boolean error = false;
			if (dna != null && dna.length() > 0) {
				for (int i = 0; i < dna.length(); i++) {
					info = dna.getString(i).toUpperCase();
					mat = pat.matcher(info);
					if (!mat.find()) {
						listOpcion.put(info);
						horizontal(mapHorizontal, i, info);
						oblicua(mapOblicua, i, info);
					} else {
						error = true;
					}

				}

				if (!error) {
					Set<Map.Entry<Integer, String>> entrySet = mapHorizontal.entrySet();
					for (Map.Entry<Integer, String> entry : entrySet) {
						if (entry.getValue().length() >= 4)
							listOpcion.put(entry.getValue());
					}

					entrySet = mapOblicua.entrySet();
					for (Map.Entry<Integer, String> entry : entrySet) {
						if (entry.getValue().length() >= 4)
							listOpcion.put(entry.getValue());
					}
					int secuencia = 0;
					for (int i = 0; i < listOpcion.length(); i++) {
						info = listOpcion.getString(i);
						secuencia += validateIsMutant(info);
					}
					boolean isMutant = isMutant(secuencia, dna, error);
					result.put("isMutant", isMutant);
					result.put("error", error);

					return result;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Busca los nuevos codigos de ADN
	 * 
	 * @param mapHorizontal
	 * @param i
	 * @param info
	 */
	private void horizontal(Map<Integer, String> mapHorizontal, int i, String info) {
		char[] arraycadena;
		arraycadena = info.toCharArray();
		for (int j = 0; j < arraycadena.length; j++) {
			if (mapHorizontal.isEmpty() || !mapHorizontal.containsKey(j)) {
				mapHorizontal.put(j, arraycadena[j] + "");
			} else {
				mapHorizontal.put(j, mapHorizontal.get(j) + arraycadena[j] + "");
			}
		}

	}

	/**
	 * 
	 * @param mapOblicua
	 * @param i
	 * @param info
	 */
	private void oblicua(Map<Integer, String> mapOblicua, int i, String info) {
		char[] arraycadena;

		int control = 0;
		int controlI = 0;
		int controlJ = 0;
		boolean cont = true;
		arraycadena = info.toCharArray();
		cont = true;
		controlI = i;
		for (int j = 0; j < arraycadena.length; j++) {
			control = j;
			if (mapOblicua.isEmpty() || !mapOblicua.containsKey(control)) {
				mapOblicua.put(control, arraycadena[j] + "");
			} else {
				if ((j + i) < arraycadena.length)
					mapOblicua.put(control, mapOblicua.get(control) + arraycadena[control + i] + "");
			}

			if (i > 0) {
				if (cont && j == 0) {
					control = mapOblicua.size();
					cont = false;
					mapOblicua.put(control, arraycadena[j] + "");
				}
			}

		}
		controlJ = 0;
		for (int j2 = arraycadena.length; j2 < mapOblicua.size() - 1; j2++) {
			if ((i - 1 + controlJ) < arraycadena.length)
				if ((i - 1 - controlJ) < arraycadena.length && controlI == i) {
					mapOblicua.put(j2, mapOblicua.get(j2) + arraycadena[i - 1 - controlJ] + "");
				}
			controlJ++;
		}

	}

	private int validateIsMutant(String textoEntrada) {
		int secuencia = 0;
		int cantidadRepetida = 0;
		List<String> textoList = Arrays.asList(textoEntrada.split(""));
		for (String item : textoList) {
			int cantidad = Collections.frequency(textoList, item);
			if (cantidadRepetida < cantidad && !item.equals(" ")) {

				if (cantidad > 3) {
					secuencia++;
				}
			}
		}
		return secuencia;
	}

	private boolean isMutant(int secuencia, JSONArray dna, boolean error) {
		boolean isMutant = error ? error : secuencia > 1;
		if (!error) {
			save(dna, isMutant);
		}
		return isMutant;
	}

	private void save(JSONArray dna, boolean isMutant) {
		Persons person = getPersonsDNA(dna.toString());
		if (person == null) {
			Persons newPerson = new Persons();
			newPerson.dna = dna.toString();
			newPerson.mutant = isMutant;
			if (engine.insert(newPerson)) {
				System.out.println("Creo correctamente");
			} else {
				System.out.println("Error creando ");

			}
		}
	}

}
