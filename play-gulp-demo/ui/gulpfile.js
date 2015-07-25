'use strict';

var gulp = require('gulp');
//var plugins = require('gulp-load-plugins')();
var bowerFiles = require('main-bower-files');
var inject = require('gulp-inject');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var changed = require('gulp-changed');
var plumber = require('gulp-plumber');
var ngAnnotate = require('gulp-ng-annotate');
var ngHtml2Js = require('gulp-ng-html2js');
var minifyHtml = require('gulp-minify-html');
var jshint = require('gulp-jshint');
var rev = require('gulp-rev');
var minifyCss = require('gulp-minify-css');
var sourcemaps = require('gulp-sourcemaps');
var autoprefixer = require('gulp-autoprefixer');
var sass = require('gulp-sass');
var imagemin = require('gulp-imagemin');
var svgmin = require('gulp-svgmin');
var browserSync = require('browser-sync');
var gutil = require('gulp-util');
var del = require('del');
var runSequence = require('run-sequence'); // Temporary solution until gulp 4 release https://github.com/gulpjs/gulp/issues/355
var merge = require('event-stream').merge;

/**
 * newer jshint
 * test karma
 * rev
 * usemin
 * add plumber to all
 **/

var dirs = {
  app: './app',
  tmp: './.tmp',
  dist: './dist'
};

var paths = {
  template: {
    src: [dirs.app+'/*.html', dirs.app+'/views/**/*.html'],
    dest: dirs.dist
  },
  js: {
    src: dirs.app+'/scripts/**/*.js',
    dest: dirs.dist+'/scripts'
  },
  jstest: 'test/spec/**/*.js',
  css: {
    src: dirs.app+'/styles/**/*.css',
    tmp: dirs.tmp+'/styles',
    dest: dirs.dist+'/styles'
  },
  sass: {
    src: dirs.app+'/styles/**/*.sass',
    dest: dirs.app+'/styles'
  },
  jshint: ['gulpfile.js', dirs.app+'/scripts/{,*/}*.js'],
  index: dirs.app+'/index.html',
  image: {
    src: dirs.app+'/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}',
    dest: dirs.dist+'/images'
  },
  svg: {
    src: dirs.app+'/images/**/*.svg',
    dest: dirs.dist+'/images'
  }
};

var handleError = function (err) {
  gutil.beep();
  console.log(err);
  this.emit('end');
};

gulp.task('clean', del.bind(null, [dirs.tmp, dirs.dist], {force:true}));
gulp.task('clean2', function(cb) {
  del([dirs.tmp, dirs.dist], {force:true}, cb);
});

gulp.task('index', function () {
  return gulp.src(paths.index)
    .pipe(plumber({errorHandler: handleError}))
    // It's not necessary to read the files (will speed up things), we're only after their paths:
    .pipe(inject(gulp.src([paths.js.src, paths.css.src], {read: false}), {relative: true}))
    .pipe(inject(gulp.src(bowerFiles(), {read: false}), {name: 'bower'}))
    .pipe(gulp.dest('./app'));
});

gulp.task('index:dist', function () {
  return gulp.src(paths.index)
    .pipe(uglify())
    .pipe(gulp.dest(dirs.dist));
});

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

gulp.task('jshint', function () {
  return gulp.src(paths.jshint)
    .pipe(plumber({errorHandler: handleError}))
    .pipe(jshint('.jshintrc'))
    .pipe(jshint.reporter('jshint-stylish'))
    .pipe(jshint.reporter('fail'));
});

gulp.task('js', ['clean'], function () {
  return gulp.src(paths.js.src)
    .pipe(plumber({errorHandler: handleError}))
    .pipe(sourcemaps.init())
    .pipe(changed(paths.js.dest))
    .pipe(ngAnnotate())
    .pipe(concat('app.min.js'))
    .pipe(uglify())
    .pipe(sourcemaps.write())
    .pipe(gulp.dest(paths.js.dest));
});

gulp.task('foo', function() {
  return merge(
    gulp.src('src/*.coffee')
      //.pipe(coffee())
      .pipe(gulp.dest('js/')),
    gulp.src('src/*.less')
      //.pipe(less())
      .pipe(gulp.dest('css/'))
  );
});

gulp.task('css:tmp', ['sass'], function () {
  return gulp.src(paths.css.src)
    .pipe(plumber({errorHandler: handleError}))
    .pipe(changed(paths.css.tmp))
    .pipe(autoprefixer({browsers: ['last 1 versions']}))
    .pipe(gulp.dest(paths.css.tmp))
    .pipe(browserSync.reload({stream:true}));
});

gulp.task('css:dist', ['sass'], function () {
  return gulp.src(paths.css.src)
    .pipe(plumber({errorHandler: handleError}))
    .pipe(sourcemaps.init())
    .pipe(autoprefixer({browsers: ['last 1 versions']}))
    .pipe(concat('all.css'))
    .pipe(minifyCss())
    .pipe(sourcemaps.write())
    .pipe(gulp.dest(paths.css.dest));
});

// Compile sass into CSS & auto-inject into browsers
gulp.task('sass', function() {
  return gulp.src(paths.sass.src)
    .pipe(plumber({errorHandler: handleError}))
    .pipe(sourcemaps.init())
    .pipe(sass({outputStyle:'compressed'}).on('error', sass.logError)) // nested, compact, compressed, expanded
    .pipe(sourcemaps.write())
    .pipe(gulp.dest(paths.sass.dest));
});

gulp.task('image', function () {
  return gulp.src(paths.image.src)
    .pipe(plumber({errorHandler: handleError}))
    .pipe(imagemin({optimizationLevel: 5}))
    .pipe(rev())
    .pipe(gulp.dest(paths.image.dest));
});

gulp.task('svgmin', function () {
  return gulp.src(paths.svg.src)
    .pipe(plumber({errorHandler: handleError}))
    .pipe(svgmin())
    .pipe(gulp.dest(paths.svg.dest));
});

gulp.task('misc', function () {
  return gulp.src([
    '*.{ico,png,txt}',
    '.htaccess',
    '*.html',
    'views/{,*/}*.html',
    'bower_components/**/*',
    'images/{,*/}*.{webp}',
    'fonts/*'
  ], {
    cwd: dirs.app, // base directory to copy files from
    base: dirs.app, // needed to keep directory structures (https://github.com/gulpjs/gulp/issues/151#issuecomment-41508551)
    dot: true // include hidden files
  })
    .pipe(gulp.dest(dirs.dist));
});

// Triggered by sbt run command
gulp.task('watch', function () {
  gulp.watch([paths.template.src, paths.image.src], browserSync.reload);
  gulp.watch(['gulpfile.js', paths.js.src], ['jshint'], browserSync.reload);
  //gulp.watch(paths.jstest, ['jstest']);
  gulp.watch(paths.css.src, ['css:tmp']);
});

gulp.task('serve', ['jshint', 'css:tmp', 'sass'], function () {
  browserSync({
    server: {
      baseDir: './app'
    }
  });
  gulp.watch([paths.template.src, paths.image.src], browserSync.reload);
  gulp.watch(['gulpfile.js', paths.js.src], ['jshint'], browserSync.reload);
  //gulp.watch(paths.jstest, ['jstest']);
  gulp.watch(paths.css.src, ['css:tmp']);
  gulp.watch(paths.sass, ['sass']);
});

//gulp.task('test', [
//  'clean:server',
//  'concurrent:test',
//  'autoprefixer',
//  'connect:test',
//  'karma'
//]);

//gulp.task('build', [
//  'clean',
//  'index',
//  'template',
//  'js',
//  'css',
//  'svgmin',
//  'image',
//  'misc:dist:'
//  //'clean:dist',
//  //'bower-install',
//  //'useminPrepare',
//  //'concurrent:dist',
//  //'autoprefixer',
//  //'concat',
//  //'ngmin',
//  //'copy:dist',
//  //'cdnify',
//  //'cssmin',
//  //'uglify',
//  //'rev',
//  //'usemin'
//]);

// Triggered by sbt compile, stage and dist commands
gulp.task('build', function (done) {
  runSequence(
    'clean',
    [
      'index:dist',
      'template',
      'js',
      'css:dist',
      'image',
      'svgmin',
      'misc'
    ],
    done);
});

gulp.task('default', ['build']);