'use strict';

var gulp = require('gulp');
var inject = require('gulp-inject');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var changed = require('gulp-changed');
var ngAnnotate = require('gulp-ng-annotate');
var ngHtml2Js = require('gulp-ng-html2js');
var minifyHtml = require('gulp-minify-html');
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

/**
 * newer jshint
 * test
 * rev
 **/

var dirs = {
  app: './app',
  dist: './dist'
};

var paths = {
  template: {
    src: [dirs.app+'/*.html', dirs.app+'/views/{,*/}*.html'],
    dest: dirs.dist
  },
  js: {
    src: [dirs.app+'/scripts/{,*/}*.js'],
    dest: dirs.dist+'/scripts'
  },
  jstest: ['test/spec/{,*/}*.js'],
  css: {
    src: [dirs.app+'/styles/{,*/}*.css'],
    dest: dirs.dist+'/styles'
  },
  lint: ['gulpfile.js', dirs.app+'/scripts/{,*/}*.js'],
  indexhtml: {
    src: [dirs.app+'/index.html'],
    dest: dirs.dist
  },
  image: {
    src: [dirs.app+'/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}'],
    dest: dirs.dist+'/images'
  },
  svg: {
    src: [dirs.app+'/images/{,*/}*.svg'],
    dest: dirs.dist+'/images'
  },
  watch: [dirs.app+'/{,*/}*.html', dirs.app+'/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}']
};

var handleError = function (err) {
  gutil.beep();
  console.log(err);
  this.emit('end');
};

gulp.task('clean', del.bind(null, [dirs.dist]));
//gulp.task('clean', function (cb) {
//  del([dirs.dist], cb);
//});

gulp.task('template', function () {
  return gulp.src(paths.template.src)
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
  return gulp.src(paths.indexhtml.src)
    .pipe(plumber({errorHandler: handleError}))
    // It's not necessary to read the files (will speed up things), we're only after their paths:
    .pipe(inject(gulp.src([paths.js.src, paths.css.src], {read: false})))
    .pipe(gulp.dest(paths.indexhtml.dest));
});

gulp.task('lint', function () {
  return gulp.src(paths.lint)
    .pipe(jshint('.jshintrc'))
    .pipe(jshint.reporter('jshint-stylish'));
});

gulp.task('js', ['clean'], function () {
  return gulp.src(paths.js.src)
    .pipe(plumber({errorHandler: handleError}))
    .pipe(sourcemaps.init())
    .pipe(changed(paths.js.dest))
    .pipe(ngAnnotate())
    .pipe(concat('app.min.js'))
    .pipe(uglify())
    .pipe(sourcemaps.write('.'))
    .pipe(gulp.dest(paths.js.dest));
});

gulp.task('css', function () {
  return gulp.src(paths.css.src)
    .pipe(plumber({errorHandler: handleError}))
    .pipe(sourcemaps.init())
    .pipe(autoprefixer({browsers: ['last 1 versions']}))
    .pipe(concat('all.css'))
    .pipe(sourcemaps.write('.'))
    .pipe(gulp.dest(paths.css.dest));
});

gulp.task('css:svgmin', function () {
  return gulp.src(paths.svg.src)
    .pipe(svgmin())
    .pipe(gulp.dest(paths.svg.dest));
});

gulp.task('image', ['clean'], function () {
  return gulp.src(paths.image.src)
    .pipe(plumber({errorHandler: handleError}))
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