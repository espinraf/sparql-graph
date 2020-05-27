package tech.miniswp.sparql;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.util.FileManager;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SparqlGraph {

    public static void main(String[] args) {
        List<TriplePath> tpList = null;

        ClassPathResource fileOWL = new ClassPathResource("scaniaperson.owl");

        Model ont = null;
        try {
            ont = FileManager.get().loadModel(fileOWL.getURL().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String queryString = "PREFIX sp: <http://www.semanticweb.org/fwxda9/ontologies/2020/1/scaniaperson#>\n" +
                "\n" +
                "\n" +
                "CONSTRUCT {\n" +
                "    ?emp ?p ?o . \n" +
                "    ?o ?p1 ?o2 .\n" +
                "}\n" +
                "WHERE {\n" +
                "    \n" +
                "    sp:Tugay sp:worksAtDepartment ?dep .\n" +
                "    ?emp ?p ?dep .\n" +
                "    #?dep ?p1 ?o2 .\n" +
                "    \n" +
                "    #MINUS { ?x sp:hasMonthlySalary ?o . }\n" +
                "    #MINUS { ?s1 sp:worksAtDepartment sp:IXCB . }\n" +
                "    #FILTER NOT EXISTS { ?w sp:hasMonthlySalary ?o . }\n" +
                "    #FILTER NOT EXISTS { ?s1 sp:worksAtDepartment sp:IXCB . }\n" +
                "    #MINUS { sp:L1 sp:hasProtection ?m . FILTER(?p = ?m) }\n" +
                "    FILTER NOT EXISTS { sp:L1 sp:hasProtection ?m . FILTER(?p = ?m) }\n" +
                "    #FILTER NOT EXISTS { <http://simuino.com/$level> <http://simuino.com/hasProtection> ?m . FILTER(?p1 = ?m)}\n" +
                "}";


        SparqlAnalysis spa = new SparqlAnalysis();
        Query query = QueryFactory.create( queryString );

        tpList = SparqlMgmt.getListTriples(query);

        List<SparqlGraphTP> queryTPs = new ArrayList<SparqlGraphTP>();
        int i = 0;

        for (TriplePath tp : tpList){
            System.out.println(tp);
            //String ttp = SparqlGraphUtils.getTypeOfTriple(tp);
            SparqlGraphTP qtp = new SparqlGraphTP();
            qtp.setTp(tp);
            //qtp.setTypeTP(ttp);
            queryTPs.add(qtp);

            if (tp.getPredicate().isURI()){
                System.out.println("Executing HAS PREDICATE");
                spa.tpHasPredicate(i, queryTPs, ont);
                System.out.println(qtp.ntp);
            }
            else {
                System.out.println("Executing HAS NO PREDICATE");
                spa.tpHasNoPredicate(i, queryTPs, ont);
                System.out.println(queryTPs.get(i).ntp);
            }
            i++;
        }

        for (SparqlGraphTP sgtp : queryTPs){
            List<Triple> ntp = sgtp.ntp;
            for(Triple t : ntp){
                System.out.println("<" + t.getSubject() + ">  <" + t.getPredicate() + ">  <"+ t.getObject() + "> .");
            }
        }
    }
}
