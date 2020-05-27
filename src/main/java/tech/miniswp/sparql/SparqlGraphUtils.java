package tech.miniswp.sparql;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.core.TriplePath;

public class SparqlGraphUtils {

    static ResultSet queryModel(Query query, Model model){

        ResultSet rs;

        try(QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            // Assumption: it's a SELECT query.
            rs = qexec.execSelect() ;

            /*
            System.out.println("Titles: ") ;
            for ( ; rs.hasNext() ; )
            {
                QuerySolution rb = rs.nextSolution() ;

                // Get title - variable names do not include the '?' (or '$')
                RDFNode x = rb.get("title") ;

                // Check the type of the result value
                if ( x instanceof Literal)
                {
                    Literal titleStr = (Literal)x  ;
                    System.out.println("    "+titleStr) ;
                }
                else
                    System.out.println("Strange - not a literal: "+x) ;

            }

             */
        }
        return rs;
    }

    static String getTypeOfTriple(TriplePath tp){
        String res = "";
        String sub;
        String pre;
        String obj;


        if (tp.getSubject().isURI()) {
            sub = "U";
        }
        else if (tp.getSubject().isVariable()) {
            sub = "V";
        }
        else {
            sub = "L";
        }
        res = res + sub;

        if (tp.getPredicate().isURI()) {
            pre = "U";
        }
        else if (tp.getPredicate().isVariable()) {
            pre = "V";
        }
        else {
            pre = "L";
        }
        res = res + pre;

        if (tp.getObject().isURI()) {
            obj = "U";
        }
        else if (tp.getObject().isVariable()) {
            obj = "V";
        }
        else {
            obj = "L";
        }
        res = res + obj;

        return res;
    }
}
