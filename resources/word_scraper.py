# var table = document.getElementById("tablepress-1")
# var table_elements = table.children[1].children
# var json_elements = '['
# for (var i = 0; i < table_elements.length; i++) {
#     var child_text = table_elements[i].children[0].innerText
#     json_elements += '"'+child_text + '",'
# }
# json_elements += ']'

import asyncio
import ssl
import aiohttp
import json
from bs4 import BeautifulSoup

BASE_URL = "https://wheeloffortuneanswer.com/"


async def getWords(session, link, category):
    words = []
    async with session.get(link, ssl=ssl.SSLContext()) as r:
        body = await r.text()
        soup = BeautifulSoup(body, "html.parser")
        # print(soup)
        tables = soup.find_all("table", class_="tablepress")
        if tables:
            table = tables[0]
            table_elements = table.find_all("td", class_="column-1")
            for table_element in table_elements:
                words.append(table_element.text.strip())
    return {category: words}


async def main():
    categories = {}
    async with aiohttp.ClientSession(loop=asyncio.get_event_loop(),
                                     connector=aiohttp.TCPConnector(ssl=False)) as session:
        category_links = {}
        async with session.get(BASE_URL, ssl=ssl.SSLContext()) as r:
            body = await r.text()
            soup = BeautifulSoup(body, "html.parser")
            # print(soup)
            tables = soup.find_all("table", class_="tablepress")
            if tables:
                table = tables[0]
                table_elements = table.find_all("td", class_="column-1")
                for table_element in table_elements:
                    link_elements = table_element.find_all("a")
                    for link in link_elements:
                        category_links[link.get("href")] = link.text.strip()

        responses = await asyncio.gather(*[getWords(session, key, value) for key, value in category_links.items()], return_exceptions=False)
        # print(responses)
        
        for response in responses:
            for key, value in response.items():
                categories[key] = value
    
    with open("words.json", "w") as file:
        file.write(json.dumps(categories, indent=4))


loop = asyncio.get_event_loop()
loop.run_until_complete(main())
