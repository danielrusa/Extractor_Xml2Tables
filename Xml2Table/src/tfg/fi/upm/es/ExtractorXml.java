package tfg.fi.upm.es;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ExtractorXml {

	private BaseDatos bd;
	private String xml,raiz,padre,tipo,path,id;
	private Document doc;
	private int posicion;
	private String tablaActual;
	private Tablas t;
	private Datos d;
	public boolean tratable;
		
	
	public ExtractorXml(BaseDatos bd,String id,Tablas t,Datos d) {
		super();
		this.bd = bd;
		this.id=id;
		String[] resultado=bd.obtenerXmlPorId(id);
		this.tipo=resultado[0];
		this.xml=resultado[1];
		tratable=(xml.trim()!=null && xml.trim()!="");
		if (tratable){
			this.d=d;
			this.t=t;
			//System.out.println(tipo);

			try {

				doc = UtilidadesXML.String2Document(xml);
				NodeList nl=doc.getChildNodes();
				raiz=nl.item(0).getNodeName().trim();
				padre=raiz;
				tablaActual=raiz;
				path="/"+raiz;
				//System.out.println("Raiz="+raiz);
				t.crearTabla(raiz,padre,path,tipo);
				//t.imprimirTablas();
				d.crearTabla(nl.item(0), this.getHijos(nl.item(0)), path+"/",id,tipo);
				//d.imprimirDatos();
				//System.exit(0);
				posicion=t.getPoscion(raiz);//bd.crearTablaSiNoExiste(raiz,"",0,path,tipo);////////////////////////////////////
				//System.out.println("posicion="+posicion);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void recorrerNodos(){
		XPath xpath = XPathFactory.newInstance().newXPath();
        String expression = "/"+raiz+"/descendant ::*";//child::*"; 
        
        NodeList nodes;
		try {
			nodes = (NodeList) xpath.evaluate(expression, doc.getDocumentElement(), XPathConstants.NODESET);
	        
	        //System.out.println("Longitud "+nodes.getLength());
	        
	        for(int i=0;i<nodes.getLength();i++) {
	        	
	        	String path=obtenerPath(nodes.item(i)).replaceAll("cbc:(.*?)/","").replaceAll("ext:", "");
	        	//System.out.println("PPPPAAAAATHHH   "+path);
	        	if (nodes.item(i).getNodeName().contains("cbc:")) // atributos de la tabla
	        	{
	        		t.insertarColumna(this.getPadre(nodes.item(i)), this.getColumna(nodes.item(i)), this.getLongitud(nodes.item(i)),this.getPadre(nodes.item(i)),path,tipo);
	        		d.insertaColumna(nodes.item(i), this.getValue(nodes.item(i)), this.getHijos(nodes.item(i).getParentNode()), path,id,tipo);
	        		//d.imprimirDatos();
	        		/*if (i==13){
	        			d.imprimirDatos();
	        				System.exit(0);
	        		}*/
	        		//d.insertarDato(this.getPadre(nodes.item(i)), this.getColumna(nodes.item(i)), this.getValue(nodes.item(i)));
	        		/* Obtencion de atributos */
	        		/*
	        		NamedNodeMap nm =nodes.item(i).getAttributes();
	        		if(nm.getLength()>0){
	        			for (int j=0;j<nm.getLength();j++){
	        				//System.out.println("Valor----->"+nm.item(j).getNodeValue()+"  Nombre   ---------<"+nm.item(j).getNodeName());

	        				t.insertarColumna(this.getPadre(nodes.item(i)), this.getNombreAtributo(nodes.item(i), nm.item(j)), this.getLongitud(nm.item(j)));
	        				d.insertaColumna( this.getNombreAtributo(nodes.item(i), nm.item(j)),this.getPadre(nodes.item(i)),nodes.item(i), this.getValue(nm.item(j)), this.getHijos(nodes.item(i).getParentNode()), path);
	        				//d.imprimirDatos();
	        		
	        				//d.insertarDato(this.getPadre(nodes.item(i)), this.getNombreAtributo(nodes.item(i), nm.item(j)), this.getValue(nm.item(j)));
	        				//t.insertarColumna(nodes.item(i).getParentNode().getNodeName().replaceAll("cac:", ""), nodes.item(i).getNodeName().replaceAll("cbc:", "")+nm.item(j).getNodeName(), nm.item(j).getTextContent().length());
	        				//bd.insertarEnColumna(posicion, nodes.item(i).getParentNode().getNodeName().replaceAll("cac:", ""), nodes.item(i).getNodeName().replaceAll("cbc:", "")+nm.item(j).getNodeName(), nm.item(j).getTextContent(),path);
	        				//System.out.println("Para el nodo: " +nodes.item(i).getNodeName().replaceAll("cbc:", "")+"		Atributo "+j+" "+nodes.item(i).getNodeName().replaceAll("cbc:", "")+nm.item(j).getNodeName()+": "+nm.item(j).getTextContent());
	        			}
	        		}
	        		*/
	        		
	        		/*
	        		if (nodes.item(i).getNodeName().contains("ActivityCode"))
	        			System.out.println("Contiene el puto "+nodes.item(i).getNodeName());
	        		bd.insertarEnColumna(posicion, nodes.item(i).getParentNode().getNodeName().replaceAll("cac:", ""), nodes.item(i).getNodeName().replaceAll("cbc:", ""), nodes.item(i).getTextContent(),path);
	        		NamedNodeMap nm =nodes.item(i).getAttributes();
	        		//System.out.println("Tamaño="+nm.getLength());
	        		if(nm.getLength()>0){
	        			for (int j=0;j<nm.getLength();j++){
	        				bd.insertarEnColumna(posicion, nodes.item(i).getParentNode().getNodeName().replaceAll("cac:", ""), nodes.item(i).getNodeName().replaceAll("cbc:", "")+nm.item(j).getNodeName(), nm.item(j).getTextContent(),path);
	        				//System.out.println("Para el nodo: " +nodes.item(i).getNodeName().replaceAll("cbc:", "")+"		Atributo "+j+" "+nodes.item(i).getNodeName().replaceAll("cbc:", "")+nm.item(j).getNodeName()+": "+nm.item(j).getTextContent());
	        			}
	        		}*/
	        		
	        		//bd.insertarEnColumna(posicion, nodes.item(i).getParentNode().getNodeName().replaceAll("cac:", ""), nodes.item(i).getNodeName().replaceAll("cbc:", ""), nodes.item(i).getTextContent());
	        	} else { //(String tabla,String padre,int pos,String path)
	        		//System.out.println(obtenerPath(nodes.item(i)));
	        		//System.out.println("Padre: "+nodes.item(i).getNodeName()+"\n\tHijos:  "+this.getHijos(nodes.item(i)));
	        		t.crearTabla(nodes.item(i).getNodeName().replaceAll("cac:", "").replaceAll("ext:", "").toLowerCase(),this.getPadre(nodes.item(i)).toLowerCase(),path,tipo);
	        		//System.out.println("Path--<"+path);
	        		d.crearTabla(nodes.item(i), this.getHijos(nodes.item(i)), path,id,tipo);
	        		// contractawardnotice
	        		// ContractAwardNotice
	        		//bd.crearTablaSiNoExiste(nodes.item(i).getNodeName().replaceAll("cac:", ""),nodes.item(i).getParentNode().getNodeName().replaceAll("cac:", ""),posicion,obtenerPath(nodes.item(i)),tipo);
	        	}
	        	
	        	/*
	        	//System.out.println("Papa:"+nodes.item(i).getParentNode());
	        	if (nodes.item(i).getNodeName().contains("cbc"))
	        		System.out.print("\t");
	        	System.out.print(nodes.item(i).getNodeName()+"----->"+"Papa:"+nodes.item(i).getParentNode().getNodeName() );
	        	if (nodes.item(i).getNodeName().contains("cbc"))
	        		System.out.println(nodes.item(i).getTextContent());
	        	else
	        		System.out.println("\n");*/
	        }
	       
	       
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        //System.out.println(UtilidadesXML.Nodes2String(nodes));
		t.ejecutarQuerys();
		t.vaciarQuerys();
		d.insertarDatos();
		
		
	}
	
	private String getPadre(Node n){
		return n.getParentNode().getNodeName().replaceAll("cac:", "").replaceAll("ext:", "").toLowerCase();
	}
	
	private String getColumna(Node n){
		return n.getNodeName().replaceAll("cbc:", "");
	}
	
	private String getValue(Node n){
		return n.getTextContent();
	}
	
	private int getLongitud(Node n){
		return n.getTextContent().length();
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
			return t.toLowerCase();
		}
	}
	
	private String obtenerPath(Node node){
		String salida="/";
		Node nodoAux=node;
		List<String> padres=new ArrayList<String>();
		padres.add(nodoAux.getNodeName().replaceAll("cac:", "").replaceAll("ext:", ""));
		
		while (nodoAux.getParentNode()!=null && !nodoAux.getParentNode().getNodeName().replaceAll("cac:", "").replaceAll("ext:", "").contains("#document"))
		{
			nodoAux=nodoAux.getParentNode();
			//System.out.println("Nodo="+node.getNodeName().replaceAll("cac:", "")+",   Padre"+nodoAux.getNodeName().replaceAll("cac:", ""));
			padres.add(nodoAux.getNodeName().replaceAll("cac:", "").replaceAll("ext:", ""));
		}
		
		for (int i=padres.size()-1;i>=0;i--){
			salida=salida+padres.get(i)+"/";
		}
		
		return salida;
		
	}
	
	public String getHijos(Node n){
		NodeList nl=n.getChildNodes();
		String aux="";
		for (int i=0;i<nl.getLength();i++){
			if (!nl.item(i).getNodeName().equals("#text") && nl.item(i).getNodeName().contains("cac") && !aux.contains(nl.item(i).getNodeName().replaceAll("cac:", "")) ){
				aux=aux+nl.item(i).getNodeName().replaceAll("cac:", "").replaceAll("ext:", "");
				if (i!=(nl.getLength()-2)){
					aux=aux+" | ";
				}
			}
		}
		return aux;
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

}
