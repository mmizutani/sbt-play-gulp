var gulp = require('gulp');

gulp.task('clean');
gulp.task('build');
gulp.task('test');
gulp.task('watch');
gulp.task('default', ['clean', 'build']);