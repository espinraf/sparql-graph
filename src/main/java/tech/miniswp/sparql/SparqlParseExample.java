package tech.miniswp.sparql;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.Var;


public class SparqlParseExample {

    public static void main(String[] args) {

        final String queryString  = "" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX champ: <http://sdp.collaborationlayer-traton.com/champ#>\n" +
                "                 \n" +
                "SELECT DISTINCT ?crr ?part\n" +
                " WHERE {\n" +
                "?part champ:PartId \"1888075\" .\n" +
                "?part1 champ:PartId \"81.27120-0059\" .\n" +
                "?crr champ:hasPart ?part .\n" +
                "?crr champ:hasPart ?part1 .\n" +
                "} ";

        final Query query = QueryFactory.create( queryString );

        System.out.println( "== before ==\n" + query );
        System.out.println("Graph URIs: " + query.getDatasetDescription());
        System.out.println("Result Vars: " + query.getResultVars());
        System.out.println("GROUP BY: " + query.getGroupBy());
        System.out.println("HAVING: " + query.getHavingExprs());
        System.out.println("LIMIT: " + query.getLimit());


        // CHECK IF IS SELECT
        System.out.println("Is SELECT: \n" + SparqlMgmt.isSelect(query));

        // GET MAP WITH PREFIXES
        System.out.println("Get Prefix Map: \n" + SparqlMgmt.getPrefixMap(query));

        // ADD A PREFIX
        SparqlMgmt.setPrefixMap(query, "scania", "http://ixcb.scania.com/demo#");

        // GET LIST OF TRIPLES
        System.out.println("GetListTriples: \n" + SparqlMgmt.getListTriples(query));

        //CREATE A TRIPLE
        Node s = NodeFactory.createURI("http://ixcb.scania.com/demo#part1");
        Node p = NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        final Var o = Var.alloc( "crr" );
        Triple myt = new Triple( s, p, o );

        // ADD TRIPLE AFTER POS 2, STARTS WITH 0
        SparqlMgmt.addTriple(query, myt,2);
        // REMOVE FIRST TRIPLE
        SparqlMgmt.removeTriple(query, 0);

        System.out.println( "== after ==\n" + query );


    }

}
