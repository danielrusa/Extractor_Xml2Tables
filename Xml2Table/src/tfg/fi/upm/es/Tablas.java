package tfg.fi.upm.es;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Tablas {

	 HashMap <String,Columnas> tablas = new HashMap<String,Columnas>(); // estructura Nombre tabla + Objeto (id,Map<NombreColumna,longitud>)
	 
	 
	 public boolean existeTabla(String nombre){
		 return tablas.containsKey(nombre);
	 }
	 
	 public void crearTabla(String nombre){
		 if (!this.existeTabla(nombre)){
			 Columnas col= new Columnas();
			 tablas.put(nombre, col);
		 }
	 }
	 
	 public void insertarColumna(String tabla,String columna,int longitud){
		 if (!this.existeTabla(tabla))
			 this.crearTabla(tabla);
		 Columnas c=this.tablas.get(tabla);
		 c.insertarColumna(columna, longitud);
		 
	 }
	 
	 public int getPoscion(String tabla){
		 if (this.existeTabla(tabla)){
			 Columnas c=this.tablas.get(tabla);
			 //System.out.println(tabla);
			 return c.getId();
		 } else
			 return -1;

	 }
	 
	 public int incrementaPoscion(String tabla){
		 /*if (!tabla.equals("#document")){
		 System.out.println(tabla);System.out.println(this.existeTabla(tabla));System.out.println(this.getPoscion(tabla));
		 System.exit(0);}*/
		 if (this.existeTabla(tabla) && !tabla.equals("#document")){
			 //System.out.println("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff        "+tabla);
			 Columnas c=this.tablas.get(tabla);
			 return c.incrementaId();
		 } else
			 return 0;
	 }
	 
	 public int decrementaId(String tabla){
		 if (!this.existeTabla(tabla)){
			 Columnas c=this.tablas.get(tabla);
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
		 
		 
		 public void insertarColumna(String nombre,int longitud){
			 int l;
			 if (this.existeColumna(nombre)){
				 l=col.get(nombre);
			 } else {
				 l=200;
			 }
			 if (longitud>l){
				 l=longitud*2;
			 }
			 col.put(nombre, l);
		 }
		 
		 public void imprimirColumnas(){
			 System.out.println("\tID ["+this.id+"]");

			 for (Entry<String, Integer> c : col.entrySet())
				 System.out.println("\t\tColumna ["+c.getKey()+"],		Longitud ["+c.getValue()+"]");


		 }
		 
	 }
	 
}
