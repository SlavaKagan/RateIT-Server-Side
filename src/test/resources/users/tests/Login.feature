Feature: Logging into the server
	
Scenario: Getting a registered user from the server successfully # PASSED #Automated

	Given the server is up 
	And theres a registered user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", 
	When I GET "/playground/users/login/2019A.Kagan/rubykozel@gmail.com" 
	Then the response is 200 
	And the output is '{"email": "rubykozel@gmail.com","playground": "2019A.Kagan","userName": "ruby","avatar": ":-)","role": "Reviewer","points": 0}' 
	
Scenario: Getting an unconfirmed user # PASSED #Automated

	Given the server is up 
	And theres an unconfirmed user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", 
	When I GET "/playground/users/login/2019A.Kagan/rubykozel@gmail.com" 
	Then the response is 500 with message: "This is an unconfirmed account" 
	
Scenario: Getting an unregistered user # PASSED #Automated

	Given the server is up 
	And there are no accounts 
	When I GET "/playground/users/login/2019A.Kagan/rubykozel@gmail.com" 
	Then the response is 500 with message: "This is an unregistered account" 