package com.example.misapps.ui.mapas.util;

import android.content.Context;
import android.os.Environment;

import com.google.android.gms.maps.model.LatLng;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class GpxParser {

    private static String ruta = "/misApps/gpx";

    /**
     * Método para escribir un XML en la memoria externa
     * Fuente --> https://www.lawebdelprogramador.com/codigo/Java/3202-Como-crear-un-archivo-XML-con-Java.html
     * @param listaRuta
     */
    public static void escribirXML(ArrayList<LatLng> listaRuta, Context context){

        File directorio = new File (Environment.getExternalStorageDirectory()+ruta);
        if (!directorio.exists()){
            directorio.mkdirs();
        }

        System.out.println(directorio.getAbsolutePath());
        File ficheroXML = new File(Environment.getExternalStorageDirectory()+ruta,System.currentTimeMillis()+".xml");


        try{
            ficheroXML.createNewFile();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element raiz = doc.createElement("gpx");
            raiz.setAttribute("xmlns","http://www.topografix.com/GPX/1/1");
            raiz.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
            raiz.setAttribute("creator","misApps");
            raiz.setAttribute("version","1.1");
            raiz.setAttribute("xsi:schemaLocation","http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");
            doc.appendChild(raiz);
            Element trk = doc.createElement("trk");
            raiz.appendChild(trk);
            Element trkseg = doc.createElement("trkseg");
            trk.appendChild(trkseg);

            //Ahora metemos todos los puntos de la ruta
            for (int i=0;i<listaRuta.size();i++){
                Element trkpt = doc.createElement("trkpt");
                trkpt.setAttribute("lat",String.valueOf(listaRuta.get(i).latitude));
                trkpt.setAttribute("lon",String.valueOf(listaRuta.get(i).longitude));
                trkseg.appendChild(trkpt);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(ficheroXML.getPath());
            transformer.transform(source, result);

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public static ArrayList<LatLng> leerFicheroXML(File fichero){
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(fichero);
            NodeList itemes = document.getElementsByTagName("trkpt");
            ArrayList<LatLng> listRuta = new ArrayList<>();

            for (int i=0;i<itemes.getLength();i++){
                Node node = itemes.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element)node;
                    Double lat = Double.parseDouble(element.getAttribute("lat"));
                    Double lon = Double.parseDouble(element.getAttribute("lon"));
                    LatLng aux = new LatLng(lat,lon);
                    listRuta.add(aux);
                }
            }
            return  listRuta;

        }catch(Exception e){
            return null;

        }
    }
}
