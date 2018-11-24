Feature: Posting new user 
Scenario: Posting new user successfully # PASSED #Automated

	Given the server is up 
	When I POST "/playground/users" with '{"email":"rubykozel@gmail.com", "username":"ruby", "avatar":":-)", "role":"Guest"}' 
	Then the response is 200 ok 
	And the database contains the user : '{"email": "rubykozel@gmail.com", "playground": "2019A.Kagan", "userName": "ruby", "avatar": ":-)", "role": "Guest", "points": 0}' 
	
Scenario: Posting new user unsuccessfully # PASSED #Automated

	Given the server is up 
	When I POST "/playground/users" with nothing 
	Then the response is <> 2xx 
	
Scenario: Posting new user with given email as null # PASSED #Automated

	Given the server is up 
	When I POST "/playground/users" with '{"email": null,"username":"ruby","avatar":":-)","role":"Guest"}' 
	Then the response is 500
	
#Feature: Confirming a new registered user
	
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
	
#Feature: Logging into the server
	
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

#Feature: Change the details of the user
	
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
	
#Feature: Creating an element
	
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
				

#Feature: Change the Details of an element

Scenario: Change the name of the element # PASSED #Automated
	
	Given the server is up
	And theres an element with playground: "2019A.Kagan", email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "517788786",
	When I PUT "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/517788786" with '{"playground": "2019A.Kagan","id": "517788786","location": {"x": 3.6830377111762047,"y": 8.3868617407449,"name": "MyBoard","creationDate": "2018-11-11T19:19:18.786+0000","expirationDate": "2018-11-12T19:19:18.786+0000","type": "Messaging Board","attributes": {"creatorsName": "Manager","isActive": "True","isAMovie": "False","movieName": "Venom 2018"},"creatorPlayground": "2019A.Kagan","creatorEmail": "roee@gmail.com"}'
	Then the reponse is "200 OK"
	
Scenario: Trying to change type name with null # PASSED #Automated
	
	Given the server is up
	And theres an element with playground: "2019A.Kagan", email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "567",
	When I PUT "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/567" with '{type:null}'
	Then the reponse is 500 with message: "JSON parse error: One of the paramters provided was null"

#Feature: Get an element by specific playground, creator email and id
	
Scenario: Getting an element successfully # PASSED

	Given the server is up 
	And theres an element with playground: "2019A.Kagan", email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "1025028332",
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/1025028332"
	Then the response is 200 
	And the output is '{"playground": "2019A.Kagan", "id": "1586158061", "location": { "x": Any, "y": Any }, "name": "Messaging Board", "creationDate": Any valid date, "expirationDate": null, "type": "Messaging Board", "attributes": { "creatorsName": "Manager", "isActive": "True", "isAMovie": "False", "movieName": "Venom 2018" }, "creatorPlayground": "2019A.Kagan", "creatorEmail": "rubykozel@gmail.com" }' 
	
Scenario: Getting an element is unsuccessful with wrong id  # PASSED

	Given the server is up
	And theres an element with playground: "2019A.Kagan", email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "1025028332", 
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/1586158061" 
	Then the response is 404 with message: "Element does not exist" 
	
Scenario: Getting an element is unsuccessful with wrong creator email # PASSED

	Given the server is up
	And theres an element with playground: "2019A.Kagan", email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "1025028332", 
	When I GET "/playground/elements/2019A.Kagan/dudidavidov@gmail.com/2019A.Kagan/1025028332" 
	Then the response is 404 with message: "Element does not exist" 
	
#Feature: Get all elements made by specific creator email and playground
	
Scenario: Getting all elements successfully # PASSED

	Given the server is up
	And there are elements with playground: "2019A.Kagan", email: "rubykozel@gmail.com",
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/all"
	Then the response is 200 
	And the output is '[ { "playground": "2019A.Kagan", "id": "1025028332", "location": { "x": Any, "y": Any }, "name": "Messaging Board", "creationDate": Any valid date, "expirationDate": null, "type": "Messaging Board", "attributes": { "creatorsName": "Manager", "isActive": "True", "isAMovie": "False", "movieName": "Venom 2018" }, "creatorPlayground": "2019A.Kagan", "creatorEmail": "rubykozel@gmail.com" } ...]'
	
Scenario: Getting none elements fail, with creator email that has no elements # PASSED

	Given the server is up
	And there are elements with playground: "2019A.Kagan", email: "rubykozel@gmail.com", 
	When I GET "/playground/elements/2019A.Kagan/dudidavidov@gmail.com/all" 
	Then the response is 500 with message: "Creator has no elements it created" 
	
#Feature: Get all elements made by specific creator email and playground at max {distance} from ({x},{y})
	
Scenario: Getting an element with specific distance successfully # PASSED

	Given the server is up
	And there is an element with playground: "2019A.Kagan", email: "rubykozel@gmail.com", x: Any x that its distance from (0,0) is less then 10, y: Any y that its distance from (0,0) is less then 10
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/near/0/0/10"
	Then the response is 200 
	And the output is '[ { "playground": "2019A.Kagan", "id": "1025028332", "location": { "x": Any x that its distance from (0,0) is less then 10, "y": Any y that its distance from (0,0) is less then 10 }, "name": Any name, "creationDate": Any valid date, "expirationDate": null, "type": Any type, "attributes": { "creatorsName": "Manager", "isActive": "True", "isAMovie": "False", "movieName": "Venom 2018" }, "creatorPlayground": "2019A.Kagan", "creatorEmail": "rubykozel@gmail.com" } ]'
	
Scenario: Getting elements when there are no elements in the playground

	Given the server is up 
	And there are no elements in the playground # PASSED
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/near/0/0/1" 
	Then the response is 404 with message: "No elements at the distance specified from the (x, y) specified"
	
#Feature: Get elements by attribute's value

Scenario: Get elements by attributes value succesfully # PASSED

	Given the server is up 
	And theres are elements with playground: "2019A.Kagan", email: "rubykozel@gmail.com", attribute: "isAMovie", value:"False"
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/search/isAMovie/False"
	Then the response is 200
	And the output is '[{"playground":"2019A.Kagan","id":"1025028332","location":{"x": Any x ,"y": Any y },"name": Any name,"creationDate": Any valid date ,"expirationDate":null, "type": any type ,"attributes":{"creatorsName":"Manager","isActive":"True","isAMovie":"False","movieName":"Venom 2018"},"creatorPlayground":"2019A.Kagan","creatorEmail":"rubykozel@gmail.com"} ... ]'
	
Scenario: Get elements by null attributes value # PASSED

	Given the server is up
	And theres are elements with playground: "2019A.Kagan", email: "rubykozel@gmail.com", attribute: "isAMovie", value:"False"
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/search/null/False"
	Then the response is 500 with message: "One of the paramters provided was null"
		
Scenario: Get elements by attributes value that does not exist # PASSED

	Given the server is up
	And theres are no elements with playground: "2019A.Kagan", email: "rubykozel@gmail.com", attribute: "movieName", value:"Venom 1018"
	When I GET "/playground/elements/2019A.Kagan/rubykozel@gmail.com/search/movieName/Venom 1018"
	Then the response is 404 with message "No element was found with key: movieName and value: Venom 1018"
	
#Feature: Posting a new activity

Scenario: Post a new activity successfuly # PASSED

	Given the server is up 
	When I POST "/playground/activities/2019A.Kagan/rubykozel@gmail.com" with '{"elementId": "1025028355","elementPlayground": "2019A.Kagan","type": "x"}'
	Then the response is 200
	And the output is '{"email":"rubykozel@gmail.com","playground":"2019A.Kagan","userName":"ruby","avatar":":-)","role":"Guest","points":0}'
	
Scenario: Post a new activity with null email unsuccessfuly # PASSED

	Given the server is up
	When I POST "/playground/activities/2019A.Kagan/null" with '{"elementId": "1025028355","elementPlayground": "2019A.Kagan","type": "x"}'
	Then the response is 500 with message: "One of the paramters provided was null"

Scenario: Post a new activity with an empty JSON unsuccessfuly # PASSED

	Given the server is up
	When I POST "/playground/activities/2019A.Kagan/rubykozel@gmail.com" with '{}'
	Then the response is 500 
	