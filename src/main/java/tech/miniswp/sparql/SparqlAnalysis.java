package tech.miniswp.sparql;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

import java.util.ArrayList;
import java.util.List;

public class SparqlAnalysis {

    void tpHasPredicate(int i, List<SparqlGraphTP> ltp, Model model){

        System.out.println("TP no:" + i) ;

        SparqlGraphTP tp = ltp.get(i);

        ResultSet rs;
        String queryDomain = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?domain ?range WHERE { " +
                "<" + tp.getTp().getPredicate() + "> rdfs:domain ?domain . " +
                "<" + tp.getTp().getPredicate() + "> rdfs:range ?range }";

        Query dom = QueryFactory.create(queryDomain);

        // The order of results is undefined.
        System.out.println("Create TP using Predicate: ") ;
        try(QueryExecution qexec = QueryExecutionFactory.create(dom, model)) {
            rs = qexec.execSelect() ;
            for (; rs.hasNext(); ) {
                QuerySolution rb = rs.nextSolution();

                // Get Domain - variable names do not include the '?' (or '$')
                RDFNode d = rb.get("domain");
                RDFNode r = rb.get("range");
                Triple ntp = new Triple(NodeFactory.createURI(d.toString()),
                        NodeFactory.createURI(tp.getTp().getPredicate().toString()),
                        NodeFactory.createURI(r.toString()));
                System.out.println(ntp);
                tp.ntp.add(ntp);
            }
        }

    }

    void tpHasNoPredicate(int i, List<SparqlGraphTP> ltp, Model model) {
        System.out.println("TP no:" + i) ;

        SparqlGraphTP prevTp = null;
        SparqlGraphTP actualTp = ltp.get(i);
        List<Triple> tp = null;


        if (i > 0) {
            prevTp = ltp.get(i - 1);
            tp = prevTp.ntp;
        }
        else {
            // Add something to reject the query, if is only one TP ?s ?p ?o
            return;
        }

        System.out.println("PREV SS VAR: " + prevTp.getTp().getSubject().toString());
        System.out.println("ACTUAL SS VAR: " + actualTp.getTp().getSubject().toString());
        System.out.println("PREV OS VAR: " + prevTp.getTp().getObject().toString());
        System.out.println("ACTUAL OS VAR: " + actualTp.getTp().getObject().toString());
        String prevSS =  prevTp.getTp().getSubject().toString();
        String actualSS = actualTp.getTp().getSubject().toString();
        String prevOS = prevTp.getTp().getObject().toString();
        String actualOS =  actualTp.getTp().getObject().toString();

        String toUseInQuery = "";
        String rdfsDomainRange = null;

        //Check if we use Subject or Object in the query
        /*
        v1 prevSS ------- v2 prevOS
        v1,v2 actualSS ------ v1,v2 actualOS
         */
        if (prevSS.equals(actualSS)){
            toUseInQuery = "S";
            rdfsDomainRange = "rdfs:domain";
        }
        else if (prevOS.equals(actualSS)){
            toUseInQuery = "O";
            rdfsDomainRange = "rdfs:domain";
        }
        else if (prevSS.equals(actualOS)){
            toUseInQuery = "S";
            rdfsDomainRange = "rdfs:range";
        }
        else if (prevOS.equals(actualOS)){
            toUseInQuery = "O";
            rdfsDomainRange = "rdfs:range";
        }
        System.out.println("RESULT COMP: " + toUseInQuery + " - " + rdfsDomainRange);
        String usedByQuery = null;

        for (Triple t : tp) {
            ResultSet rs;
            if (toUseInQuery == "S") {
                usedByQuery = t.getSubject().toString();
            }
            else {
                usedByQuery = t.getObject().toString();
            }
            System.out.println("USING: " + usedByQuery);
            String queryPredicates = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?pred WHERE { ?pred " + rdfsDomainRange + " <" + usedByQuery + "> }";

            System.out.println(queryPredicates);
            Query pre = QueryFactory.create(queryPredicates);


            // The order of results is undefined.
            System.out.println("Get Predicates from Domain/Range: ");
            try (QueryExecution qexec = QueryExecutionFactory.create(pre, model)) {
                rs = qexec.execSelect();
                for (; rs.hasNext(); ) {
                    QuerySolution rb = rs.nextSolution();

                    // Get Domain - variable names do not include the '?' (or '$')
                    RDFNode p = rb.get("pred");
                    System.out.println("PREDICATES: " + p);
                    List<Triple> newListTp = tpGetTPFromPredicate(NodeFactory.createURI(p.toString()), model);
                    actualTp.ntp.addAll(newListTp);
                }
            }
        }
    }

    List<Triple> tpGetTPFromPredicate(Node pred, Model model) {

        List<Triple> ntpList = new ArrayList<Triple>();

        ResultSet rs;
        String queryDomain = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?domain ?range WHERE { " +
                "<" + pred + "> rdfs:domain ?domain . " +
                "<" + pred + "> rdfs:range ?range }";

        Query dom = QueryFactory.create(queryDomain);

        // The order of results is undefined.
        System.out.println("Create TP using Predicate: ");
        try (QueryExecution qexec = QueryExecutionFactory.create(dom, model)) {
            rs = qexec.execSelect();
            for (; rs.hasNext(); ) {
                QuerySolution rb = rs.nextSolution();

                // Get Domain - variable names do not include the '?' (or '$')
                RDFNode d = rb.get("domain");
                RDFNode r = rb.get("range");
                Triple ntp = new Triple(NodeFactory.createURI(d.toString()),
                        NodeFactory.createURI(pred.toString()),
                        NodeFactory.createURI(r.toString()));
                System.out.println("RESULT for PREDICATE: " + ntp);
                ntpList.add(ntp);
            }
        }
        return ntpList;
    }
}
