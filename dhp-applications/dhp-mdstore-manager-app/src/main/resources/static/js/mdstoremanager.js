var app = angular.module('mdstoreManagerApp', []);

app.controller('mdstoreManagerController', function($scope, $http) {
	$scope.mdstores = [];
	$scope.versions = [];
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
		$http.put(url).success(function(data) {
			
			$scope.reload();
		}).error(function() {
			alert("error");
		});
	};
	 		
	$scope.deleteMdstore = function(mdId) {
		if (confirm("Are you sure ?")) {
			$http.delete('/mdstores/mdstore/' + mdId).success(function(data) {
				$scope.reload, 500;
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
				angular.forEach($scope.versions, function(value, key) {
					if (value.id == versionId) {
						value.current = true;
						value.writing = false;
						value.size = size;
					} else {
						value.current = false;
					}
				});
			}).error(function() {
				alert("error");
			});
		}
	};
	
	$scope.listVersions = function(mdId, current) {
		$scope.versions = [];
		$http.get('/mdstores/mdstore/' + mdId + '/versions?' + $.now()).success(function(data) {
			angular.forEach(data, function(value, key) {
				value.current = (value.id == current);
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
				var nv = [];
				angular.forEach($scope.versions, function(value, key) {
					if (value.id != versionId) {
						nv.push(value);
					}
				});
				$scope.versions = nv;
				$scope.reload();
			}).error(function() {
				alert("error");
			});
		}
	};
	
	$scope.reload();
	
});

