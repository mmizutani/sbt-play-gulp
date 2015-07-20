var gulp = require("gulp");
var ngAnnotate = require('gulp-ng-annotate');
var concat = require("gulp-concat");
var uglify = require("gulp-uglify");
var ngHtml2Js = require('gulp-ng-html2js');
var minifyHtml = require('gulp-minify-html');
var plumber = require('gulp-plumber');
var browserSync = require('browser-sync');
var gutil = require('gulp-util');
//var minifyCss = require('gulp-minify-css');

var path = {
  js: ['./app/assets/**/*.js'],
  template: ['./app/assets/**/*.tpl.html'],
  watch: ['app/assets/**/*.*']
};

var handleError = function (err) {
  gutil.beep();
  console.log(err);
  this.emit('end');
};

gulp.task('js:compile', function () {
  gulp.src(path.js)
      .pipe(plumber({errorHandler:handleError}))
      .pipe(ngAnnotate())
      .pipe(concat('app.js'))
      .pipe(gulp.dest('./public/javascripts/'));
});

gulp.task('js:minifyCompile', function () {
  gulp.src(path.js)
      .pipe(plumber({errorHandler:handleError}))
      .pipe(ngAnnotate())
      .pipe(concat('app.min.js'))
      .pipe(uglify())
      .pipe(gulp.dest('./public/javascripts/'));
});

gulp.task('template:compile', function () {
  gulp.src(path.template)
      .pipe(plumber({errorHandler:handleError}))
      .pipe(ngHtml2Js({
        moduleName: 'app.tpl',
        prefix: '/'
      }))
      .pipe(concat('app.tpl.js'))
      .pipe(gulp.dest('./public/javascripts/'));
});

gulp.task('template:minifyCompile', function () {
  gulp.src(path.template)
      .pipe(plumber({errorHandler:handleError}))
      .pipe(minifyHtml({
        empty: true,
        space: true,
        quotes: true
      }))
      .pipe(ngHtml2Js({
        moduleName: 'app.tpl',
        prefix: '/'
      }))
      .pipe(concat('app.tpl.min.js'))
      .pipe(uglify())
      .pipe(gulp.dest('./public/javascripts/'));
});

// Let's watch our LESS files and compile them at each modification
gulp.task('watch', function () {
  gulp.watch(path.watch, ['js:compile', 'template:compile']);
});

gulp.task('serve', function () {
  browserSync({
    // By default, Play is listening on port 9000
    proxy: 'localhost:9000',
    // We will set BrowserSync on the port 9001
    port: 9001,
    // Reload all assets
    // Important: you need to specify the path on your source code
    // not the path on the url
    files: ['public/stylesheets/*.css', 'public/javascripts/*.js', 'app/views/*.html'],
    open: false
  });
});

gulp.task('dist', []);

gulp.task('clean', []);

//gulp.task('test', [
//  'clean:server',
//  'concurrent:test',
//  'autoprefixer',
//  'connect:test',
//  'karma'
//]);

gulp.task('build', [
  'js:compile',
  'template:compile'
]);

gulp.task('default', [
  //'newer:jshint',
  //'test',
  'build'
]);