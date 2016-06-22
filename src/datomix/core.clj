(ns datomix.core
  (:use [datomix.commons])
  (:require [datomic.api :as d]))

(defn has-group-by
  "Cleans the input map from ... symbol and returns true if such was found"
  [ structure ]
  (if (map? structure)
    (reduce (fn [ [e r m ] [ k v] ]
              (let[ rr (if (col? v)
                         (is? (which? v '... )) false) ]
                [e (or r rr) (assoc m  k (if rr (del v '...) v) ) ] ) )
            [true false {}] structure  ) [false (is? (which? structure '... )) (preserve (filter (partial not= '...)) structure)]))


(defn find-group-by
  "Find out by which key we can group the map (to be changed, now it is proof-of-concept!)"
  [input-maps]
  (when (some identity input-maps)
    (let[ [ map & maps ] input-maps ]
      (let [find-collision (fn [ k a b ]
                             (if (and (scalar? a) (not= a b)) [k a]
                                                              [nil (mash-up a b)]))
            merge-entry (fn [[r m] e]
                          (if r [r m]
                                (let [k (key e) v (val e)]
                                  (if (contains? m k)
                                    (let [ [ r nv ] (find-collision k (get m k) v) ]
                                      [r (assoc m k nv)])
                                    [r (assoc m k v)]))))
            merge2 (fn [[r m1] m2]
                     (reduce merge-entry [r (or m1 {})] (seq m2)))]
        (first (reduce merge2 [nil map] maps))))))

(defn group-by-maps
  "Merge all maps with respect to found grouping value"
  [ maps ]
  (if-let [ g (find-group-by maps)]
    (map (fn [ [_ b ] ] (apply mash-up b)) (group-by g  maps)  )
    (apply mash-up maps)))



(defn extract-mapping
  "Find the structure between :find and :in or :where clause"
  [ structure ]
  (let [ result (->> (drop-while #(not= :find %) structure)
                     rest
                     (take-while #(not (contains? #{ :in :where } %)))  ) first-result (first result) ]
    (if (seek? first-result) first-result
                             (vec result)  )))


(defn reshape-query
  "Replace structure from :find clause with flat one expected by datomic"
  [ structure ex-vars ]
  (let [head (take-while #(not= :find %) structure) tail (drop-while #(not (contains? #{ :in :where } %)) structure)]
    (let [ new-head (conj head :find )
          new-middle (apply conj new-head ex-vars)
          new-end (apply conj new-middle tail)
          ] (vec (reverse new-end) )) ))



(defmacro qx
  "Query with mapping"
  [ original-query & args ]
  (let[ unquoted-query (unquote-sentence original-query)
       result-mapping (extract-mapping unquoted-query)
       [ is-maps-list is-grouping-query cleaned-mapping ] (has-group-by result-mapping)
       found-vars (deep-filter datamic-var? cleaned-mapping) ]
    (let [query (reshape-query unquoted-query  found-vars)]
      `(let[ result# (datomic.api/q  (quote ~query) ~@args ) ]
         (let[ qex# (map (fn[  ~found-vars  ] ~cleaned-mapping )   result#  ) ]
           (if ~is-grouping-query
             (if ~is-maps-list (group-by-maps qex#) (apply mash-up qex#))  qex# ))))))
