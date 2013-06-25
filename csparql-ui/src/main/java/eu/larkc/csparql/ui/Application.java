package eu.larkc.csparql.ui;

/*
 * @(#)Application.java   1.0   Oct 02, 2009
 *
 * Copyright 2009-2009 Politecnico di Milano. All Rights Reserved.
 *
 * This software is the proprietary information of Politecnico di Milano.
 * Use is subject to license terms.
 *
 * @(#) $Id: Application.java 243 2010-05-13 14:26:54Z dbarbieri $
 */

import java.text.ParseException;

import eu.larkc.csparql.cep.api.RDFStreamAggregationTestGenerator;
import eu.larkc.csparql.cep.api.RdfStream;
import eu.larkc.csparql.cep.api.TestGenerator;
import eu.larkc.csparql.core.engine.CsparqlEngine;
import eu.larkc.csparql.core.engine.CsparqlEngineImpl;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;

public final class Application {

   /**
    * @param args
    */

   public static void main(final String[] args) {

	   //final String queryGetAll = "REGISTER QUERY PIPPO AS SELECT ?S ?P ?O FROM STREAM <http://www.glue.com/stream> [RANGE 5s STEP 1s] WHERE { ?S ?P ?O }";
	   final String queryGetAll = "REGISTER QUERY PIPPO AS SELECT ?S ?P ?O FROM STREAM <http://myexample.org/stream> [RANGE TRIPLES 10] WHERE { ?S ?P ?O }";

	   final String queryGetEverythingFromBothStream = "REGISTER QUERY PIPPO AS SELECT ?S ?P ?O FROM STREAM <http://www.glue.com/stream> [RANGE TRIPLES 1] FROM STREAM <http://myexample.org/stream> [RANGE TRIPLES 1] WHERE { ?S ?P ?O }";
	   
	   final String queryAnonymousNodes = "REGISTER QUERY PIPPO AS CONSTRUCT {                        [] <http://ex.org/by> ?s  ;  <http://ex.org/count> ?n . } FROM STREAM <http://www.larkc.eu/defaultRDFInputStream> [RANGE TRIPLES 10]                        WHERE {                                { SELECT ?s ?p (count(?o) as ?n)                                  WHERE { ?s ?p ?o }                                  GROUP BY ?s }                              }";
	   
	   
	   final String queryNoCount = "REGISTER QUERY PIPPO AS "
				+ "SELECT ?p "
				+ " FROM STREAM <http://myexample.org/stream> [RANGE TRIPLES 1] "
				+ " FROM <http://dbpedia.org/resource/Castello_Sforzesco> "
				+ "WHERE { ?s ?p ?o }";

	  final String queryCount = "REGISTER QUERY PIPPO AS "
			+ "SELECT ?t (count(?t) AS ?conto)" + " FROM STREAM <http://www.glue.com/stream> [RANGE TRIPLES 30] WHERE { ?s <http://rdfs.org/sioc/ns#topic> ?t } "
			+ "GROUP BY ?t ";
	  
	  final String querySimpleCount = "REGISTER QUERY PIPPO AS "
			+ "SELECT ?s (COUNT(?s) AS ?conto) FROM STREAM <http://www.glue.com/stream> [RANGE TRIPLES 1] WHERE { ?s ?p ?o } GROUP BY ?s";
	  
	  final String queryGetKB = "REGISTER QUERY PIPPO AS "
			+ "SELECT ?s ?p ?o FROM <http://rdfs.org/sioc/ns>\n FROM STREAM <http://www.glue.com/stream> [RANGE TRIPLES 1] WHERE { ?s ?p ?o }";
	  
	  final String queryGetAll2 = "REGISTER QUERY PIPPO AS "
		  + "CONSTRUCT { <http://www.streams.org/s> <http://www.streams.org/s> ?n }" +
		 " FROM STREAM <http://myexample.org/stream> [RANGE TRIPLES 2] " +
		 " WHERE {" +
		 "  { SELECT (count(?o) as ?n) " +
		 "  { ?s ?p ?o }" +
		 "   GROUP BY ?p } " +
		 "} ";
	  
      final CsparqlEngine engine = new CsparqlEngineImpl();
      engine.initialize();

//      final RDFStreamAggregationTestGenerator tg = new RDFStreamAggregationTestGenerator("http://www.larkc.eu/defaultRDFInputStream");
//      final GlueStreamGenerator tg = new GlueStreamGenerator();
      TestGenerator tg = new TestGenerator("http://www.larkc.eu/defaultRDFInputStream");
      
      RdfStream rs = engine.registerStream(tg);
      engine.unregisterStream(rs.getIRI());
      engine.registerStream(tg);
      //engine.registerStream(tg2);
      final Thread t = new Thread(tg);
      t.start();
      
      CsparqlQueryResultProxy c1 = null;
      final CsparqlQueryResultProxy c2 = null;

      try {
         c1 = engine.registerQuery(queryGetAll);
      } catch (final ParseException ex) {
         System.out.println("errore di parsing: " + ex.getMessage());
      }
      if (c1 != null) {
         c1.addObserver(new TextualFormatter());
      }
   }

   private Application() {
      // hidden constructor
   }

}