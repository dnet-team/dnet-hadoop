var app = angular.module('mdstoreManagerApp', []);

app.controller('mdstoreManagerController', function($scope, $http) {
	$scope.mdstores = [];
	$scope.versions = [];
	$scope.openMdstore = '';
	$scope.openCurrentVersion = ''
	
	$scope.forceVersionDelete = false;
	
	$scope.reload = function() {
		$http.get('/mdstores/?' + $.now()).success(function(data) {
			$scope.mdstores = data;
		}).error(function() {
			alert("error");
		});
	};
		
	$scope.newMdstore = function(format, layout, interpretation, dsName, dsId, apiId) {
		var url = '/mdstores/new/' + encodeURIComponent(format) + '/' + encodeURIComponent(layout) + '/' + encodeURIComponent(interpretation);
		if (dsName || dsId || apiId) {
			url += '?dsName=' + encodeURIComponent(dsName) + '&dsId=' + encodeURIComponent(dsId) + '&apiId=' + encodeURIComponent(apiId);
		}
		$http.get(url).success(function(data) {
			$scope.reload();
		}).error(function() {
			alert("error");
		});
	};
	 		
	$scope.deleteMdstore = function(mdId) {
		if (confirm("Are you sure ?")) {
			$http.delete('/mdstores/mdstore/' + mdId).success(function(data) {
				$scope.reload();
			}).error(function() {
				alert("error");
			});
		}
	};

	$scope.prepareVersion = function(mdId, currentVersion) {
		$scope.versions = [];
		$http.get('/mdstores/mdstore/' + mdId + '/newVersion?' + $.now()).success(function(data) {
			$scope.reload();
			$scope.listVersions(mdId, currentVersion);
		}).error(function() {
			alert("error");
		});
	};

	$scope.commitVersion = function(versionId) {
		var size = parseInt(prompt("New Size", "0"));
		if (size >= 0) {
			$http.get("/mdstores/version/" + versionId + "/commit/" + size + '?' + $.now()).success(function(data) {
				$scope.reload();
				$scope.openCurrentVersion = versionId;
				$scope.refreshVersions();
			}).error(function() {
				alert("error");
			});
		}
	};
	
	$scope.listVersions = function(mdId, current) {
		$scope.openMdstore = mdId;
		$scope.openCurrentVersion = current;
		$scope.versions = [];
		$scope.refreshVersions();
	};
	
	$scope.refreshVersions = function() {
		$http.get('/mdstores/mdstore/' + $scope.openMdstore + '/versions?' + $.now()).success(function(data) {
			angular.forEach(data, function(value, key) {
				value.current = (value.id == $scope.openCurrentVersion);
			});
			$scope.versions = data;
		}).error(function() {
			alert("error");
		});
	};
	
	$scope.deleteVersion = function(versionId, force) {
		if (confirm("Are you sure ?")) {
			var url = '/mdstores/version/' + versionId;
			if (force) { url += '?force=true'; }
						
			$http.delete(url).success(function(data) {
				$scope.reload();
				$scope.refreshVersions();
			}).error(function() {
				alert("error");
			});
		}
	};
	
	$scope.reload();
	
});

