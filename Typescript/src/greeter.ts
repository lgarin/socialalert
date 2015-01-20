/*
var map = new L.Map("map");
map.setView(new L.LatLng(51.505, -0.09), 14);

var layer = new L.TileLayer("http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", 
    {maxZoom: 18,
     attribution: "&copy; <a href='http://www.openstreetmap.org/copyright'>OpenStreetMap</a> contributors"});
layer.addTo(map); 

var marker = new L.Marker(new L.LatLng(51.5, -0.09));
marker.addTo(map).bindPopup("<b>Hello world!</b><br />I am a popup."); 

// popup on mapclick
var popup = new L.Popup();

function onMapClick(e) {
    popup.setLatLng(e.latlng).setContent("You clicked the map at " + e.latlng.toString()).openOn(map);
}
 
map.on("click", onMapClick); 
*/
class Customer {
    constructor(public name : string, public city : string) {}
}

var globalCustomerData : Customer[] = [
    new Customer("Dave Jones", "Phoenix"),
    new Customer("Jamie Riley", "Atlanta"),
    new Customer("Heedy Wahlin", "Chandler"),
    new Customer("Thomas Winter", "Seattle")
];

class SimpleController {
  constructor( private $scope, private $location) {
       // $validator.uniqueCustomer = (modelValue, viewValue) => { return !globalCustomerData.some(c => c.name == viewValue); };
  }
    
  public customers = globalCustomerData;
  public newName : string = "initial";
  public newCity : string = "Bern";
    
  public isExistingCustomer(name : string) {
    return globalCustomerData.some(c => c.name == name);
  }
    
  public addCustomer() {
      globalCustomerData.push(new Customer(this.newName, this.newCity));
      this.$location.url('/view1');
  }
    
  public deleteCustomer(customer : Customer) {
      globalCustomerData = globalCustomerData.filter(c => c !== customer);
      this.customers = globalCustomerData;
  }
}

class JsonRpcService {
    private id : number = 0;
    
    constructor(private $http : ng.IHttpService) {
    }
    
    public call<T>(url : string, method : string, parameters : any) : ng.IHttpPromise<T> {
        var data = {"jsonrpc": "2.0", "method": method, "params": parameters, "id" : this.id++};
        //return this.$http.post(url, data, {'headers':{'Content-Type': 'application/json'}});
        return this.$http.get(url);
    }
}

class Config {
    constructor($routeProvider : ng.route.IRouteProvider) {
      $routeProvider.
        when('/view1', {templateUrl: 'partial/view1.html'}).
        when('/view2', {templateUrl: 'partial/view2.html'}).
        otherwise({redirectTo: '/view1' });
    }
}


var app = angular.module("app", ['ngRoute']);
app.service('jsonRpc', ['$http', JsonRpcService]);
app.config(['$routeProvider', Config]);
app.controller("SimpleController", ['$scope', '$location', SimpleController]);
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
app.run(['jsonRpc', function(jsonRpc : JsonRpcService) {
    jsonRpc.call('http://localhost:8080/Typescript/data.json', 'test', []).
        success((data: Customer[]) => { globalCustomerData = data; } ).
        error((data: any, status: number) => { alert(status); });
}]);
