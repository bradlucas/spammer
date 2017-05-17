# spammer

A Clojure application

See [doc/intro.md](doc/intro.md)

### Note:

The system genearates test email records containing email address and spam-score. These are processed with process-input and a set of email addresses to send to is generated.
The assumption here is that this set will be passed to another process or system system for actual emailing. This is not implemented here and no emails are actually sent.

## Usage


- lein run [NUM]

Or

- lein uberjar
- java -jar target/spammer-VERSION-standalone.jar [NUM]

The parameter NUM tells the system how many test cases to generate. The default if not present is 10.


```
--------------------------------------------------------------------------------
Processed  100  sample email data records
--------------------------------------------------------------------------------
Running total:      2.835467881792283
Running count:      59
Running mean:       0.048058777657496325
Recent 100 mean:    0.048058777657496325
Sent-emails count:  59
--------------------------------------------------------------------------------
68J1972mPhQm6ER9JBx5d7@glassbore.com
zML5@glassbore.com
l8llbBAB5601@glassbore.com
snubd298kZ3@careershiller.com
NeW02t@glassbore.com
334n85I6mB5@lice.com
bg4fak3AUZXfch6cIdVGyx1Tw@careershiller.com
3ui5U2f4F2vX34@monstrous.com
1vE6Encl8O6737@dired.com
sCu3I83cRQ@linkedarkpattern.com
....
```


## License

Copyright © 2017 Brad Lucas

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
