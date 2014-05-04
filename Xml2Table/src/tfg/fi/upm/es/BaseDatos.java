package tfg.fi.upm.es;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import tfg.fi.upm.es.Tablas.Columnas;
import tfg.fi.upm.es.Tablas.Metadatos;




public class BaseDatos {

	Connection miConexion;
	Connection miConexionlicitaciones;
	int id=0;
	int nivel=1;
	Tablas tablas;

	public BaseDatos() {
		super();
		this.miConexion =ConexionDB.GetConnection("licitaciones");
		this.miConexionlicitaciones =ConexionDB.GetConnection("tfg");

		if(miConexion!=null)
		{
			System.out.println("Conectado a Base de datos licitaciones correctamente...........");
			//JOptionPane.showMessageDialog(null, "Conexión Realizada Correctamente");
		}
		if(miConexionlicitaciones!=null)
		{
			System.out.println("Conectado a Base de datos TFG correctamente...........");
			//JOptionPane.showMessageDialog(null, "Conexión Realizada Correctamente");
		}
		
		
	}  // fin constructor

	public void setTablas(Tablas t){
		this.tablas=t;
	}
	
	public  String[] obtenerXmlPorId(String id){
		String[] resultado= new String[2];
		try {
			Statement st = (Statement) miConexionlicitaciones.createStatement();
			String query = "select tipo,xml from licitaciones where id ='"+id+"'" ;
			ResultSet rs = st.executeQuery(query) ;
			
			while (rs.next()){
				resultado[0]=(String) rs.getObject(1);
				resultado[1]=(String) rs.getObject(2);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultado;
	}
	
	public void ejecutarQuery(String query){
		try {
			Statement st = (Statement) miConexion.createStatement();
			st.execute(query) ;
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Fallo en la ejecucion de la Query:   <"+query+">");
			e.printStackTrace();
			
		}
	}
	
	public void ejecutarQueryModify(String query){
		try {
			Statement st = (Statement) miConexion.createStatement();
			st.execute(query) ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			String tabla=query.substring(query.indexOf("TABLE"), query.indexOf("MODIFY")).replaceAll("NTO ", "").replaceAll("TABLE", "").trim();
			if(!comprobarExisteTabla(tabla))
			{
				this.crearTablaBBDD(tabla);
			}
			
			String q=query.toLowerCase().replaceAll("modify", "add");

			try {
				Statement st2 = (Statement) miConexion.createStatement();
				st2.execute(q) ;
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				System.out.println("Fallo en la ejecucion de la Query:   <"+query+">");
				e1.printStackTrace();
			}
			
			
		}
	}
	
	public void crearTablaBBDD(String tabla){
		String query="CREATE TABLE "+tabla+" (idTabla bigint primary key,padre nvarchar(200),posicion int,hijos nvarchar(400),path nvarchar(800),idLicitacion nvarchar(10),Tipo nvarchar(200), INDEX `posicion_ind` (`posicion`),INDEX `padre_ind` (`padre`),INDEX `idLicitacion_ind` (`idLicitacion`),INDEX `Tipo_ind` (`Tipo`))";
		Statement st;
		try {
			st = (Statement) miConexion.createStatement();
			st.execute(query) ;
		} catch (SQLException e) {
			System.out.println("xxxxxxxxxxxxxxxx  Fallo en la RE-creacion de la tabla "+tabla+"    query <"+query+">");
			e.printStackTrace();
		}

	}
	
	public boolean comprobarExisteTabla(String tabla){
		String q="select count(table_name) from information_schema.tables WHERE table_schema = 'licitaciones' AND table_name like '"+tabla+"'";
		int res=0;
		try {
			Statement stcet = (Statement) miConexion.createStatement();
			ResultSet rs = stcet.executeQuery(q);
			while (rs.next()){
				res=rs.getInt(1);
				break;
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (res>0);
	}
	
	public void ejecutarQueryInsert(String query){
		try {
			Statement st = (Statement) miConexion.createStatement();
			st.execute(query) ;
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			
			ArrayList<String> lista = this.extraerCadenaColumnas(query);
			String tabla =this.extraerTabla(query);
			
			for (int i=0;i<lista.size();i++){
				String columna=lista.get(i);
				if (!this.estaColumnaEnTabla(tabla, columna)){
					String q2="ALTER TABLE "+tabla.toLowerCase()+" ADD "+columna+" VARCHAR(600)";
					try {
						Statement st2 = (Statement) miConexion.createStatement();
						st2.execute(q2) ;
						
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						System.out.println("+++++++++++++++Fallo en la ejecucion de la Query de Alter tras fallo en insert :   <"+query+">");
						e1.printStackTrace();
					}
				}
			}
			
			try {
				System.out.println("Fallo en la ejecucion de la Query:   <"+query+">          ------------------		Se reintentara.........");
				Statement st3 = (Statement) miConexion.createStatement();
				st3.execute(query) ;
				System.out.println("Re-Ejecutada con exito");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				System.out.println("Fallo en el reintento de la query <"+query+">");
				e1.printStackTrace();
			}

			

			
		}
	}
	
	
	public boolean estaColumnaEnTabla(String tabla,String columna){
		String query="SELECT COUNT(information_schema.COLUMNS.COLUMN_NAME) FROM information_schema.columns"
				+ " where information_schema.COLUMNS.TABLE_NAME='"+tabla+"' AND information_schema.COLUMNS.COLUMN_NAME LIKE '"+columna+"'";
		Statement st;
		int resultado=0;
		try {
			st = (Statement) miConexionlicitaciones.createStatement();
			ResultSet rs = st.executeQuery(query) ;

			while (rs.next()){
				resultado=rs.getInt(1);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (resultado>0);

	}
	
	public String extraerTabla(String q){
		return q.substring(q.indexOf("INTO")+1, q.indexOf("(")).replaceAll("NTO ", "").trim();
	}

	public ArrayList<String> extraerCadenaColumnas(String q){
		String cad=q.substring(q.indexOf("(")+1, q.indexOf(")"));
		ArrayList<String> lista = new ArrayList<String>();
		
		StringTokenizer st = new StringTokenizer(cad,",");
	    while (st.hasMoreTokens())
	    	lista.add(st.nextToken());
	    return lista;
	    
	}
	
	
	public int obtenerNumeroDeLicitacienes(){
		int resultado=0;
		try {
			Statement st = (Statement) miConexionlicitaciones.createStatement();
			String query = "select max(id) from licitaciones";
			ResultSet rs = st.executeQuery(query) ;

			while (rs.next()){
				//System.out.println(rs.getObject(1).toString());
				resultado=rs.getInt(1);
				break;
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultado;
	}
	
	
	public void obtenerEstructuraTablas(Tablas t){

		
		HashMap<String,Integer> id=this.obtenerEstructuraMaxIdTablas();
		
		 for (Entry<String, Integer> c : id.entrySet()){
			 String tabla=c.getKey();
			 int idTabla=c.getValue();
			 /*
				if (tabla.equals("contractawardnotice")){
					System.out.println(idTabla);
					System.exit(0);
				}
				*/
			 
			 String query="SELECT information_schema.COLUMNS.column_name,information_schema.COLUMNS.character_maximum_length FROM information_schema.columns WHERE table_schema = 'licitaciones' AND table_name  LIKE '"+tabla+"';";
			 
			 try {
				 
				Statement st = (Statement) miConexion.createStatement();
				ResultSet rs = st.executeQuery(query) ;
				Columnas col=t.crearColumnasBBDD(tabla); // Recupero Estructura columna
				col.id=idTabla;
				while (rs.next()){ // Para todas las columnas de tabla

					String columna=rs.getString(1); // Nombre Columna
					if (!columna.equals("idTabla") && !columna.equals("padre") && !columna.equals("posicion") && !columna.equals("hijos") && !columna.equals("path") && !columna.equals("idLicitacion") && !columna.equals("Tipo")){
						int longitud=rs.getInt(2); // Longitud
						col.col.put(columna, longitud); //Inserto valor
						col.id=id.get(tabla);
					}
					
				}
				t.tablas.put(tabla, col);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }

	}
	
	
	public HashMap<String,Integer> obtenerEstructuraMaxIdTablas(){
		HashMap<String,Integer> maxId=new HashMap<String,Integer>();
		String query="select table_name from information_schema.tables WHERE table_schema = 'licitaciones' AND table_name not LIKE 'tablas'";
		try {
			Statement st = (Statement) miConexion.createStatement();
			ResultSet rs = st.executeQuery(query) ;
			while (rs.next()){
				String tabla=rs.getString(1);
				//System.out.println(columna);
				//String q2="select max(idTabla) from "+rs.getObject(1).toString();
				//ResultSet rs2 = st.executeQuery(q2) ;
				maxId.put(tabla, obtenerMaxIdPorTabla(tabla));
			}

			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return maxId;
	}
	
	
	public int obtenerMaxIdPorTabla(String tabla){
		int id = 0;
		try {
			Statement st = (Statement) miConexion.createStatement();
			String q2="select max(idTabla) from licitaciones."+tabla;
			

			
			//System.out.println(q2);
			ResultSet rs = st.executeQuery(q2);
			while (rs.next()){
				id= rs.getInt(1);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
			id=0;
		}
		
		return id;
	}
	
	public void cargarMatedatos(Tablas t){
		//t.metadatos.put(key, value)
		String query="select id,tabla,padre,path,abs(nivel),tipo from tablas";
		
		try {
			Statement st = (Statement) miConexion.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()){
				String tabla=rs.getString(2);
				String padre=rs.getString(3);
				String path=rs.getString(4);
				int nivel=rs.getInt(5);
				String tipo=rs.getString(6);
				Metadatos m=t.new Metadatos(tabla, padre, path, tipo,nivel);
				t.metadatos.put(path.trim()+tipo.trim(), m);

			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Metadatos m=t.new Metadatos(null, null, null, null);
	}
	
	
	public void cargarEstructuras(Tablas t){
		this.obtenerEstructuraTablas(t);
		this.cargarMatedatos(t);
	}
	
	public int maximoIdLicitacionProcesado(){
		int res=0;
		ArrayList<String> tablas=getTablas();
		
		for (int i=0;i<tablas.size();i++){
			String query="SELECT max(CAST(idLicitacion AS UNSIGNED)) FROM "+tablas.get(i);
			try {
				Statement st = (Statement) miConexion.createStatement();
				ResultSet rs = st.executeQuery(query);
				while (rs.next()){
					if (rs.getInt(1)>res)
						res=rs.getInt(1);
				}
				rs.close();
				st.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return res;
	}
	
	public ArrayList<String> getTablas(){
		ArrayList<String> tablas= new ArrayList<String>();
		
		String query="select table_name from information_schema.tables WHERE table_schema = 'licitaciones' AND table_name not LIKE 'tablas'";
		
		try {
			Statement st = (Statement) miConexion.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()){
				tablas.add(rs.getString(1));
			}
			rs.close();
			st.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tablas;
		
	}
	/*
	
	public int crearTablaSiNoExiste(String tabla,String padre,int pos,String path,String tipo,String idLicitacion){
		//System.out.println("Solicitado crear tabla con tablaName="+tabla+",		Padre="+padre+",		Pos="+pos+",   	Path="+path+",		Tipo="+tipo);
		String resultado = null;
		int posicion = 0;
		
		try {
			Statement st = (Statement) miConexion.createStatement();
			String query = "SELECT COUNT(*) AS count FROM information_schema.tables WHERE  table_name = '"+tabla+"' and table_schema like 'licitaciones'";
			ResultSet rs = st.executeQuery(query) ;
			
			while (rs.next()){
				//System.out.println(rs.getObject(1).toString());
				resultado=(String) rs.getObject(1).toString();
				break;
			}
			

			
			if (resultado.trim().equals("0")){
				posicion=1;
				String query2 = "CREATE TABLE "+tabla+" (IdClavePrimaria BIGINT NOT NULL,padre nvarchar(200),posicion nvarchar(200),path nvarchar(400),idLicitacion nvarchar(30),PRIMARY KEY (IdClavePrimaria))";
				String query3 = "INSERT INTO TABLAS (TABLA,PADRE,PATH,NIVEL,TIPO) VALUES ('"+tabla+"','"+padre+"','"+path+"',"+(contar(path,'/')-1)+",'"+tipo+"')";
				String query4 = "INSERT INTO "+tabla+" (IdClavePrimaria,padre,posicion,path,idLicitacion) VALUES ("+posicion+",'"+padre+"',"+maxId(padre)+",'"+path+"','"+idLicitacion+"')";
				
				
				st.execute(query2) ;
				//System.out.println("Ejecutada q2:"+query2);
				//System.out.println("Creada tabla: "+tabla+ ", con padre "+padre);
				//System.out.println(query4);
				st.execute(query4);
				//System.out.println("Ejecutada q4:"+query4);
				//System.out.println("Voy a ejecutar  ----> "+query3);
				if (!existeEnTablas (tabla,padre,path,tipo)){
					st.execute(query3); 
				//System.out.println("Ejecutada q3:"+query3);
				}
				//System.out.println("Insertado registro en tabla TABLAS tabla: "+tabla+", con padre "+padre+", nivel"+nivel);
			} else {
				posicion=this.maxId(tabla)+1;
				String query4 = "INSERT INTO "+tabla+" (IdClavePrimaria,padre,posicion,path,idLicitacion) VALUES ("+posicion+",'"+padre+"',"+maxId(padre)+",'"+path+"','"+idLicitacion+"')";
				st.execute(query4);
				//System.out.println("Ejecutada q4 (rama else):"+query4);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			
			
			System.exit(1);
		}
		
		return posicion;
	}
	
	public boolean existeEnTablas (String tabla,String padre,String path,String tipo){
		
		boolean res=false;
		try {
			
			Statement st = (Statement) miConexion.createStatement();
			String query = "SELECT COUNT(*) FROM TABLAS WHERE TABLA='"+tabla.trim()+"' AND PADRE='"+padre.trim()+"' AND PATH='"+path.trim()+"' AND TIPO='"+tabla.trim()+"'" ;
			//System.out.println(query);

			ResultSet rs = st.executeQuery(query) ;
			
			
			if (rs.getInt(1)==0)
				res=false;
			else
				res=true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public void insertarEnColumna(int id,String tabla,String columna,String valor,String path){
		//System.out.println("IdClavePrimaria="+id+" ,Tabla= "+tabla+",Columna = "+columna+",Valor = "+valor+"       ,Path = "+path);
		String auxValor="";
		columna=columna.substring(0,63);
		
		if(!this.ExisteColumna(tabla, columna)){ // Si no existe columna
			this.crearColumna(tabla, columna);  // la creo
		} else {
		
			auxValor=this.recuperarValorColumna(id, tabla, columna,path); 
		
		}
		
		if (!auxValor.isEmpty()){ 
			//System.out.println("Valor auxiliar"+auxValor);
			valor=auxValor+" , "+valor;
		}
		
		String query = "Update "+tabla+" set "+columna+" = '"+valor+"' where posicion="+id+" and path ='"+path.trim()+"'";
		if (tabla.contains("PriorInformationNotice"))
			System.out.println(query);
		
		try {
			Statement st = (Statement) miConexion.createStatement();
			st.execute(query); // inserto valor en columna
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

	}
	
	public String recuperarValorColumna(int id,String tabla,String columna,String path) {

		String valor="";
		Statement st;
		String query="";
		columna=columna.substring(0,63);
		try {
			st = (Statement) miConexion.createStatement();
			query = "SELECT IFNULL((select "+columna+" from "+tabla+" where path='"+path+"' and posicion="+id+" limit 1),'ES_NULO')";
			ResultSet rs = st.executeQuery(query) ;
			while (rs.next()){
				if (!rs.getString(1).contains("ES_NULO"))
					valor=rs.getString(1).trim();
				break;
			}
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block()
			System.out.println(query);
			
			e.printStackTrace();
			System.exit(0);
		}
		//System.out.println(valor);
		return valor;
	}
	
	
	public boolean ExisteColumna(String tabla,String columna){
		boolean res=false;
		int col=0;
		columna=columna.substring(0,63);
		try {
			Statement st = (Statement) miConexion.createStatement();
			String query = "select count(*) FROM INFORMATION_SCHEMA.COLUMNS AS c1 where table_schema like 'licitaciones' and c1.column_name = '"+columna+"' and c1.table_name = '"+tabla+"'" ;
			ResultSet rs = st.executeQuery(query) ;

			while (rs.next()){
				col=rs.getInt(1);
				break;
			}
			
			if (col!=0)
				res=true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public void crearColumna(String tabla,String columna){
		

		columna=columna.substring(0,63);
		String query="ALTER TABLE "+tabla+" add "+columna+" VARCHAR(400)";
		
		try {
			Statement st = (Statement) miConexion.createStatement();
			st.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("No se pudo agregar al columna: "+query);
			//e.printStackTrace();
		}
	}
	
	public int maxId (String tabla){

		if (!tabla.equals("")){ 

			int id=-1;
			try {
				Statement st = (Statement) miConexion.createStatement();
				String query = "select max(IdClavePrimaria) from "+tabla ;
				//System.out.println(query);
				ResultSet rs = st.executeQuery(query) ;
				

				
				while (rs.next()){
					id=rs.getInt(1);
					break;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return id;
		} else {
			return -1;
		}
	} 

	public static int contar(String cadena, char caracter){
		int aux=0;
		int i;
		for (i=0;i<cadena.length();i++){
			if (cadena.charAt(i)==caracter){
				aux++;
			}
		}
		return aux;
	}
	*/
}
