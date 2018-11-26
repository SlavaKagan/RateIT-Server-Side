Feature: Posting a new activity 

Scenario: Post a new activity successfuly # PASSED # Automated

	Given the server is up 
	And there's an element with id: 1025028355 
	When I POST "/playground/activities/2019A.Kagan/rubykozel@gmail.com" with '{"elementId": "1025028355","elementPlayground": "2019A.Kagan","type": "x"}' 
	Then the response is 200 
	And the output is '{"email":"rubykozel@gmail.com","playground":"2019A.Kagan","userName":"ruby","avatar":":-)","role":"Guest","points":0}' 
	
Scenario: Post a new activity with null email unsuccessfuly # PASSED # Automated

	Given the server is up 
	And there's an element with id: 1025028355 
	When I POST "/playground/activities/2019A.Kagan/null" with '{"elementId": "1025028355","elementPlayground": "2019A.Kagan","type": "x"}' 
	Then the response is 500 with message: "One of the paramters provided was null" 
	
Scenario: Post a new activity with an empty JSON unsuccessfuly # PASSED # Automated

	Given the server is up 
	And there's an element with id: 1025028355 
	When I POST "/playground/activities/2019A.Kagan/rubykozel@gmail.com" with '{}' 
	Then the response is 500