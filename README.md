# spammer

A Clojure application

See [doc/intro.md](doc/intro.md)

## Note:

The system at this version will generate a number of test case email records and pass them to a process-input funcion will will return a set of email records to send to. The assumption here is that this set will be passed to a system for actual emailing. No emails are sent with this version.

## Usage

- lein run [NUM]

The parameter NUM tells the system how many test cases to generate. The default if not present is 10.


## License

Copyright © 2017 Brad Lucas

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
