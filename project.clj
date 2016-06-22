(defproject datomix "0.1.0"
  :description "Useful tools for working with Datomic"
  :url "https://github.com/naleksander/datomix"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"] [com.datomic/datomic-free "0.9.5372"]]
  :main ^:skip-aot datomix.core
  :target-path "target/%s"
  :global-vars {*warn-on-reflection* false}
  :profiles {:uberjar {:aot :all}})
