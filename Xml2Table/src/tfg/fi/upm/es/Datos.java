package tfg.fi.upm.es;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class Datos {

	// Metadatos (posicion,padre,hijos,path)
	// Por cada dato ya insertado nueva linea (Que ya exista)
	public Tablas ta;
    public HashMap<String,ArrayList<Tuplas>> tablas=new HashMap<String,ArrayList<Tuplas>>(); // Estructura map tabla -> Tupla (columna,ArrayList)
    public BaseDatos bd;
    
    public Datos (Tablas t,BaseDatos bd){
		this.ta=t;
		this.bd=bd;
	}
	
	
    
    public void crearTabla(Node n,String hijos,String path,String idLicitacion,String tipo){
    	String tabla=n.getNodeName().replaceAll("cac:", "").replaceAll("ext:", "");
		String padre=n.getParentNode().getNodeName().replaceAll("cac:", "").replaceAll("ext:", ""); // capturo padre
		int pos=ta.getPoscion(padre); // Capturo posicion de Padre incrementado la anterior
    	if (!tablas.containsKey(tabla)){ // si no existe la tabla
    		Tuplas t= new Tuplas(pos,tabla,padre,hijos,path,idLicitacion,tipo); // Creo una tupla con los metadatos
    		
    		ArrayList<Tuplas> at = new  ArrayList<Tuplas>(); // Creo un Array list de Tuplas
    		at.add(t); // inserto tupla creada
    		tablas.put(tabla, at); // Inserto tabla + tupla
    	} else { // Si existe la tabla
    		ArrayList at=this.tablas.get(tabla); // Capturo las estructuras asociadas a esta tabla
    		boolean encontrado=false;  // aviso de encontrar el mismo path (Misma herencia)
    		for (int i=0;i<at.size()&&!encontrado;i++)
    		{
    			Tuplas aux=(Tuplas) at.get(i); // Capturo la tupla en la posicion i
    			if (aux.getPath().equals(path.trim())){ //
    				
    				encontrado=true;
    				break;
    			}
    		}
    		if (!encontrado){  // si no la encontre es que es nuevo, (distinto path, distinta hubicacion, nueva tupla)
        		Tuplas t= new Tuplas(pos,tabla,padre,hijos,path,idLicitacion,tipo); // Creo una tupla con los metadatos
    			at.add(t);  // La añado en la lista
    		} else {
    			//ta.decrementaId(tabla);
    		}
    		
    	}
    }

    public void insertaColumna(Node n,String dato,String hijos,String path,String idLicitacion,String tipo){
    	String nombreColumna=n.getNodeName().replaceAll("cbc:", "").replaceAll(":cbc", ""); // Nombre de columna
    	String tabla=n.getParentNode().getNodeName().replaceAll("cac:", "").replaceAll("ext:", "");
    	String nombrePadreTabla=n.getParentNode().getParentNode().getNodeName().replaceAll("cac:", "").replaceAll("ext:", "");
    	ArrayList<Tuplas> at=this.tablas.get(tabla);
    	boolean insertado=false;   	
    	for (int i=0;i<at.size()&&!insertado;i++){ // Para todos el array de Tuplas columna,valor
    		Tuplas t=at.get(i);
    		
    		if (!t.existeColumna(nombreColumna) && t.getPath().trim().equals(path)){ // Si no esta la columna lo añado
    			t.insertarDatoEnColumna(nombreColumna, dato);
    			this.insertarAtributos(t, n,path,tipo);
    			insertado=true;
    			break;
    		}
    	}
    	if (!insertado){ // Si esta en todas Creo nueva tupla y la añado a la lista
    		Tuplas t=new Tuplas(ta.getPoscion(nombrePadreTabla),tabla,nombrePadreTabla,hijos,path,idLicitacion,tipo);
    		this.insertarAtributos(t, n,path,tipo);
    		//(int posicion,String tabla, String padre, String hijos, String path)
    		at.add(t);
    	}
    }
    
    public void insertarAtributos(Tuplas t,Node n,String path,String tipo){
		NamedNodeMap nm =n.getAttributes();
		String nombrePadreTabla=n.getParentNode().getParentNode().getNodeName().replaceAll("cac:", "").replaceAll("ext:", "");
		if(nm.getLength()>0){
			for (int j=0;j<nm.getLength();j++){
				ta.insertarColumna(this.getPadre(n), this.getNombreAtributo(n, nm.item(j)), this.getLongitud(nm.item(j)),nombrePadreTabla,path,tipo);
				if (nm.item(j).getNodeName().equals("schemeName"))
					t.insertarDatoEnColumna(this.getNombreAtributo(n, nm.item(j)), n.getTextContent());
				else
					t.insertarDatoEnColumna(this.getNombreAtributo(n, nm.item(j)), nm.item(j).getTextContent());
			}
		}
    }
    
	private String getNombreAtributo(Node n1,Node n2){
		String aux="";
		if (n2.getNodeName().contains("schemeName"))
			aux=n2.getNodeValue();
		else
			aux=n2.getNodeName();
			
		String t=n1.getNodeName().replaceAll("cbc:", "").replaceAll(":cbc", "")+"_"+aux;
		if (t.length()>63){
			return t.substring(t.length()-63, t.length());
		} else {
			return t;
		}
	}
    
	private String getPadre(Node n){
		return n.getParentNode().getNodeName().replaceAll("cac:", "").replaceAll("ext:", "");
	}
	
	
	private int getLongitud(Node n){
		return n.getTextContent().length();
	}
   
    public void imprimirDatos(){
    	//System.out.println("Tamaño: "+this.tablas.size());
		 for (Entry<String, ArrayList<Tuplas>> c : tablas.entrySet()){
			 System.out.println("Tabla: ["+c.getKey()+"]");
			 ArrayList<Tuplas> at=c.getValue();
			 for (int i=0;i<at.size();i++){
				 at.get(i).imprimirTuplas();
			 }
			 System.out.println("\n\n=====================================================================================\n\n");
		 }
		 
    }
    
    public void insertarDatos(){
		 for (Entry<String, ArrayList<Tuplas>> c : tablas.entrySet()){
			 
			 ArrayList<Tuplas> at=c.getValue();
			 for (int i=0;i<at.size();i++){
				 String query="INSERT INTO "+c.getKey();
				 query=query+" ("+at.get(i).obtenerColumnas()+") VALUES ("+at.get(i).obtenerDatos()+")";
				 bd.ejecutarQuery(query);
			 }
		 }
    }

	public class Tuplas {  // Si ya existe columna+dato en lista nueva linea con metadatos
		public int posicion,id;
		public String padre,hijos,path,idLicitacion,tabla,tipo;
		public HashMap<String,String> insercion; // String Columna ;valores Valor
		
		public Tuplas(int posicion,String tabla, String padre, String hijos, String path,String idLicitacion,String tipo) { // el constructor rellena Metadatos
			super();
			this.id=ta.incrementaPoscion(tabla); // Incremento la posicion de la tabla y creo tubla con id
			this.posicion = posicion;
			this.padre = padre;
			this.hijos = hijos;
			this.path = path;
			this.idLicitacion=idLicitacion;
			this.tabla=tabla;
			this.tipo=tipo;
			insercion = new HashMap<String,String>(); // Columna-valor
		}

		private boolean existeColumna(String columna){
			return this.insercion.containsKey(columna);
		}
		
		private void insertarDatoEnColumna(String Columna,String dato){
			this.insercion.put(Columna, dato);
		}
		
		private void imprimirTuplas(){
			System.out.println("\t++Id: ["+this.id+"]");
			System.out.println("\t++Padre: ["+this.padre+"]");
			System.out.println("\t++Posicion: ["+this.posicion+"]");
			System.out.println("\t++hijos: ["+this.hijos+"]");
			System.out.println("\t++Path: ["+this.path+"]");
			System.out.println("\t++IdLicitacion: ["+this.idLicitacion+"]");
			System.out.println("\t++Tipo: ["+this.tipo+"]");
			
			 for (Entry<String, String> c : insercion.entrySet())
				 System.out.println("\t"+c.getKey()+"	["+c.getValue()+"]");
			 
			 System.out.println("----------------------------------------------------------------------------------------");
			
		}
		
		private String obtenerColumnas(){
			String columnas="idTabla,padre,posicion,hijos,path,idLicitacion,tipo";
			
			 for (Entry<String, String> c : insercion.entrySet())
				 columnas=columnas+","+c.getKey();
			 return columnas;
			
		}
		
		private String obtenerDatos(){
			String datos=this.id+",'"+this.padre+"',"+this.posicion+",'"+this.hijos+"','"+this.path+"','"+this.idLicitacion+"'"+",'"+this.tipo+"'";
			
			 for (Entry<String, String> c : insercion.entrySet())
				 datos=datos+",'"+c.getValue().replace("'", "¨")+"'";
			 return datos;
			
		}
		
		public int getPosicion() {
			return posicion;
		}

		public void setPosicion(int posicion) {
			this.posicion = posicion;
		}

		public String getPadre() {
			return padre;
		}

		public void setPadre(String padre) {
			this.padre = padre;
		}

		public String getHijos() {
			return hijos;
		}

		public void setHijos(String hijos) {
			this.hijos = hijos;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}
		
		

	}
	


}
