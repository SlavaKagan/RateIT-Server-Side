Feature: Posting new user 

Scenario: Posting new user successfully 

	Given the server is up 
	When I POST "/playground/users" with '{"email":"rubykozel@gmail.com", "username":"ruby", "avatar":":-)", "role":"Guest"}' 
	Then the response is 200 ok 
	And the output is '{"email": "rubykozel@gmail.com", "playground": "2019A.Kagan", "userName": "ruby", "avatar": ":-)", "role": "Guest", "points": 0}' 
	
Scenario: Posting new user unsuccessfully 

	Given the server is up 
	When I POST "/playground/users" with nothing 
	Then the response is <> 2xx 
	
Scenario: Posting new user with given email as null 

	Given the server is up 
	When I POST "/playground/users" with '{"email": null,"username":"ruby","avatar":":-)","role":"Guest"}' 
	Then the response is 500 
	
#Feature: Confirming a new registered user
	
Scenario: Confirming a new registered user successfully 

	Given the server is up 
	And theres a user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", code: "1234" 
	When I GET "/playground/users/confirm/2019A.Kagan/rubykozel@gmail.com/1234" 
	Then the response is 200 
	And the output is '{"email": "rubykozel@gmail.com", "playground": "2019A.Kagan", "userName": "ruby", "avatar": ":-)", "role": "Reviewer", "points": 0}' 
	
Scenario: Confirming a new registered user unsuccessfully with different code 

	Given the server is up 
	And theres an unconfirmed user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", code: "1234" 
	When I GET "/playground/users/confirm/2019A.Kagan/rubykozel@gmail.com/1235" 
	Then the response is 500 with message: "You have entered the wrong confirmation code" 
	
Scenario: Confirming an existing user 

	Given the server is up 
	And theres a user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Reviewer" 
	When I GET "/playground/users/confirm/2019A.Kagan/rubykozel@gmail.com/Any_Code" 
	Then the response is 500 with message: "User is already confirmed" 
	
#Feature: Logging into the server
	
Scenario: Getting a user from the server successfully 

	Given the server is up 
	And theres a registered user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", 
	When I GET "/playground/users/login/2019A.Kagan/rubykozel@gmail.com" 
	Then the response is 200 
	And the output is '{"email": "rubykozel@gmail.com","playground": "2019A.Kagan","userName": "ruby","avatar": ":-)","role": "Reviewer","points": 0}' 
	
Scenario: Getting an unconfirmed user 

	Given the server is up 
	And theres an unconfirmed user with playground: "2019A.Kagan", email: "rubykozel@gmail.com", 
	When I GET "/playground/users/login/2019A.Kagan/rubykozel@gmail.com" 
	Then the response is 500 with message: "This is an unconfirmed account" 
	
#Feature: Creating an element
	
Scenario: Creating an element successfully 

	Given the server is up 
	And theres an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Manager" 
	When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with '{"type":"Messaging Board", "name":"Messaging Board"}' 
	Then the response is 200 
	And the output is '{"playground": "2019A.Kagan","id": Any ID ,"location": {"x": Any X,"y": Any Y},"name": "Messaging Board","creationDate": Any valid date ,"experationDate": null,"type": "Messaging Board","attributes": {"creatorsName": "Manager","isActive": "True","isAMovie": "False","movieName": "Venom 2018"},"creatorPlayground": "2019A.Kagan","creatorEmail": "rubykozel@gmail.com"}'
	
Scenario: Creating an element with a user that is not a manager

	Given the server is up
	And theres an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Reviewer",
	When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with '{"type":"Messaging Board", "name":"Messaging Board"}'
	Then the response is 500
	
Scenario: Creating an element without delivering any valid JSON
	
	Given the server is up
	And theres an account with playground: "2019A.Kagan", email: "rubykozel@gmail.com", role: "Manager"
	When I POST "/playground/elements/2019A.Kagan/rubykozel@gmail.com" with nothing
	Then the response is <> 2xx
	
#Feature: Get an element by specific playground, creator email and id
	
Scenario: Getting an element successfully

	Given the server is up 
	And theres an element with playground: "2019A.Kagan", email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "1025028332",
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/1025028332"
	Then the response is 200 
	And the output is '{"playground": "2019A.Kagan", "id": "1586158061", "location": { "x": Any, "y": Any }, "name": "Messaging Board", "creationDate": Any valid date, "expirationDate": null, "type": "Messaging Board", "attributes": { "creatorsName": "Manager", "isActive": "True", "isAMovie": "False", "movieName": "Venom 2018" }, "creatorPlayground": "2019A.Kagan", "creatorEmail": "rubykozel@gmail.com" }' 
	
Scenario: Getting an element is unsuccessful with wrong id 

	Given the server is up 
	And theres an element with playground: "2019A.Kagan", email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "1025028332", 
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/1586158061" 
	Then the response is 500 with message: "Element does not exist" 
	
Scenario: Getting an element is unsuccessful with wrong creator email

	Given the server is up 
	And theres an element with playground: "2019A.Kagan", email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "1025028332", 
	When I GET "/playground/elements/2019A.Kagan/dudidavidov@gmail.com/2019A.Kagan/1025028332" 
	Then the response is 500 with message: "Element does not exist" 
	
#Feature: Get all elements made by specific creator email and playground
	
Scenario: Getting all elements successfully

	Given the server is up 
	And there are elements with playground: "2019A.Kagan", email: "rubykozel@gmail.com",
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/all"
	Then the response is 200 
	And the output is '[ { "playground": "2019A.Kagan", "id": "1025028332", "location": { "x": Any, "y": Any }, "name": "Messaging Board", "creationDate": Any valid date, "expirationDate": null, "type": "Messaging Board", "attributes": { "creatorsName": "Manager", "isActive": "True", "isAMovie": "False", "movieName": "Venom 2018" }, "creatorPlayground": "2019A.Kagan", "creatorEmail": "rubykozel@gmail.com" }, { "playground": "2019A.Kagan", "id": "1485184136", "location": { "x": Any, "y": Any }, "name": "Venom 2018", "creationDate": Any valid date, "expirationDate": null, "type": "Movie Panel", "attributes": { "creatorsName": "Manager", "isActive": "True", "isAMovie": "False", "movieName": "Venom 2018" }, "creatorPlayground": "2019A.Kagan", "creatorEmail": "rubykozel@gmail.com" }, { "playground": "2019A.Kagan", "id": "1770773541", "location": { "x": Any, "y": Any }, "name": "Halloween", "creationDate": Any valid date, "expirationDate": null, "type": "Movie Panel", "attributes": { "creatorsName": "Manager", "isActive": "True", "isAMovie": "False", "movieName": "Venom 2018" }, "creatorPlayground": "2019A.Kagan", "creatorEmail": "rubykozel@gmail.com" } ]'
	
Scenario: Getting none elements fail, with creator email that has no elements

	Given the server is up 
	And there are elements with playground: "2019A.Kagan", email: "rubykozel@gmail.com", 
	When I GET "/playground/elements/2019A.Kagan/dudidavidov@gmail.com/all" 
	Then the response is 500 with message: "Creator has no elements it created" 
	
#Feature: Get all elements made by specific creator email and playground at max {distance} from ({x},{y})
	
Scenario: 

	Given the server is up 
	And there are elements with playground: "2019A.Kagan", email: "rubykozel@gmail.com", x: random(), y: random(),
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/near/0/0/10"
	Then the response is 200 
	And the output is 'Any elements that has been generated with max {distance} from the ({x},{y}) that has been generated. show as array of ElementTO[]'
	
Scenario: 

	Given the server is up 
	And there are elements with playground: "2019A.Kagan", email: "rubykozel@gmail.com", x: random(), y: random(),
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/near/0/0/1" 
	Then the response is 500 with message: "No elements at the distance specified from the (x, y) specified"
	
	
	