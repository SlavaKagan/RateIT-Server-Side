Feature: Posting a new activity 

Scenario: Post a new activity successfuly # PASSED # Automated

	Given the server is up 
	And there's a confirmed user in the database
	And there's an element with id: 0 
	When I POST "/playground/activities/2019A.Kagan/rubykozel@gmail.com" with '{"elementPlayground":"2019A.Kagan","elementId":0,"type":"PostReview","attributes": {"review":"This is a review"}}' 
	Then the response is 200 
	And the output is '{"playground": "2019A.Kagan","playerPlayground": "2019A.Kagan","playerEmail": "admin@gmail.com","id": "0","elementPlayground": "2019A.Kagan","elementId": "0","type": "PostReview","attributes": {"review": "This is a review"}}' 
	
Scenario: Post a new activity with null email unsuccessfuly # PASSED # Automated

	Given the server is up 
	And there's a valid user in the database
	And there's an element with id: 0 
	When I POST "/playground/activities/2019A.Kagan/null" with '{"elementPlayground":"2019A.Kagan","elementId":0,"type":"PostReview","attributes": {"review":"This is a review"}}' 
	Then the response is 500
	
Scenario: Post a new activity with an empty JSON unsuccessfuly # PASSED # Automated

	Given the server is up 
	And there's a valid user in the database
	And there's an element with id: 0 
	When I POST "/playground/activities/2019A.Kagan/rubykozel@gmail.com" with '{}' 
	Then the response is 500

Scenario: Post a new activity with an unsupported type unsuccessfully
	Given the server is up
	And there's a valid user in the database
	And there's an element with id: 0
	When I POST "/playground/activities/2019A.Kagan/rubykozel@gmail.com" with '{"elementPlayground":"2019A.Kagan","elementId":0,"type": unsupported type ,"attributes": {"review":"This is a review"}}'
	Then the response is 500