(ns etl
  [:require [clojure.string :as s]])

(defn transform [source]
  (reduce-kv (fn [m k v]
               (into m (zipmap (map s/lower-case v) (repeat (count v) k))))
             {}
             source))
