import re
import time
from datetime import datetime, timedelta

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
import mysql.connector
import os
from dotenv import load_dotenv
from selenium.webdriver.support.wait import WebDriverWait

load_dotenv()

db = mysql.connector.connect(
    host="localhost",
    port=3307,
    user=os.getenv('DB_USERNAME'),
    password=os.getenv('DB_USER_PASSWORD'),
    database=os.getenv('DB_DATABASE')
)

cursor = db.cursor()

driver = webdriver.Chrome()

wait = WebDriverWait(driver, 10)

page_number = 1

while True:

    url = f'https://www.jobkorea.co.kr/Recruit/Joblist?menucode=local&localorder=1#anchorGICnt_{page_number}'
    driver.get(url)

    if page_number == 1:
        # 직무 클릭
        duty_btn = driver.find_element(By.CSS_SELECTOR, 'p.btn_tit')
        duty_btn.click()

        # 개발·데이터 클릭
        dev_data_label = driver.find_element(By.CSS_SELECTOR, 'label[for="duty_step1_10031"]')
        dev_data_label.click()

        # 백엔드개발자 클릭
        backend_dev = driver.find_element(By.XPATH, '//span[contains(text(), "백엔드개발자")]')
        backend_dev.click()

        # 프론트엔드개발자 클릭
        frontend_dev = driver.find_element(By.XPATH, '//span[contains(text(), "프론트엔드개발자")]')
        frontend_dev.click()

        # 웹개발자 클릭
        web_dev = driver.find_element(By.XPATH, '//span[contains(text(), "웹개발자")]')
        web_dev.click()

        # 앱개발자 클릭
        app_dev = driver.find_element(By.XPATH, '//span[contains(text(), "앱개발자")]')
        app_dev.click()

        # 경력 클릭
        career_btn = driver.find_element(By.XPATH, '//p[contains(text(), "경력")]')
        career_btn.click()

        # 신입 클릭
        newbie_label = driver.find_element(By.XPATH, '//label[contains(@for, "career1") and .//span[text()="신입"]]')
        newbie_label.click()

        time.sleep(2)

        # 선택된 조건 검색하기 클릭
        search_button = driver.find_element(By.ID, 'dev-btn-search')
        search_button.click()

    time.sleep(4)

    try:
        companies = wait.until(EC.presence_of_all_elements_located((By.CSS_SELECTOR, 'td.tplCo')))
    except:
        break

    companies = wait.until(EC.presence_of_all_elements_located((By.CSS_SELECTOR, 'td.tplCo')))
    contents = wait.until(EC.presence_of_all_elements_located((By.CSS_SELECTOR, 'td.tplTit strong a.link.normalLog')))
    date_elements = wait.until(EC.presence_of_all_elements_located((By.CSS_SELECTOR, 'span.date.dotum')))
    urls = wait.until(EC.presence_of_all_elements_located((By.CSS_SELECTOR, 'td.tplTit strong a.link.normalLog')))

    if not companies:
        break

    data_list = []  # 데이터 저장을 위한 빈 리스트 생성

    for i in range(len(companies)):
        company_name = companies[i].text.strip()
        content = contents[i].get_attribute("title").strip()

        date_text = date_elements[i].text.strip()
        date_match = re.search(r"~(\d{2}/\d{2})\((\w+)\)", date_text)

        if date_match:
            month_day, day_of_week = date_match.groups()
            current_year = datetime.now().year
            date_text = f"{current_year}-{month_day}"
            expiration_date = datetime.strptime(date_text, "%Y-%m/%d")
        elif "오늘마감" in date_text:
            expiration_date = datetime.now()
        elif "내일마감" in date_text:
            expiration_date = datetime.now() + timedelta(days=1)
        elif "모레마감" in date_text:
            expiration_date = datetime.now() + timedelta(days=2)
        elif "상시채용" in date_text:
            expiration_date = datetime.now() + timedelta(days=365)
        else:
            expiration_date = None

        if expiration_date:
            expiration_date = expiration_date.strftime("%Y-%m-%d %H:%M:%S.%f")
        else:
            expiration_date = ""

        url = urls[i].get_attribute("href")

        data_list.append((company_name, content, expiration_date, url))

    # 데이터 출력
    for data in data_list:
        print("회사:", data[0])
        print("제목:", data[1])
        print("만료일:", data[2])
        print("URL:", data[3])
        print()

    # 데이터베이스에 데이터 추가
    insert_query = "INSERT INTO job (company_name, title, expiration_date, url) VALUES (%s, %s, %s, %s)"
    cursor.executemany(insert_query, data_list)
    db.commit()

    page_number += 1

# 브라우저 종료
driver.quit()
