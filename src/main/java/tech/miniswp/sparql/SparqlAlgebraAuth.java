package tech.miniswp.sparql;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.algebra.*;
import org.apache.jena.sparql.algebra.op.*;
import org.apache.jena.sparql.core.BasicPattern;
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

        String queryString1 = "PREFIX sp: <http://www.semanticweb.org/fwxda9/ontologies/2020/1/scaniaperson#>\n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "\n" +
                "\n" +
                "SELECT ?emp ?p ?o ?p1 ?o2 ?dep\n" +
                "WHERE {\n" +
                "    \n" +
                "    {\n" +
                "    ?emp sp:worksAtDepartment sp:IXCA .\n" +
                "    ?emp ?p ?o .\n" +
                "    ?o ?p1 ?o2 .\n" +
                "}\n" +
                "    UNION {\n" +
                "    ?dep rdf:type sp:Department\n" +
                "}\n" +
                "}";

        Query query = QueryFactory.create(queryString1) ;

        System.out.println("Query: ");
        System.out.println(query);
        Op op = Algebra.compile(query) ;

        System.out.println("Query Algebra: ");
        System.out.println(op);
        OpWalker.walk(op, new AuthOpBGPVisitor());
        OpWalker.walk(op, new AuthOpProjectVisitor());
        OpWalker.walk(op, new AuthOpUnionVisitor());
        OpWalker.walk(op, new AuthOpFilterVisitor());
    }

    public static class AuthOpBGPVisitor extends OpVisitorBase {
        @Override
        public void visit(OpBGP opBGP) {
            System.out.println("Visitor BGP: ");
            System.out.println(opBGP);
            System.out.println(opBGP.getName());
            int i = 0;
            for (Triple pttr : opBGP.getPattern().getList()) {
                System.out.println("BGP: " + i); i++;
                System.out.println(pttr.getSubject());
                System.out.println(pttr.getPredicate());
                System.out.println(pttr.getObject());
            }
        }
    }

    public static class AuthOpUnionVisitor extends OpVisitorBase {
        @Override
        public void visit(OpUnion opUnion) {
            System.out.println("UNION BGP: ");
            System.out.println(opUnion);
            System.out.println(opUnion.getName());

            System.out.println("Union Left: " + opUnion.getLeft());
            OpWalker.walk(opUnion.getLeft(), new AuthOpBGPVisitor());
            System.out.println("Union Right: " + opUnion.getRight());
            OpWalker.walk(opUnion.getRight(), new AuthOpBGPVisitor());

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
