package com.example.misapps.ui.noticias;



import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.io.*;
import java.util.ArrayList;


/**
 * Parseador que se encarga de recuperar el xml de una página pasada y devolver
 * un ArrayList de objetos noticia
 */
public class Parser {

    private ArrayList<Noticia> noticias;
    private String uri;

    public Parser(String uri){
        this.noticias = new ArrayList<>();
        this.uri = uri;
    }

    public ArrayList<Noticia> getNoticias() {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        ArrayList<Noticia> noticias = new ArrayList();

        try {

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(this.uri);
            NodeList items = document.getElementsByTagName("item");

            for (int i = 0; i < items.getLength(); i++) {
                Node nodo = items.item(i);
                Noticia noticia = new Noticia();
                int contadorImagenes = 0;
                for (Node n = nodo.getFirstChild(); n != null; n = n.getNextSibling()) {

                    if (n.getNodeName().equals("title")) {
                        String titulo = n.getTextContent();
                        noticia.setTitulo(titulo);
                        //System.out.println("Título: " + titulo);
                    }
                    if (n.getNodeName().equals("link")) {
                        String enlace = n.getTextContent();
                        noticia.setLink(enlace);
                        //System.out.println("Enlace: " + enlace);
                    }
                    if (n.getNodeName().equals("description")) {
                        String descripcion = n.getTextContent();
                        noticia.setDescripcion(descripcion);
                        //System.out.println("Descripción: " + descripcion);
                    }
                    if (n.getNodeName().equals("pubDate")) {
                        String fecha = n.getTextContent();
                        noticia.setFecha(fecha);
                        //System.out.println("Fecha: " + fecha);
                    }
                    if (n.getNodeName().equals("content:encoded")) {
                        String contenido = n.getTextContent();
                        noticia.setContenido(contenido);
                        //System.out.println("Contenido: " + contenido);

                    }
                    if (n.getNodeName().equals("enclosure")) {
                        Element e = (Element) n;
                        String imagen = e.getAttribute("url");
                        //Controlamos que solo rescate una imagen
                        if (contadorImagenes == 0) {
                            noticia.setImagen(imagen);
                        }
                        contadorImagenes++;
                    }
                }
                noticias.add(noticia);
            }

        } catch (ParserConfigurationException e) {

            System.out.println(e.getMessage());

        } catch (IOException e) {
            System.out.println(e.getMessage());

        } catch (DOMException e) {
            System.out.println(e.getMessage());

        } catch (SAXException e) {
            System.out.println(e.getMessage());

        }

        return noticias;
    }
}
