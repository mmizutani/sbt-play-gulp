(function(module) {
try {
  module = angular.module('app.tpl');
} catch (e) {
  module = angular.module('app.tpl', []);
}
module.run(['$templateCache', function($templateCache) {
  $templateCache.put('/main/main-index.tpl.html',
    '<div class="container">\n' +
    '  <div class="col-sm-12">\n' +
    '    <div class="page-header">\n' +
    '      <h1>Hello main-index!</h1>\n' +
    '      <p>helohelohelo......</p>\n' +
    '      <p>nihaonihaonihao......</p>\n' +
    '    </div>\n' +
    '    <a class="btn btn-default" ui-sref="main-sub">Go to main-sub!</a>\n' +
    '  </div>\n' +
    '</div>\n' +
    '');
}]);
})();

(function(module) {
try {
  module = angular.module('app.tpl');
} catch (e) {
  module = angular.module('app.tpl', []);
}
module.run(['$templateCache', function($templateCache) {
  $templateCache.put('/main/main-sub.tpl.html',
    '<div class="container">\n' +
    '  <div class="col-sm-12">\n' +
    '    <div class="page-header">\n' +
    '      <h1>Hello main-sub!</h1>\n' +
    '    </div>\n' +
    '    <a class="btn btn-default" ui-sref="main-index">Go to main-index!</a>\n' +
    '  </div>\n' +
    '</div>\n' +
    '');
}]);
})();
