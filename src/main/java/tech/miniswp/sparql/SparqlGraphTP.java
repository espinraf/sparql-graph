package tech.miniswp.sparql;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.TriplePath;

import java.util.ArrayList;
import java.util.List;

public class SparqlGraphTP {

    public TriplePath tp;
    public String typeTP; //NOT USED
    public List<Triple> ntp= new ArrayList<Triple>();

    public TriplePath getTp() {
        return tp;
    }

    public void setTp(TriplePath tp) {
        this.tp = tp;
    }

    public String getTypeTP() {
        return typeTP;
    }

    public void setTypeTP(String typeTP) {
        this.typeTP = typeTP;
    }

}
