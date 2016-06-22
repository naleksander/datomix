# datomix

Shape query results in way you satisfy. Instead plain list of values:

        (datomic.api/q '[:find  ?k ?pathElem
                         :in [[?k ?v]]
                         :where [ (.endsWith ?k "path")]
                         [(.split ?v ";") [?pathElem ...]]
                         [(.endsWith ?pathElem ".jar")]]
                       (System/getProperties))


You can request the result as map:

        (qx [:find   {:key ?k :val #{ ?pathElem ... }}
             :in [[?k ?v]]
             :where [ (.endsWith ?k "path")]
             [(.split ?v ";") [?pathElem ...]]
             [(.endsWith ?pathElem ".jar")]]
            (System/getProperties))

## License

Copyright © 2016 Aleksander

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
