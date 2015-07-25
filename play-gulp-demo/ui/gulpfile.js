'use strict';

var gulp = require("gulp");
var inject = require('gulp-inject');
var concat = require("gulp-concat");
var uglify = require("gulp-uglify");
var ngAnnotate = require('gulp-ng-annotate');
var ngHtml2Js = require('gulp-ng-html2js');
var minifyHtml = require('gulp-minify-html');
//var minifyCss = require('gulp-minify-css');
var jshint = require('gulp-jshint');
var rev = require('gulp-rev');
var imagemin = require('gulp-imagemin');
var svgmin = require('gulp-svgmin');
var sourcemaps = require('gulp-sourcemaps');
var autoprefixer = require('gulp-autoprefixer');
var plumber = require('gulp-plumber');
var browserSync = require('browser-sync');
var gutil = require('gulp-util');
var del = require('del');

var yeoman = {
  // configurable paths
  app: require('./bower.json').appPath || 'app',
  dist: 'dist'
};

var paths = {
  template: {
    src: ['<%= yeoman.app %>/*.html', '<%= yeoman.app %>/views/{,*/}*.html'],
    dest: ['<%= yeoman.dist %>']
  },
  js: {
    src: ['<%= yeoman.app %>/scripts/{,*/}*.js'],
    dest: ['<%= yeoman.dist %>/scripts']
  },
  jstest: ['test/spec/{,*/}*.js'],
  css: {
    src: ['<%= yeoman.app %>/styles/{,*/}*.css'],
    dest: ['<%= yeoman.dist %>/styles']
  },
  lint: ['gulpfile.js', '<%= yeoman.app %>/scripts/{,*/}*.js'],
  indexhtml: {
    src: ['<%= yeoman.app %>/index.html'],
    dest: ['<%= yeoman.dist %>']
  },
  image: {
    src: ['<%= yeoman.app %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}'],
    dest: ['<%= yeoman.dist %>/images']
  },
  svg: {
    src: ['<%= yeoman.app %>/images/{,*/}*.svg'],
    dest: ['<%= yeoman.dist %>/images']
  },
  watch: ['<%= yeoman.app %>/{,*/}*.html', '<%= yeoman.app %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}']
};

var handleError = function (err) {
  gutil.beep();
  console.log(err);
  this.emit('end');
};

gulp.task('clean', function (cb) {
  del(['<%= yeoman.dist %>/*'], cb);
});

gulp.task('template', function () {
  gulp.src(paths.template.src)
    .pipe(plumber({errorHandler: handleError}))
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
    .pipe(gulp.dest(paths.template.dest));
});

gulp.task('bowerinject', function () {
  gulp.src(paths.indexhtml.src)
    // It's not necessary to read the files (will speed up things), we're only after their paths:
    .pipe(inject(gulp.src([paths.js, paths.css], {read: false})))
    .pipe(gulp.dest(paths.indexhtml.dest));
});

gulp.task('lint', function () {
  gulp.src(paths.lint)
    .pipe(jshint())
    .pipe(jshint.reporter('jshint-stylish'));
});

gulp.task('js', ['clean'], function () {
  gulp.src(paths.js.src)
    .pipe(plumber({errorHandler: handleError}))
    //.pipe(coffee())
    .pipe(changed(paths.js.dest))
    .pipe(ngAnnotate())
    .pipe(concat('app.min.js'))
    .pipe(uglify())
    .pipe(gulp.dest(paths.js.dest));
});

gulp.task('css', function () {
  gulp.src(paths.css.src)
    .pipe(sourcemaps.init())
    .pipe(autoprefixer({browsers: ['last 1 versions']}))
    .pipe(concat('all.css'))
    .pipe(sourcemaps.write('.'))
    .pipe(gulp.dest(paths.css.dest));
});

gulp.task('css:svgmin', function () {
  gulp.src(paths.svg.src)
    .pipe(svgmin())
    .pipe(gulp.dest(paths.svg.dest));
});

gulp.task('image', ['clean'], function () {
  gulp.src(paths.image.src)
    .pipe(imagemin({optimizationLevel: 5}))
    .pipe(rev())
    .pipe(gulp.dest(paths.image.dest));
});

gulp.task('watch', function () {
  gulp.watch(paths.js.src, ['js']);
  gulp.watch(paths.image.src, ['image']);
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

//gulp.task('test', [
//  'clean:server',
//  'concurrent:test',
//  'autoprefixer',
//  'connect:test',
//  'karma'
//]);

gulp.task('build', [
  'clean',
  'bowerinject',
  'template',
  'js',
  'css',
  'css:svgmin',
  'image'
  //'clean:dist',
  //'bower-install',
  //'useminPrepare',
  //'concurrent:dist',
  //'autoprefixer',
  //'concat',
  //'ngmin',
  //'copy:dist',
  //'cdnify',
  //'cssmin',
  //'uglify',
  //'rev',
  //'usemin'
]);

gulp.task('default', [
  //'newer:jshint',
  //'test',
  'build'
]);