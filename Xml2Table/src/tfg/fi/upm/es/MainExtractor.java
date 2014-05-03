package tfg.fi.upm.es;


import java.sql.Connection;

import javax.xml.soap.Node;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;



public class MainExtractor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BaseDatos db = new BaseDatos();
		Tablas t= new Tablas();
		for (int i=3608;i<3610;i++){
		Datos d=new Datos(t);
		 // bucle de recorrido de licitaciones
		
		ExtractorXml ex= new ExtractorXml(db,String.valueOf(3608),t,d);
		ex.recorrerNodos();
		System.out.println("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
		}
		
		t.imprimirTablas();
		//d.imprimirDatos();
	}		
}
