
var MillisPerDay : number = 1000 * 60 * 60 * 24;

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

class MapWrapper {
     private map : L.Map;
    
    constructor() {
    }
    
    static getLocation(info : PictureInfo) {
       return new L.LatLng(info.pictureLatitude, info.pictureLongitude);
    }
    
    static hasLocation(info : PictureInfo) {
       if (info.pictureLongitude && info.pictureLatitude) {
          return true; 
       } else {
          return false; 
       }
    }
    
    public displayDataCallback(data: QueryResult<PictureInfo>) : void {
        this.map = new L.Map("map");
        var layer = new L.TileLayer("http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", 
            {maxZoom: 15, attribution: "&copy; <a href='http://www.openstreetmap.org/copyright'>OpenStreetMap</a> contributors"});
        layer.addTo(this.map);
        
        var points = data.content.filter(MapWrapper.hasLocation).map(MapWrapper.getLocation);
        this.map.fitBounds(L.latLngBounds(points));
        data.content.filter(MapWrapper.hasLocation).forEach(info => {
            var marker = new L.Marker(MapWrapper.getLocation(info));
            marker.addTo(this.map).bindPopup("<b>" + info.title + "</b><img src='http://jcla3ndtozbxyghx.myfritz.net:18789/socialalert-app/thumbnail/" + info.pictureUri + "' height=220 width=300/>");
        });
    }
}

class MapController {
    
    constructor(private map : MapWrapper) {
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
    
    constructor(private $http : ng.IHttpService, private baseUrl : string) {
    }
    
    public call<T>(service : string, method : string, parameters : any, success : RpcCallback<T>, error : RpcCallback<RpcError>) : void {
        var request = new RpcRequest(this.id++, method, parameters);
        this.$http.post(this.baseUrl + service, request, {'headers':{'Content-Type': 'application/json'}}).
            success((data : RpcResponse<T>) => {if (data.error) error(data.error); else success(data.result);}).
            error((data: any, status: number) =>  error(new RpcError(status, "Cannot connect")));
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

var app = angular.module('app', ['ngRoute']);
app.value('baseUrl', 'http://jcla3ndtozbxyghx.myfritz.net:18789/socialalert-app/rest/');
app.service('mapWrapper', MapWrapper);
app.service('rpcService', ['$http', 'baseUrl', RpcService]);
app.config(['$routeProvider', AppConfig]);
app.controller('MapController', ['mapWrapper', MapController]);
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
    replace: true,
    templateUrl: "thumbnail.html"
  };
});

function errorCallback(error : RpcError) {
   alert(error.message); 
}

app.run(['rpcService', 'mapWrapper', function(rpcService : RpcService, mapWrapper : MapWrapper) {
    var request : SearchPicturesInCategoryRequest = new SearchPicturesInCategoryRequest();
    request.maxAge = 720 * MillisPerDay;
    request.category = 'ART';
    request.pageNumber = 0;
    request.pageSize = 20;
    request.keywords = null;
    request.longitude = null;
    request.latitude = null;
    request.maxDistance = null;
    rpcService.call('pictureFacade', "searchPicturesInCategory", request, mapWrapper.displayDataCallback, errorCallback);
}]);
