# play-gulp-demo
A demo application for the [SBT Play Gulp plugin](http://www.github.com/mmizutani/sbt-play-gulp).

## How to Run

1. Install the prerequisites:
```bash
$ npm install -g yo gulp bower
```

and a Yeoman frontend generator you like:

```bash
$ npm install -g generator-gulp-angular
```

2. Clone this sample Play app project and populate a frontend template in the ui sub-directory:
```bash
$ git clone https://github.com/mmizutani/sbt-play-gulp.git
$ cd play-gulp-sample/ui
$ yo gulp-angular
```

3. Make sure that you import the sbt-play-gulp plugin:
```bash
$ cd ../
$ echo 'addSbtPlugin("com.github.mmizutani" % "sbt-play-gulp" % "0.1.0")' >> project/plugins.sbt
```


```bash
$ sbt
```

```bash
> compile
> run
```

```bash
> testProd
```

```bash
> ;clean;stage;dist
```
