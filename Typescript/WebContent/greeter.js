var Customer = (function () {
    function Customer(name, city) {
        this.name = name;
        this.city = city;
    }
    return Customer;
})();
var globalCustomerData = [
    new Customer("Dave Jones", "Phoenix"),
    new Customer("Jamie Riley", "Atlanta"),
    new Customer("Heedy Wahlin", "Chandler"),
    new Customer("Thomas Winter", "Seattle")
];
var SimpleController = (function () {
    function SimpleController($scope, $location) {
        this.$scope = $scope;
        this.$location = $location;
        this.customers = globalCustomerData;
        this.newName = "initial";
        this.newCity = "Bern";
    }
    SimpleController.prototype.isExistingCustomer = function (name) {
        return globalCustomerData.some(function (c) { return c.name == name; });
    };
    SimpleController.prototype.addCustomer = function () {
        globalCustomerData.push(new Customer(this.newName, this.newCity));
        this.$location.url('/view1');
    };
    SimpleController.prototype.deleteCustomer = function (customer) {
        globalCustomerData = globalCustomerData.filter(function (c) { return c !== customer; });
        this.customers = globalCustomerData;
    };
    return SimpleController;
})();
var JsonRpcService = (function () {
    function JsonRpcService($http) {
        this.$http = $http;
        this.id = 0;
    }
    JsonRpcService.prototype.call = function (url, method, parameters) {
        var data = { "jsonrpc": "2.0", "method": method, "params": parameters, "id": this.id++ };
        return this.$http.get(url);
    };
    return JsonRpcService;
})();
var Config = (function () {
    function Config($routeProvider) {
        $routeProvider.when('/view1', { templateUrl: 'partial/view1.html' }).when('/view2', { templateUrl: 'partial/view2.html' }).otherwise({ redirectTo: '/view1' });
    }
    return Config;
})();
var app = angular.module("app", ['ngRoute']);
app.service('jsonRpc', ['$http', JsonRpcService]);
app.config(['$routeProvider', Config]);
app.controller("SimpleController", ['$scope', '$location', SimpleController]);
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
app.run(['jsonRpc', function (jsonRpc) {
    jsonRpc.call('http://localhost:8080/Typescript/data.json', 'test', []).success(function (data) {
        globalCustomerData = data;
    }).error(function (data, status) {
        alert(status);
    });
}]);
