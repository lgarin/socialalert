
var MillisPerDay : number = 1000 * 60 * 60 * 24;
var EarthRadius : number = 6378.1370;

interface QueryResult<T> {
    content : T[];
    pageNumber : number;
    pageCount : number;
}

enum UserApprovalModifier {
    LIKE,
    DISLIKE
}

interface PictureInfo {
    pictureUri : string;
    title : string;
    description : string;
    profileId : string;
    creation : Date;
    lastUpdate : Date;
    pictureTimestamp : Date;
    pictureWidth : number;
    pictureHeight : number;
    pictureLongitude : number;
    pictureLatitude : number;
    locality : string;
    country : string;
    cameraMaker : string;
    cameraModel : string;
    hitCount : number;
    likeCount : number;
    dislikeCount : number;
    commentCount : number;
    categories : string[];
    tags : string[];
    userApprovalModifier : UserApprovalModifier;
    creator : string;
    online : boolean;
}

interface GeoArea {
   latitude : number;
   longitude : number;
   radius : number; 
}

interface GeoStatistic extends GeoArea {
   count : number;
}

class SearchPicturesInCategoryRequest {
    public maxAge : number;
    public keywords : string;
    public maxDistance : number;
    public pageSize : number;
    public pageNumber : number;
    public longitude : number;
    public latitude : number;
    public category : string;
}

class SearchPicturesRequest {
    public maxAge : number;
    public keywords : string;
    public maxDistance : number;
    public pageSize : number;
    public pageNumber : number;
    public longitude : number;
    public latitude : number;
}

class MapPictureMatchCountRequest {
    public latitude : number;
    public longitude : number;
    public radius : number;
    public keywords : string;
    public maxAge : number;
    public profileId : string[];
}

class FindKeywordSuggestionsRequest {
   public partial : string; 
}

class MapController {
    
    public keyword : string;
    private map : L.Map;
    private initialSearch : boolean;
    private markers : L.LayerGroup<L.ILayer>;
    public topPictures : PictureInfo[];
    
    constructor(private rpcService : RpcService, private $scope : ng.IScope, private $compile : ng.ICompileService) {
        this.startSearch("");
    }
    
    static toGeoArea(map : L.Map) : GeoArea {
       return {
           longitude : map.getCenter().lng,
           latitude : map.getCenter().lat,
           radius : map.getCenter().distanceTo(map.getBounds().getNorthEast()) / 1000
        } 
    }
    
    static getLocation(info : PictureInfo) {
       return new L.LatLng(info.pictureLatitude, info.pictureLongitude);
    }
    
    static getPoint(info : GeoArea) {
       return new L.LatLng(info.latitude, info.longitude); 
    }
    
    static hasLocation(info : PictureInfo) {
       if (info.pictureLongitude && info.pictureLatitude) {
          return true; 
       } else {
          return false; 
       }
    }
    
    private populateMap = () => {
        var request = new MapPictureMatchCountRequest();
        var area = MapController.toGeoArea(this.map);
        request.latitude = area.latitude;
        request.longitude = area.longitude;
        request.radius = area.radius;
        if (this.keyword.length == 0) {
            request.keywords = null;
        } else {
            request.keywords = this.keyword;
        }
        request.maxAge = 2000 * MillisPerDay;
        request.profileId = null;
        this.rpcService.call('pictureFacade', 'mapPictureMatchCount', request).then(this.displayStatisticCallback).catch(errorCallback);
    }
    
    private displayItems(data: QueryResult<PictureInfo>) {
        data.content.filter(MapController.hasLocation).forEach(info => {
            var marker = new L.Marker(MapController.getLocation(info));
            var scope = this.$scope.$new();
            scope['info'] = info;
            var template = this.$compile("<thumbnail/>")(scope);
            marker.bindPopup(template[0]);
            this.markers.addLayer(marker);
        });
        
        this.map.on('zoomend', this.populateMap);
        this.map.on('moveend', this.populateMap);
        this.initialSearch = false;
        this.populateTopPictures();
    }
    
    public populateTopPictures = () => {
        var request = new SearchPicturesRequest();
        request.maxAge = 2000 * MillisPerDay;
        request.pageNumber = 0;
        request.pageSize = 6;
        if (this.keyword.length == 0) {
            request.keywords = null;
        } else {
            request.keywords = this.keyword;
        }
        var area = MapController.toGeoArea(this.map);
        request.longitude = area.longitude;
        request.latitude = area.latitude;
        request.maxDistance = area.radius;
        this.rpcService.call('pictureFacade', "searchPictures", request).then(this.showTopPicturesCallback).catch(errorCallback);
    }
    
    public showTopPicturesCallback = (data: QueryResult<PictureInfo>) => {
        this.topPictures = data.content;
    }
    
    public displayDataCallback = (data: QueryResult<PictureInfo>) => {

        var points = data.content.filter(MapController.hasLocation).map(MapController.getLocation);
        this.initMap(points);
        this.displayItems(data);
    }
    
    public displayStatisticCallback = (data : GeoStatistic[]) => {
        
        var points = data.map(MapController.getPoint);
        this.initMap(points);
        
        var totalCount = data.map(i => i.count).reduce((c, n) => c + n, 0);
        
        if (totalCount < 10) {
           var area = MapController.toGeoArea(this.map); 
           this.searchItems(area);
        } else {
           this.displayStatistic(data);
        }
    }
    
    private initMap(points : L.LatLng[]) {
        if (!this.map) {
            this.map = new L.Map("mapDiv");
            var layer = new L.TileLayer("http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", 
                {maxZoom: 15, attribution: "&copy; <a href='http://www.openstreetmap.org/copyright'>OpenStreetMap</a> contributors"});
            layer.addTo(this.map);
            this.markers = new L.LayerGroup<L.Marker>();
            this.markers.addTo(this.map);
        }
        
        this.markers.clearLayers(); 

        this.map.off('zoomend', this.populateMap);
        this.map.off('moveend', this.populateMap);
        
        if (this.initialSearch) {
            if (points.length > 0) {
               this.map.fitBounds(L.latLngBounds(points));
            } else {
               this.map.fitWorld(); 
            }
        }
    }
    
    public initialDisplayCallback = (data : GeoStatistic[]) => {
        
        var points = data.map(MapController.getPoint);
        this.initMap(points);

        if (data.length == 1 && data[0].count < 100)  {
            this.searchItems(data[0]);
        } else if (data.length == 1) {
            this.drillDown(data[0]);
        } else if (data.length > 1) {
            this.displayStatistic(data);
        }
    }
    
    private displayStatistic(data : GeoStatistic[]) {
        data.forEach(info => {
            var markerOption = {
                color: 'red',
                fillColor: '#f03',
                fillOpacity: 0.5
            }
            var marker = new L.Circle(MapController.getPoint(info), 500 * info.radius, markerOption);
            var infoIcon = new L.DivIcon({html: '<div>' + info.count + '</div>', className: 'marker-cluster'});
            var marker2 = new L.Marker(MapController.getPoint(info), {icon : infoIcon});
            this.markers.addLayer(marker);
            this.markers.addLayer(marker2);
         });
        
        this.map.on('zoomend', this.populateMap);
        this.map.on('moveend', this.populateMap);
        this.initialSearch = false;
        this.populateTopPictures();
    }
    
    private searchItems(data : GeoArea) {
        var request = new SearchPicturesRequest();
        request.maxAge = 2000 * MillisPerDay;
        request.pageNumber = 0;
        request.pageSize = 20;
        if (this.keyword.length == 0) {
            request.keywords = null;
        } else {
            request.keywords = this.keyword;
        }
        request.longitude = data.longitude;
        request.latitude = data.latitude;
        request.maxDistance = data.radius;
        this.rpcService.call('pictureFacade', "searchPictures", request).then(this.displayDataCallback).catch(errorCallback);
    }
    
    private drillDown(data : GeoStatistic) {
     
        var request = new MapPictureMatchCountRequest();
        request.latitude = data.latitude;
        request.longitude = data.longitude;
        request.radius = data.radius;
        if (this.keyword.length == 0) {
            request.keywords = null;
        } else {
            request.keywords = this.keyword;
        }
        request.maxAge = 2000 * MillisPerDay;
        request.profileId = null;
        this.rpcService.call('pictureFacade', 'mapPictureMatchCount', request).then(this.initialDisplayCallback).catch(errorCallback);
    }
    
    public reloadStatisticData() {
       this.initialSearch = true;
       var request = new MapPictureMatchCountRequest();
        request.latitude = 0;
        request.longitude = 0;
        request.radius = EarthRadius;
        if (this.keyword.length == 0) {
            request.keywords = null;
        } else {
            request.keywords = this.keyword;
        }
        request.maxAge = 2000 * MillisPerDay;
        request.profileId = null;
        this.rpcService.call('pictureFacade', 'mapPictureMatchCount', request).then(this.initialDisplayCallback).catch(errorCallback);
    }
    /*
    public reloadData() {
        var request = new SearchPicturesRequest();
        request.maxAge = 2000 * MillisPerDay;
        request.pageNumber = 0;
        request.pageSize = 20;
        if (this.keyword.length == 0) {
            request.keywords = null;
        } else {
            request.keywords = this.keyword;
        }
        request.longitude = null;
        request.latitude = null;
        request.maxDistance = null;
        this.rpcService.call('pictureFacade', "searchPictures", request).then(this.displayDataCallback).catch(errorCallback);
    }
    */
    public startSearch(keyword : string) {
        this.keyword = keyword;
        this.reloadStatisticData();
    }

    public getSuggestions(input : string) : ng.IPromise<Array<string>> {
        var request = new FindKeywordSuggestionsRequest();
        request.partial = input;
       return this.rpcService.call("pictureFacade", "findKeywordSuggestions", request);
    }
}

class RpcRequest {
   public id : number;
   public jsonrpc = "2.0";
   public method : string;
   public params : any; 
    
   constructor(id : number, method : string, params : any) {
      this.id = id;
       this.method = method;
       this.params = params; 
   }
}

class RpcError {
  public constructor(public code : number, public message : string) {
  }
}

class RpcResponse<T> {
   public id : number;
   public jsonrpc : string;
   public error : RpcError;
   public result : T;
}

interface RpcCallback<T> {
    (data: T): void;
}

class RpcService {
    private id : number = 0;
    
    constructor(private $http : ng.IHttpService, private $q : ng.IQService, private baseUrl : string) {
    }
    
    private successCallback = (arg : ng.IHttpPromiseCallbackArg<RpcResponse<any>>) => {
       if (arg.data.error) {
          return this.$q.reject(arg.data.error); 
       } else {
           return arg.data.result;
       }
    }
    
    private errorCallback = (arg : ng.IHttpPromiseCallbackArg<any>) => {
       return this.$q.reject(new RpcError(arg.status, "Cannot connect"));
    }
    
    public call<T>(service : string, method : string, parameters : any) : ng.IPromise<T> {
        var request = new RpcRequest(this.id++, method, parameters);
        return this.$http.post(this.baseUrl + service, request, {'headers':{'Content-Type': 'application/json'}}).then(this.successCallback, this.errorCallback); 
    }
}

class AppConfig {
    constructor($routeProvider : ng.route.IRouteProvider) {
      $routeProvider.
        when('/view1', {templateUrl: 'partial/view1.html'}).
        when('/view2', {templateUrl: 'partial/view2.html'}).
        otherwise({redirectTo: '/view1' });
    }
}

var app = angular.module('app', ['ngRoute', 'ui.bootstrap']);
app.value('baseUrl', 'http://jcla3ndtozbxyghx.myfritz.net:18789/socialalert-app/rest/');
app.service('rpcService', ['$http', '$q', 'baseUrl', RpcService]);
app.config(['$routeProvider', AppConfig]);
app.controller('MapController', ['rpcService', '$scope', '$compile', MapController]);
app.directive('ensureExpression', ['$http', '$parse', function($http, $parse) {
    return {
        require: 'ngModel',
        link: function(scope, ele, attrs, ngModelController) {
            scope.$watch(attrs.ngModel, function(value) {
                var booleanResult = $parse(attrs.ensureExpression)(scope);
                ngModelController.$setValidity('expression', booleanResult);
            });
        }
    };
}]);

app.directive("thumbnail", function() {
  return {
    restrict: "E",
    replace: false,
    transclude: true,
    templateUrl: "partial/thumbnail.html"
  };
});

function errorCallback(error : RpcError) {
   alert(error.message); 
}
