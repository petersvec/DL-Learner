package org.dllearner.algorithm.tbsl.exploration.Sparql;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class testClass_new {

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		// TODO Auto-generated method stub
		ArrayList<Template> temp_list_result = new ArrayList<Template>();
		
		TemplateBuilder testobject = new TemplateBuilder();
		//String question = "Is the wife of president Obama called Michelle?";
		//String question = "Who is the mayor of Berlin?";
		//temp_list_result=testobject.createTemplates(question);
		
		
		
		ArrayList<queryInformation> list_of_structs = new ArrayList<queryInformation>();
		//if you dont want to use the hints in the questions, use false
		list_of_structs=generateStruct("/home/swalter/Dokumente/dbpedia-train.xml");
		System.out.println("Start Templating");
		for(queryInformation s : list_of_structs){
			System.out.println("In For Schleife");
			ArrayList<Template> temp_list = new ArrayList<Template>();
			temp_list=testobject.createTemplates(s.getQuery().replace("<[CDATA[", "").replace("]]>", ""));
			for(Template t : temp_list){
				temp_list_result.add(t);
			}
			
		}
		
		String result ="";
		for(Template t: temp_list_result){
			//t.printAll();
			result+="###### Template ######\n";
			result+="condition: "+t.getCondition()+"\n";
			//System.out.println("hypotesen: "+hypothesen);
			int anzahl = 1;
			for(ArrayList<Hypothesis> x : t.getHypothesen()){
				result+="\nSet of Hypothesen"+anzahl+":\n";
				anzahl+=1;
				for ( Hypothesis z : x){
					result+="%%%%%%%%%%%"+"\n";
					result+="Variable: "+z.getVariable()+"\n";
					result+="Uri: " + z.getUri()+"\n";
					result+="Type: " + z.getType()+"\n";
					result+="Rank: "+z.getRank()+"\n";
						result+="%%%%%%%%%%%"+"\n";
				}
			}
			result+="\n";
			result+="selectTerm: "+t.getSelectTerm()+"\n";
			result+="having: "+t.getHaving()+"\n";
			result+="filter: "+t.getFilter()+"\n";
			result+="OrderBy: "+t.getOrderBy()+"\n";
			result+="limit: "+t.getLimit()+"\n";
			result+="###### Template printed ######\n";
		}
		
		//System.out.println(result);
		
		File file = new File("/home/swalter/Dokumente/Ausgabe_temp.txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));

        bw.write(result);
        bw.flush();
        bw.close();
        
	}
	


private static ArrayList<queryInformation> generateStruct(String filename) {
	System.out.println("In generate Struct");
	String XMLType=null;
	
	BufferedReader in = null;
	
    String tmp="";
	// Lies Textzeilen aus der Datei in einen Vector:
    try {
      in = new BufferedReader(
                          new InputStreamReader(
                          new FileInputStream(filename) ) );
      String s;
	while( null != (s = in.readLine()) ) {
        tmp=tmp+s;
        //System.out.println(tmp);
      }
    } catch( FileNotFoundException ex ) {
    } catch( Exception ex ) {
      System.out.println( ex );
    } finally {
      if( in != null )
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }	
    
    
   // System.out.println("XML read in");
	//System.out.println(tmp);
    String string=tmp;
    Pattern p = Pattern.compile (".*\\<question(.*)\\</question\\>.*");
    Matcher m = p.matcher (string);
    
    
    if(string.contains("id=\"dbpedia-train\"><question")){
    	string=string.replace("id=\"dbpedia-train\"><question", "");
    	XMLType="dbpedia-train";
    	System.out.println("dbpedia-train");
    }
    if(string.contains("id=\"dbpedia-test\"><question")){
    	string=string.replace("id=\"dbpedia-test\"><question", "");
    	XMLType="dbpedia-test";
    	System.out.println("dbpedia-test");
    }
    ArrayList<queryInformation> querylist = new ArrayList<queryInformation>();
    if(string.contains("</question><question")){
    	System.out.println("true");
    }
    else System.out.println("false");
    String [] bla = string.split("</question><question");
    System.out.println(bla.length);
    for(String s : bla){
    	System.out.println("in bla");
    	String query="";
    	String type="";
   	 	boolean fusion=false;
   	 	boolean aggregation=false;
   	 	boolean yago=false;
   	 	String id="";
   	 
    	//Pattern p1= Pattern.compile("(id.*)\\</string\\>\\<keywords\\>.*\\</keywords\\>\\<query\\>.*");
   	 Pattern p1= Pattern.compile("(id.*)\\</string\\>\\<keywords\\>.*");
    	Matcher m1 = p1.matcher(s);
    	//System.out.println("");
    	while(m1.find()){
    		//System.out.println(m1.group(1));
    		Pattern p2= Pattern.compile(".*><string>(.*)");
	    	Matcher m2 = p2.matcher(m1.group(1));
	    	while(m2.find()){
	    		System.out.println("Query: "+ m2.group(1));
	    		query=m2.group(1).replace("<[CDATA[", "");
	    		query=query.replace("CDATA", "");
	    		query=query.replace("CDATA", "");
	    		query=query.replace("[", "");
	    		query=query.replace("<", "");
	    	}
	    	Pattern p3= Pattern.compile("id=\"(.*)\" answer.*");
	    	Matcher m3 = p3.matcher(m1.group(1));
	    	while(m3.find()){
	    		//System.out.println("Id: "+ m3.group(1));
	    		id=m3.group(1);
	    	}
	    	
	    	Pattern p4= Pattern.compile(".*answertype=\"(.*)\" fusion.*");
	    	Matcher m4 = p4.matcher(m1.group(1));
	    	while(m4.find()){
	    		//System.out.println("answertype: "+ m4.group(1));
	    		type=m4.group(1);
	    	}
	    	
	    	Pattern p5= Pattern.compile(".*fusion=\"(.*)\" aggregation.*");
	    	Matcher m5 = p5.matcher(m1.group(1));
	    	while(m5.find()){
	    		//System.out.println("fusion: "+ m5.group(1));
	    		if(m5.group(1).contains("true"))fusion=true;
	    		else fusion=false;
	    	}
	    	
	    	Pattern p6= Pattern.compile(".*aggregation=\"(.*)\" yago.*");
	    	Matcher m6 = p6.matcher(m1.group(1));
	    	while(m6.find()){
	    		//System.out.println("aggregation: "+ m6.group(1));
	    		if(m6.group(1).contains("true"))aggregation=true;
	    		else aggregation=false;
	    	}
	    	
	    	Pattern p7= Pattern.compile(".*yago=\"(.*)\" ><string>.*");
	    	Matcher m7 = p7.matcher(m1.group(1));
	    	while(m7.find()){
	    		//System.out.println("yago: "+ m7.group(1));
	    		if(m7.group(1).contains("true"))yago=true;
	    		else yago=false;
	    	}
	    	
	    	
	    	
    	}
    	queryInformation blaquery=new queryInformation(query, id,type,fusion,aggregation,yago,XMLType,false);
    	if(id!=""&&id!=null) querylist.add(blaquery);
    }
   /* for(queryInformation s : querylist){
    	System.out.println("");
    	if(s.getId()==""||s.getId()==null)System.out.println("NO");
		System.out.println("ID: "+s.getId());
		System.out.println("Query: "+s.getQuery());
		System.out.println("Type: "+s.getType());
		System.out.println("XMLType: "+s.getXMLtype());
	}*/
    return querylist;
}
}