package tfg.fi.upm.es;


import java.sql.Connection;




import java.util.Date;

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
		Tablas t= new Tablas(db);
		int fin=db.obtenerNumeroDeLicitacienes();
		int ini=1;
		Date ti=new Date();
		for (int i=ini;i<=fin;i++){
			System.out.println("\n<<<<<<<<<<		Licitacion Nº ["+i+"]			>>>>>>>>>>");
			Datos d=new Datos(t,db);
			// bucle de recorrido de licitaciones
			Date t1= new Date();
			ExtractorXml ex= new ExtractorXml(db,String.valueOf(i),t,d);
			if (ex.tratable)
				ex.recorrerNodos();
			Date t2= new Date();
			Double porcentaje=(double)((i*100.0)/(fin*1.0));
			Double difT=(double) (t2.getSeconds()-ti.getSeconds());
			Double tMed=difT/((i-ini)+1);
			int tEsperado=(int) (tMed*(fin-i));
			System.out.println("\n===================================================================================================================================");
			System.out.println("		Segundos transcurridos entre 2 iteraciones:	["+(t1.getSeconds()-t2.getSeconds())+"]");
			System.out.println("		Procesada iteracion: ["+i+"],		Iteraciones Restantes:	["+(fin-i)+"],		Porcentaje Completado ["+porcentaje+"]");
			System.out.println("		Tiempo restante estimado para finalizacion:		"+pasarSegundos(tEsperado));
			System.out.println("===================================================================================================================================\n");

		}

		//t.imprimirTablas();
		//d.imprimirDatos();
	}
	
	public static String pasarSegundos(int t){

		int dias,hor,min,seg;
		dias=t/(3600*24);
        hor=(t-(3600*24)*dias)/3600;  
        min=(t-(3600*hor)-((3600*24)*dias))/60;  
        seg=t-((dias*(3600*24))+(hor*3600)+(min*60));  
        return "Dias: ["+dias+"],	Horas: ["+hor+"],	Minutos: ["+min+"],	Segundos: ["+seg+"]";
	}
}
