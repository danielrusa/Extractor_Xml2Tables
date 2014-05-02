package tfg.fi.upm.es;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class ConexionDB {
     
    @SuppressWarnings("finally")
    private static boolean selecionado=false;
    private static String	ip="";

    
	public static Connection GetConnection(String bbdd)
    {
        Connection conexion=null;
     
        try
        {

        	
        	Scanner teclado =new Scanner(System.in);
        	int opcion;
        	
        	while (!selecionado){
        		System.out.println("\n***************************************************************");
        		System.out.println("*\tSeleccione tipo de conexion a BBDD                    *");
        		System.out.println("*\t----------------------------------                    *");
            	System.out.println("*\t1.................Local host                          *");
            	System.out.println("*\t2.................Conexion red local                  *");
            	System.out.println("*\t3.................Conexion remota                     *");
            	System.out.println("*                                                             *");
            	System.out.println("***************************************************************\n\n");
            	opcion=teclado.nextInt();
            	System.out.println(opcion);
            	
            	if (opcion==1){
            		ip="localhost";
            		System.out.println("Conectandose a "+ip);
            		selecionado=true;
            	}
            	
            	if (opcion==2){
            		ip="192.168.1.50";
            		System.out.println("Conectandose a "+ip);
            		selecionado=true;
            	}
            	
            	if (opcion==3){
            		ip="79.148.243.141";
            		System.out.println("Conectandose a "+ip);
            		selecionado=true;
            	}
            	
            	
            		
        	}

            Class.forName("com.mysql.jdbc.Driver");
            //String ipLocal="192.168.1.50";
            //String ipRemota="79.148.243.141";
            
            String servidor = "jdbc:mysql://"+ip+":3306/"+bbdd;
            String usuarioDB="daniel";
            String passwordDB="020202";
            conexion= DriverManager.getConnection(servidor,usuarioDB,passwordDB);
        }
        catch(ClassNotFoundException ex)
        {
            JOptionPane.showMessageDialog(null, ex, "Error1 en la Conexión con la BD "+ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion=null;
        }
        catch(SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex, "Error2 en la Conexión con la BD "+ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion=null;
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex, "Error3 en la Conexión con la BD "+ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion=null;
        }
        finally
        {
            return conexion;
        }
    }
}

