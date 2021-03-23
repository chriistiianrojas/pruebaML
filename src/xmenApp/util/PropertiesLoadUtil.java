package xmenApp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

/**
 * This class load a file properties defined
 */
public class PropertiesLoadUtil {
	
	/**path absolute and name file*/
	private String file = "";
	
	/**indicate if the file is correct load or not*/
	private boolean status = false;
	
	/**load properties file*/
	private Properties props = new Properties();

	/**
	 * Constructor
	 * @param file file to load
	 */
	public PropertiesLoadUtil(String file) throws IllegalArgumentException{
		if(file != null){
		  this.file = file;
		  props = load(props, file);
		}else{
			//logger..error("The file to load is null.");
			throw new IllegalArgumentException("The file is null");
		}
	}
	
	/**
	 * Show the file loaded
	 * @return name the file load
	 */
	public String getLoadFile(){
		return this.file;
	}
	
	/**
	 * Show the status load file
	 * @return <b>true</b> if load or <b>false</b> in error case
	 */
	public boolean isLoaded(){
		return this.status;
	}
	
	public Properties getFileProperties(){
		return this.props;
	}
	
	public int size(){
		
		return props.size();
	}

	private Properties load(Properties prop,String nameFile){
		if(prop==null){
			prop = new Properties();
		}else{
			if(prop.size()>0){
				prop.clear();
			}
		}
		FileInputStream fis = null;
		try {
			File file = new File(nameFile);	
			fis = new FileInputStream(file);
			prop.load(fis);
			status = true;
			return prop;
		} catch (FileNotFoundException fne) {
			fne.printStackTrace();
			status = false;
			return null;
		} catch (NullPointerException ne) {
			ne.printStackTrace();
			status = false;
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			status = false;
			return null;
		} finally {
			try{
				fis.close();
			}catch(NullPointerException ne){
				//logger..warn("fail in close file");
			}catch(Exception e){
				//logger..warn("fal in close file");
			}
		}		
	}	
	
	public String getValue(String key){
		return (String) props.get(key);
	}
}
