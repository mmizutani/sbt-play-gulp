'use strict';

var app = angular.module('app', ['app.tpl', 'ui.router', 'hc.marked', 'toaster']);

app.config(["$httpProvider", function ($httpProvider) {

  // $http を利用した通信に、必ず X-Requested-With ヘッダーを付ける。
  // JSON 関連の攻撃対策
  //   - 直接ブラウズ
  //   - JSON ハイジャック
  //   - UTF-7
  // see: 3分で分かるAngularJSセキュリティ - teppeis blog
  //      http://teppeis.hatenablog.com/entry/2013/12/angularjs-security
  $httpProvider.defaults.headers.common = {
    'X-Requested-With' : 'XMLHttpRequest'
  };

}])

.config(["$locationProvider", "$stateProvider", "$urlRouterProvider", function ($locationProvider, $stateProvider, $urlRouterProvider) {

  $locationProvider.html5Mode({
    enabled: true,
    requireBase: false
  });

}]);

angular.module('app').controller('main-index.controller', ["$scope", function($scope) {

  console.log('Hello main-index.controller!');
  alert('attention');

}]);

angular.module('app').controller('main-sub.controller', ["$scope", function ($scope) {

  console.log('Hello main-sub.controller!');

}]);

angular.module('app').config(["$stateProvider", function ($stateProvider) {

  $stateProvider
    .state({
      name: 'main-index',
      url: '/',
      templateUrl: '/main/main-index.tpl.html',
      controller: 'main-index.controller'
    })
    .state({
      name: 'main-sub',
      url: '/sub',
      templateUrl: '/main/main-sub.tpl.html',
      controller: 'main-sub.controller'
    });

}]);
