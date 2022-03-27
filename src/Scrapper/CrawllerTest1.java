package Scrapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.text.Document;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class CrawllerTest1 {

    private static final String LINKEDIN_BASE_JOB_SEARCH_URL = "https://www.linkedin.com/jobs/search?";

    public static void main(String[] args){
        
        //String url = "https://www.linkedin.com/jobs/search?keywords=Full%20Stack%20Engineer&location=Windsor%2C%20Ontario%2C%20Canada&geoId=101750980&trk=public_jobs_jobs-search-bar_search-submit&position=1&pageNum=0";
        //String url2 = "https://www.linkedin.com/jobs/search?keywords=Full%20Stack%20Engineer";
        ArrayList<String> visited = new ArrayList<>();
        String position = "Salesforce Consulatant";
        String location = "bengaluru";

        String urlToScrape = buildUrl(position, location);
        //crawl(1, url, visited, 1);..
        ArrayList<JobWrapper> jobs = singleCrawl(urlToScrape);
    }

    private static String buildUrl(String position, String location){
        
        String url = LINKEDIN_BASE_JOB_SEARCH_URL;

        String[] positionKeywords = null;
        String[] locationKeywords = null;
        
        String positionBuild = "";

        if(position != null && position != ""){
            positionKeywords = position.split(" ");
            positionBuild = "keywords=";
        }// in else part add throw error "Position cannot be null or can it be? "

        

        for(String keyWord : positionKeywords){
            positionBuild += keyWord.trim() + "%20";
            //System.out.println("keyWord ---> " + keyWord);
        }

        if(positionBuild != null){
            positionBuild = positionBuild.substring(0, positionBuild.length() - 3);
           //System.out.println("positionBuild ---> " + positionBuild);

        }
        
        String locationBuild = "";

        if(location != null && location != ""){
            locationKeywords = location.split(" ");
            locationBuild = "location=";
            
        }// in else part add throw error "Position cannot be null or can it be? "
        
        for(String keyWord : locationKeywords){
            locationBuild += keyWord.trim() + "%2C%20";
            //System.out.println("keyWord ---> " + keyWord);
        }

        if(locationBuild != null){
            locationBuild = locationBuild.substring(0, locationBuild.length() - 6);
            //System.out.println("locationBuild ---> " + locationBuild);

        }

        url = positionBuild != null ? url + positionBuild : url;

        //System.out.println("url pos ---> " + url);

        url = positionBuild != null && locationBuild != null ?
                url + "&" + locationBuild : url + locationBuild;

        //System.out.println("url pos + loc ---> " + url);

        return url;

    }

    private static ArrayList<JobWrapper> singleCrawl(String url){

        ArrayList<JobWrapper> jobs = new ArrayList<JobWrapper>();


        // Save original out stream.
        PrintStream originalOut = System.out;

        
        try {
            // Create a new file output stream.
            PrintStream fileOut = new PrintStream("./out.txt");

            // Redirect standard out to file.
            System.setOut(fileOut);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        org.jsoup.nodes.Document doc =  request(url, null);
        Elements elements = doc.getElementsByClass("base-search-card__info");

        for(Element element : elements){

            JobWrapper job = new JobWrapper();

            job.position = getValueFromChild("base-search-card__title", element);
            job.location = getValueFromChild("job-search-card__location", element);
            job.companyName = getValueFromChild("hidden-nested-link", element);
            job.link = getLinkFromSibling("base-card__full-link", element);

            jobs.add(job);
        }

        for(JobWrapper job : jobs){
            System.out.println("\ncompany -----> " + job.companyName);
            System.out.println("position -----> " + job.position);
            System.out.println("location -----> " + job.location);
            System.out.println("link -----> " + job.link);
            

            originalOut.println("\ncompany -----> " + job.companyName);
            originalOut.println("position -----> " + job.position);
            originalOut.println("location -----> " + job.location);
            originalOut.println("link -----> " + job.link);

        }
        
        originalOut.println("Program exit. ");

        return jobs;

    }

    private static String getValueFromChild(String className, Element element){

        for(Element childElement : element.getElementsByClass(className)){

            String value = childElement.text();
            if(value != null && value != ""){
                return value;
            }

        }
        return null;
    }

    private static String getLinkFromSibling(String className, Element element){

        for(Element siblingElement : element.siblingElements()){

            if(siblingElement.className().equals(className)){
            String link = siblingElement.attr("href");
                if(link != null && link != ""){
                    return link;
                }
            }
            
        }
        return null;
    }

    public static class JobWrapper{
        String position;
        String location;
        String companyName;
        String link;
    }

    private static void crawl(int level, String url, ArrayList<String> visited, int numberOfLinks){


        if(level <= 2 && numberOfLinks < 2){
            org.jsoup.nodes.Document doc =  request(url, visited);

            if(doc != null){
                Elements eles = doc.getElementsByClass("search-entity-media");

                for(Element e : eles.select("a[href]")){
                    String  nextLink = e.absUrl("href");
                    if(!visited.contains(nextLink) && !nextLink.contains("blackboard") 
                        && nextLink.contains("https://www.linkedin.com/jobs/")){

                        System.out.println("attributes from link " + e.attributes());

                        crawl(level++, nextLink, visited, numberOfLinks++);
                    }
                }
            }
        }
    }

    private static org.jsoup.nodes.Document request(String url, ArrayList<String> visited){

        try{
            Connection con = Jsoup.connect(url);
            org.jsoup.nodes.Document doc =  con.get();

            if(con.response().statusCode() == 200){
                System.out.println("\nLink : " + url);
                System.out.println("Page Title---> " + doc.title());

                if(visited != null){
                    visited.add(url);
                }

                return doc;

            }
            return null;

        }catch(IOException e){

            return null;
        }
    }

    
}
