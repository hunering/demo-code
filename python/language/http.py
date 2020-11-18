import http.client
import ssl
import requests

# tell the server we want to accept json
headers = {"content-type": "application/json"}
#the username and password
auth = ("admin", "Intel123")
#send the request and wait for the response
response = requests.get("https://10.239.172.50:8643/energymanager/rest/rows", headers=headers, verify=False, auth=auth)
#print the response
print(response.status_code, response.text)
