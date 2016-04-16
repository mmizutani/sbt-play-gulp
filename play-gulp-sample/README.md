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

3. Move bower_components directory from ui to ui/src and adjust the paths in two config files accordingly:
```bash
$ mv bower_components src
$ sed -e 's/"bower_components"/"src\/bower_components"/' .bowerrc
$ sed -e 's/\x27bower_components\x27/\x27src\/bower_components\x27/' gulp/conf.js
```

4. Make sure that you import the latest sbt-play-gulp plugin:
```bash
$ cd ../
$ echo 'addSbtPlugin("com.github.mmizutani" % "sbt-play-gulp" % "0.1.1")' >> project/plugins.sbt
```

5. Add settings specific to the sbt-play-gulp plugin in build.sbt:
```bash
import com.github.mmizutani.sbt.gulp.PlayGulpPlugin

...

PlayGulpPlugin.playGulpSettings ++ PlayGulpPlugin.withTemplates
```

or in project/Build.scala:
```bash
import com.github.mmizutani.sbt.gulp.PlayGulpPlugin
lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(PlayGulpPlugin.playGulpSettings: _*)
  .settings(PlayGulpPlugin.withTemplates: _*)
```

6. Now you can build and run the Play app including the Gulp frontend project in the ui directory:
```bash
$ sbt
> update
> compile
> run
> test
> testProd
> ;clean;stage;dist
```
  In the background, the Gulp taskrunner builds and packages your frontend part all the way through this Play app workflow.