Feature: Change the Details of an element
	
Scenario: Change the name of the element # PASSED #Automated

	Given the server is up 
	And theres an element with playground: "2019A.Kagan", email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "517788786", 
	When I PUT "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/517788786" with '{"playground": "2019A.Kagan","id": "517788786","location": {"x": 3.6830377111762047,"y": 8.3868617407449,"name": "MyBoard","creationDate": "2018-11-11T19:19:18.786+0000","expirationDate": "2018-11-12T19:19:18.786+0000","type": "Messaging Board","attributes": {"creatorsName": "Manager","isActive": "True","isAMovie": "False","movieName": "Venom 2018"},"creatorPlayground": "2019A.Kagan","creatorEmail": "rubykozel@gmail.com"}' 
	Then the reponse is "200 OK" 
	
Scenario: Trying to change type name with null # PASSED #Automated

	Given the server is up 
	And theres an element with playground: "2019A.Kagan", email: "rubykozel@gmail.com", playground: "2019A.Kagan", id: "567", 
	When I PUT "/playground/elements/2019A.Kagan/rubykozel@gmail.com/2019A.Kagan/567" with '{type:null}' 
	Then the reponse is 500 with message: "JSON parse error: One of the parameters provided was null" 