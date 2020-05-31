package tech.miniswp.sparql;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.algebra.*;
import org.apache.jena.sparql.algebra.op.*;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprList;

public class SparqlAlgebraAuth {

    public static void main(String[] args) {


        String queryString = "PREFIX sp: <http://www.semanticweb.org/fwxda9/ontologies/2020/1/scaniaperson#>\n" +
                "\n" +
                "\n" +
              /*  "CONSTRUCT {\n" +
                "    ?emp ?p ?o . \n" +
                "    ?o ?p1 ?o2 .\n" +
                "}\n" + */
                "SELECT ?emp\n" +
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

        Query query = QueryFactory.create(queryString) ;

        System.out.println("Query: ");
        System.out.println(query);
        Op op = Algebra.compile(query) ;

        System.out.println("Query Algebra: ");
        System.out.println(op);
        OpWalker.walk(op, new AuthOpBGPVisitor());
        OpWalker.walk(op, new AuthOpProjectVisitor());
        OpWalker.walk(op, new AuthOpFilterVisitor());
    }

    public static class AuthOpBGPVisitor extends OpVisitorBase {
        @Override
        public void visit(OpBGP opBGP) {
            System.out.println("Visitor BGP: ");
            System.out.println(opBGP);
            System.out.println(opBGP.getName());
            System.out.println(opBGP.getPattern().get(0).getSubject());
            System.out.println(opBGP.getPattern().get(0).getPredicate());
            System.out.println(opBGP.getPattern().get(0).getObject().isVariable());
        }
    }

    public static class AuthOpProjectVisitor extends OpVisitorBase {
        @Override
        public void visit(OpProject opProject) {
            System.out.println("Visitor Project: ");
            //System.out.println(opProject);
            System.out.println(opProject.getVars());
        }
    }

    public static class AuthOpFilterVisitor extends OpVisitorBase {
        @Override
        public void visit(OpFilter opFilter) {
            System.out.println("Visitor Filter: ");
            //System.out.println(opProject);
            for (Expr exp : opFilter.getExprs()) {
                System.out.println("Exp: ");
                System.out.println(exp);
            }
        }
    }
}
