package tfg.fi.upm.es;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Tablas {

	 HashMap <String,Columnas> tablas = new HashMap<String,Columnas>(); // estructura Nombre tabla + Objeto (id,Map<NombreColumna,longitud>)
	 HashMap<String,String> creates = new HashMap<String,String>();
	 HashMap<String,HashMap<String,Integer>> alter = new HashMap<String,HashMap<String,Integer>>(); // Map de nombre de tabla que contiene Map de nombre columna, longitud;
	 ArrayList<String> modificaciones = new ArrayList<String>();
	 HashMap<String,Metadatos> metadatos = new HashMap<String,Metadatos>(); // Estructura para metadatos Tabla
	 BaseDatos bd;
	 
	 public Tablas (BaseDatos bd){
		 this.bd=bd;
	 }
	 
	 public void vaciarQuerys(){
		 this.creates.clear();
		 this.alter.clear();
	 }
	 
	 public void insertarQueryCreate(String tabla){
		 if (!this.existeTabla(tabla.toLowerCase())){
			 String query="CREATE TABLE "+tabla+" (idTabla bigint primary key,padre nvarchar(200),posicion int,hijos nvarchar(400),path nvarchar(800),idLicitacion nvarchar(10),Tipo nvarchar(200), INDEX `posicion_ind` (`posicion`),INDEX `padre_ind` (`padre`),INDEX `idLicitacion_ind` (`idLicitacion`),INDEX `Tipo_ind` (`Tipo`))";
			 this.creates.put(tabla.toLowerCase(), query);
		 }
	 }
	 
	 public void insertarColumnaQueryAlter(String tabla,String columna,int longitud){
		 if (!this.existeColumnaEnTabla(tabla.toLowerCase(), columna)){
			 if (!alter.containsKey(tabla.toLowerCase())){
				 HashMap<String,Integer> col = new HashMap<String,Integer>(); // Creo estructura Map tipo columna-longitud
				 col.put(columna, longitud); // Inserto los valores
				 alter.put(tabla.toLowerCase(), col); // Inserto la estructura
			 } else { // Si ya existia la tabla
				 HashMap<String,Integer> col=alter.get(tabla.toLowerCase()); // Obtengo la estructura
				 col.put(columna, longitud); // Inserto los valores
				 alter.put(tabla.toLowerCase(), col); // Inserto la estructura
			 }
		 }
	 }
	 
	 
	 
	 public void imprimirMetadatos(){
		 for (Entry<String, Metadatos> c : metadatos.entrySet()){
			 String clave=c.getKey();
			 Metadatos m=c.getValue();
			 System.out.println("ClaveMetadatos: ["+clave+"]");
			 System.out.println("\t"+m.toString());
		 }
	 }
	 
	 public void ejecutarQuerys(){
		 //System.out.println(creates.size());
		 //System.out.println(alter.size());
		 for (Entry<String, String> c : creates.entrySet()){
			 //System.out.println("Query tipo Create:   "+c.getValue());
			 bd.ejecutarQueryInsert(c.getValue());
			 }
		 
		 for (Entry<String, HashMap<String,Integer>> c : alter.entrySet()){
			 HashMap<String,Integer> aux =c.getValue();
			 String param="";
			 for (Entry<String, Integer> cc : aux.entrySet()){
				 if (!param.equals(""))
					 param=param+(", "+cc.getKey()+" NVARCHAR("+cc.getValue()+")");
				 else 
					 param=param+(cc.getKey()+" NVARCHAR("+cc.getValue()+")");
			 }
			 String queryAlter="ALTER TABLE "+c.getKey()+" ADD COLUMN ("+param+")";
			 //System.out.println("Query tipo Alter:   "+queryAlter);
			 bd.ejecutarQuery(queryAlter);
		 }
		 
		 for (int i=0;i<modificaciones.size();i++){
			 String query=modificaciones.get(i);
			 bd.ejecutarQueryModify(query);
		 }
	 }
	 
	 public boolean existeTabla(String nombre){
		 return tablas.containsKey(nombre.toLowerCase());
	 }
	 
	 public void crearTabla(String nombre,String padre,String path,String tipo){
		 if (!this.existeTabla(nombre.toLowerCase())){
			 Columnas col= new Columnas();
			 tablas.put(nombre.toLowerCase(), col);
			 this.insertarQueryCreate(nombre);

		 }
		 
		 if (!metadatos.containsKey(path.trim()+tipo.trim())){
			 Metadatos m = new Metadatos(nombre, padre, path, tipo);
			 metadatos.put(path.trim()+tipo.trim(), m);
		 }
		 /*
		 if (!this.existeTabla(nombre)){
		 System.out.println(("¿Existe tabla "+nombre+"?  "+this.existeTabla(nombre)));
		 System.exit(0);
		 }
		 */
	 }
	 
	 
	 public Columnas crearColumnasBBDD(String tabla){
			 return new Columnas();
		 }
	 
	 public void insertarColumna(String tabla,String columna,int longitud,String padre,String path,String tipo){
		 if (!this.existeTabla(tabla.toLowerCase())){ // si no existe tabla la creo
			 this.crearTabla(tabla.toLowerCase(),padre,path,tipo);
			 this.insertarQueryCreate(tabla.toLowerCase());
		 }
		 Columnas c=this.tablas.get(tabla.toLowerCase());
		 boolean existe=this.existeColumnaEnTabla(tabla.toLowerCase(), columna);		
		 int l=c.insertarColumna(columna, longitud,tabla.toLowerCase());////////////////////////////////
		 if (!existe)
			 	this.insertarColumnaQueryAlter(tabla.toLowerCase(), columna, l);
 
	 }
	 

	 public boolean existeColumnaEnTabla(String tabla,String columna){
		 Columnas c=tablas.get(tabla.toLowerCase());
		 return c.existeColumna(columna);
	 }
	 
	 public int obtenerLongitudColumna(String tabla,String columna){
		 Columnas c=tablas.get(tabla.toLowerCase());
		 return c.obtenerLongitudColumna(columna);
	 }
	 /// No usar
	 public void CambiarLongitudColumna(String tabla,String columna,int longitud){
		 Columnas c=tablas.get(tabla.toLowerCase());
		 c.setLongitudColumna(columna, longitud);
		 String query="ALTER TABLE "+tabla.toLowerCase()+" MODIFY "+columna+" VARCHAR("+longitud+")";
		 bd.ejecutarQuery(query);
		 System.out.println("Ejecutada query: "+query);
	 }
	 
	 
	 public int getPoscion(String tabla){
		 if (this.existeTabla(tabla.toLowerCase())){
			 Columnas c=this.tablas.get(tabla.toLowerCase());
			 //System.out.println(tabla);
			 return c.getId();
		 } else
			 return -1;

	 }
	 
	 public int incrementaPoscion(String tabla){
		 /*if (!tabla.equals("#document")){
		 System.out.println(tabla);System.out.println(this.existeTabla(tabla));System.out.println(this.getPoscion(tabla));
		 System.exit(0);}*/
		 if (this.existeTabla(tabla.toLowerCase()) && !tabla.toLowerCase().equals("#document")){
			 //System.out.println("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff        "+tabla);
			 Columnas c=this.tablas.get(tabla.toLowerCase());
			 return c.incrementaId();
		 } else
			 return 0;
	 }
	 
	 public int decrementaId(String tabla){
		 if (!this.existeTabla(tabla.toLowerCase())){
			 Columnas c=this.tablas.get(tabla.toLowerCase());
			 return c.decrementaId();
		 } else
			 return -1;
	 }
	 
	 public void imprimirTablas(){
		 for (Entry<String, Columnas> t : tablas.entrySet()){
			 System.out.println("Tabla ["+t.getKey()+"]");
			 Columnas c=t.getValue();
			 c.imprimirColumnas();
		 }

	 }
	 
	 public class Columnas {
		 int id;
		 HashMap <String,Integer> col;
		 
		 public Columnas () { // Constructor que inicializa posicion y Map de columnas(Nombre,longitud)
			this.id=0;
			col = new HashMap<String,Integer>(); // Map(Nombrecolumna,longitud)
		 }
		 
		 public int getId(){
			 return this.id;
		 }
		 
		 public int incrementaId(){
			 return ++this.id;
		 }
		 
		 public int decrementaId(){
			 return --this.id;
		 }
		 
		 public int obtenerLongitudColumna(String nombre){
			 if (this.existeColumna(nombre))
				 return col.get(nombre);
			 else
				 return 0;
		 }
		 
		 public void setLongitudColumna(String nombre,int longitud){
			 col.put(nombre, longitud);
		 }
		 
		 public boolean existeColumna(String nombre){
			 return col.containsKey(nombre);
		 }
		 
		 
		 public int insertarColumna(String nombre,int longitud,String tabla){
			 int l;
			 if (this.existeColumna(nombre)){
				 l=col.get(nombre);
			 } else {
				 if (nombre.equalsIgnoreCase("Description") || nombre.equalsIgnoreCase("Name") || nombre.equalsIgnoreCase("Note")){
						 l=1000;
						 if ((longitud<l)){
							 String query="ALTER TABLE "+tabla.toLowerCase()+" MODIFY "+nombre+" VARCHAR("+l+")";
							 modificaciones.add(query);

						 }
				 }
				 else
					 l=200;
			 }
			 if (longitud>l && longitud < 1000){
				 l=1000;
				 String query="ALTER TABLE "+tabla+" MODIFY "+nombre+" VARCHAR("+l+")";
				 modificaciones.add(query);
			 }
			 col.put(nombre, l);
			 
			 return l;
		 }
		 
		 public void imprimirColumnas(){
			 System.out.println("\tID ["+this.id+"]");

			 for (Entry<String, Integer> c : col.entrySet())
				 System.out.println("\t\tColumna ["+c.getKey()+"],		Longitud ["+c.getValue()+"]");


		 }
		 
	 }
	 

	 public class Metadatos {
		 String tabla,padre,path,tipo;
		 int nivel;
		public Metadatos(String tabla, String padre, String path, String tipo) {
			super();
			
			this.tabla = tabla;
			this.padre = padre;
			this.path = path;
			this.tipo = tipo;
			this.nivel = (this.contar(path,'/')-1);
			bd.ejecutarQuery("INSERT INTO TABLAS(TABLA,PADRE,PATH,NIVEL,TIPO) VALUES ('"+this.tabla+"','"+this.padre+"','"+this.path+"',"+this.nivel+",'"+this.tipo+"')");
		}

		public Metadatos(String tabla, String padre, String path, String tipo,int nivel) {
			super();
			
			this.tabla = tabla;
			this.padre = padre;
			this.path = path;
			this.tipo = tipo;
			this.nivel = nivel;
		}

		
		@Override
		public String toString() {
			return "Metadatos [tabla=" + tabla + ", padre=" + padre + ", path="
					+ path + ", tipo=" + tipo + ", nivel=" + nivel + "]";
		}



		public int contar(String cadena, char caracter){
			int aux=0;
			int i;
			for (i=0;i<cadena.length();i++){
				if (cadena.charAt(i)==caracter){
					aux++;
				}
			}
			if (aux==1)
				aux=2;
			return aux;
		}
		 
	 }
	



}
