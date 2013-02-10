package de.fernuni.browserhistoryclustering.common.types;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;



public abstract class FilePersistanceObject implements Serializable {
	
	private static final long serialVersionUID = -7034432844164418804L;

	public void save(String p_Filename) throws IOException {
		
		FileOutputStream v_FileOutStream = new FileOutputStream(p_Filename);
				
		ObjectOutputStream v_ObjOutStream = new ObjectOutputStream(
				v_FileOutStream);
		
		v_ObjOutStream.writeObject(this);
		
		v_ObjOutStream.close(); 
	}

	protected static FilePersistanceObject load(String p_Filename) throws IOException, ClassNotFoundException {
		
		FileInputStream v_FileInStream = new FileInputStream(p_Filename);
		
		ObjectInputStream v_ObjInStream = new ObjectInputStream(v_FileInStream);
		
		FilePersistanceObject v_Object = (FilePersistanceObject) v_ObjInStream.readObject();		
		
		v_ObjInStream.close();
		
		return v_Object;
	}
}
