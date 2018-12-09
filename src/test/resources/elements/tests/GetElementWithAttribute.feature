Feature: Get elements by attribute's value
	
Scenario: Get elements by attributes value succesfully # PASSED

	Given the server is up 
	And theres are elements with playground: "2019A.Kagan", email: "rubykozel@gmail.com", name: "Messaging Board" 
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/search/name/Messaging Board" 
	Then the response is 200 
	And the output is '[{"playground":"2019A.Kagan","name": Messaging Board, "type": any type,"creatorPlayground":"2019A.Kagan","creatorEmail":"rubykozel@gmail.com"} ... ]' 
	
Scenario: Get elements by null attributes value # PASSED

	Given the server is up 
	And theres are elements with playground: "2019A.Kagan", email: "rubykozel@gmail.com" 
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/search/name/null" 
	Then the response is 500 with message: "One of the paramters provided was null" 
	
Scenario: Get elements by attributes value that does not exist # PASSED

	Given the server is up 
	And theres are no elements with playground: "2019A.Kagan", email: "rubykozel@gmail.com", type: "something"
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/search/type/something" 
	Then the response is 200 
	And the output is []