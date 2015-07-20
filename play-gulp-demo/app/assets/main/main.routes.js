angular.module('app').config(function ($stateProvider) {

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

});
