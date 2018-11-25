Feature: Posting new user 

Scenario: Posting new user successfully # PASSED #Automated

	Given the server is up 
	When I POST "/playground/users" with '{"email":"rubykozel@gmail.com", "username":"ruby", "avatar":":-)", "role":"Guest"}' 
	Then the response is 200 ok 
	And the database contains the user : '{"email": "rubykozel@gmail.com", "playground": "2019A.Kagan", "userName": "ruby", "avatar": ":-)", "role": "Guest", "points": 0}' 
	
Scenario: Posting new user unsuccessfully # PASSED #Automated

	Given the server is up 
	When I POST "/playground/users" with nothing 
	Then the response is <> 2xx 
	
Scenario: Posting new user with given email as null # PASSED #Automated

	Given the server is up 
	When I POST "/playground/users" with '{"email": null,"username":"ruby","avatar":":-)","role":"Guest"}' 
	Then the response is 500 