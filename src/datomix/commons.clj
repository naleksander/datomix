(ns datomix.commons
  (:require [clojure.set]))


(defn datamic-var?
  "Is symbol a datomic variable"
  [ a ]
  (and (symbol? a) (.startsWith (name a) "?")))

(defn is?
  "Returns true if a value is not null"
  [ a ]
  (not (nil? a)))


(defn col?
  "Checks whenever the input collection is a no-associative container structure"
  [ coll ]
  (or (vector? coll) (set? coll) (seq? coll)))

(defn seek?
  "Checks whenever the value is seekable"
  [ v ]
  (or (map? v) (col? v)))

(defn scalar?
  "Not a collection nor map"
  [v]
  (not (seek? v)))

(defn del
  "Removes elements from coll which can be set, vector, list, map or string"
  [ coll & rest ]
  (let [ [ w & tail ] rest  ]
    (if w
      (apply del (cond
                   (set? coll) (disj coll w)
                   (seq? coll)  (remove #(= w %) coll)
                   (vector? coll) (vec (remove #(= w % ) coll))
                   (map? coll) (dissoc coll w)
                   (string? coll) (.replaceAll coll (str w) "")
                   :else (throw (Exception. "unsupported collection"))) tail) coll)))

(defn preserve
  "Process collection with transducer in a given manner preserving input data structure"
  [ xf args ]
  (let [ v (eduction xf args) ]
    (cond
      (set? args) (into #{} v)
      (vector? args) (into [] v)
      (map? args) (into {} v)
      (seq? args) (sequence  v)
      :else (throw (Exception. "not a collection")))))

(defn unquote-sentence
  "Get rid of quoting for given data structure"
  [ a ]
  (if (and (seq? a) (= 'quote (first a)))
    (first (rest a)) a))

(defn which?
  "Checks if any of elements is included in coll and says which one
  was found as first. Coll can be map, list, vector and set"
  [ coll & rest ]
  (let [ncoll (if (map? coll) (keys coll) coll)]
    (reduce
      #(or %1  (first (filter (fn[a] (= a %2))
                              ncoll))) nil rest )))


(defn mash-up
  "Merge collections of given type"
  [ & collections ]
  (let [ head-col (first collections) ]
    (cond
      (set? head-col) (apply clojure.set/union collections)
      (vector? head-col) (into [] (apply concat collections))
      (seq? head-col) (apply concat collections)
      (map? head-col) (reduce (partial merge-with mash-up) collections)
      :else head-col)))

(defn deep-filter
  "Filter collection in recursive manner"
  [ pred coll ]
  (reduce (fn [ a b  ]
            (apply conj a (cond
                            (map-entry? b) (deep-filter pred (list (second b)))
                            (seek? b) (deep-filter pred b)
                            (pred b) [b]
                            :else []) )) [] coll))

