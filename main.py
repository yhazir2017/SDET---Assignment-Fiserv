from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
import unittest
import time
import logging

class SearchWebSite:
    def __init__(self, name, query,xpath, url) -> None:
        self.name = name
        self.xpath = xpath
        self.query = query
        self.url = url

class SearchTest(unittest.TestCase):
    def setUp(self):
        # Set up the WebDriver
        self.driver = webdriver.Chrome()
        self.google = SearchWebSite('Google', 'q', '//h3',"https://www.google.com")
        self.bing = SearchWebSite('Bing', 'q', '//*[@id="b_results"]/li[2]/h2/a',"https://www.bing.com")
        self.yahoo = SearchWebSite('Yahoo', 'p',  '//*[@id="web"]/ol/li[2]/div/div[1]/h3/a',"https://www.yahoo.com")

        # Set up logging to a file
        logging.basicConfig(filename='test_log.txt', level=logging.INFO)
        # Start measuring test duration
        self.start_time = time.time()

    def tearDown(self):
        # End measuring test duration
        end_time = time.time()
        duration_seconds = end_time - self.start_time

        # Log the test name and duration
        test_name = self.id()
        logging.info(f"Test '{test_name}' duration: {duration_seconds} seconds")

        # Close the browser window
        self.driver.quit()
    def search_and_find(self, search_engine:SearchWebSite, search_term):
        # Navigate to Browser
        self.driver.get(search_engine.url)

        if search_engine.query:
            search_input = self.driver.find_element(By.NAME, search_engine.query)
        else:
            print("Input name undefined")

        search_input.send_keys(search_term, Keys.RETURN)

        # Wait for the results page to load
        self.driver.implicitly_wait(5)

        results = self.driver.find_element(By.XPATH, search_engine.xpath)
        return results

    def test_google_search(self):
        results = self.search_and_find(self.google, "Fiserv")
        expected_result = "Fiserv: Financial Services Technology, Mobile Banking ..."
        self.assertEqual(results.text, expected_result)

    def test_yahoo_search(self):
        results = self.search_and_find(self.yahoo, "Fiserv")
        expected_result = "Financial Services Technology, Mobile Banking, Payments | Fiserv"
        self.assertEqual(results.accessible_name, expected_result)

    def test_bing_search(self):
        results = self.search_and_find(self.bing, "Fiserv")
        expected_result = "Financial Services Technology, Mobile Banking, Payments | Fiserv"
        self.assertEqual(results.text, expected_result)

    def test_page_title(self):
        results = self.search_and_find(self.google, "Fiserv")
        expected_title = "Fiserv - Google Search"
        self.assertEqual(self.driver.title, expected_title)

    def test_search_numbers_special_characters(self):
        search_term = "!@#$%^&*1234567890"
        expected_result_Google = "`1234567890-=qwertyuiop[]\\asdfghjkl ... - Urban Dictionary"
        expected_result_Yahoo = "Number 1234567890 English version Toy Soda - YouTube"

        # Test for Google
        results = self.search_and_find(self.google, search_term)
        self.assertEqual(results.text, expected_result_Google)

        # Test for Yahoo
        results = self.search_and_find(self.yahoo, search_term)
        self.assertEqual(results.accessible_name, expected_result_Yahoo)

if __name__ == "__main__":
    unittest.main()

