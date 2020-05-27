package tech.miniswp.sparql;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;

import java.util.*;

public class SparqlMgmt {

    static List<TriplePath> tpList = new ArrayList<TriplePath>();
    static Map<String, String> prefixMap = new HashMap<String, String>();

    public static Map<String, String> getPrefixMap(Query query) {
        for (Map.Entry<String, String> e : query.getPrefixMapping().getNsPrefixMap().entrySet()) {
            prefixMap.put(e.getKey(), e.getValue());
        }
        return prefixMap;
    }

    public static void setPrefixMap(Query query, String prefix, String uri) {

        query.setPrefix(prefix, uri);

    }

    public static boolean isSelect(Query query){
        return query.isSelectType();
    }
    public static boolean isConstruct(Query query){
        return query.isConstructType();
    }
    public static boolean isAsk(Query query){
        return query.isAskType();
    }

    public static List<TriplePath> getListTriples(Query query){
        ElementWalker.walk(query.getQueryPattern(),
                new SparqlGetVisitorImpl());
        return tpList;
    }

    public static void addTriple(Query query, Triple tp, Integer idx) {
        ElementWalker.walk(query.getQueryPattern(),
                new SparqlAddVisitorImpl(tp, idx));
    }

    public static void removeTriple(Query query, Integer idx) {
        ElementWalker.walk(query.getQueryPattern(),
                new SparqlRemoveVisitorImpl(idx));
    }


    public static class SparqlGetVisitorImpl extends ElementVisitorBase {

        Triple tp;
        Integer idx;

        public SparqlGetVisitorImpl() {
            super();
        }

        @Override
        public void visit(ElementPathBlock el) {
            ListIterator<TriplePath> it = el.getPattern().iterator();
            while (it.hasNext()) {
                final TriplePath tph = it.next();
                tpList.add(tph);
            }
        }
    }


    public static class SparqlAddVisitorImpl extends ElementVisitorBase {

        Triple tp;
        Integer idx;

        public SparqlAddVisitorImpl(Triple tp, Integer idx) {
            this.tp = tp;
            this.idx = idx;
        }

        @Override
        public void visit(ElementPathBlock el) {
            int c = 0;
            ListIterator<TriplePath> it = el.getPattern().iterator();
            while (it.hasNext()) {
                final TriplePath tph = it.next();
                if (this.idx == c) {
                    it.add(new TriplePath(tp) );
                }
                c = c + 1;
            }
        }
    }

    public static class SparqlRemoveVisitorImpl extends ElementVisitorBase {

        Triple tp;
        Integer idx;

        public SparqlRemoveVisitorImpl( Integer idx) {
            this.idx = idx;
        }

        @Override
        public void visit(ElementPathBlock el) {
            int c = 0;
            ListIterator<TriplePath> it = el.getPattern().iterator();
            while (it.hasNext()) {
                final TriplePath tph = it.next();
                if (this.idx == c) {
                    it.remove();
                }
                c = c + 1;
            }
        }
    }
}