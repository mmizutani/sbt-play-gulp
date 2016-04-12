# play-gulp-demo
A demo application for the [SBT Play Gulp plugin](http://www.github.com/mmizutani/sbt-play-gulp).

## How to Run

```
$ npm install -g yo gulp bower
```

```
$ npm install -g generator-gulp-angular
```

```
$ git clone git@github.com:mmizutani/play-gulp-demo.git
$ cd play-gulp-demo/ui
$ yo gulp-angular
```

```
$ cd ../
$ sbt
```

```
> compile
> run
```

```
> testProd
```

```
> ;clean;stage;dist
```
