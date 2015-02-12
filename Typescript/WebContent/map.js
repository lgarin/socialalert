var MillisPerDay = 1000 * 60 * 60 * 24;
var EarthRadius = 6378.1370;
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
var MapPictureMatchCountRequest = (function () {
    function MapPictureMatchCountRequest() {
    }
    return MapPictureMatchCountRequest;
})();
var FindKeywordSuggestionsRequest = (function () {
    function FindKeywordSuggestionsRequest() {
    }
    return FindKeywordSuggestionsRequest;
})();
var MapController = (function () {
    function MapController(rpcService, $scope, $compile) {
        var _this = this;
        this.rpcService = rpcService;
        this.$scope = $scope;
        this.$compile = $compile;
        this.populateMap = function () {
            var request = new MapPictureMatchCountRequest();
            request.latitude = _this.map.getCenter().lat;
            request.longitude = _this.map.getCenter().lng;
            request.radius = _this.map.getCenter().distanceTo(_this.map.getBounds().getNorthEast()) / 1000;
            if (_this.keyword.length == 0) {
                request.keywords = null;
            }
            else {
                request.keywords = _this.keyword;
            }
            request.maxAge = 2000 * MillisPerDay;
            request.profileId = null;
            _this.rpcService.call('pictureFacade', 'mapPictureMatchCount', request).then(_this.displayStatisticCallback).catch(errorCallback);
        };
        this.displayDataCallback = function (data) {
            var points = data.content.filter(MapController.hasLocation).map(MapController.getLocation);
            _this.initMap(points);
            _this.displayItems(data);
        };
        this.displayStatisticCallback = function (data) {
            var points = data.map(MapController.getPoint);
            _this.initMap(points);
            if (data.length == 1 && data[0].count < 100) {
                _this.searchItems(data[0]);
            }
            else if (data.length == 1) {
                _this.drillDown(data[0]);
            }
            else if (data.length > 1) {
                _this.displayStatistic(data);
            }
        };
        this.keyword = "";
        this.reloadStatisticData();
    }
    MapController.getLocation = function (info) {
        return new L.LatLng(info.pictureLatitude, info.pictureLongitude);
    };
    MapController.getPoint = function (info) {
        return new L.LatLng(info.latitude, info.longitude);
    };
    MapController.hasLocation = function (info) {
        if (info.pictureLongitude && info.pictureLatitude) {
            return true;
        }
        else {
            return false;
        }
    };
    MapController.prototype.displayItems = function (data) {
        var _this = this;
        data.content.filter(MapController.hasLocation).forEach(function (info) {
            var marker = new L.Marker(MapController.getLocation(info));
            var scope = _this.$scope.$new();
            scope['info'] = info;
            var template = _this.$compile("<thumbnail/>")(scope);
            marker.bindPopup(template[0]);
            _this.markers.addLayer(marker);
        });
        this.map.on('zoomend', this.populateMap);
    };
    MapController.prototype.initMap = function (points) {
        if (!this.map) {
            this.map = new L.Map("mapDiv");
            var layer = new L.TileLayer("http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", { maxZoom: 15, attribution: "&copy; <a href='http://www.openstreetmap.org/copyright'>OpenStreetMap</a> contributors" });
            layer.addTo(this.map);
            this.markers = new L.LayerGroup();
            this.markers.addTo(this.map);
        }
        this.markers.clearLayers();
        this.map.off('zoomend', this.populateMap);
        if (points.length > 0) {
            this.map.fitBounds(L.latLngBounds(points));
        }
        else {
            this.map.fitWorld();
        }
    };
    MapController.prototype.displayStatistic = function (data) {
        var _this = this;
        data.forEach(function (info) {
            var markerOption = {
                color: 'red',
                fillColor: '#f03',
                fillOpacity: 0.5
            };
            var marker = new L.Circle(MapController.getPoint(info), 500 * info.radius, markerOption);
            var infoIcon = new L.DivIcon({ html: '<div>' + info.count + '</div>', className: 'marker-cluster' });
            var marker2 = new L.Marker(MapController.getPoint(info), { icon: infoIcon });
            _this.markers.addLayer(marker);
            _this.markers.addLayer(marker2);
        });
        this.map.on('zoomend', this.populateMap);
    };
    MapController.prototype.searchItems = function (data) {
        var request = new SearchPicturesRequest();
        request.maxAge = 2000 * MillisPerDay;
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
        this.rpcService.call('pictureFacade', "searchPictures", request).then(this.displayDataCallback).catch(errorCallback);
    };
    MapController.prototype.drillDown = function (data) {
        var request = new MapPictureMatchCountRequest();
        request.latitude = data.latitude;
        request.longitude = data.longitude;
        request.radius = data.radius;
        if (this.keyword.length == 0) {
            request.keywords = null;
        }
        else {
            request.keywords = this.keyword;
        }
        request.maxAge = 2000 * MillisPerDay;
        request.profileId = null;
        this.rpcService.call('pictureFacade', 'mapPictureMatchCount', request).then(this.displayStatisticCallback).catch(errorCallback);
    };
    MapController.prototype.reloadStatisticData = function () {
        var request = new MapPictureMatchCountRequest();
        request.latitude = 0;
        request.longitude = 0;
        request.radius = EarthRadius;
        if (this.keyword.length == 0) {
            request.keywords = null;
        }
        else {
            request.keywords = this.keyword;
        }
        request.maxAge = 2000 * MillisPerDay;
        request.profileId = null;
        this.rpcService.call('pictureFacade', 'mapPictureMatchCount', request).then(this.displayStatisticCallback).catch(errorCallback);
    };
    MapController.prototype.startSearch = function (keyword) {
        this.keyword = keyword;
        this.reloadStatisticData();
    };
    MapController.prototype.getSuggestions = function (input) {
        var request = new FindKeywordSuggestionsRequest();
        request.partial = input;
        return this.rpcService.call("pictureFacade", "findKeywordSuggestions", request);
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
    function RpcService($http, $q, baseUrl) {
        var _this = this;
        this.$http = $http;
        this.$q = $q;
        this.baseUrl = baseUrl;
        this.id = 0;
        this.successCallback = function (arg) {
            if (arg.data.error) {
                return _this.$q.reject(arg.data.error);
            }
            else {
                return arg.data.result;
            }
        };
        this.errorCallback = function (arg) {
            return _this.$q.reject(new RpcError(arg.status, "Cannot connect"));
        };
    }
    RpcService.prototype.call = function (service, method, parameters) {
        var request = new RpcRequest(this.id++, method, parameters);
        return this.$http.post(this.baseUrl + service, request, { 'headers': { 'Content-Type': 'application/json' } }).then(this.successCallback, this.errorCallback);
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
app.service('rpcService', ['$http', '$q', 'baseUrl', RpcService]);
app.config(['$routeProvider', AppConfig]);
app.controller('MapController', ['rpcService', '$scope', '$compile', MapController]);
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
        replace: false,
        transclude: true,
        templateUrl: "partial/thumbnail.html"
    };
});
function errorCallback(error) {
    alert(error.message);
}
