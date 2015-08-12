var app = angular.module('bookManagerApp', ['ngRoute', 'ngCookies', 'ngResource']);

app.config(['$routeProvider', function($routeProvider) {
	return $routeProvider.when('/', {
		templateUrl:'/views/loginPage',
		controller:'loginCtrl'
	}).when('/booklist', {
		templateUrl:'/views/bookListPage',
		controller:'bookCtrl'
	});
}]);

/*
app.config(['$locationProvider', function($locationProvider) {
	return $locationProvider.html5Mode(true).hasPrefix('!');
}]);
*/

app.controller('loginCtrl', function($scope, $location, $http, $cookies) {

	if(typeof $cookies.get('userId') == 'undefined') {
		
		$scope.userId = '';
		$scope.password = '';
		$scope.loginMessage = '';
		$scope.isMsg = false;
	
		$scope.error = false;
		$scope.incomplete = false;
	
		$scope.loginAuth = function() {
			$http({
				method: 'POST',
				url: "/api/login",
				headers: {'Content-Type': 'application/json; charset=UTF-8'},
				data: {"userId" : $scope.userId, "password" : $scope.password}
			}).success(function(response) {
				console.log(response);
				$scope.userId = '';
				$scope.password = '';
				if(response.status === 'OK') {
					$location.path('/booklist');
					$cookies.put('userId', response.userId);
				}
				else {
					$scope.isMsg = true;
					$scope.loginMessage = response.message;
				}
			});
		};
		
		$scope.test = function() {
			$scope.incomplete = false;
			if (typeof $scope.userId == 'undefined' || !$scope.userId.length || typeof $scope.password == 'undefined' || !$scope.password.length){
				$scope.incomplete = true;
			}
		};
	}
	else {
		$location.path('/booklist');
	}
});


app.controller('bookCtrl', function($scope, $location, $http, $cookies) {
	
	if(typeof $cookies.get('userId') == 'undefined')
		$location.path('/');
	else {
		$scope.id = '';
		$scope.userId = $cookies.get('userId');
		$scope.title = '';
		$scope.author = '';
		$scope.createOn = '';
		$scope.content = '';
		$scope.alertMessage = '';
		$scope.hasMsg = false;
	
		$scope.sortType = 'title';
		$scope.sortReverse = false;
		$scope.searchTitle = '';
	
		console.log($cookies.get('userId'));
	
		$http.get('/api/books/' + $cookies.get('userId') + '/list').success(function(response) {
		//$http.get('/api/books').success(function(response) {
			$scope.books = response;
		});
	
		$scope.edit = true;
		$scope.error = false;
		$scope.incomplete = false;

		$scope.editBook = function(id) {
			$scope.alertMessage = '';
			$scope.hasMsg = false;
		
			if (id == 'new') {
				$scope.clearFields();
				$scope.incomplete = true;
				$scope.alertMessage = '';
			} else {
				$scope.edit = false;
			
				angular.forEach($scope.books, function(book) {
					if(book.id === id) {
						$scope.bookId = book.id;
						$scope.userId = book.userId;
						$scope.title = book.title;
						$scope.author = book.author;
						$scope.dtArr = book.date.split('-');
						$scope.createOn = new Date($scope.dtArr[0], ($scope.dtArr[1]-1), $scope.dtArr[2]);
						$scope.content = book.content;
					}
				});
			}
		};
	
		$scope.deleteBook = function(id) {
			$http.post("/api/book/"+id+"/delete").success(function(response) {
				$scope.deleteBookById(id);
				$scope.alertMessage = response.message;
				$scope.hasMsg = true;
			});
		};

		$scope.$watch('title', function() {$scope.test();});
		$scope.$watch('author', function() {$scope.test();});
		$scope.$watch('createOn',function() {$scope.test();});
		$scope.$watch('content',function() {$scope.test();});

		$scope.test = function() {
			$scope.incomplete = false;
			if ($scope.edit && (typeof $scope.title == 'undefined' || !$scope.title.length || typeof $scope.content == 'undefined' || !$scope.content.length)) {
				$scope.incomplete = true;
			}
		};
	
		$scope.createBook = function() {
			$http({
				method: 'POST',
				url: "/api/book",
				headers: {'Content-Type': 'application/json; charset=UTF-8'},
				data: {"id": $scope.bookId, "userId" : $scope.userId, "title" : $scope.title, "date" : $scope.createOn, "content" : $scope.content, "author" : $scope.author}
			}).success(function(response) {
				console.log(response);
			
				if (typeof $scope.bookId != 'undefined') {
					console.log('I am in EDIT');
					console.log($scope.createOn);
					$scope.deleteBookById($scope.bookId);
					$scope.books.push({"id": $scope.bookId, "userId" : $scope.userId, "title" : $scope.title, "date" : $scope.toJSONLocal($scope.createOn), "content" : $scope.content, "author" : $scope.author});
				
				} 
				else {
					console.log('I am in ADD');
					console.log(response.bookId);
					$scope.books.push({"id": response.bookId, "userId" : $scope.userId, "title" : $scope.title, "date" : $scope.toJSONLocal($scope.createOn), "content" : $scope.content, "author" : $scope.author});
				
				}
			
				$scope.alertMessage = response.message;
				$scope.hasMsg = true;
				$scope.clearFields(); 
			});
		};
	
		$scope.clearFields = function() {
			$scope.edit = true;
			//$scope.incomplete = true;
			$scope.bookId = '';
			$scope.title = '';
			$scope.author = '';
			$scope.createOn = '';
			$scope.content = '';
		};
	
		$scope.deleteBookById = function(id) {
			$scope.indexToDel = -1;
			$scope.index = 0;
			angular.forEach($scope.books, function(book) {
				if(book.id === id) {
					$scope.indexToDel = $scope.index;
				}
				$scope.index++;
			});

			if ($scope.indexToDel > -1) {
				$scope.books.splice($scope.indexToDel, 1);
			}
		};
	
		$scope.toJSONLocal = function(date) {
			var local = new Date(date);
			local.setMinutes(date.getMinutes() - date.getTimezoneOffset());
			return local.toJSON().slice(0, 10);
		};
		
		$scope.logoutFromApp = function() {
			$cookies.remove('userId');
			$location.path('/');		
		};
	}
});
