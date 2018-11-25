Feature: Getting Elements with pagination
	
Scenario: Getting elements using pagination successfully # PASSED # Automated 

	Given the server is up 
	And theres at list 1 element in the database with playground: "2019A.Kagan", email: "rubykozel@gmail.com" 
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/all?size=5&page=0" 
	Then the response 200 
	And the output is '[ { "playground": "2019A.Kagan", "id": "1025028332", "location": { "x": Any, "y": Any }, "name": "Messaging Board", "creationDate": Any valid date, "expirationDate": null, "type": "Messaging Board", "attributes": { "creatorsName": "Manager", "isActive": "True", "isAMovie": "False", "movieName": "Venom 2018" }, "creatorPlayground": "2019A.Kagan", "creatorEmail": "rubykozel@gmail.com" } x 1 .. 5]' 
	
Scenario:
Getting no elements from page with no elements using pagination successfully # PASSED # Automated 

	Given the server is up 
	And theres at list 1 element in the database with playground: "2019A.Kagan", email: "rubykozel@gmail.com" 
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/all?size=5&page=1" 
	Then the response is 200 
	And the output is '[]' 
	
Scenario: Using bad page number to retreive elements # PASSED # Automated
	Given the server is up 
	And theres at list 1 element in the database with playground: "2019A.Kagan", email: "rubykozel@gmail.com" 
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/all?size=5&page=1" 
	Then the response  500 