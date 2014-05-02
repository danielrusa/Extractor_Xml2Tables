package tfg.fi.upm.es;


import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class UtilidadesXML {
   
    /**
     * Este método busca un fichero de tipo XML en el classpath crea un objeto
     * de tipo org.w3c.dom.Document.
     * @param fichero: El nombre del fichero a procesar.
     * @return
     * @throws Exception
     */
    public static Document File2Document(String fichero) throws Exception {
      System.out.println(fichero);
      /*
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      ClassLoader loader=(UtilidadesXML.class).getClassLoader();
      URL urlfichero=loader.getResource(fichero);       
      Document XMLDoc=factory.newDocumentBuilder().parse(new   InputSource(urlfichero.openStream()));
      */
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      Document XMLDoc = factory.newDocumentBuilder().parse(new InputSource(new
      FileInputStream(fichero)));
      return XMLDoc;
    }
   
    /**
     * Este método convierte un objeto de tipo org.w3c.dom.Node a String
     * @param nodo
     * @return
     * @throws Exception
     */
    public static String Node2String (Node nodo) throws Exception {
        StringWriter sw = new StringWriter();       
        StreamResult sR=new StreamResult(sw);               
        Transformer transformer =      TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(nodo),sR);       
        return sw.toString();       
    }
   
    
    public static Document String2Document(String xmlString) throws Exception {
        DocumentBuilder db =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document XMLDoc = db.parse(new InputSource(new StringReader(xmlString)));
        return XMLDoc;
      }
    
/**
     * Este método convierte un objeto de tipo org.w3c.dom.NodeList a String
     * Este método solo sirve para pintar los resultados.
     * @param nodo
     * @return
     * @throws Exception
     */
    public static String Nodes2String (NodeList nodes) throws Exception {
        StringBuffer lista = new StringBuffer();
       
        for(int i=0;i<nodes.getLength();i++) {
            Node node = (Node)nodes.item(i);
            lista.append(UtilidadesXML.Node2String(node));           
        }
       
        return lista.toString();
    }

    /**
     * Devuelve true si el nodo es de tipo Node.ELEMENT_NODE y se llama "nombre"
     * @param nodo
     * @param nombre
     * @return
     */
    public static boolean esNodo(Node nodo, String nombre) {
        return (esNodo(nodo) && (nodo.getNodeName().equalsIgnoreCase(nombre)));
    }
   
    /**
     * Devuelve true si el nodo es de tipo Node.ELEMENT_NODE
     * @param nodo
     * @param nombre
     * @return
     */
    public static boolean esNodo(Node nodo) {
        return (nodo.getNodeType() == Node.ELEMENT_NODE);
    }
   
    /**
     * Devuelve true si el nodo es de tipo TEXT_NODE
     * @param nodo
     * @return
     */
    public static boolean esTexto(Node nodo) {
        return (nodo.getNodeType() == Node.TEXT_NODE);
    }
   
    /**
     * Devuelve el texto de un nodo: <tag>TEXTO</tag>
     * @param n
     * @return
     */
    public static String getTexto (Node n) {
        NodeList nl = n.getChildNodes();
        Node act=null;
        for (int i=0; i < nl.getLength(); i++) {
            act = nl.item(i);
            if (act == null) return null;
            if ((act.getNodeType() == Node.CDATA_SECTION_NODE)||(act.getNodeType() == Node.TEXT_NODE)) return act.getNodeValue();
        }
        return "";
      }
   
    /**
     * Devuelve el valor del atributo "nombre" de un nodo
     * @param nombre
     * @param nodo
     * @return
     */
    public static String dameAtributoNodo(String nombre, Node nodo) {
        NamedNodeMap mapa = nodo.getAttributes();
        String valor = null;
        if(mapa!=null) {
            Node nodoAt = mapa.getNamedItem(nombre);
            if(nodoAt!=null)
                valor = nodoAt.getNodeValue();
        }
        return valor;
    }
   
}