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
	public Datos (Tablas t){
		this.ta=t;
	}
    
    public void crearTabla(Node n,String hijos,String path){
    	String tabla=n.getNodeName().replaceAll("cac:", "");
		String padre=n.getParentNode().getNodeName().replaceAll("cac:", ""); // capturo padre
		int pos=ta.getPoscion(padre); // Capturo posicion de Padre incrementado la anterior
    	if (!tablas.containsKey(tabla)){ // si no existe la tabla
    		Tuplas t= new Tuplas(pos,tabla,padre,hijos,path); // Creo una tupla con los metadatos
    		
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
        		Tuplas t= new Tuplas(pos,tabla,padre,hijos,path); // Creo una tupla con los metadatos
    			at.add(t);  // La añado en la lista
    		} else {
    			//ta.decrementaId(tabla);
    		}
    		
    	}
    }

    public void insertaColumna(Node n,String dato,String hijos,String path){
    	String nombreColumna=n.getNodeName().replaceAll("cbc:", ""); // Nombre de columna
    	String tabla=n.getParentNode().getNodeName().replaceAll("cac:", "");
    	String nombrePadreTabla=n.getParentNode().getParentNode().getNodeName().replaceAll("cac:", "");
    	ArrayList<Tuplas> at=this.tablas.get(tabla);
    	boolean insertado=false;   	
    	for (int i=0;i<at.size()&&!insertado;i++){ // Para todos el array de Tuplas columna,valor
    		Tuplas t=at.get(i);
    		
    		if (!t.existeColumna(nombreColumna) && t.getPath().trim().equals(path)){ // Si no esta la columna lo añado
    			t.insertarDatoEnColumna(nombreColumna, dato);
    			this.insertarAtributos(t, n);
    			insertado=true;
    			break;
    		}
    	}
    	if (!insertado){ // Si esta en todas Creo nueva tupla y la añado a la lista
    		Tuplas t=new Tuplas(ta.getPoscion(nombrePadreTabla),tabla,nombrePadreTabla,hijos,path);
    		this.insertarAtributos(t, n);
    		//(int posicion,String tabla, String padre, String hijos, String path)
    		at.add(t);
    	}
    }
    
    public void insertarAtributos(Tuplas t,Node n){
		NamedNodeMap nm =n.getAttributes();
		if(nm.getLength()>0){
			for (int j=0;j<nm.getLength();j++){
				ta.insertarColumna(this.getPadre(n), this.getNombreAtributo(n, nm.item(j)), this.getLongitud(nm.item(j)));
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
			
		String t=n1.getNodeName().replaceAll("cbc:", "")+"-"+aux;
		if (t.length()>63){
			return t.substring(t.length()-63, t.length());
		} else {
			return t;
		}
	}
    
	private String getPadre(Node n){
		return n.getParentNode().getNodeName().replaceAll("cac:", "");
	}
	
	
	private int getLongitud(Node n){
		return n.getTextContent().length();
	}
    /*
    public void insertaColumna(Node n,String dato,String hijos,String path){ // Node n es un atributo
    	//this.imprimirDatos();
    	String nombreColumna=n.getNodeName().replaceAll("cbc:", "");
    	//System.out.println(nombreColumna);
    	//System.out.println(n.getPreviousSibling());
    	String tabla=n.getParentNode().getNodeName().replaceAll("cac:", "");
    	String nombrePadreTabla=n.getParentNode().getParentNode().getNodeName().replaceAll("cac:", "");
    	ArrayList<Tuplas> at=this.tablas.get(tabla);
    	boolean insertado=false;
    	for (int i=0;i<at.size()&&!insertado;i++){
    		Tuplas t=at.get(i);
    		if (!t.existeColumna(nombreColumna) && t.getPath().trim().equals(path)){
    			t.insertarDatoEnColumna(nombreColumna, dato);
    			insertado=true;
    			break;
    		}
    	}
    	if (!insertado){
    		Tuplas t=new Tuplas(ta.getPoscion(tabla),tabla,nombrePadreTabla,hijos,path);
    		//(int posicion,String tabla, String padre, String hijos, String path)
    		at.add(t);
    	}

    }
    
    // PAra el caso de que sea un atributo
    public void insertaColumna(String atributo,String padre,Node n2,String dato,String hijos,String path){ // Node n es un atributo
    	//this.imprimirDatos();
    	String nombreColumna=atributo;

    	String nombrePadre=n2.getParentNode().getNodeName().replaceAll("cac:", "");
    	ArrayList<Tuplas> at=this.tablas.get(nombrePadre);
    	boolean insertado=false;
    	for (int i=0;i<at.size()&&!insertado;i++){
    		Tuplas t=at.get(i);
    		if (!t.existeColumna(n2.getNodeName().replaceAll("cbc:", ""))//!t.existeColumna(nombreColumna) 
    				&& t.getPath().trim().equals(path)){
    			t.insertarDatoEnColumna(nombreColumna, dato);
    			insertado=true;
    			break;
    		}
    	}
    	if (!insertado){
        	//System.out.println("Nombre papa     "+ta.incrementaPoscion(nombrePadre));
    		Tuplas t=new Tuplas(ta.getPoscion(nombrePadre),padre,nombrePadre,hijos,path);
    		t.insertarDatoEnColumna(nombreColumna, dato);
    		//////////////////////////////////Tuplas t=new Tuplas(ta.incrementaPoscion(nombrePadre),padre,nombrePadre,hijos,path);
    		at.add(t);
    	}

    }
    
    */
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

	public class Tuplas {  // Si ya existe columna+dato en lista nueva linea con metadatos
		public int posicion,id;
		public String padre,hijos,path;
		public HashMap<String,String> insercion; // String Columna ;valores Valor
		
		public Tuplas(int posicion,String tabla, String padre, String hijos, String path) { // el constructor rellena Metadatos
			super();
			this.id=ta.incrementaPoscion(tabla); // Incremento la posicion de la tabla y creo tubla con id
			this.posicion = posicion;
			this.padre = padre;
			this.hijos = hijos;
			this.path = path;
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
			
			 for (Entry<String, String> c : insercion.entrySet())
				 System.out.println("\t"+c.getKey()+"	["+c.getValue()+"]");
			 
			 System.out.println("----------------------------------------------------------------------------------------");
			
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
