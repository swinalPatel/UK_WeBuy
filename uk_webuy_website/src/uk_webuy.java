import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assume;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;


// go to http://uk.webuy.com
// need to add 5 different products in basket
// keep all 5 product name and quantities in the excel file
// read data from excel file 
// need to check basket updated everytime when item is added to the cart (number will be increase)
// open the basket by clicking on view basket
// need to update the quantity of the product into the cart which is mention into the excel sheet 
// need to compare the grand total of the items added to the cart including delivery cost will match 
// to the grand total mention on to the website


public class uk_webuy {
	
	
		// testing using TestNg 
		@Test
		public void productTest() throws IOException, InterruptedException {
		
		/*  **** reading data from xlsx file...
			**** storing product name into array list...
			**** maping product name with quantity into hashtable
		*/
		// creating object of file class to open xlsx file 
		File file = new File("C:\\Swinal\\Study\\Eclipse_Workspace\\uk_webuy_website\\src\\we_buy.xlsx");
		// creating object of fileinputstream class to reads xlsx file
		FileInputStream fn = new FileInputStream(file);
		// creating object of workbook class for new empty workbook and loading new xlsx file in it
		Workbook workbook = new XSSFWorkbook(fn);
		// creating object of sheet class to read sheet inside the workbook by its name
		Sheet we_buy_sheet = workbook.getSheet("products");
		// counting number of rows into the sheet
		int rowcount = we_buy_sheet.getLastRowNum() - we_buy_sheet.getFirstRowNum();
		// printing total no of rows of sheet
		System.out.println("no of rows: " + rowcount);
		// now making array list to store product name from excel file
		List<String> productNamestobeAdded = new ArrayList<String>();
		// making hastable to map product name(key) with their quantity(value) from excel file
		Hashtable<String,String> productQuantitytobeAdded = new Hashtable<String, String>();
		
		// creating loop over the row of excel file to read the data row wise
		for(int i=1; i<=rowcount; i++) {
			// creating object of row class...pointing row from i to end of row
			Row row = we_buy_sheet.getRow(i);
			// creating loop over the column of excel file to read the data column wise
			for(int j=0; j<=row.getFirstCellNum(); j++) {
				//now adding all product name of excel file into array list
				productNamestobeAdded.add(row.getCell(j).getStringCellValue());
				//now adding all product name and quantity of excel file into hashtable ..mapping(key,value)
				productQuantitytobeAdded.put(row.getCell(j).getStringCellValue(),row.getCell(j+1).getStringCellValue());
				// printing all the values of excel file 
				System.out.println(row.getCell(j).getStringCellValue() + "-------" + row.getCell(j+1).getStringCellValue() );
			}
		}
		
		
		WebDriver driver = null;
		// Initialising browser and navigating to given URL
		System.setProperty("webdriver.gecko.driver","C:\\Swinal\\Study\\Eclipse_Workspace\\uk_webuy_website\\Driver\\geckodriver.exe");
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(15,TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.get("https://uk.webuy.com/search?categoryIds=844&categoryName=phones-iphone");
		Thread.sleep(3000L);
		
		
		// closing the popup window 
		driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[7]/img[2]")).click();
		Thread.sleep(3000L);
		
		
		/* 
		 * collecting all  webelements (products name) as list from the loaded page of website
		 * printing total no of links of the page 
		 */
		List<WebElement> all_links =  driver.findElements(By.xpath("//div[@class='searchRecord']/div[2]/h1/a"));
		System.out.println("Total links on page: " + all_links.size());
		/* 
		 * collecting all webelements (want to buy item) as list from the loaded page of website
		 * printing total buttons (want to buy) of the page 
		 */
		List<WebElement> all_buy_buttons = driver.findElements(By.xpath("//div[@class='action btn_uk']/div/div/a[2]/div/span"));
		System.out.println("Total buy buttons on page: " + all_buy_buttons.size());
		// creating loop over all the links of the page 
		int prodCount = 0;
		for(int i=0; i<all_links.size(); i++) {
			/* System.out.println("(" + (i+1) + ") " + all_links.get(i).getText());   */
			/* comparing products stored into list(productNamestobeAdded) from excel file with all links
			 * printing the matched link text
			 * adding the matched item into the basket */
			if(productNamestobeAdded.contains(all_links.get(i).getText())) {
				System.out.println("Matched product found: " + all_links.get(i).getText());
				all_buy_buttons.get(i).click();
				// extracting no when basket updating every time and printing basket count after adding each item
				String basketcount = driver.findElement(By.xpath("//span[@id='buyBasketCount']")).getText();
				System.out.println("basket count: " + basketcount);
				// comparing no of basket item adding with no of product items want to add
				// converting prodCount from int to string for comparison
				Assert.assertEquals(basketcount, String.valueOf(prodCount+1));
				all_links =  driver.findElements(By.xpath("//div[@class='searchRecord']/div[2]/h1/a"));
				all_buy_buttons = driver.findElements(By.xpath("//div[@class='action btn_uk']/div/div/a[2]/div/span"));
				prodCount++;
			}
		}
		Thread.sleep(5000L);
		
		// clicking on the view basket after adding all the products into the basket
		driver.findElement(By.xpath("//td[@class='basketTableCellLnk']")).click();
		
		
		/*
		 * extracting all added products name from the basket....stored into first column 
		 * extracting total no of quantity from the basket .....stored into second column 	
		 */
		List<WebElement> firstcolm = driver.findElements(By.xpath("//div[@class='chktableout']/table/tbody/tr/td[1]/a[1]"));
		System.out.println("size of first column= " + firstcolm.size());
		List<WebElement> secondcolm = driver.findElements(By.xpath("//td[@class='txtcenter']/select"));
		System.out.println("size of second column= " + secondcolm.size());
		// creating loop over the first column of basket to get the name of the product added in basket
		for(int i=0; i<firstcolm.size(); i++) {
			String key = firstcolm.get(i).getText();
			/* productQuantitytobeAdded.get(key) returns the value(quantity) of the matching key(product name) from 
			 * hashtable(productQuantitytobeAdded)
			 * printing product name from the basket and quantity from the excel sheet after finding the matching product name
			 */
			System.out.println(key + "------" + productQuantitytobeAdded.get(key));
			/*  getting i'th index web element of drop down quantity 
			 * Declaring the drop down element as an instance(s) of the Select class
			 * selecting quantity from drop down using selectByVisibleText method   */
			WebElement dropdown = secondcolm.get(i);
			Select s = new Select(dropdown);
			s.selectByVisibleText(productQuantitytobeAdded.get(key));
			firstcolm = driver.findElements(By.xpath("//div[@class='chktableout']/table/tbody/tr/td[1]/a[1]"));
			secondcolm = driver.findElements(By.xpath("//td[@class='txtcenter']/select"));
		}
		 
		
		
		/****** now comparing actual grand total with expected grand total ****/
		
		// collecting all the price of items stored in to fourth column into list 
		List<WebElement> fourthcolm = driver.findElements(By.xpath("//div[@class='chktableout']/table/tbody/tr/td[4]"));
		double subTotal = 00.00;
		// creating loop around all sub total column and getting sub total of individual item
		for(int i=0; i<fourthcolm.size(); i++) {
		String item_price = fourthcolm.get(i).getText().split("\\£")[1];	    // Separating £ sign from price
		System.out.println("Subtotal of individual iteam: " + item_price);
		// converting string into double		
		subTotal = subTotal + Double.parseDouble(item_price);
		}
		System.out.println("Subtotal after adding all iteams of basket: " + subTotal);
		
		
		// extracting delivery cost 
		String deliverycost = driver.findElement(By.xpath("//div[@class='txtsumrrybasket']/p[2]")).getText().split("\\Shipping Cost ")[1];
		// saperating £ sign from delivery cost and converting string into double
 		double deliveryCost = Double.parseDouble(deliverycost.split("\\£")[1]);
 		System.out.println("Delivery Cost: " + deliveryCost);
 		// adding sub total and delivery cost to get actual result(grand total)
		double actual_result = subTotal + deliveryCost;
		System.out.println("Actual Result: " + actual_result);
		
		
		// now extracting the grand total from the website to get expected result(grand total)
		String grandtotal = driver.findElement(By.xpath("//div[@class='txtsumrrybasket']/p[3]")).getText().split("\\GRAND TOTAL")[0];
		// Separating £ from string value of grand total
		String g_total = grandtotal.split("\\£")[1];
		// removing comma sign from the grand total string value
		String st = g_total.replaceAll(",","");
		// converting string into double
		double expected_result =  Double.parseDouble(st);
		System.out.println("expected result: " + expected_result);
		
		
		// comparing actual result with expected result......throw error message if its not
		Assert.assertEquals(actual_result, expected_result);
		}
		
	}


