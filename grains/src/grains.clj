(ns grains)

;; from https://stackoverflow.com/questions/5057047/how-to-do-exponentiation-in-clojure
(defn- exp [x n]
  (reduce *' (repeat n x)))

(defn square [s]
  (if (not (<= 1 s 64))
    (throw (IllegalArgumentException. "Argument out of range")))
  (exp 2 (dec s)))

;; In `total`, it's more efficient to use `exp` directly rather than `square`,
;; because we don't need to check that the argument falls between 0 and 64.
(defn total []
  (reduce +' (map (partial exp 2) (range 0 64))))

