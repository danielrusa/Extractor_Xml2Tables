package tfg.fi.upm.es;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;




public class BaseDatos {

	Connection miConexion;
	Connection miConexionlicitaciones;
	int id=0;
	int nivel=1;

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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Fallo en la ejecucion de la Query:   <"+query+">");
			e.printStackTrace();
		}
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
