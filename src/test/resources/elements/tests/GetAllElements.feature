Feature: Get all elements made by specific creator email and playground
	
Scenario: Getting all elements successfully # PASSED

	Given the server is up 
	And there are elements with playground: "2019A.Kagan", email: "rubykozel@gmail.com", 
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/all" 
	Then the response is 200 
	And the output is '[ { "playground": "2019A.Kagan", "id": "1025028332", "location": { "x": Any, "y": Any }, "name": "Messaging Board", "creationDate": Any valid date, "expirationDate": null, "type": "Messaging Board", "attributes": { "creatorsName": "Manager", "isActive": "True", "isAMovie": "False", "movieName": "Venom 2018" }, "creatorPlayground": "2019A.Kagan", "creatorEmail": "rubykozel@gmail.com" } ...]' 
	
Scenario: Getting none elements fail, with creator email that has no elements # PASSED

	Given the server is up 
	And there might be elements with playground: "2019A.Kagan" but not the email: "rubykozel@gmail.com", 
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/all" 
	Then the response is 500 with message: "Creator has no elements it created" 