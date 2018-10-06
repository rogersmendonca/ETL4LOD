package br.ufrj.ppgi.greco.kettle.sparqlupdate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

/*

 sudo virtuoso-t +configfile virtuoso.ini

 sudo killall virtuoso-t

 sudo rm virtuoso.db virtuoso.lck virtuoso.pxa virtuoso-temp.db virtuoso.trx


 select * from <http://www.lodbr.com.br> where { ?a ?b ?c . }

 */

public class Main
{

    public static void main(String[] args) throws Exception
    {
        SparqlUpdate su = new SparqlUpdate("http", "146.164.3.27",
                "sparql-auth", 8890, "lodbr", "123456");

        // File file = new File("C:\\Users\\Expedito\\Downloads\\download.rdf");
        File file = new File("D:\\User\\LodBr\\Kettle\\rdf\\lattes.rdf");
        // File file = new File("D:\\User\\LodBr\\Kettle\\rdf\\teste.rdf");
        byte[] buffer = new byte[(int) file.length()];
        BufferedInputStream f = new BufferedInputStream(new FileInputStream(
                file));
        f.read(buffer);
        String rdf = new String(buffer, "UTF-8");

        String graph = "http://www.lodbr.com.br";

        System.out.println("delete graph: " + su.deleteGraph(graph));
        System.out.println("create graph: " + su.createGraph(graph));
        System.out.println("insert data:  " + su.insertTriples(graph, rdf));
        f.close();
    }

}
