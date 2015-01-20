var MillisPerDay = 1000 * 60 * 60 * 24;
var QueryResult = (function () {
    function QueryResult() {
    }
    return QueryResult;
})();
var UserApprovalModifier;
(function (UserApprovalModifier) {
    UserApprovalModifier[UserApprovalModifier["LIKE"] = 0] = "LIKE";
    UserApprovalModifier[UserApprovalModifier["DISLIKE"] = 1] = "DISLIKE";
})(UserApprovalModifier || (UserApprovalModifier = {}));
var PictureInfo = (function () {
    function PictureInfo() {
    }
    return PictureInfo;
})();
var SearchPicturesInCategoryRequest = (function () {
    function SearchPicturesInCategoryRequest() {
    }
    return SearchPicturesInCategoryRequest;
})();
var MapWrapper = (function () {
    function MapWrapper() {
    }
    MapWrapper.prototype.displayDataCallback = function (data) {
        var _this = this;
        this.map = new L.Map("map");
        this.map.setView(new L.LatLng(51.505, -10.09), 2);
        var layer = new L.TileLayer("http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", { maxZoom: 18, attribution: "&copy; <a href='http://www.openstreetmap.org/copyright'>OpenStreetMap</a> contributors" });
        layer.addTo(this.map);
        data.content.forEach(function (info, index, array) {
            if (info.pictureLatitude && info.pictureLongitude) {
                var marker = new L.Marker(new L.LatLng(info.pictureLatitude, info.pictureLongitude));
                marker.addTo(_this.map).bindPopup("<b>" + info.title + "</b><img src='http://jcla3ndtozbxyghx.myfritz.net:18789/socialalert-app/thumbnail/" + info.pictureUri + "' height=240 width=320/>");
            }
        });
    };
    return MapWrapper;
})();
var MapController = (function () {
    function MapController(map) {
        this.map = map;
    }
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
        this.$http.post(this.baseUrl + service, request, { 'headers': { 'Content-Type': 'application/json' } }).success(function (data) {
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
var app = angular.module('app', ['ngRoute']);
app.value('baseUrl', 'http://jcla3ndtozbxyghx.myfritz.net:18789/socialalert-app/rest/');
app.service('mapWrapper', MapWrapper);
app.service('rpcService', ['$http', 'baseUrl', RpcService]);
app.config(['$routeProvider', AppConfig]);
app.controller('MapController', ['mapWrapper', MapController]);
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
function errorCallback(error) {
    alert(error.message);
}
app.run(['rpcService', 'mapWrapper', function (rpcService, mapWrapper) {
    var request = new SearchPicturesInCategoryRequest();
    request.maxAge = 360 * MillisPerDay;
    request.category = 'ART';
    request.pageNumber = 0;
    request.pageSize = 20;
    request.keywords = null;
    request.longitude = null;
    request.latitude = null;
    request.maxDistance = null;
    rpcService.call('pictureFacade', "searchPicturesInCategory", request, mapWrapper.displayDataCallback, errorCallback);
}]);
