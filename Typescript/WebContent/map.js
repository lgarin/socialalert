var MillisPerDay = 1000 * 60 * 60 * 24;
var UserApprovalModifier;
(function (UserApprovalModifier) {
    UserApprovalModifier[UserApprovalModifier["LIKE"] = 0] = "LIKE";
    UserApprovalModifier[UserApprovalModifier["DISLIKE"] = 1] = "DISLIKE";
})(UserApprovalModifier || (UserApprovalModifier = {}));
var SearchPicturesInCategoryRequest = (function () {
    function SearchPicturesInCategoryRequest() {
    }
    return SearchPicturesInCategoryRequest;
})();
var SearchPicturesRequest = (function () {
    function SearchPicturesRequest() {
    }
    return SearchPicturesRequest;
})();
var FindKeywordSuggestionsRequest = (function () {
    function FindKeywordSuggestionsRequest() {
    }
    return FindKeywordSuggestionsRequest;
})();
var MapWrapper = (function () {
    function MapWrapper() {
    }
    MapWrapper.getLocation = function (info) {
        return new L.LatLng(info.pictureLatitude, info.pictureLongitude);
    };
    MapWrapper.hasLocation = function (info) {
        if (info.pictureLongitude && info.pictureLatitude) {
            return true;
        }
        else {
            return false;
        }
    };
    MapWrapper.prototype.displayDataCallback = function (data) {
        var _this = this;
        if (!this.map) {
            this.map = new L.Map("mapDiv");
            var layer = new L.TileLayer("http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", { maxZoom: 15, attribution: "&copy; <a href='http://www.openstreetmap.org/copyright'>OpenStreetMap</a> contributors" });
            layer.addTo(this.map);
        }
        var points = data.content.filter(MapWrapper.hasLocation).map(MapWrapper.getLocation);
        this.map.fitBounds(L.latLngBounds(points));
        data.content.filter(MapWrapper.hasLocation).forEach(function (info) {
            var marker = new L.Marker(MapWrapper.getLocation(info));
            marker.addTo(_this.map).bindPopup("<b>" + info.title + "</b><img src='http://jcla3ndtozbxyghx.myfritz.net:18789/socialalert-app/thumbnail/" + info.pictureUri + "' height=220 width=300/>");
        });
    };
    return MapWrapper;
})();
var MapController = (function () {
    function MapController(rpcService, mapWrapper) {
        this.rpcService = rpcService;
        this.mapWrapper = mapWrapper;
        this.keyword = "";
    }
    MapController.prototype.reloadData = function () {
        var request = new SearchPicturesRequest();
        request.maxAge = 720 * MillisPerDay;
        request.pageNumber = 0;
        request.pageSize = 20;
        if (this.keyword.length == 0) {
            request.keywords = null;
        }
        else {
            request.keywords = this.keyword;
        }
        request.longitude = null;
        request.latitude = null;
        request.maxDistance = null;
        this.rpcService.call('pictureFacade', "searchPictures", request, this.mapWrapper.displayDataCallback, errorCallback);
    };
    MapController.prototype.debug = function (v) {
        alert(v);
        return v;
    };
    MapController.prototype.getSuggestions = function (input) {
        var _this = this;
        var request = new FindKeywordSuggestionsRequest();
        request.partial = input;
        return this.rpcService.call("pictureFacade", "findKeywordSuggestions", request, function (data) { return data; }, errorCallback).then(function (data) { return _this.debug(data.result); });
    };
    return MapController;
})();
var RpcRequest = (function () {
    function RpcRequest(id, method, params) {
        this.jsonrpc = "2.0";
        this.id = id;
        this.method = method;
        this.params = params;
    }
    return RpcRequest;
})();
var RpcError = (function () {
    function RpcError(code, message) {
        this.code = code;
        this.message = message;
    }
    return RpcError;
})();
var RpcResponse = (function () {
    function RpcResponse() {
    }
    return RpcResponse;
})();
var RpcService = (function () {
    function RpcService($http, baseUrl) {
        this.$http = $http;
        this.baseUrl = baseUrl;
        this.id = 0;
    }
    RpcService.prototype.call = function (service, method, parameters, success, error) {
        var request = new RpcRequest(this.id++, method, parameters);
        return this.$http.post(this.baseUrl + service, request, { 'headers': { 'Content-Type': 'application/json' } }).success(function (data) {
            if (data.error)
                error(data.error);
            else
                success(data.result);
        }).error(function (data, status) { return error(new RpcError(status, "Cannot connect")); });
    };
    return RpcService;
})();
var AppConfig = (function () {
    function AppConfig($routeProvider) {
        $routeProvider.when('/view1', { templateUrl: 'partial/view1.html' }).when('/view2', { templateUrl: 'partial/view2.html' }).otherwise({ redirectTo: '/view1' });
    }
    return AppConfig;
})();
var app = angular.module('app', ['ngRoute', 'ui.bootstrap']);
app.value('baseUrl', 'http://jcla3ndtozbxyghx.myfritz.net:18789/socialalert-app/rest/');
app.service('mapWrapper', MapWrapper);
app.service('rpcService', ['$http', 'baseUrl', RpcService]);
app.config(['$routeProvider', AppConfig]);
app.controller('MapController', ['rpcService', 'mapWrapper', MapController]);
app.directive('ensureExpression', ['$http', '$parse', function ($http, $parse) {
    return {
        require: 'ngModel',
        link: function (scope, ele, attrs, ngModelController) {
            scope.$watch(attrs.ngModel, function (value) {
                var booleanResult = $parse(attrs.ensureExpression)(scope);
                ngModelController.$setValidity('expression', booleanResult);
            });
        }
    };
}]);
app.directive("thumbnail", function () {
    return {
        restrict: "E",
        replace: true,
        templateUrl: "thumbnail.html"
    };
});
function errorCallback(error) {
    alert(error.message);
}
app.run(['rpcService', 'mapWrapper', function (rpcService, mapWrapper) {
    var request = new SearchPicturesRequest();
    request.maxAge = 720 * MillisPerDay;
    request.pageNumber = 0;
    request.pageSize = 20;
    request.keywords = null;
    request.longitude = null;
    request.latitude = null;
    request.maxDistance = null;
    rpcService.call('pictureFacade', "searchPictures", request, mapWrapper.displayDataCallback, errorCallback);
}]);
