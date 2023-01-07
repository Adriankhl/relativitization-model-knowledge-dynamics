# Knowledge dynamics model example

## Run the simulation

If you haven't cloned the [Relativitization](https://github.com/Adriankhl/relativitization) repo,
go up one level of the directory tree and clone it:

```
cd ..
git clone https://github.com/Adriankhl/relativitization.git

```

Run the `createModelBase` task in the `relativitization` directory to extract all essential files to 
`relativitization/relativitization-model-base`:

```
cd relativitization
./gradlew createModelBase
```

Copy all essential files from Relativitization:

```
cd ../relativitization-model-knowledge-dynamics
cp -rT ../relativitization/relativitization-model-base/ .
```

Note that it also copies the `.gitignore` file to this directory. If you don't want to copy the
hidden file, run `cp -r ../relativitization/relativitization-model-base/* .` instead.

Now you can open this project with your favourite IDE, or you can run simulations directly on
your terminal. This command run the main function in 
`./simulations/src/main/kotlin/relativitization/abm/KDScan1,kt`:

```
./gradlew :simulations:run -PmainClass=relativitization.abm.KDScan1Kt
```

You can use `-PprocessorCount` and `-PramPercentage` to limit cpu usage and ram usage respectively:

```
./gradlew :simulations:run -PmainClass=relativitization.abm.KDScan1Kt -PprocessorCount=2 -PramPercentage=25
```
