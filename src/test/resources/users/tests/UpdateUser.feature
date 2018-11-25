Feature: Change the details of the user
	
Scenario: Change the user name succesfully # PASSED #Automated

	Given the server is up 
	And theres a user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", 
	When I PUT "/playground/users/2019A.Kagan/rubykozel@gmail.com" with '{"email":"rubykozel@gmail.com","playground": "2019A.Kagan","userName": "rubson","avatar": ":-)","role": "Reviewer","points": 0}' 
	Then the response is 200 
	
Scenario: Change user name of unregistered user # PASSED #Automated

	Given the server is up 
	And there is an unregistered user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", 
	When I PUT "/playground/users/2019A.Kagan/rubykozel@gmail.com" with '{"email":"rubykozel@gmail.com","playground": "2019A.Kagan","userName": "omer","avatar": ":-)","role": "Guest","points": 0}' 
	Then the response is with message: "This is an unregistered account" 
	
Scenario: Change the user avatar to null # PASSED #Automated

	Given the server is up 
	And theres a user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", 
	When I PUT "/playground/users/2019A.Kagan/rubykozel@gmail.com" with '{"email":rubykozel@gmail.com,"playground": "2019A.Kagan","username": null,"avatar": ":-)","role": "Reviewer","points": 0}' 
	Then the response is 500 