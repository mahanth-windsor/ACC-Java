package Scrapper;

import javax.lang.model.element.Element;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebScrapperTest {
	
	public static void main(String[] args) {

		final String url1 = "https://www.linkedin.com/in/nicole-sadeghi-846541?trk=org-employees" ;
		final String url2LinkedIn = "https://www.linkedin.com/jobs/search?keywords=Full%20Stack%20Engineer&location=Windsor%2C%20Ontario%2C%20Canada&geoId=101750980&trk=public_jobs_jobs-search-bar_search-submit&position=1&pageNum=0" ;
		final String url2Indeed = "https://ca.indeed.com/jobs?q=full%20stack%20developer&l=windsor%20area&vjk=562bfe8cd14a1c87" ;

		try{

			final Document doc = Jsoup.connect(url2LinkedIn).userAgent("Mozilla").get();
			System.out.println(doc.title());

			Elements links = doc.select("class[base-card__full-linkcl]");

			for(org.jsoup.nodes.Element link : links){
				//System.out.println("\nlink: " + link.attr("href"));
				//System.out.println("text: " + link.text());
				System.out.println("text: " + link.text());
				

			}

			/*for(org.jsoup.nodes.Element row : doc.select("table.table-static.kurser-table tr")){
				String scrappedRow = row.select("td:nth-of-type(3)").text();
				if(scrappedRow.equals("")){
					continue;
				}else{
					final String scrappedInfo = scrappedRow;
					System.out.println(scrappedInfo);
				}
			}*/

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
