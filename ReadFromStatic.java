/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kdserver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 *
 * @author ss
 */
public class ReadFromStatic {
    private static Pattern patternDomainName;
  private Matcher matcher;
  private static final String DOMAIN_NAME_PATTERN 
	= "([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}";
  static {
	patternDomainName = Pattern.compile(DOMAIN_NAME_PATTERN);
  }
	
  public static void main(String[] args) {
      try
      {

	ReadFromStatic obj = new ReadFromStatic();
        String input=JOptionPane.showInputDialog(null,"Enter String to search");
        
         try {
            
            String userName = "root";
            String password = "";
            String url = "jdbc:mysql://localhost/kbserver";
            //String url = "jdbc:mysql://localhost/sspais";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection(url, userName, password);
            Statement st = con.createStatement();
            String sql= "select * from rootword where key1 like '%"+input+"%'";
            ResultSet rs=st.executeQuery(sql);
            if(rs.next()){
              String sp=rs.getString(1);
              sp=sp.trim();
              if(sp.length()>0)
              {
              String sp1[]=sp.split(" ");
              input=sp1[0];
              }
            }
         }catch(Exception e){
             System.out.println(e);
         }
        
        
	Set<String> result = obj.getDataFromGoogle(input);
        FileOutputStream fout=new FileOutputStream("result.txt");
        PrintStream ps=new PrintStream(fout);
	for(String temp : result){
		System.out.println(temp);
                ps.println(temp);
	}
        ps.close();
        fout.close();
	System.out.println(result.size());
      }catch(Exception ee) {System.out.println(ee);}
  }

  public String getDomainName(String url){
		
	String domainName = "";
	matcher = patternDomainName.matcher(url);
	if (matcher.find()) {
		domainName = matcher.group(0).toLowerCase().trim();
	}
	return domainName;
		
  }
	
  private Set<String> getDataFromGoogle(String query) {
		
	Set<String> result = new HashSet<String>();	
	String request = "https://www.google.com/search?q=" + query + "&num=20";
	System.out.println("Sending request..." + request);
		
	try {

		// need http protocol, set this as a Google bot agent :)
		Document doc = Jsoup
			.connect(request)
			.userAgent(
			  "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
			.timeout(5000).get();

		// get all links
		Elements links = doc.select("a[href]");
		for (Element link : links) {

			String temp = link.attr("href");		
			if(temp.startsWith("/url?q=")){
                                //use regex to get domain name
				result.add(getDomainName(temp));
			}

		}

	} catch (IOException e) {
		e.printStackTrace();
	}
		
	return result;
  }

    
}
