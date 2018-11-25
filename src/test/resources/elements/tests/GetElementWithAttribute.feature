Feature: Get elements by attribute's value
	
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