# Interstellar knowledge dynamics model

## Run the simulation

This command run the main function in 
`./simulations/src/main/kotlin/relativitization/abm/KnowledgeDynamics.kt`:

```shell
./gradlew :simulations:run -PmainClass=relativitization.knowledge.KnowledgeDynamicsKt
```

You can use `-PprocessorCount` and `-PramPercentage` to limit cpu usage and ram usage respectively:

```shell
./gradlew :simulations:run -PmainClass=relativitization.knowledge.KnowledgeDynamicsKt -PprocessorCount=2 -PramPercentage=25
```

## License

The source code is licensed under the [GPLv3 License](./LICENSE.md).

        Copyright (C) 2022-2023  Lai Kwun Hang

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
