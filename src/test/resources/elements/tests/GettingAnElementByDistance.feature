Feature: Get all elements made by specific creator email and playground at max {distance} from ({x},{y})
	
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
	Then the response 200
	And the output is []