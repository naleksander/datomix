(ns datomix.examples
  (:use datomix.core))


(qx [:find   {:key ?k :val #{ ?pathElem ... }}
     :in [[?k ?v]]
     :where [ (.endsWith ?k "path")]
     [(.split ?v ";") [?pathElem ...]]
     [(.endsWith ?pathElem ".jar")]]
    (System/getProperties))

(qx [:find #{ ?pathElem ... }
     :in [[?k ?v]]
     :where [ (.endsWith ?k "path")]
     [(.split ?v ";") [?pathElem ...]]
     [(.endsWith ?pathElem ".jar")]]
    (System/getProperties))

(qx [:find    [ ?pathElem ... ]
     :in [[?k ?v]]
     :where [ (.endsWith ?k "path")]
     [(.split ?v ";") [?pathElem ...]]
     [(.endsWith ?pathElem ".jar")]]
    (System/getProperties))

(qx [:find  ?k ?pathElem
     :in [[?k ?v]]
     :where [ (.endsWith ?k "path")]
     [(.split ?v ";") [?pathElem ...]]
     [(.endsWith ?pathElem ".jar")]]
    (System/getProperties))

(qx [:find  ?k
     :in [[?k ?v]]
     :where [ (.endsWith ?k "path")]
     [(.split ?v ";") [?pathElem ...]]
     [(.endsWith ?pathElem ".jar")]]
    (System/getProperties))

(datomic.api/q '[:find  ?k ?pathElem
                 :in [[?k ?v]]
                 :where [ (.endsWith ?k "path")]
                 [(.split ?v ";") [?pathElem ...]]
                 [(.endsWith ?pathElem ".jar")]]
               (System/getProperties))