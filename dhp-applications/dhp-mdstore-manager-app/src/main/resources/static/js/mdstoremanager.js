var app = angular.module('mdstoreManagerApp', []);

app.controller('mdstoreManagerController', function($scope, $http) {
	$scope.mdstores = [];
	
	$http.get('/mdstores/').success(function(data) {
		$scope.mdstores = data;
	}).error(function() {
		alert("error");
	});
});

