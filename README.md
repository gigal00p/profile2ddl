# profile2ddl

This program converts output created by runnig `xsv stats` against csv file into sql `create table` file.
You can find xsv [here](https://github.com/BurntSushi/xsv).

## Installation

Tou must have [Leiningen](https://leiningen.org/) installed. Once done clone this repo and run `lein uberjar` inside it.
Under target/uberjar you'll find standalone jar to run.

## Usage

    $ java -jar profile2ddl-0.1.0-standalone.jar [args]

## Options

`-i PATH` - path with csv profile files created by `xsv stats`
`-o PATH` - path where the ddl files will be written

## Examples

`java -jar profile2ddl-0.1.0-SNAPSHOT-standalone.jar -i ~/code/csv/profiles -o ~/code/csv/ddl`

## License

Copyright © 2019 Krzysztof Walkiwicz

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
