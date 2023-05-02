# Interstellar knowledge dynamics model

## Run the simulation

This command run the main function in 
`./simulations/src/main/kotlin/relativitization/abm/KnowledgeDynamics.kt`:

```
./gradlew :simulations:run -PmainClass=relativitization.knowledge.KnowledgeDynamicsKt
```

You can use `-PprocessorCount` and `-PramPercentage` to limit cpu usage and ram usage respectively:

```
./gradlew :simulations:run -PmainClass=relativitization.knowledge.KnowledgeDynamicsKt -PprocessorCount=2 -PramPercentage=25
```
