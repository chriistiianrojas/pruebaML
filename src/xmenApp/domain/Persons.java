package xmenApp.domain;

import com.iciql.Iciql.IQColumn;
import com.iciql.Iciql.IQTable;

/**
 * The Persons entity.
 *
 * @version 1.0.0
 * @since
 *
 */
@IQTable(name = "persons")
public class Persons {

	public static final String TABLE_NAME = "persons";

	/**
	 * doc
	 */
	@IQColumn(name = "id", primaryKey = true, autoIncrement = true)
	public Long id;

	/**
	 * doc
	 */
	@IQColumn(name = "dna")
	public String dna;

	/**
	 * doc
	 */
	@IQColumn(name = "mutant")
	public Boolean mutant;

	public Persons() {
	}

	/**
	 * way of represent the object into application
	 */
	public String toString() {
		return dna;
	}

}
