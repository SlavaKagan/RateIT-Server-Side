Feature: Creating an element
	
Scenario: Creating an element successfully # PASSED #Automated

	Given the server is up 
	And theres an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Manager" 
	When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with '{"type":"Messaging Board", "name":"Messaging Board"}' 
	Then the response is 200 
	And the database containts the element '{"playground": "2019A.Kagan","name": "Messaging Board", "experationDate": null,"type": "Messaging Board","creatorPlayground": "2019A.Kagan","creatorEmail": "rubykozel@gmail.com"}' 
	
Scenario: Creating an element with a user that is not a manager # PASSED #Automated

	Given the server is up 
	And theres an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Reviewer", 
	When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with '{"type":"Messaging Board", "name":"Messaging Board"}' 
	Then the response is 500 
	
Scenario: Creating an element without delivering any valid JSON # PASSED #Automated

	Given the server is up 
	And theres an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Manager" 
	When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with nothing 
	Then the response is <> 2xx 
	
Scenario: Creating an element with email as null # PASSED #Automated

	Given the server is up 
	And theres an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Manager" 
	When I POST "/playground/elements/2019A.Kagan/null" with '{"type": "Messaging Board", "name":"Messaging Board"}' 
	Then the response is 500 
	
Scenario: Creating an element with empty JSON # PASSED #Automated

	Given the server is up 
	And theres an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Manager" 
	When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with '{}' 
	Then the response is 500