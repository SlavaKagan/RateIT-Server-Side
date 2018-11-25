Feature: Confirming a new registered user
	
Scenario: Confirming a new registered user successfully # PASSED #Automated

	Given the server is up 
	And theres a user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", code: "1234" 
	When I GET "/playground/users/confirm/2019A.Kagan/rubykozel@gmail.com/1234" 
	Then the response is 200 
	And the database contains the user: '{"email": "rubykozel@gmail.com", "playground": "2019A.Kagan", "userName": "ruby", "avatar": ":-)", "role": "Reviewer", "points": 0}' 
	
Scenario: Confirming a new registered user unsuccessfully with different code # PASSED #Automated

	Given the server is up 
	And theres an unconfirmed user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", code: "1234" 
	When I GET "/playground/users/confirm/2019A.Kagan/rubykozel@gmail.com/1235" 
	Then the response is 500 
	
Scenario: Confirming an existing user # PASSED #Automated

	Given the server is up 
	And theres a user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Reviewer" 
	When I GET "/playground/users/confirm/2019A.Kagan/rubykozel@gmail.com/Any_Code" 
	Then the response is 500 